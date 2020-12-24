package ch.ivyteam.enginecockpit.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.configuration.restricted.ConfigValueFormat;
import ch.ivyteam.ivy.configuration.restricted.Property;

@SuppressWarnings("restriction")
public class ConfigProperty
{
  private String key;
  private String value;
  private String defaultValue;
  private boolean isDefault;
  private String source;
  private boolean password;
  private ConfigValueFormat configValueFormat;
  private List<String> enumerationValues;
  private boolean restartRequired;
  private String description;
  private File file;
  private String fileExtension;
  
  public ConfigProperty()
  {
    configValueFormat = ConfigValueFormat.STRING;
  }

  public ConfigProperty(Property property)
  {
    this.key = property.getKey();
    this.value = property.getValue();
    this.defaultValue = property.getMetaData().getDefaultValue();
    this.isDefault = property.isDefault();
    this.source = property.getSource();
    this.password = property.getMetaData().isPassword();
    this.configValueFormat = property.getMetaData().getFormat();
    this.enumerationValues = property.getMetaData().getEnumerationValues();
    this.restartRequired = property.getMetaData().isRestartRequired();
    this.description = property.getMetaData().getDescription();
    this.fileExtension = property.getMetaData().getFileExtension();
    correctValuesIfDaytimeFormat();
    getFile();
  }

  public String getKey()
  {
    return key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getDefaultValue()
  {
    return defaultValue;
  }
  
  public void setDefaultValue(String defaultValue)
  {
    this.defaultValue = defaultValue;
  }
  
  public boolean isDefault()
  {
    return isDefault;
  }
  
  public void setDefault(boolean isDefault)
  {
    this.isDefault = isDefault;
  }

  public String getSource()
  {
    return source;
  }
  
  public void setSource(String source)
  {
    this.source = source;
  }

  public String getShortSource()
  {
    return StringUtils.substring(source, StringUtils.lastIndexOf(source, '/') + 1, getIndexOfSourceSuffix());
  }
  
  public boolean isPassword()
  {
    return password;
  }

  public void setPassword(boolean password)
  {
    this.password = password;
  }
  
  public String getConfigValueFormat()
  {
    return configValueFormat.name();
  }
  
  public void setConfigValueFormat(ConfigValueFormat format)
  {
    this.configValueFormat = format;
  }
  
  public List<String> getEnumerationValues()
  {
    return enumerationValues;
  }
  
  public void setEnumerationValues(List<String> values)
  {
    this.enumerationValues = values;
  }
  
  public boolean isRestartRequired()
  {
    return restartRequired;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public String getHtmlDescription()
  {
    return "<pre>" + UrlUtil.replaceLinks(description) + "</pre>";
  }
  
  public boolean hasDescription()
  {
    return StringUtils.isNotBlank(description);
  }
  
  public String getEditorMode()
  {
    return StringUtils.equals(fileExtension, "json") ? "javascript" : "";
  }
  
  public boolean fileExist()
  {
    return file != null;
  }
  
  public String getFileContent()
  {
    if (!fileExist())
    {
      return "";
    }
    try
    {
      return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }
    catch (IOException e)
    {
      return "Could not read file (" + e.getMessage() + ")";
    }
  }
  
  public StreamedContent downloadFile()
  {
    try
    {
      InputStream newInputStream = Files.newInputStream(file.toPath());
      return new DefaultStreamedContent(newInputStream, "text/plain", file.getName());
    }
    catch (IOException e)
    {
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load file: " + source));
      return null;
    }
  }
  
  private void getFile()
  {
    try
    {
      file = new File(new URI(StringUtils.substring(source, 0, getIndexOfSourceSuffix())));
    }
    catch (Exception ex)
    {
      file = null;
    }
  }
  
  private int getIndexOfSourceSuffix()
  {
    int prefixString = StringUtils.lastIndexOf(source, ',');
    if(prefixString == -1) 
    {
      prefixString = StringUtils.length(source);
    }
    return prefixString;
  }
  
  private void correctValuesIfDaytimeFormat()
  {
    if (configValueFormat.equals(ConfigValueFormat.DAYTIME))
    {
      if (defaultValue.equals("0"))
      {
        this.defaultValue = "00:00";
      }
      if (value.equals("0"))
      {
        this.value = "00:00";
      }
    }
  }
}
