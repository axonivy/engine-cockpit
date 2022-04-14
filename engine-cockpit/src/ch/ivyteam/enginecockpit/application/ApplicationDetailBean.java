package ch.ivyteam.enginecockpit.application;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.commons.ContentFilter;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.configuration.model.ConfigProperty;
import ch.ivyteam.enginecockpit.configuration.model.ConfigViewImpl;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.application.ILibrary;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.workflow.StandardProcessType;
import ch.ivyteam.ivy.workflow.standard.StandardProcessConfigurator;

@ManagedBean
@ViewScoped
public class ApplicationDetailBean {

  private String appName;

  private Application app;
  private SecuritySystem securitySystem;
  private String changeSecuritySystem;
  private List<String> environments;

  private ConfigViewImpl configView;

  private ManagerBean managerBean;

  public ApplicationDetailBean() {
    managerBean = ManagerBean.instance();
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppName() {
    return appName;
  }

  @SuppressWarnings("removal")
  public void onload() {
    managerBean.reloadApplications();
    app = managerBean.getApplications().stream()
            .filter(a -> a.getName().equals(appName))
            .findAny()
            .orElse(null);
    if (app == null) {
      ResponseHelper.notFound("Application '" + appName + "' not found");
      return;
    }

    securitySystem = app.getSecuritySystem();
    changeSecuritySystem = app.getSecuritySystemName();
    environments = managerBean.getIApplication(app.getId())
              .getEnvironmentsSortedByName()
              .stream()
              .map(e -> e.getName())
              .collect(Collectors.toList());
    configView = new ConfigViewImpl(((IApplicationInternal) getIApplication()).getConfiguration(),
            this::enrichPmvProperties, List.of(ConfigViewImpl.defaultFilter(),
                    new ContentFilter<>("Variables", "Show Variables",
                            p -> !StringUtils.startsWithIgnoreCase(p.getKey(), "Variables."), true),
                    new ContentFilter<>("Databases", "Show Databases",
                            p -> !StringUtils.startsWithIgnoreCase(p.getKey(), "Databases."), true),
                    new ContentFilter<>("RestClients", "Show Rest Clients",
                            p -> !StringUtils.startsWithIgnoreCase(p.getKey(), "RestClients."), true),
                    new ContentFilter<>("WebServiceClients", "Show Web Service Clients",
                            p -> !StringUtils.startsWithIgnoreCase(p.getKey(), "WebServiceClients."), true)));
  }

  public Application getApplication() {
    return app;
  }

  public SecuritySystem getSecuritySystem() {
    return securitySystem;
  }

  public String deleteApplication() {
    managerBean.getManager().deleteApplication(appName);
    managerBean.reloadApplications();
    return "applications.xhtml?faces-redirect=true";
  }

  public String getSessionCount() {
    return managerBean.formatNumber(getIApplication().getSecurityContext().sessions().count());
  }

  public String getUsersCount() {
    return managerBean.formatNumber(securitySystem.getUsersCount());
  }

  public String getCasesCount() {
    return managerBean.formatNumber(app.getRunningCasesCount());
  }

  public String getPmCount() {
    return managerBean.formatNumber(getIApplication().getProcessModels().stream()
            .mapToInt(pm -> pm.getProcessModelVersions().size()).sum());
  }

  public List<String> getEnvironments() {
    return environments;
  }

  @SuppressWarnings("removal")
  public void saveApplicationInfos() {
    getIApplication().setActiveEnvironment(app.getActiveEnv());
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess",
            new FacesMessage("Active Environment change saved"));
  }

  private IApplication getIApplication() {
    return managerBean.getIApplication(app.getId());
  }

  public void setSecuritySystem() {
    app.setSecuritySystem(changeSecuritySystem);
    securitySystem = app.getSecuritySystem();
  }

  public String getChangeSecuritySystem() {
    return changeSecuritySystem;
  }

  public void setChangeSecuritySystem(String changeSecuritySystem) {
    this.changeSecuritySystem = changeSecuritySystem;
  }

  public ConfigViewImpl getConfigView() {
    return configView;
  }

  @SuppressWarnings("restriction")
  private ConfigProperty enrichPmvProperties(ConfigProperty property) {
    if (StringUtils.startsWith(property.getKey(), "StandardProcess")) {
      property.setConfigValueFormat(ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat.ENUMERATION);
      property.setEnumerationValues(availableStandardProcesses(property));
    }
    if (Objects.equals(property.getKey(), "OverrideProject")) {
      property.setConfigValueFormat(ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat.ENUMERATION);
      property.setEnumerationValues(librariesOf(managerBean.getSelectedIApplication()));
    }
    return property;
  }

  private List<String> availableStandardProcesses(ConfigProperty config) {
    var configurator = StandardProcessConfigurator.of(getIApplication());
    var libraries = new LinkedHashSet<String>();
    libraries.add("");
    libraries.add(config.getValue());
    for (var processType : processTypesForConfig(config.getKey())) {
      libraries.addAll(configurator.getAvailableStandardProcessImplementations(processType));
    }
    return List.copyOf(libraries);
  }

  private Set<StandardProcessType> processTypesForConfig(String key) {
    if (StringUtils.endsWith(key, "DefaultPages")) {
      return StandardProcessType.DEFAULT_PAGES_PROCESS_TYPES;
    }
    return StandardProcessType.MAIL_NOTIFICATION_PROCESS_TYPES;
  }

  private static List<String> librariesOf(IApplication app) {
    List<String> libs = new LinkedList<>();
    libs.add("");
    var available = app.getProcessModels().stream()
      .flatMap(pm -> pm.getProcessModelVersions().stream())
      .map(IProcessModelVersion::getLibrary)
      .filter(Objects::nonNull)
      .map(ILibrary::getId)
      .distinct()
      .collect(Collectors.toList());
    libs.addAll(available);
    return libs;
  }
}
