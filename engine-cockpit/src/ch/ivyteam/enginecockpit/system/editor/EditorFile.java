package ch.ivyteam.enginecockpit.system.editor;

import java.nio.file.Path;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.extensions.event.CompleteEvent;

import ch.ivyteam.ivy.configuration.file.provider.ConfigFile;

public class EditorFile {

  private ConfigFile config;
  private String content;
  private boolean migrated;

  public EditorFile(ConfigFile config) {
    this.config = config;
  }

  public List<String> complete(@SuppressWarnings("unused") CompleteEvent event) {
    return List.of(config.getKeys().split(",")).stream()
            .map(s -> s + ": ")
            .toList();
  }

  public String getFileName() {
    return config.name();
  }

  public Path getPath() {
    return config.file();
  }

  public boolean isMigrated() {
    return migrated;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getContent() {
    var read = config.read();
    this.migrated = config.isMigrated(read);
    return read;
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
}
