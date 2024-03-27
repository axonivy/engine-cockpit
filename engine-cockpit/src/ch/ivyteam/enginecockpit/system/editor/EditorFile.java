package ch.ivyteam.enginecockpit.system.editor;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.extensions.event.CompleteEvent;

import ch.ivyteam.ivy.configuration.file.provider.ConfigFile;

public class EditorFile {

  private ConfigFile config;
  private String content;

  public EditorFile(ConfigFile config) {
    this.config = config;
  }

  public String getFileName() {
    return config.name();
  }

  public Path getPath() {
    return config.file();
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent() {
    return config.read();
  }

  public void save() {
    try {
      config.write(content);
      FacesContext.getCurrentInstance().addMessage("editorMessage",
              new FacesMessage("Saved " + config.name() + " Successfully", ""));
    } catch (Exception ex) {
      FacesContext.getCurrentInstance().addMessage("editorMessage",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error write config '" + config.name() + "' file",
                      ex.getMessage()));
    }
  }

  public String getProvider() {
    return config.provider();
  }

  public boolean isReadOnly() {
    return config.isReadOnly();
  }

  public List<String> complete(@SuppressWarnings("unused") CompleteEvent event) {
    return List.of(getKeys().split(",")).stream()
            .map(s -> s + ": ")
            .toList();
  }

  @SuppressWarnings("restriction")
  private String getKeys() {
    if (config.config() == null) {
      return "";
    }
    return config.config().getMetadata().keySet().stream()
            .flatMap(key -> Arrays.asList(key.split("\\.")).stream())
            .distinct()
            .collect(Collectors.joining(","));
  }

  public boolean isOriginalFile() {
    return config.isOriginalFile();
  }

  public Path getOriginalPath() {
    return config.originalFile();
  }
}
