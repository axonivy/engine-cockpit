package ch.ivyteam.enginecockpit;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.configuration.ConfigView;
import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationInternal;

@ManagedBean
@ViewScoped
public class ApplicationDetailBean
{
  private String appName;
  private Application app;
  private SecuritySystem security;
  private String changeSecuritySystem;
  private List<String> environments;
  
  private ConfigView configView;
  
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
    
    configView = new ConfigView(((IApplicationInternal) getIApplication()).getConfiguration());
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
    SecuritySystem securitySystem = new SecuritySystem(app.getSecuritySystemName(), Optional.of(getIApplication().getSecurityContext()), Arrays.asList(applicationName));
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
  
  public ConfigView getConfigView()
  {
    return configView;
  }
  
}
