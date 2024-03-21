package ch.ivyteam.enginecockpit.system.editor;

import java.io.InputStream;
import java.nio.file.Path;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import ch.ivyteam.ivy.configuration.file.provider.ConfigFile;

public class EditorFile {

  private ConfigFile config;
  private String content;
  private boolean migrated;

  public EditorFile(ConfigFile config) {
    this.config = config;
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

  public void setBinary(InputStream is) {
    config.setBinaryFile(is);
  }

  public boolean isBinary() {
    return config.isKeystoreFile();
  }

  public boolean isReadOnly() {
    return config.isReadOnly();
  }
}
