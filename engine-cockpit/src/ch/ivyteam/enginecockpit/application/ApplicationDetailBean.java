package ch.ivyteam.enginecockpit.application;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.commons.ContentFilter;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.configuration.model.ConfigProperty;
import ch.ivyteam.enginecockpit.configuration.model.ConfigViewImpl;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.restricted.IApplicationInternal;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.workflow.StandardProcessType;
import ch.ivyteam.ivy.workflow.standard.DefaultPagesConfigurator;
import ch.ivyteam.ivy.workflow.standard.StandardProcessStartFinder;

@ManagedBean
@ViewScoped
public class ApplicationDetailBean {

  private String appName;
  private String appVersion;

  private Application app;

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

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }
  
  public String getAppVersion() {
    return appVersion;
  }

  public void onload() {
    managerBean.reloadApplications();
    app = managerBean.getApplications().stream()
        .filter(a -> a.getName().equals(appName))
        .filter(a -> a.version() == Integer.parseInt(appVersion))
        .findAny()
        .orElse(null);
    if (app == null) {
      ResponseHelper.notFound(Ivy.cm().content("/common/NotFoundApplication").replace("application", appName).get());
      return;
    }

    configView = new ConfigViewImpl(((IApplicationInternal) getIApplication()).getConfiguration(),
        this::enrichPmvProperties, List.of(ConfigViewImpl.defaultFilter(),
            new ContentFilter<>("Variables", Ivy.cm().co("/configuration/ShowVariablesMessage"),
                p -> !Strings.CI.startsWith(p.getKey(), "Variables."), true),
            new ContentFilter<>("Databases", Ivy.cm().co("/configuration/ShowDatabasesMessage"),
                p -> !Strings.CI.startsWith(p.getKey(), "Databases."), true),
            new ContentFilter<>("RestClients", Ivy.cm().co("/configuration/ShowRestClientsMessage"),
                p -> !Strings.CI.startsWith(p.getKey(), "RestClients."), true),
            new ContentFilter<>("WebServiceClients", Ivy.cm().co("/configuration/ShowWebServiceClientsMessage"),
                p -> !Strings.CI.startsWith(p.getKey(), "WebServiceClients."), true)));
  }

  public Application getApplication() {
    return app;
  }

  public SecuritySystem getSecuritySystem() {
    return new SecuritySystem(app.getSecurityContext());
  }

  public String deleteApplication() {
    managerBean.apps().delete(app.getName(), app.version());
    managerBean.reloadApplications();
    return "applications.xhtml?faces-redirect=true";
  }

  public void reloadConfig() {
    var app = ((IApplicationInternal) getIApplication());
    app.reloadConfig();
    Message.info()
        .clientId("applicationMessage")
        .summary(Ivy.cm().content("/configuration/ReloadApplicationConfigurationMessage")
            .replace("application", app.getName()).get())
        .show();
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
    return managerBean.formatNumber(getIApplication().getProcessModelVersions().count());
  }

  private IApplication getIApplication() {
    return managerBean.getIApplication(app.getId());
  }

  public ConfigViewImpl getConfigView() {
    return configView;
  }

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
        .forEach(type -> libraries.addAll(configurator.findLibraries(type)));
    return List.copyOf(libraries);
  }

  private static List<String> librariesOf(IApplication app) {
    List<String> libs = new LinkedList<>();
    libs.add("");
     var available = app.getProcessModelVersions()     
     .map(pmv -> pmv.getLibraryId())
     .distinct()
     .collect(Collectors.toList());
    libs.addAll(available);
    return libs;
  }
}
