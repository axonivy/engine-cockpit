package ch.ivyteam.enginecockpit;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.configuration.ConfigViewImpl;
import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.enginecockpit.model.ConfigProperty;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;
import ch.ivyteam.ivy.workflow.StandardProcessType;
import ch.ivyteam.ivy.workflow.WorkflowNavigationUtil;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class ApplicationDetailBean
{
  private String appName;
  private Application app;
  private SecuritySystem security;
  private String changeSecuritySystem;
  private List<String> environments;

  private ConfigViewImpl configView;

  private ManagerBean managerBean;

  public ApplicationDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }

  public void setAppName(String appName)
  {
    if (this.appName == null || this.appName != appName)
    {
      this.appName = appName;
      reloadDetailApplication();
    }
  }

  public String getAppName()
  {
    return appName;
  }

  private void reloadDetailApplication()
  {
    managerBean.reloadApplications();
    app = managerBean.getApplications().stream().filter(a -> a.getName().equals(appName)).findFirst().get();
    security = initSecuritySystem(appName);
    environments = managerBean.getIApplication(app.getId()).getEnvironmentsSortedByName()
            .stream().map(e -> e.getName()).collect(Collectors.toList());
    configView = new ConfigViewImpl(((IApplicationInternal) getIApplication()).getConfiguration(),
            this::enrichStandardProcessConfigs, List.of(ConfigViewImpl.defaultFilter(), 
                    new ContentFilter<ConfigProperty>("Variables", "Show Variables", 
                            p -> !StringUtils.startsWithIgnoreCase(p.getKey(), "Variables."), true)));
  }

  public Application getApplication()
  {
    return app;
  }

  public SecuritySystem getSecuritySystem()
  {
    return security;
  }

  public String deleteApplication()
  {
    managerBean.getManager().deleteApplication(appName);
    managerBean.reloadApplications();
    return "applications.xhtml?faces-redirect=true";
  }

  public String getSessionCount()
  {
    return managerBean.formatNumber(getIApplication().getSecurityContext().getSessionCount());
  }

  public String getUsersCount()
  {
    return managerBean.formatNumber(security.getUsersCount());
  }

  public String getCasesCount()
  {
    return managerBean.formatNumber(app.getRunningCasesCount());
  }

  public String getPmCount()
  {
    return managerBean.formatNumber(getIApplication().getProcessModels().stream()
            .mapToInt(pm -> pm.getProcessModelVersions().size()).sum());
  }

  public List<String> getEnvironments()
  {
    return environments;
  }

  public void saveApplicationInfos()
  {
    managerBean.getIApplication(app.getId()).setActiveEnvironment(app.getActiveEnv());
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess",
            new FacesMessage("Active Environment change saved"));
  }

  private IApplication getIApplication()
  {
    return managerBean.getIApplication(app.getId());
  }

  private SecuritySystem initSecuritySystem(String applicationName)
  {
    SecuritySystem securitySystem = new SecuritySystem(app.getSecuritySystemName(),
            Optional.of(getIApplication().getSecurityContext()), Arrays.asList(applicationName));
    changeSecuritySystem = securitySystem.getSecuritySystemName();
    return securitySystem;
  }

  public void setSecuritySystem()
  {
    app.setSecuritySystem(changeSecuritySystem);
    security = initSecuritySystem(getAppName());
  }

  public String getChangeSecuritySystem()
  {
    return changeSecuritySystem;
  }

  public void setChangeSecuritySystem(String changeSecuritySystem)
  {
    this.changeSecuritySystem = changeSecuritySystem;
  }

  public ConfigViewImpl getConfigView()
  {
    return configView;
  }

  private ConfigProperty enrichStandardProcessConfigs(ConfigProperty property)
  {
    if (StringUtils.startsWith(property.getKey(), "StandardProcess"))
    {
      property.setConfigValueFormat(ConfigValueFormat.ENUMERATION);
      property.setEnumerationValues(availableStandardProcesses(property));
    }
    return property;
  }

  private List<String> availableStandardProcesses(ConfigProperty config)
  {
    var workflow = WorkflowNavigationUtil.getWorkflowContext(managerBean.getSelectedIApplication());
    var libraries = new LinkedHashSet<String>();
    libraries.add("");
    libraries.add(config.getValue());
    for (StandardProcessType processType : processTypesForConfig(config.getKey()))
    {
      libraries.addAll(workflow.getAvailableStandardProcessImplementations(processType));
    }
    return List.copyOf(libraries);
  }

  private Set<StandardProcessType> processTypesForConfig(String key)
  {
    if (StringUtils.endsWith(key, "DefaultPages"))
    {
      return StandardProcessType.DEFAULT_PAGES_PROCESS_TYPES;
    }
    return StandardProcessType.MAIL_NOTIFICATION_PROCESS_TYPES;
  }
}
