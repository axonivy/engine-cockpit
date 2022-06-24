package ch.ivyteam.enginecockpit.services.model;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ivyteam.ivy.language.LanguageConfigurator;
import ch.ivyteam.ivy.security.IEMailNotificationSettings;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.IUserEMailNotificationSettings;
import ch.ivyteam.ivy.security.email.EmailNotificationConfigurator;
import ch.ivyteam.ivy.workflow.StandardProcessType;
import ch.ivyteam.ivy.workflow.standard.MailProcessConfigurator;
import ch.ivyteam.ivy.workflow.standard.StandardProcessStartFinder;
import ch.ivyteam.util.date.Weekday;

public class EmailSettings {

  private Locale language;
  private boolean notificationDisabled;
  private boolean notificationCheckboxRender = true;
  private boolean sendOnNewWorkTasks;
  private boolean useApplicationDefault;
  private String[] sendDailyTasks;
  private ISecurityContext securityContext;
  private String mailProcessLib;

  public static final Locale ENGLISH = Locale.ENGLISH;

  public EmailSettings(ISecurityContext securityContext) {
    this.securityContext = securityContext;
    var configurator = new EmailNotificationConfigurator(securityContext);
    this.language = new LanguageConfigurator(securityContext).content();
    useApplicationDefault = false;
    initEmailSettings(configurator.settings());
  }

  public EmailSettings(IUser user, IEMailNotificationSettings defaultAppSettings) {
    this.securityContext = user.getSecurityContext();
    this.language = user.getLanguage() != null ? user.getLanguage() : new Locale("app");
    useApplicationDefault = user.getEMailNotificationSettings().isUseApplicationDefault();
    if (useApplicationDefault) {
      initEmailSettings(defaultAppSettings);
    } else {
      initEmailSettings(user.getEMailNotificationSettings());
    }
  }

  private void initEmailSettings(IEMailNotificationSettings settings) {
    notificationDisabled = settings.isNotificationDisabled();
    sendOnNewWorkTasks = settings.isSendOnNewWorkTasks();
    sendDailyTasks = settings.getSendDailyTaskSummary().stream()
            .map(w -> w.toString())
            .toArray(String[]::new);
    mailProcessLib = mailProcessConfig().getLibrary();
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
    if (isUseApplicationDefault()) {
      return "Application";
    }
    return "Specific";
  }

  public void setSelectedSettings(String selectedSettings) {
    setUseApplicationDefault(selectedSettings.equals("Application"));
  }

  public IEMailNotificationSettings saveEmailSettings(IEMailNotificationSettings settings) {
    settings.setNotificationDisabled(isNotificationDisabled());
    settings.setSendOnNewWorkTasks(isSendOnNewWorkTasks());
    if (sendDailyTasks != null && sendDailyTasks.length > 0) {
      var days = Arrays.stream(sendDailyTasks)
                .map(day -> Weekday.valueOf(day))
              .collect(Collectors.toList());
      settings.setSendDailyTaskSummary(EnumSet.copyOf(days));
    } else {
      settings.setSendDailyTaskSummary(EnumSet.noneOf(Weekday.class));
    }
    mailProcessConfig().setLibrary(mailProcessLib);
    return settings;
  }

  public IUserEMailNotificationSettings saveUserEmailSettings(IUserEMailNotificationSettings settings) {
    settings.setUseApplicationDefault(isUseApplicationDefault());
    saveEmailSettings(settings);
    return settings;
  }
  
  public String getMailProcess() {
    return mailProcessLib;
  }

  public void setMailProcess(String lib) {
    this.mailProcessLib = lib;
  }

  public Set<String> getMailProcesses() {
    var configurator = mailProcessConfig();
    var libs = new LinkedHashSet<String>();
    libs.add("");
    libs.add(StandardProcessStartFinder.AUTO);
    for (var type : StandardProcessType.values()) {
      if (type.kind() == StandardProcessType.Kind.MAIL) {
        var lib = configurator.findLibraries(type);
        libs.addAll(lib);
      }
    }
    return libs;
  }

  private MailProcessConfigurator mailProcessConfig() {
    return MailProcessConfigurator.of(securityContext);
  }
}
