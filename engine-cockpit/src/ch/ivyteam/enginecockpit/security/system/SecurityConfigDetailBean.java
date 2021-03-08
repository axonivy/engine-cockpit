package ch.ivyteam.enginecockpit.security.system;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.ldapbrowser.LdapBrowser;
import ch.ivyteam.enginecockpit.security.system.SecuritySystemConfig.ConfigKey;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.internal.config.ExternalSecuritySystemConfiguration;
import ch.ivyteam.ivy.security.internal.config.JndiConfigBuilder;
import ch.ivyteam.naming.JndiConfig;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SecurityConfigDetailBean
{
  private String name;
  private ManagerBean managerBean;
  private List<String> usedByApps;

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
  private boolean enableInsecureSsl;
  private String referral;
  private String defaultContext;
  private String importUsersOfGroup;
  private String userFilter;
  private String updateTime;
  private boolean importOnDemand;
  private boolean updateEnabled;
  private LdapBrowser ldapBrowser;
  private String ldapBrowserTarget;
  private ExternalSecuritySystemConfiguration securityConfiguration;

  public SecurityConfigDetailBean()
  {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }
  
  public SecurityConfigDetailBean(String secSystemName)
  {
    this();
    setSecuritySystemName(secSystemName);
  }
  
  public String getSecuritySystemName()
  {
    return name;
  }
  
  public void setSecuritySystemName(String secSystemName)
  {
    if (StringUtils.isBlank(name))
    {
      name = secSystemName;
      securityConfiguration = new ExternalSecuritySystemConfiguration(name);
      loadSecuritySystem();
    }
  }

  private void loadSecuritySystem()
  {
    usedByApps = managerBean.getApplications().stream()
            .filter(app -> StringUtils.equals(app.getSecuritySystemName(), name))
            .map(app -> app.getName())
            .collect(Collectors.toList());

    derefAliases = Arrays.asList("always", "never", "finding", "searching");
    protocols = Arrays.asList("", "ssl");
    referrals = Arrays.asList("follow", "ignore", "throw");

    provider = getConfiguration(ConfigKey.PROVIDER);
    url = getConfiguration(ConfigKey.CONNECTION_URL);
    userName = getConfiguration(ConfigKey.CONNECTION_USER_NAME);
    password = getConfiguration(ConfigKey.CONNECTION_PASSWORD);
    
    useLdapConnectionPool = getInitBooleanValue(ConfigKey.CONNECTION_USE_LDAP_CONNECTION_POOL, 
            securityConfiguration.getDefaultBooleanValue(ConfigKey.CONNECTION_USE_LDAP_CONNECTION_POOL));
    derefAlias = getConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_ALIASES);
    ssl = getConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_PROTOCOL)
            .equalsIgnoreCase("ssl");
    enableInsecureSsl = getInitBooleanValue(ConfigKey.CONNECTION_ENABLE_INSECURE_SSL,
            securityConfiguration.getDefaultBooleanValue(ConfigKey.CONNECTION_ENABLE_INSECURE_SSL));
    referral = getConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_REFERRAL);
    defaultContext = getConfiguration(ConfigKey.BINDING_DEFAULT_CONTEXT);
    importUsersOfGroup = getConfiguration(ConfigKey.BINDING_IMPORT_USERS_OF_GROUP);
    userFilter = getConfiguration(ConfigKey.BINDING_USER_FILTER);
    updateTime = getConfiguration(ConfigKey.UPDATE_TIME);
    importOnDemand = getInitBooleanValue(ConfigKey.IMPORT_ONDEMAND,
            securityConfiguration.getDefaultBooleanValue(ConfigKey.IMPORT_ONDEMAND));
    updateEnabled = getInitBooleanValue(ConfigKey.UPDATE_ENABLED,
            securityConfiguration.getDefaultBooleanValue(ConfigKey.UPDATE_ENABLED));
    ldapBrowser = new LdapBrowser();
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
    if (StringUtils.isAllEmpty(password))
    {
      return;
    }
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
  
  private boolean getInitBooleanValue(String key, boolean defaultValue)
  {
    var connectionPool = getConfiguration(key);
    if (StringUtils.isBlank(connectionPool))
    {
      return defaultValue;
    }
    return Boolean.parseBoolean(connectionPool);
  }
  
  private Object getSaveBooleanValue(boolean value, boolean defaultValue)
  {
    if (value == defaultValue)
    {
      return "";
    }
    return value;
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

  public boolean getEnableInsecureSsl()
  {
    return enableInsecureSsl;
  }

  public void setEnableInsecureSsl(boolean enableInsecureSsl)
  {
    this.enableInsecureSsl = enableInsecureSsl;
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
  
  public boolean isImportOnDemand()
  {
    return importOnDemand;
  }

  public void setImportOnDemand(boolean importOnDemand)
  {
    this.importOnDemand = importOnDemand;
  }
  
  public boolean isUpdateEnabled()
  {
    return updateEnabled;
  }

  public void setUpdateEnabled(boolean updateEnabled)
  {
    this.updateEnabled = updateEnabled;
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
    if (!validateUpdateTime())
    {
      return;
    }
    setConfiguration(ConfigKey.CONNECTION_URL, this.url);
    setConfiguration(ConfigKey.CONNECTION_USER_NAME, this.userName);
    setConfiguration(ConfigKey.CONNECTION_PASSWORD, this.password);
    setConfiguration(ConfigKey.CONNECTION_USE_LDAP_CONNECTION_POOL, 
            getSaveBooleanValue(this.useLdapConnectionPool, securityConfiguration.getDefaultBooleanValue(ConfigKey.CONNECTION_USE_LDAP_CONNECTION_POOL)));
    setConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_ALIASES, 
            StringUtils.equals(this.derefAlias, securityConfiguration.getDefaultValue(ConfigKey.CONNECTION_ENVIRONMENT_ALIASES)) ? "" : this.derefAlias);
    setConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_PROTOCOL, this.ssl ? "ssl" : "");
    setConfiguration(ConfigKey.CONNECTION_ENABLE_INSECURE_SSL,
            getSaveBooleanValue(this.enableInsecureSsl, securityConfiguration.getDefaultBooleanValue(ConfigKey.CONNECTION_ENABLE_INSECURE_SSL)));
    setConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_REFERRAL, 
            StringUtils.equals(this.referral, securityConfiguration.getDefaultValue(ConfigKey.CONNECTION_ENVIRONMENT_REFERRAL)) ? "" : this.referral);
    setConfiguration(ConfigKey.UPDATE_TIME, this.updateTime);
    setConfiguration(ConfigKey.IMPORT_ONDEMAND, 
            getSaveBooleanValue(this.importOnDemand, securityConfiguration.getDefaultBooleanValue(ConfigKey.IMPORT_ONDEMAND)));
    setConfiguration(ConfigKey.UPDATE_ENABLED, 
            getSaveBooleanValue(this.updateEnabled, securityConfiguration.getDefaultBooleanValue(ConfigKey.UPDATE_ENABLED)));
    SecuritySystemConfig.setAuthenticationKind(name);
    FacesContext.getCurrentInstance().addMessage("securitySystemConfigSaveSuccess",
            new FacesMessage("Security System configuration saved"));
  }
  
  private boolean validateUpdateTime()
  {
    if (StringUtils.isEmpty(updateTime))
    {
      return true;
    }
    final Pattern pattern = Pattern.compile("^[0-2][0-9]:[0-5][0-9]$");
    if (!pattern.matcher(this.updateTime).matches()) {
      FacesContext.getCurrentInstance().addMessage("syncTime", 
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error","Please check that synchronization Time is max '23:59'"));
      return false;
    }
    return true;
  }

  public void saveBinding()
  {
    setConfiguration(ConfigKey.BINDING_DEFAULT_CONTEXT, this.defaultContext);
    setConfiguration(ConfigKey.BINDING_IMPORT_USERS_OF_GROUP, this.importUsersOfGroup);
    setConfiguration(ConfigKey.BINDING_USER_FILTER, this.userFilter);
    FacesContext.getCurrentInstance().addMessage("securitySystemBindingSaveSuccess",
            new FacesMessage("Security System binding saved"));
  }
  
  private String getConfiguration(String key)
  {
    return SecuritySystemConfig.getOrBlank(SecuritySystemConfig.getPrefix(name) + key);
  }
  
  public void setConfiguration(String key, Object value)
  {
    SecuritySystemConfig.setOrRemove(SecuritySystemConfig.getPrefix(name) + key, value);
  }
  
  public String deleteConfiguration()
  {
    IConfiguration.instance().remove(SecuritySystemConfig.getPrefix(name));
    return "securitysystem.xhtml?faces-redirect=true";
  }
  
  public LdapBrowser getLdapBrowser()
  {
    return ldapBrowser;
  }
  
  public void browseLdap(String field)
  {
    ldapBrowserTarget = field;
    JndiConfig jndiConfig = getJndiConfig(null);
    if (LdapBrowser.IMPORT_USERS_OF_GROUP.equals(ldapBrowserTarget))
    {
      jndiConfig = getJndiConfig(getDefaultContext());
    }
    ldapBrowser.browse(jndiConfig, enableInsecureSsl);
  }
  
  public void chooseLdapName()
  {
    if (LdapBrowser.DEFAULT_CONTEXT.equals(ldapBrowserTarget))
    {
      setDefaultContext(ldapBrowser.getSelectedLdapName());
    }
    if (LdapBrowser.IMPORT_USERS_OF_GROUP.equals(ldapBrowserTarget))
    {
      setImportUsersOfGroup(ldapBrowser.getSelectedLdapName());
    }
  }
  
  public JndiConfig getJndiConfig(String browseDefaultContext)
  {
    return JndiConfigBuilder.create(getSecuritySystemName())
            .url(url)
            .user(userName)
            .password(password)
            .useLdapConnectionPool(useLdapConnectionPool)
            .defaultContext(browseDefaultContext)
            .toJndiConfig();
  }

}
