package ch.ivyteam.enginecockpit.model;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

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
  private String keyPrefix;
  
  public SecuritySystem(ISecurityContext securityContext, String appName)
  {
    securitySystemName = IConfiguration.get().get("Applications." + appName + ".SecuritySystem")
            .orElse(securityContext.getExternalSecuritySystemName());
    securitySystemProvider = securityContext.getExternalSecuritySystemProvider().getProviderName();
    id = securityContext.getId();
    this.appName = appName;
    this.usersCount = securityContext.getUsers().size() - 1;
    this.rolesCount = securityContext.getRoles().size();
    this.keyPrefix = "SecuritySystems." + securitySystemName + ".";
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
    this.keyPrefix = "SecuritySystems." + securitySystemName + ".";
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
	return IConfiguration.get().get(keyPrefix + key).orElse("");
  }

  public void setConfiguration(String key, String value)
  {
	if (StringUtils.isBlank(value) || StringUtils.equals(value, "${encrypt:}"))
	{
	  removeUnusedKey(key);
	  return;
	}
	IConfiguration.get().set(keyPrefix + key, value);
  }
  
  public Map<String, String> getConfigurationMap(String key)
  {
    return IConfiguration.get().getMap(keyPrefix + key);
  }
  
  private void removeUnusedKey(String key)
  {
	if (IConfiguration.get().getProperties().stream().anyMatch(prop -> prop.getKey().equals(keyPrefix + key)))
	{
	  IConfiguration.get().remove(keyPrefix + key);
	}
  }
  
  public void cleanLdapPropertiesMapping()
  {
    Map<String, String> ldapProperties = getConfigurationMap("UserAttribute.Properties");
    for (String key : ldapProperties.keySet())
    {
      removeUnusedKey(key);
    }
  }
  
  public void setAuthenticationKind()
  {
	if (!IConfiguration.get().get(keyPrefix + "Connection.UserName").isPresent())
	{
	  IConfiguration.get().set(keyPrefix + "Connection.AuthenticationKind", "none");
	}
	else
	{
	  removeUnusedKey(keyPrefix + "Connection.AuthenticationKind");
	}
  }
}
