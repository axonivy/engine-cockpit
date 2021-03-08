package ch.ivyteam.enginecockpit.security.model;

public class SecurityMemberProperty
{
  private String key;
  private String value;
  private boolean managed;
  
  public SecurityMemberProperty()
  {
    this("", "", false);
  }

  public SecurityMemberProperty(String key, String property, boolean isManaged)
  {
    this.key = key;
    this.value = property;
    this.managed = isManaged;
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

  public boolean isManaged()
  {
    return managed;
  }
}
