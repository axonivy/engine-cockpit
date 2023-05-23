package ch.ivyteam.enginecockpit.system.editor;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
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

  public List<EditorFile> completeFiles(String query) {
    String queryLowerCase = query.toLowerCase();
    return configFiles.stream()
            .filter(file -> file.getFileName().toLowerCase().contains(queryLowerCase))
            .toList();
  }

  public void onload() {
    if (selectedFile == null) {
      activeConfigFile = configFiles.get(0);
    } else {
      activeConfigFile = configFiles.stream()
            .filter(f -> f.getFileName().equalsIgnoreCase(selectedFile))
            .findFirst()
            .orElse(null);
    }
    if (activeConfigFile == null) {
      ResponseHelper.notFound("Config File '" + selectedFile + "' not found");
      return;
    }
    PrimeFaces.current().executeScript("handleAutocompleteSelection()");
  }

  public EditorFile getActiveConfigFile() {
    return activeConfigFile;
  }

  public void setActiveConfigFile(EditorFile activeConfigFile) {
    this.activeConfigFile = activeConfigFile;
  }

  public String getSelectedFile() {
    return selectedFile;
  }

  public void setSelectedFile(String selectedFile) {
    this.selectedFile = selectedFile;
  }
}
