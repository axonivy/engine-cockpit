package ch.ivyteam.enginecockpit.security.system;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.ldapbrowser.LdapBrowser;
import ch.ivyteam.enginecockpit.security.system.SecuritySystemConfig.ConfigKey;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.identity.jndi.ads.MicrosoftActiveDirectoryIdentityProvider;
import ch.ivyteam.ivy.security.identity.jndi.nds.NovellEDirectoryIdentityProvider;
import ch.ivyteam.naming.JndiConfig;
import ch.ivyteam.naming.JndiProvider;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SecurityLdapBean {

  private String name;
  private String provider;

  private List<String> derefAliases;
  private List<String> protocols;
  private List<String> referrals;

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
  private LdapBrowser ldapBrowser;
  private String ldapBrowserTarget;
  private ExternalSecuritySystemConfiguration securityConfiguration;

  public SecurityLdapBean() {}

  public SecurityLdapBean(String secSystemName) {
    this();
    setSecuritySystemName(secSystemName);
  }

  public String getSecuritySystemName() {
    return name;
  }

  public void setSecuritySystemName(String secSystemName) {
    if (StringUtils.isBlank(name)) {
      name = secSystemName;
      securityConfiguration = new ExternalSecuritySystemConfiguration(name);
      loadLdapConfig();
    }
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getLink() {
    return "security-ldap.xhtml?securitySystemName=" + name;
  }

  private void loadLdapConfig() {
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
    ssl = "ssl"
        .equalsIgnoreCase(getConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_PROTOCOL));
    enableInsecureSsl = getInitBooleanValue(ConfigKey.CONNECTION_ENABLE_INSECURE_SSL,
        securityConfiguration.getDefaultBooleanValue(ConfigKey.CONNECTION_ENABLE_INSECURE_SSL));
    referral = getConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_REFERRAL);
    defaultContext = getConfiguration(ConfigKey.BINDING_DEFAULT_CONTEXT);
    importUsersOfGroup = getConfiguration(ConfigKey.BINDING_IMPORT_USERS_OF_GROUP);
    userFilter = getConfiguration(ConfigKey.BINDING_USER_FILTER);
    ldapBrowser = new LdapBrowser();
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    if (StringUtils.isAllEmpty(password)) {
      return;
    }
    this.password = password;
  }

  public boolean getUseLdapConnectionPool() {
    return useLdapConnectionPool;
  }

  public void setUseLdapConnectionPool(boolean useLdapConnectionPool) {
    this.useLdapConnectionPool = useLdapConnectionPool;
  }

  private boolean getInitBooleanValue(String key, boolean defaultValue) {
    var connectionPool = getConfiguration(key);
    if (StringUtils.isBlank(connectionPool)) {
      return defaultValue;
    }
    return Boolean.parseBoolean(connectionPool);
  }

  private Object getSaveBooleanValue(boolean value, boolean defaultValue) {
    if (value == defaultValue) {
      return "";
    }
    return value;
  }

  public String getDerefAlias() {
    return derefAlias;
  }

  public void setDerefAlias(String derefAlias) {
    this.derefAlias = derefAlias;
  }

  public boolean getSsl() {
    return ssl;
  }

  public void setSsl(boolean ssl) {
    this.ssl = ssl;
  }

  public boolean getEnableInsecureSsl() {
    return enableInsecureSsl;
  }

  public void setEnableInsecureSsl(boolean enableInsecureSsl) {
    this.enableInsecureSsl = enableInsecureSsl;
  }

  public String getReferral() {
    return referral;
  }

  public void setReferral(String referral) {
    this.referral = referral;
  }

  public String getDefaultContext() {
    return defaultContext;
  }

  public void setDefaultContext(String defaultContext) {
    this.defaultContext = defaultContext;
  }

  public String getImportUsersOfGroup() {
    return importUsersOfGroup;
  }

  public void setImportUsersOfGroup(String importUsersOfGroup) {
    this.importUsersOfGroup = importUsersOfGroup;
  }

  public String getUserFilter() {
    return userFilter;
  }

  public void setUserFilter(String userFilter) {
    this.userFilter = userFilter;
  }

  public List<String> getDerefAliases() {
    return derefAliases;
  }

  public List<String> getReferrals() {
    return referrals;
  }

  public List<String> getProtocols() {
    return protocols;
  }

  public void saveConnection() {
    setConfiguration(ConfigKey.CONNECTION_URL, this.url);
    setConfiguration(ConfigKey.CONNECTION_USER_NAME, this.userName);
    setConfiguration(ConfigKey.CONNECTION_PASSWORD, this.password);
    setConfiguration(ConfigKey.CONNECTION_USE_LDAP_CONNECTION_POOL,
        getSaveBooleanValue(this.useLdapConnectionPool, securityConfiguration
            .getDefaultBooleanValue(ConfigKey.CONNECTION_USE_LDAP_CONNECTION_POOL)));
    setConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_ALIASES,
        Objects.equals(this.derefAlias,
            securityConfiguration.getDefaultValue(ConfigKey.CONNECTION_ENVIRONMENT_ALIASES)) ? ""
                : this.derefAlias);
    setConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_PROTOCOL, this.ssl ? "ssl" : "");
    setConfiguration(ConfigKey.CONNECTION_ENABLE_INSECURE_SSL,
        getSaveBooleanValue(this.enableInsecureSsl,
            securityConfiguration.getDefaultBooleanValue(ConfigKey.CONNECTION_ENABLE_INSECURE_SSL)));
    setConfiguration(ConfigKey.CONNECTION_ENVIRONMENT_REFERRAL,
        Objects.equals(this.referral,
            securityConfiguration.getDefaultValue(ConfigKey.CONNECTION_ENVIRONMENT_REFERRAL)) ? ""
                : this.referral);
    SecuritySystemConfig.setAuthenticationKind(name);
    FacesContext.getCurrentInstance().addMessage("securityLdapConnectionSaveSuccess",
        new FacesMessage("Security System Connection saved"));
  }

  public void saveBinding() {
    setConfiguration(ConfigKey.BINDING_DEFAULT_CONTEXT, this.defaultContext);
    setConfiguration(ConfigKey.BINDING_IMPORT_USERS_OF_GROUP, this.importUsersOfGroup);
    setConfiguration(ConfigKey.BINDING_USER_FILTER, this.userFilter);
    FacesContext.getCurrentInstance().addMessage("securityLdapBindingSaveSuccess",
        new FacesMessage("Security System Binging saved"));
  }

  private String getConfiguration(String key) {
    return SecuritySystemConfig.getOrBlank(SecuritySystemConfig.getPrefix(name) + key);
  }

  public void setConfiguration(String key, Object value) {
    SecuritySystemConfig.setOrRemove(SecuritySystemConfig.getPrefix(name) + key, value);
  }

  public LdapBrowser getLdapBrowser() {
    return ldapBrowser;
  }

  public void browseDefaultContext() {
    ldapBrowserTarget = LdapBrowser.DEFAULT_CONTEXT;
    ldapBrowser.browse(getJndiConfig(null), enableInsecureSsl, defaultContext);
  }

  public void browseUsersOfGroup() {
    ldapBrowserTarget = LdapBrowser.IMPORT_USERS_OF_GROUP;
    ldapBrowser.browse(getJndiConfig(getDefaultContext()), enableInsecureSsl, importUsersOfGroup);
  }

  public void chooseLdapName() {
    if (LdapBrowser.DEFAULT_CONTEXT.equals(ldapBrowserTarget)) {
      setDefaultContext(ldapBrowser.getSelectedNameString());
    }
    if (LdapBrowser.IMPORT_USERS_OF_GROUP.equals(ldapBrowserTarget)) {
      setImportUsersOfGroup(ldapBrowser.getSelectedNameString());
    }
  }

  public JndiConfig getJndiConfig(String browseDefaultContext) {
    var authenticationKind = getConfiguration(ConfigKey.CONNECTION_AUTHENTICATION_KIND);
    if (StringUtils.isBlank(authenticationKind)) {
      authenticationKind = StringUtils.isBlank(userName) && StringUtils.isBlank(password) ? JndiConfig.AUTH_KIND_NONE : JndiConfig.AUTH_KIND_SIMPLE;
    }
    var envProps = getEnvironmentProperties();

    var jndiProvider = provider();
    if (jndiProvider == null) {
      throw new IllegalStateException("provider is null " + provider);
    }

    return new JndiConfig(jndiProvider,
        StringUtils.defaultIfBlank(url, ExternalSecuritySystemConfiguration.props(provider).get(ConfigKey.CONNECTION_URL)),
        authenticationKind,
        StringUtils.defaultIfBlank(userName, ExternalSecuritySystemConfiguration.props(provider).get(ConfigKey.CONNECTION_USER_NAME)),
        StringUtils.defaultIfBlank(password, ExternalSecuritySystemConfiguration.props(provider).get(ConfigKey.CONNECTION_PASSWORD)),
        useLdapConnectionPool,
        browseDefaultContext,
        envProps);
  }

  public JndiProvider provider() {
    switch (provider) {
      case NovellEDirectoryIdentityProvider.ID:
        return JndiProvider.NOVELL_E_DIRECTORY;
      case MicrosoftActiveDirectoryIdentityProvider.ID:
        return JndiProvider.ACTIVE_DIRECTORY;
      default:
        return null;
    }
  }

  public Map<String, String> getEnvironmentProperties() {
    var properties = IConfiguration.instance().getMap(SecuritySystemConfig.SECURITY_SYSTEMS + "." + name + "." + "Connection.Environment");
    var newProperties = new HashMap<String, String>();
    final String prefix = "Connection.Environment.";
    for (var entry : properties.entrySet()) {
      var originalKey = entry.getKey();
      var key = (originalKey != null && originalKey.toLowerCase().startsWith(prefix.toLowerCase()))
          ? originalKey.substring(prefix.length())
          : originalKey;
      var value = entry.getValue();
      newProperties.put(key, value);
    }
    return newProperties;
  }
}
