package ch.ivyteam.enginecockpit.security.system;

import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.security.system.SecuritySystemConfig.ConfigKey;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityConstants;
import ch.ivyteam.ivy.security.internal.SecurityContext;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class SecurityProviderBean {

  private String name;
  private SecuritySystem securitySystem;
  private ExternalSecuritySystemConfiguration securityConfiguration;

  private String provider;

  private String onScheduleTime;
  private boolean onScheduleEnabled;
  private boolean synchOnLogin;
  private boolean onScheduleImportUsers;

  public String getSecuritySystemName() {
    return name;
  }

  public void setSecuritySystemName(String secSystemName) {
    if (StringUtils.isBlank(name)) {
      name = secSystemName;
      securityConfiguration = new ExternalSecuritySystemConfiguration(name);
      loadConfiguration();
    }
  }

  public void loadConfiguration() {
    securitySystem = ManagerBean.instance().getSecuritySystems().stream()
            .filter(system -> StringUtils.equals(system.getSecuritySystemName(), name))
            .findAny()
            .orElseThrow();

    provider = getConfiguration(ConfigKey.PROVIDER);
    onScheduleEnabled = getInitBooleanValue(ConfigKey.ON_SCHEDULE_ENABLED,
            securityConfiguration.getDefaultBooleanValue(ConfigKey.ON_SCHEDULE_ENABLED));
    onScheduleTime = getConfiguration(ConfigKey.ON_SCHEDULE_TIME);
    synchOnLogin = getInitBooleanValue(ConfigKey.SYNCH_ON_LOGIN,
            securityConfiguration.getDefaultBooleanValue(ConfigKey.SYNCH_ON_LOGIN));
    onScheduleImportUsers = getInitBooleanValue(ConfigKey.ON_SCHEDULE_IMPORT_USERS,
            securityConfiguration.getDefaultBooleanValue(ConfigKey.ON_SCHEDULE_IMPORT_USERS));
  }

  public boolean isJndiSecuritySystem() {
    return securitySystem.isJndiSecuritySystem();
  }

  public boolean isNotIvySecuritySystem() {
    return !securitySystem.isIvySecuritySystem();
  }

  public boolean isNotJndiAndNotIvySecuritySystem() {
    return !securitySystem.isIvySecuritySystem() && !securitySystem.isJndiSecuritySystem();
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getOnScheduleTime() {
    return onScheduleTime;
  }

  public void setOnScheduleTime(String onScheduleTime) {
    this.onScheduleTime = onScheduleTime;
  }

  public boolean isOnScheduleEnabled() {
    return onScheduleEnabled;
  }

  public void setOnScheduleEnabled(boolean onScheduleEnabled) {
    this.onScheduleEnabled = onScheduleEnabled;
  }

  public boolean isSynchOnLogin() {
    return synchOnLogin;
  }

  public void setSynchOnLogin(boolean synchOnLogin) {
    this.synchOnLogin = synchOnLogin;
  }

  public boolean isOnScheduleImportUsers() {
    return onScheduleImportUsers;
  }

  public void setScheduleImportUsers(boolean onScheduleImportUsers) {
    this.onScheduleImportUsers = onScheduleImportUsers;
  }

  public void saveProvider() {
    if (!validateUpdateTime()) {
      return;
    }

    if (! StringUtils.isEmpty(provider) && (! StringUtils.equals(provider, securitySystem.getSecuritySystemProviderId()))) {
      var context = (SecurityContext) securitySystem.getSecurityContext();
      var key = ch.ivyteam.ivy.configuration.restricted.ConfigKey.create("SecuritySystems").append(securitySystem.getSecuritySystemName());
      IConfiguration.instance().remove(key);
      context.config().setProperty(ISecurityConstants.PROVIDER_CONFIG_KEY, provider);
    }
    setConfiguration(ConfigKey.ON_SCHEDULE_ENABLED,
            getSaveBooleanValue(this.onScheduleEnabled,
                    securityConfiguration.getDefaultBooleanValue(ConfigKey.ON_SCHEDULE_ENABLED)));
    setConfiguration(ConfigKey.ON_SCHEDULE_TIME, this.onScheduleTime);

    setConfiguration(ConfigKey.SYNCH_ON_LOGIN,
            getSaveBooleanValue(this.synchOnLogin,
                    securityConfiguration.getDefaultBooleanValue(ConfigKey.SYNCH_ON_LOGIN)));

    setConfiguration(ConfigKey.ON_SCHEDULE_IMPORT_USERS,
            getSaveBooleanValue(this.onScheduleImportUsers,
                    securityConfiguration.getDefaultBooleanValue(ConfigKey.ON_SCHEDULE_IMPORT_USERS)));

    var msg = new FacesMessage("Security System Identity Provider saved");
    FacesContext.getCurrentInstance().addMessage("securityProviderSaveSuccess", msg);
  }

  private boolean validateUpdateTime() {
    if (StringUtils.isEmpty(onScheduleTime)) {
      return true;
    }
    final Pattern pattern = Pattern.compile("^[0-2][0-9]:[0-5][0-9]$");
    if (!pattern.matcher(this.onScheduleTime).matches()) {
      var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Please check that synchronization Time is max '23:59'");
      FacesContext.getCurrentInstance().addMessage("onScheduleTime", msg);
      return false;
    }
    return true;
  }

  private String getConfiguration(String key) {
    return SecuritySystemConfig.getOrBlank(SecuritySystemConfig.getPrefix(name) + key);
  }

  private void setConfiguration(String key, Object value) {
    SecuritySystemConfig.setOrRemove(SecuritySystemConfig.getPrefix(name) + key, value);
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
}
