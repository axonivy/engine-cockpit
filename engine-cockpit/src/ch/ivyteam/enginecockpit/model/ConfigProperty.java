package ch.ivyteam.enginecockpit.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

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
    if (!isDefault)
    {
      this.source = property.getSource();
    }
    this.password = property.getMetaData().isPassword();
    this.configValueFormat = property.getMetaData().getFormat();
    this.enumerationValues = property.getMetaData().getEnumerationValues();
    this.restartRequired = property.getMetaData().isRestartRequired();
    this.description = property.getMetaData().getDescription();
    correctValuesIfDaytimeFormat();
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
  
  public List<String> getEnumerationValues()
  {
    return enumerationValues;
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
    return "<pre>"+description+"</pre>";
  }
  
  public boolean hasDescription()
  {
    return StringUtils.isNotBlank(description);
  }
  
  public String getFileContent()
  {
    try
    {
      return FileUtils.readFileToString(getFile(), StandardCharsets.UTF_8);
    }
    catch (IOException | URISyntaxException e)
    {
      e.printStackTrace();
    }
    return "";
  }
  
  public StreamedContent downloadFile()
  {
    try
    {
      InputStream newInputStream = Files.newInputStream(getFile().toPath());
      return new DefaultStreamedContent(newInputStream, "text/plain", getFile().getName());
    }
    catch (IOException | URISyntaxException e)
    {
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to load file: " + source));
      return null;
    }
  }
  
  private File getFile() throws URISyntaxException
  {
    return new File(new URI(StringUtils.substring(source, 0, getIndexOfSourceSuffix())));
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
