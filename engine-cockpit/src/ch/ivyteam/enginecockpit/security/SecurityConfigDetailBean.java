package ch.ivyteam.enginecockpit.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.util.SecuritySystemConfig;
import ch.ivyteam.enginecockpit.util.SecuritySystemConfig.ConfigKey;
import ch.ivyteam.util.crypto.CryptoUtil;

@ManagedBean
@ViewScoped
public class SecurityConfigDetailBean
{
  private String name;
  private ManagerBean managerBean;
  private List<String> usedByApps;

  private List<String> providers;
  private List<String> derefAliases;
  private List<String> protocols;
  private List<String> referrals;

  private String provider;
  private String url;
  private String userName;
  private String password;
  private boolean useLdapConnectionPool;
  private String derefAlias;
  private boolean ssl;
  private String referral;
  private String defaultContext;
  private String importUsersOfGroup;
  private String userFilter;
  private String updateTime;

  public SecurityConfigDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }
  
  public String getSecuritySystemName()
  {
    return name;
  }
  
  public void setSecuritySystemName(String secSystemName)
  {
    this.name = secSystemName;
    loadSecuritySystem();
  }

  private void loadSecuritySystem()
  {
    usedByApps = managerBean.getApplications().stream()
            .filter(app -> StringUtils.equals(app.getSecuritySystemName(), name))
            .map(app -> app.getName())
            .collect(Collectors.toList());

    providers = Arrays.asList("Microsoft Active Directory", "Novell eDirectory", "ivy Security System");
    derefAliases = Arrays.asList("", "never", "finding", "searching");
    protocols = Arrays.asList("", "ssl");
    referrals = Arrays.asList("", "ignore", "throw");

    provider = getConfiguration(ConfigKey.PROVIDER);
    url = getConfiguration(ConfigKey.CONNECTION_URL);
    userName = getConfiguration(ConfigKey.CONNECTION_USER_NAME);
    password = getConfiguration(ConfigKey.CONNECTION_PASSWORD);
    
    useLdapConnectionPool = getInitValueUseLdapConnectionPool();
    derefAlias = getConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_ALIASES);
    ssl = getConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_PROTOCOL)
            .equalsIgnoreCase("ssl");
    referral = getConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_REFERRAL);
    defaultContext = getConfiguration(ConfigKey.BINDING_DEFAULT_CONTEXT);
    importUsersOfGroup = getConfiguration(ConfigKey.BINDING_IMPORT_USERS_OF_GROUP);
    userFilter = getConfiguration(ConfigKey.BINDING_USER_FILTER);
    updateTime = getConfiguration(ConfigKey.UPDATE_TIME);
  }
  
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public List<String> getUsedByApps()
  {
    return usedByApps;
  }

  public String getProvider()
  {
    return provider;
  }

  public void setProvider(String provider)
  {
    this.provider = provider;
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public boolean getUseLdapConnectionPool()
  {
    return useLdapConnectionPool;
  }

  public void setUseLdapConnectionPool(boolean useLdapConnectionPool)
  {
    this.useLdapConnectionPool = useLdapConnectionPool;
  }
  
  private boolean getInitValueUseLdapConnectionPool()
  {
    String connectionPool = getConfiguration(ConfigKey.CONNECTION_USE_LDAP_CONNECTION_POOL);
    if (StringUtils.isBlank(connectionPool))
    {
      return SecuritySystemConfig.DefaultValue.USE_LDAP_CONNECTION_POOL;
    }
    return Boolean.parseBoolean(connectionPool);
  }
  
  private Object getSaveValueUseLdapConnectionPool()
  {
    if (this.useLdapConnectionPool == SecuritySystemConfig.DefaultValue.USE_LDAP_CONNECTION_POOL)
    {
      return "";
    }
    return this.useLdapConnectionPool;
  }

  public String getDerefAlias()
  {
    return derefAlias;
  }

  public void setDerefAlias(String derefAlias)
  {
    this.derefAlias = derefAlias;
  }

  public boolean getSsl()
  {
    return ssl;
  }

  public void setSsl(boolean ssl)
  {
    this.ssl = ssl;
  }

  public String getReferral()
  {
    return referral;
  }

  public void setReferral(String referral)
  {
    this.referral = referral;
  }

  public String getDefaultContext()
  {
    return defaultContext;
  }

  public void setDefaultContext(String defaultContext)
  {
    this.defaultContext = defaultContext;
  }

  public String getImportUsersOfGroup()
  {
    return importUsersOfGroup;
  }

  public void setImportUsersOfGroup(String importUsersOfGroup)
  {
    this.importUsersOfGroup = importUsersOfGroup;
  }

  public String getUserFilter()
  {
    return userFilter;
  }

  public void setUserFilter(String userFilter)
  {
    this.userFilter = userFilter;
  }

  public String getUpdateTime()
  {
    return updateTime;
  }

  public void setUpdateTime(String updateTime)
  {
    this.updateTime = updateTime;
  }

  public List<String> getProviders()
  {
    return providers;
  }

  public List<String> getDerefAliases()
  {
    return derefAliases;
  }

  public List<String> getReferrals()
  {
    return referrals;
  }

  public List<String> getProtocols()
  {
    return protocols;
  }

  public void saveConfiguration()
  {
    setConfiguration(ConfigKey.PROVIDER, this.provider);
    setConfiguration(ConfigKey.CONNECTION_URL, this.url);
    setConfiguration(ConfigKey.CONNECTION_USER_NAME, this.userName);
    setConfiguration(ConfigKey.CONNECTION_PASSWORD, encryptPassword());
    setConfiguration(ConfigKey.CONNECTION_USE_LDAP_CONNECTION_POOL, getSaveValueUseLdapConnectionPool());
    setConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_ALIASES, this.derefAlias);
    setConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_PROTOCOL, this.ssl ? "ssl" : "");
    setConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_REFERRAL, this.referral);
    setConfiguration(ConfigKey.UPDATE_TIME, this.updateTime);
    SecuritySystemConfig.setAuthenticationKind(name);
    FacesContext.getCurrentInstance().addMessage("securitySystemConfigSaveSuccess",
            new FacesMessage("Security System configuration saved"));
  }
  
  public void saveBinding()
  {
    setConfiguration(ConfigKey.BINDING_DEFAULT_CONTEXT, this.defaultContext);
    setConfiguration(ConfigKey.BINDING_IMPORT_USERS_OF_GROUP, this.importUsersOfGroup);
    setConfiguration(ConfigKey.BINDING_USER_FILTER, this.userFilter);
    FacesContext.getCurrentInstance().addMessage("securitySystemConfigSaveSuccess",
            new FacesMessage("Security System binding saved"));
  }
  
  private String getConfiguration(String key)
  {
    return SecuritySystemConfig.getConfiguration(SecuritySystemConfig.getConfigPrefix(name) + key);
  }
  
  public void setConfiguration(String key, Object value)
  {
    SecuritySystemConfig.setConfiguration(SecuritySystemConfig.getConfigPrefix(name) + key, value);
  }
  
  private String encryptPassword()
  {
    if (StringUtils.isBlank(this.password))
    {
      return "";
    }
    try
    {
      return "${decrypt:" + CryptoUtil.encrypt(this.password) + "}";
    }
    catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
