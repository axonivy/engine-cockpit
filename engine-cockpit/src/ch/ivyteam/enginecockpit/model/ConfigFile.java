package ch.ivyteam.enginecockpit.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class ConfigFile
{
private File file;
private String content;
private String fileName;
private IConfiguration config;
  
  public ConfigFile(File file, IConfiguration config)
  {
    this.fileName = file.getName();
    this.config = config;
    this.file = file;
    this.content = getFileContent();
  }
  
  public String getFileName()
  {
    return fileName;
  }
  
  public String getVarName()
  {
    String name = StringUtils.replace(fileName, "-", "_");
    return StringUtils.removeEnd(name, ".yaml");
  }
  
  public String getContent()
  {
    return content;
  }
  
  public void setContent(String content)
  {
    this.content = content;
  }
  
  private String getFileContent()
  {
    try
    {
      return FileUtils.readLines(file, StandardCharsets.UTF_8).stream().collect(Collectors.joining("\n"));
    }
    catch (IOException e)
    {
      return "";
    }
  }
  
  public void save()
  {
    try
    {
      FileUtils.write(file, content, StandardCharsets.UTF_8);
      FacesContext.getCurrentInstance().addMessage("editorMessage",
              new FacesMessage("Saved " + fileName + " successful", ""));
    }
    catch (IOException ex)
    {
      FacesContext.getCurrentInstance().addMessage("editorMessage",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error write config '"+fileName+"' file", ex.getMessage()));
    }
  }
  
  public String getKeys()
  {
    return config.getMetadata().keySet().stream()
            .flatMap(key -> Arrays.asList(key.split("\\.")).stream())
            .distinct()
            .collect(Collectors.joining(","));
  }
}
