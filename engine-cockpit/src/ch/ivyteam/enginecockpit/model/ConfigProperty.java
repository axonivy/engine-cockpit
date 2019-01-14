package ch.ivyteam.enginecockpit.model;

import java.io.File;

import ch.ivyteam.ivy.configuration.restricted.Property;

@SuppressWarnings("restriction")
public class ConfigProperty
{
  private String key;
  private String value;
  private String source;
  private boolean password;

  public ConfigProperty(Property property)
  {
    this.key = property.getKey();
    this.value = property.getValue();
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

  public String getSource()
  {
    return source;
  }

  public String getShortSource()
  {
    return source.substring(source.lastIndexOf(File.separator) + 1);
  }

  public void setSource(String source)
  {
    this.source = source;
  }

  public boolean isPassword()
  {
    return password;
  }

  public void setPassword(boolean password)
  {
    this.password = password;
  }
}
