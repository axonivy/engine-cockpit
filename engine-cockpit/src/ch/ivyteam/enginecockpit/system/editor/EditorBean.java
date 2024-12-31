package ch.ivyteam.enginecockpit.system.editor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.ivy.configuration.file.provider.ConfigFileRepository;

@ManagedBean
@ViewScoped
public class EditorBean {
  private List<EditorFile> configFiles = new ArrayList<>();

  private EditorFile activeConfigFile;
  private String selectedFile;

  private StreamedContent stream;

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
    configFiles = ConfigFileRepository.instance().all().map(EditorFile::new).toList();
    selectedFile = selectedFile == null ? "ivy.yaml" : selectedFile;
    activeConfigFile = configFiles.stream()
        .filter(f -> f.getFileName().equalsIgnoreCase(selectedFile))
        .findFirst()
        .orElse(null);
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
    return stream;
  }

  public void fileDownloadView() {
    stream = DefaultStreamedContent.builder()
        .name(activeConfigFile.getPath().getFileName().toString())
        .stream(this::getInputStream)
        .build();
  }

  public InputStream getInputStream() {
    try {
      var path = activeConfigFile.getPath();
      return Files.newInputStream(path);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  public long getSize() {
    return activeConfigFile.size();
  }

  public String getName() {
    return activeConfigFile.getFileName();
  }

  public void upload(FileUploadEvent event) {
    try (InputStream is = event.getFile().getInputStream()) {
      String originalFileName = event.getFile().getFileName();
      String extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1);
      if (!activeConfigFile.getFileName().endsWith(extension)) {
        Message.error()
            .summary("Invalid extension")
            .detail("'" + originalFileName + "' could not be uploaded because of wrong file extension.")
            .show();
        return;
      }
      activeConfigFile.setBinary(is);
      Message.info()
          .summary("File Uploaded")
          .detail("'" + originalFileName + "' uploaded successfully and stored as " + getName() + ".")
          .show();
    } catch (Exception ex) {
      throw new RuntimeException("Failed to load inputstream", ex);
    }
  }

  public boolean canSave() {
    return !activeConfigFile.isReadOnly();
  }
}
