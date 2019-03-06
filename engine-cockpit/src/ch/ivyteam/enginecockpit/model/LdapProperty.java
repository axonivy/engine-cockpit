package ch.ivyteam.enginecockpit.model;

public class LdapProperty
{

  private String name;
  private String ldapAttribute;

  public LdapProperty()
  {
    this.name = "";
    this.ldapAttribute = "";
  }

  public LdapProperty(String name, String ldapAttribute)
  {
    this.name = name;
    this.ldapAttribute = ldapAttribute;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getLdapAttribute()
  {
    return ldapAttribute;
  }

  public void setLdapAttribute(String ldapAttribute)
  {
    this.ldapAttribute = ldapAttribute;
  }

  public boolean isComplete()
  {
    return !name.isEmpty() && !ldapAttribute.isEmpty();
  }

}
