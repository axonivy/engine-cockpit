package ch.ivyteam.enginecockpit.application;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.Strings;

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
import ch.ivyteam.ivy.application.restricted.IEnvironment;
import ch.ivyteam.ivy.workflow.StandardProcessType;
import ch.ivyteam.ivy.workflow.standard.DefaultPagesConfigurator;
import ch.ivyteam.ivy.workflow.standard.StandardProcessStartFinder;

@SuppressWarnings("removal")
@ManagedBean
@ViewScoped
public class ApplicationDetailBean {

  private String appName;

  private Application app;
  private List<String> environments;

  private ConfigViewImpl configView;

  private final ManagerBean managerBean;

  public ApplicationDetailBean() {
    managerBean = ManagerBean.instance();
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppName() {
    return appName;
  }

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

    environments = managerBean.getIApplication(app.getId())
        .getEnvironmentsSortedByName()
        .stream()
        .map(IEnvironment::getName)
        .collect(Collectors.toList());

    configView = new ConfigViewImpl(((IApplicationInternal) getIApplication()).getConfiguration(),
        this::enrichPmvProperties, List.of(ConfigViewImpl.defaultFilter(),
            new ContentFilter<>("Variables", "Show Variables",
                p -> !Strings.CI.startsWith(p.getKey(), "Variables."), true),
            new ContentFilter<>("Databases", "Show Databases",
                p -> !Strings.CI.startsWith(p.getKey(), "Databases."), true),
            new ContentFilter<>("RestClients", "Show Rest Clients",
                p -> !Strings.CI.startsWith(p.getKey(), "RestClients."), true),
            new ContentFilter<>("WebServiceClients", "Show Web Service Clients",
                p -> !Strings.CI.startsWith(p.getKey(), "WebServiceClients."), true)));
  }

  public Application getApplication() {
    return app;
  }

  public SecuritySystem getSecuritySystem() {
    return new SecuritySystem(app.getSecurityContext());
  }

  public String deleteApplication() {
    managerBean.apps().delete(appName);
    managerBean.reloadApplications();
    return "applications.xhtml?faces-redirect=true";
  }

  public String getSessionCount() {
    return managerBean.formatNumber(getIApplication().getSecurityContext().sessions().count());
  }

  public String getUsersCount() {
    return managerBean.formatNumber(getSecuritySystem().getUsersCount());
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

  public void saveApplicationInfos() {
    getIApplication().setActiveEnvironment(app.getActiveEnv());
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess",
        new FacesMessage("Active Environment change saved"));
  }

  private IApplication getIApplication() {
    return managerBean.getIApplication(app.getId());
  }

  public ConfigViewImpl getConfigView() {
    return configView;
  }

  @SuppressWarnings("restriction")
  private ConfigProperty enrichPmvProperties(ConfigProperty property) {
    if (Strings.CS.startsWith(property.getKey(), "StandardProcess")) {
      property.setConfigValueFormat(ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat.ENUMERATION);
      property.setEnumerationValues(() -> availableStandardProcesses(property));
    }
    if (Objects.equals(property.getKey(), "OverrideProject")) {
      property.setConfigValueFormat(ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat.ENUMERATION);
      property.setEnumerationValues(() -> librariesOf(managerBean.getSelectedIApplication()));
    }
    return property;
  }

  private List<String> availableStandardProcesses(ConfigProperty config) {
    var configurator = DefaultPagesConfigurator.of(getIApplication());
    var libraries = new LinkedHashSet<String>();
    libraries.add("");
    libraries.add(StandardProcessStartFinder.AUTO);
    libraries.add(config.getValue());
    Arrays.stream(StandardProcessType.values())
        .filter(type -> type.kind() == StandardProcessType.Kind.PAGE)
        .forEach(type -> libraries.addAll(configurator.findLibraries(type)));
    return List.copyOf(libraries);
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
