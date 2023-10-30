package ch.ivyteam.enginecockpit.system.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class ConfigFile {
  private Path file;
  private String content;
  private String fileName;
  private IConfiguration config;

  public ConfigFile(Path file, String filename, IConfiguration config) {
    this.fileName = filename;
    this.config = config;
    this.file = file;
    this.content = getFileContent();
  }

  public String getFileName() {
    return fileName;
  }

  public String getVarName() {
    var name = StringUtils.replace(fileName, "-", "_");
    name = StringUtils.replace(name, "/", "_");
    return StringUtils.removeEnd(name, ".yaml");
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  private String getFileContent() {
    try {
      return Files.readString(file);
    } catch (IOException e) {
      return "";
    }
  }

  public void save() {
    try {
      var dir = file.getParent();
      if (!Files.exists(dir)) {
        Files.createDirectories(dir);
      }
      Files.writeString(file, content);
      FacesContext.getCurrentInstance().addMessage("editorMessage",
              new FacesMessage("Saved " + fileName + " Successfully", ""));
    } catch (IOException ex) {
      FacesContext.getCurrentInstance().addMessage("editorMessage",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error write config '" + fileName + "' file",
                      ex.getMessage()));
    }
  }

  public String getKeys() {
    return config.getMetadata().keySet().stream()
            .flatMap(key -> Arrays.asList(key.split("\\.")).stream())
            .distinct()
            .collect(Collectors.joining(","));
  }
}
