package ch.ivyteam.enginecockpit.system.editor;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.ivy.configuration.file.provider.ConfigFileRepository;

@ManagedBean
@ViewScoped
public class EditorBean {
  private List<EditorFile> configFiles = new ArrayList<>();

  private EditorFile activeConfigFile;
  private String selectedFile;

  public EditorBean() {
    configFiles = ConfigFileRepository.instance().all().map(EditorFile::new).toList();
  }

  public List<EditorFile> getConfigFiles() {
    return configFiles;
  }

  public EditorFile getActiveConfigFile() {
    return activeConfigFile;
  }

  public void setActiveConfigFile(EditorFile activeConfigFile) {
    this.activeConfigFile = activeConfigFile;
  }

  public int getTabIndex() {
    var configFile = configFiles.stream()
            .filter(f -> f.getFileName().equals(selectedFile))
            .findFirst()
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
