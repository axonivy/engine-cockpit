package ch.ivyteam.enginecockpit.system.editor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.ivy.configuration.file.provider.ConfigFileRepository;

@ManagedBean
@ViewScoped
public class EditorBean {
  private List<EditorFile> configFiles = new ArrayList<>();

  private EditorFile activeConfigFile;
  private String selectedFile;

  private StreamedContent file;

  private DefaultStreamedContent binary;

  public EditorBean() {
    configFiles = ConfigFileRepository.instance().all().map(EditorFile::new).toList();
    selectedFile = "ivy.yaml";
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

  public boolean isBinary() {
    return activeConfigFile.isBinary();
  }

  public StreamedContent getFile() {
      return file;
  }

  public void fileDownloadView() {
    file = DefaultStreamedContent.builder()
            .name(activeConfigFile.getPath().getFileName().toString())
            .stream(() -> getInputStream())
            .build();
  }

  public StreamedContent getBinary() {
    return binary;
  }

  public FileInputStream getInputStream() {
    try {
      var path = activeConfigFile.getPath();
      var inputStream = new FileInputStream(path.toFile());
      file = DefaultStreamedContent.builder()
              .name(path.getFileName().toString())
              .stream(() -> inputStream)
              .build();
    } catch (FileNotFoundException e) {
      throw new RuntimeException();
    }
    return null;
  }

  public Integer getSize() throws IOException {
    return getInputStream().available();
  }

  public String getName() {
    return activeConfigFile.getFileName();
  }

  public void upload(FileUploadEvent event) throws Exception {
    try (InputStream is = event.getFile().getInputStream()) {
      activeConfigFile.setBinary(is);
    }
  }

  public boolean canSave() {
    return activeConfigFile != null && !activeConfigFile.isReadOnly();
  }
}
