package ch.ivyteam.enginecockpit.system.editor;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.file.provider.ConfigFile;

public class EditorFile {

  private ConfigFile config;
  private String content;

  public EditorFile(ConfigFile config) {
    this.config = config;
  }

  public String getVarName() {
    var fileName = FilenameUtils.getBaseName(config.name());
    fileName = StringUtils.replace(fileName, "-", "_");
    return StringUtils.replace(fileName, "/", "_");
  }

  public String getKeys() {
    return config.getKeys();
  }

  public String getFileName() {
    return config.name();
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
}
