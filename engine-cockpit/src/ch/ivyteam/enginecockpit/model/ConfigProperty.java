package ch.ivyteam.enginecockpit.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.ivy.configuration.restricted.Property;

@SuppressWarnings("restriction")
public class ConfigProperty
{
  private String key;
  private String value;
  private boolean isDefault;
  private String source;
  private boolean password;
  
  public ConfigProperty()
  {
    
  }

  public ConfigProperty(Property property)
  {
    this.key = property.getKey();
    this.value = property.getValue();
    this.isDefault = StringUtils.isBlank(property.getMetaData().getSource());
    this.source = property.getMetaData().getSource();
    this.password = property.getMetaData().isPassword();
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

  public String getShortSource()
  {
    return StringUtils.substring(source, StringUtils.lastIndexOf(source, '/') + 1, getIndexOfSourceSuffix());
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

  public void setSource(String source)
  {
    this.source = source;
  }
  
  private File getFile() throws URISyntaxException
  {
    return new File(new URI(StringUtils.substring(source, 0, getIndexOfSourceSuffix())));
  }
  
  public boolean isPassword()
  {
    return password;
  }

  public void setPassword(boolean password)
  {
    this.password = password;
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
}
