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

  private String updateTime;
  private boolean updateEnabled;

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
    updateEnabled = getInitBooleanValue(ConfigKey.UPDATE_ENABLED,
            securityConfiguration.getDefaultBooleanValue(ConfigKey.UPDATE_ENABLED));
    updateTime = getConfiguration(ConfigKey.UPDATE_TIME);
  }

  public boolean isJndiSecuritySystem() {
    return !securitySystem.isIvySecuritySystem();
  }

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(String updateTime) {
    this.updateTime = updateTime;
  }

  public boolean isUpdateEnabled() {
    return updateEnabled;
  }

  public void setUpdateEnabled(boolean updateEnabled) {
    this.updateEnabled = updateEnabled;
  }

  public void saveProvider() {
    if (!validateUpdateTime()) {
      return;
    }

    if (! StringUtils.isEmpty(provider) && (! StringUtils.equals(provider, securitySystem.getSecuritySystemProvider()))) {
      var context = (SecurityContext) securitySystem.getSecurityContext();
      var key = ch.ivyteam.ivy.configuration.restricted.ConfigKey.create("SecuritySystems").append(securitySystem.getSecuritySystemName());
      IConfiguration.instance().remove(key);
      context.config().setProperty(ISecurityConstants.PROVIDER_CONFIG_KEY, provider);
    }
    setConfiguration(ConfigKey.UPDATE_ENABLED,
            getSaveBooleanValue(this.updateEnabled,
                    securityConfiguration.getDefaultBooleanValue(ConfigKey.UPDATE_ENABLED)));
    setConfiguration(ConfigKey.UPDATE_TIME, this.updateTime);

    FacesContext.getCurrentInstance().addMessage("securityProviderSaveSuccess",
            new FacesMessage("Security System Provider saved"));
  }

  private boolean validateUpdateTime() {
    if (StringUtils.isEmpty(updateTime)) {
      return true;
    }
    final Pattern pattern = Pattern.compile("^[0-2][0-9]:[0-5][0-9]$");
    if (!pattern.matcher(this.updateTime).matches()) {
      FacesContext.getCurrentInstance().addMessage("syncTime",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error",
                      "Please check that synchronization Time is max '23:59'"));
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
