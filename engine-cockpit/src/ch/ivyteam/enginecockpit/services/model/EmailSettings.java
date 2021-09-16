package ch.ivyteam.enginecockpit.services.model;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.stream.Collectors;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.security.IEMailNotificationSettings;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.IUserEMailNotificationSettings;
import ch.ivyteam.util.date.Weekday;

public class EmailSettings {
  private Locale language;
  private boolean notificationDisabled;
  private boolean notificationCheckboxRender = true;
  private boolean sendOnNewWorkTasks;
  private boolean useApplicationDefault;
  private String[] sendDailyTasks;

  public static final Locale ENGLISH = Locale.ENGLISH;

  public EmailSettings(IApplication app) {
    this.language = app.getDefaultEMailLanguage();
    useApplicationDefault = false;
    initEmailSettings(app.getDefaultEMailNotifcationSettings());
  }

  public EmailSettings(IUser user, IEMailNotificationSettings defaultAppSettings) {
    this.language = user.getLanguage() != null ? user.getLanguage() : new Locale("app");
    useApplicationDefault = user.getEMailNotificationSettings().isUseApplicationDefault();
    if (useApplicationDefault) {
      initEmailSettings(defaultAppSettings);
    } else {
      initEmailSettings(user.getEMailNotificationSettings());
    }
  }

  private void initEmailSettings(IEMailNotificationSettings emailSettings) {
    notificationDisabled = emailSettings.isNotificationDisabled();
    sendOnNewWorkTasks = emailSettings.isSendOnNewWorkTasks();
    sendDailyTasks = emailSettings.getSendDailyTaskSummary().stream()
            .map(w -> w.toString()).toArray(String[]::new);
  }

  public String getLanguage() {
    return this.language.getLanguage();
  }

  public void setLanguage(String language) {
    this.language = new Locale(language);
  }

  public Locale getLanguageLocale() {
    return this.language;
  }

  public boolean isNotificationCheckboxRender() {
    return notificationCheckboxRender;
  }

  public void setNotificationCheckboxRender(boolean render) {
    this.notificationCheckboxRender = render;
  }

  public boolean isNotificationDisabled() {
    return notificationDisabled;
  }

  public void setNotificationDisabled(boolean notificationDisabled) {
    this.notificationDisabled = notificationDisabled;
  }

  public boolean isSendOnNewWorkTasks() {
    return sendOnNewWorkTasks;
  }

  public void setSendOnNewWorkTasks(boolean sendOnNewWorkTasks) {
    this.sendOnNewWorkTasks = sendOnNewWorkTasks;
  }

  public boolean isUseApplicationDefault() {
    return useApplicationDefault;
  }

  public void setUseApplicationDefault(boolean useApplicationDefault) {
    this.useApplicationDefault = useApplicationDefault;
  }

  public String[] getSendDailyTasks() {
    return sendDailyTasks;
  }

  public void setSendDailyTasks(String[] sendDailyTasks) {
    this.sendDailyTasks = sendDailyTasks;
  }

  public boolean isNotificationCheckboxDisabled() {
    return isUseApplicationDefault();
  }

  public boolean isTaskCheckboxDisabled() {
    return isUseApplicationDefault() || isNotificationDisabled();
  }

  public boolean isDailyCheckboxGroupDisabled() {
    return isUseApplicationDefault() || isNotificationDisabled();
  }

  public String getSelectedSettings() {
    if (isUseApplicationDefault())
      return "Application";
    else
      return "Specific";
  }

  public void setSelectedSettings(String selectedSettings) {
    setUseApplicationDefault(selectedSettings.equals("Application"));
  }

  public IEMailNotificationSettings saveEmailSettings(IEMailNotificationSettings settings) {
    settings.setNotificationDisabled(isNotificationDisabled());
    settings.setSendOnNewWorkTasks(isSendOnNewWorkTasks());
    if (sendDailyTasks != null && sendDailyTasks.length > 0) {
      settings.setSendDailyTaskSummary(EnumSet.copyOf(
              Arrays.stream(sendDailyTasks).map(day -> Weekday.valueOf(day)).collect(Collectors.toList())));
    } else {
      settings.setSendDailyTaskSummary(EnumSet.noneOf(Weekday.class));
    }
    return settings;
  }

  public IUserEMailNotificationSettings saveUserEmailSettings(IUserEMailNotificationSettings settings) {
    settings.setUseApplicationDefault(isUseApplicationDefault());
    saveEmailSettings(settings);
    return settings;
  }

}
