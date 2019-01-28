package ch.ivyteam.enginecockpit.model;

import java.util.Locale;

import ch.ivyteam.ivy.security.IUser;

public class EmailSettings
{
  private Locale language;
  private boolean notificationDisabled;
  private boolean sendOnNewWorkTasks;
  private boolean useApplicationDefault;
  private String[] sendDailyTasks;

  public static final Locale ENGLISH = Locale.ENGLISH;

  public EmailSettings(IUser user)
  {
    this.language = user.getEMailLanguage() != null ? user.getEMailLanguage() : new Locale("app");
    notificationDisabled = user.getEMailNotificationSettings().isNotificationDisabled();
    sendOnNewWorkTasks = user.getEMailNotificationSettings().isSendOnNewWorkTasks();
    useApplicationDefault = user.getEMailNotificationSettings().isUseApplicationDefault();
    sendDailyTasks = user.getEMailNotificationSettings().getSendDailyTaskSummary().stream()
            .map(w -> w.toString()).toArray(String[]::new);
  }

  public String getLanguage()
  {
    return this.language.getLanguage();
  }

  public void setLanguage(String language)
  {
    this.language = new Locale(language);
  }

  public Locale getLanguageLocale()
  {
    return this.language;
  }

  public boolean isNotificationDisabled()
  {
    return notificationDisabled;
  }

  public void setNotificationDisabled(boolean notificationDisabled)
  {
    this.notificationDisabled = notificationDisabled;
  }

  public boolean isSendOnNewWorkTasks()
  {
    return sendOnNewWorkTasks;
  }

  public void setSendOnNewWorkTasks(boolean sendOnNewWorkTasks)
  {
    this.sendOnNewWorkTasks = sendOnNewWorkTasks;
  }

  public boolean isUseApplicationDefault()
  {
    return useApplicationDefault;
  }

  public void setUseApplicationDefault(boolean useApplicationDefault)
  {
    this.useApplicationDefault = useApplicationDefault;
  }

  public String[] getSendDailyTasks()
  {
    return sendDailyTasks;
  }

  public void setSendDailyTasks(String[] sendDailyTasks)
  {
    this.sendDailyTasks = sendDailyTasks;
  }

}
