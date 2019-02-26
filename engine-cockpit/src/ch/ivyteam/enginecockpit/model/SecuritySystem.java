package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityContext;

@SuppressWarnings("restriction")
public class SecuritySystem
{

  private String securitySystemProvider;
  private String securitySystemName;
  private long id;
  private String appName;
  private int usersCount;
  private int rolesCount;
  
  public SecuritySystem(ISecurityContext securityContext, String appName)
  {
    securitySystemName = IConfiguration.get().get("Applications." + appName + ".SecuritySystem")
            .orElse(securityContext.getExternalSecuritySystemName());
    securitySystemProvider = securityContext.getExternalSecuritySystemProvider().getProviderName();
    id = securityContext.getId();
    this.appName = appName;
    this.usersCount = securityContext.getUsers().size() - 1;
    this.rolesCount = securityContext.getRoles().size();
  }

  public String getSecuritySystemProvider()
  {
    return securitySystemProvider;
  }

  public void setSecuritySystemProvider(String securitySystemProvider)
  {
    this.securitySystemProvider = securitySystemProvider;
  }

  public String getSecuritySystemName()
  {
    return securitySystemName;
  }

  public void setSecuritySystemName(String securitySystemName)
  {
    this.securitySystemName = securitySystemName;
  }

  public long getId()
  {
    return id;
  }

  public void setId(long id)
  {
    this.id = id;
  }

  public String getAppName()
  {
    return appName;
  }

  public void setAppName(String appName)
  {
    this.appName = appName;
  }
  
  public int getUsersCount()
  {
    return usersCount;
  }

  public int getRolesCount()
  {
    return rolesCount;
  }
  
  public String getConfiguration(String key)
  {
    return IConfiguration.get().get("SecuritySystems." + securitySystemName + "." + key).orElse("");
  }
}
