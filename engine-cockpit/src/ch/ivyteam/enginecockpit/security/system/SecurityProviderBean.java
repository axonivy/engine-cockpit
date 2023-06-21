package ch.ivyteam.enginecockpit.security.system;


import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.job.cron.CronExpression;
import ch.ivyteam.ivy.security.ISecurityManager;
import ch.ivyteam.ivy.security.external.SecuritySystemConfig;
import ch.ivyteam.ivy.security.identity.core.config.IdpKey;
import ch.ivyteam.ivy.security.internal.context.SecurityContext;
import ch.ivyteam.ivy.security.restricted.ISecurityContextInternal;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class SecurityProviderBean {

  private String name;
  private SecuritySystem securitySystem;
  private SecuritySystemConfig systemConfig;

  private String provider;

  private String onScheduleTime;
  private CronExpression onScheduleCron;
  private boolean useCron;
  private boolean onScheduleEnabled;
  private boolean synchOnLogin;
  private boolean onScheduleImportUsers;
  private boolean showWarningMessage;

  public String getSecuritySystemName() {
    return name;
  }

  public void setSecuritySystemName(String secSystemName) {
    if (StringUtils.isBlank(name)) {
      name = secSystemName;
      var securityContext = (ISecurityContextInternal) ISecurityManager.instance().securityContexts().get(secSystemName);
      if (securityContext != null) {
        systemConfig = securityContext.config();
      }
      loadConfiguration();
    }
  }

  public void loadConfiguration() {
    securitySystem = ManagerBean.instance().getSecuritySystems().stream()
            .filter(system -> StringUtils.equals(system.getSecuritySystemName(), name))
            .findAny()
            .orElseThrow();

    provider = systemConfig.identity().getName();
    var synch = systemConfig.userSynch();
    onScheduleEnabled = synch.onSchedule().enabled();
    onScheduleCron = synch.onSchedule().executeAt();
    useCron = !onScheduleCron.isDaily();
    synchOnLogin = synch.onLogin();
    onScheduleImportUsers = synch.onSchedule().importUsers();
  }

  public String getCronHelp() {
    return """
           Legend<br/>
           1st - second (optional)(0 - 59)<br/>
           2nd - minute           (0 - 59)<br/>
           3rd - hour             (0 - 23)<br/>
           4th - day of the month (1 - 31)<br/>
           5th - month            (1 - 12)<br/>
           6th - day of the week  (1 - 7)""";
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
    return onScheduleCron.toTimeOnlyString();
  }

  public void setOnScheduleTime(String onScheduleTime) {
    this.onScheduleTime = onScheduleTime;
  }

  public String getCronReadable() {
    return onScheduleCron.toHumanReadableString();
  }

  public String getOnScheduleCron() {
    return onScheduleCron.toString();
  }

  public void setOnScheduleCron(String cronString) {
    this.onScheduleTime = cronString;
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

  public void setOnScheduleImportUsers(boolean onScheduleImportUsers) {
    this.onScheduleImportUsers = onScheduleImportUsers;
  }

  public void saveProvider() {
    if (!validateUpdateTime()) {
      return;
    }
    if (!StringUtils.isEmpty(provider) && !StringUtils.equals(provider, securitySystem.getSecuritySystemProviderId())) {
      var context = (SecurityContext) securitySystem.getSecurityContext();
      deleteProvider();
      context.config().identity().setName(provider);
    }
    var synch = systemConfig.userSynch();
    if (StringUtils.isBlank(onScheduleTime)) {
      this.onScheduleCron = synch.onSchedule().defaultExecuteAt();
    } else if (useCron) {
      this.onScheduleCron = CronExpression.parse(onScheduleTime);
    } else {
      this.onScheduleCron = CronExpression.dailyAt(LocalTime.parse(onScheduleTime));
    }
    synch.onLogin(synchOnLogin);
    synch.onSchedule().enabled(onScheduleEnabled);
    synch.onSchedule().executeAt(onScheduleCron);
    synch.onSchedule().importUsers(onScheduleImportUsers);

    setShowWarningMessage(false);
    FacesContext.getCurrentInstance().addMessage("securityProviderSaveSuccess",
            new FacesMessage("Security System Identity Provider saved"));
  }

  private boolean validateUpdateTime() {
    if (StringUtils.isEmpty(onScheduleTime)) {
      return true;
    }
    if (useCron) {
      try {
        CronExpression.parse(onScheduleTime);
      } catch (IllegalArgumentException ex) {
        var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtils.getRootCauseMessage(ex));
        FacesContext.getCurrentInstance().addMessage("onScheduleTime", msg);
        return false;
      }
    } else {
      try {
        LocalTime.parse(onScheduleTime);
      } catch (DateTimeParseException ex) {
        var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ExceptionUtils.getRootCauseMessage(ex));
        FacesContext.getCurrentInstance().addMessage("onScheduleTime", msg);
        return false;
      }
    }
    return true;
  }

  /**
   * Deleting all root keys of known identity providers.
   * Later we have these settings in an own sub-node and we only need to delete the sub-node.
   */
  private void deleteProvider() {
    var key = ch.ivyteam.ivy.configuration.restricted.ConfigKey.create("SecuritySystems").append(securitySystem.getSecuritySystemName());
    var cfg = IConfiguration.instance();
    cfg.remove(key.append(IdpKey.IDENTITY_PROVIDER));
  }

  public boolean getShowWarningMessage() {
    return showWarningMessage;
  }

  public void setShowWarningMessage(boolean showWarningMessage) {
    this.showWarningMessage = showWarningMessage;
  }

  public boolean isUseCron() {
    return useCron;
  }

  public void setUseCron(boolean onScheduleUseCron) {
    this.useCron = onScheduleUseCron;
  }

}
