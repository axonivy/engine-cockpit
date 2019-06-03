package ch.ivyteam.enginecockpit.model;

public class SecurityMemberProperty
{
  private String key;
  private String value;
  private boolean backed;

  public SecurityMemberProperty(String key, String property, boolean isBacked)
  {
    this.key = key;
    this.value = property;
    this.backed = isBacked;
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

  public boolean isBacked()
  {
    return backed;
  }
  
}
