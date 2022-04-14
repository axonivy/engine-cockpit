package ch.ivyteam.enginecockpit.system;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.system.model.ConfigFile;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class EditorBean {
  private List<ConfigFile> configFiles = new ArrayList<>();

  private ConfigFile activeConfigFile;

  private ManagerBean managerBean;

  private String selectedFile;

  public EditorBean() {
    managerBean = ManagerBean.instance();
    var ivyYaml = UrlUtil.getConfigFile("ivy.yaml");
    configFiles.add(new ConfigFile(ivyYaml, ivyYaml.getFileName().toString(), IConfiguration.instance()));
    configFiles.addAll(managerBean.getIApplications().stream()
            .map(this::createAppConfigFile)
            .collect(Collectors.toList()));
  }

  private ConfigFile createAppConfigFile(IApplication app) {
    var appYaml = UrlUtil.getConfigFile("applications").resolve(app.getName())
            .resolve(IApplication.APP_CONFIG_FILE);
    var name = app.getName() + "/" + IApplication.APP_CONFIG_FILE;
    return new ConfigFile(appYaml, name, ((IApplicationInternal) app).getConfiguration());
  }

  public List<ConfigFile> getConfigFiles() {
    return configFiles;
  }

  public ConfigFile getActiveConfigFile() {
    return activeConfigFile;
  }

  public void setActiveConfigFile(ConfigFile activeConfigFile) {
    this.activeConfigFile = activeConfigFile;
  }

  public int getTabIndex() {
    var configFile = configFiles.stream().filter(f -> f.getFileName().equals(selectedFile)).findFirst()
            .orElse(configFiles.get(0));
    return configFiles.indexOf(configFile);
  }

  public void setTabIndex(@SuppressWarnings("unused") int index) {
    // Do nothing
  }

  public String getSelectedFile() {
    return selectedFile;
  }

  public void setSelectedFile(String file) {
    this.selectedFile = file;
  }

}
