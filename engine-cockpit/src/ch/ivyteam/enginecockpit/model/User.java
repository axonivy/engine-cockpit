package ch.ivyteam.enginecockpit.model;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.administrator.Administrator;

public class User
{
  private String name;
  private String fullName;
  private String email;
  private String password;
  private String realPassword = "";
  private String externalName = "";
  private String externalId = "";

  private boolean loggedIn;
  private boolean enabled = true;
  private boolean isExternal;
  private long id;

  public User()
  {
  }

  public User(IUser user)
  {
    this.name = user.getName();
    this.fullName = user.getFullName();
    this.email = user.getEMailAddress();
    this.loggedIn = false;
    this.enabled = user.isEnabled();
    this.isExternal = user.isExternal();
    this.externalName = user.getExternalName();
    this.externalId = user.getExternalId();
    this.id = user.getId();
  }

  public User(Administrator admin)
  {
    setName(admin.getUsername());
    setFullName(admin.getFullName());
    setEmail(admin.getEmail());
    setRealPassword(admin.getPassword());
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getFullName()
  {
    return fullName;
  }

  public void setFullName(String fullName)
  {
    this.fullName = fullName;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public boolean isLoggedIn()
  {
    return loggedIn;
  }

  public void setLoggedIn(boolean loggedIn)
  {
    this.loggedIn = loggedIn;
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public boolean isExternal()
  {
    return isExternal;
  }

  public String getExternalName()
  {
    return externalName;
  }

  public String getExternalId()
  {
    return externalId;
  }

  //Use for <p:password redisplay="true"> without leak the real password in the DOM
  public void setRealPassword(String realPassword)
  {
    this.password = "*".repeat(realPassword.length()); 
    this.realPassword = realPassword;
  }
  
  public String getRealPassword()
  {
    if (!StringUtils.equals(password, "*".repeat(realPassword.length())))
    {
      return password;
    }
    return realPassword;
  }
  
  public Administrator getAdmin()
  {
    return new Administrator.Builder().username(getName())
            .fullName(getFullName())
            .email(getEmail())
            .password(getRealPassword())
            .toAdministrator();
  }

  @Override
  public String toString()
  {
    return name;
  }

}
