package ch.ivyteam.enginecockpit;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.EmailSettings;
import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.IUserEMailNotificationSettings;
import ch.ivyteam.util.date.Weekday;

@ManagedBean
@ViewScoped
public class UserDetailBean
{
  private String userName;
  private User user;
  private EmailSettings emailSettings;

  private List<Role> filteredRoles;

  private ApplicationBean applicationBean;

  public UserDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}",
            ApplicationBean.class);
    user = new User();
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
    IUser iUser = getSecurityContext().findUser(userName);
    this.user = new User(iUser);
    this.emailSettings = new EmailSettings(iUser, applicationBean.getDefaultEmailLanguageForSelectedApp());
  }

  public User getUser()
  {
    return user;
  }

  public EmailSettings getEmailSettings()
  {
    return emailSettings;
  }

  public void creatNewUser()
  {
    getSecurityContext().createUser(user.getName(), user.getFullName(), user.getPassword(), null,
            user.getEmail(), null);
  }

  public void saveUserInfos()
  {
    Ivy.log().info("save user");
    IUser iUser = getIUser();
    iUser.setEMailAddress(user.getEmail());
    iUser.setFullName(user.getFullName());
    if (user.getPassword() != "")
    {
      iUser.setPassword(user.getPassword());
    }
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess",
            new FacesMessage("User information changes saved"));
  }

  public String deleteSelectedUser()
  {
    getSecurityContext().deleteUser(userName);
    return "users.xhtml";
  }

  public String getSelectedSettings()
  {
    if (emailSettings.isUseApplicationDefault())
      return "Application";
    else
      return "Specific";
  }

  public void setSelectedSettings(String selectedSettings)
  {
    this.emailSettings.setUseApplicationDefault(selectedSettings.equals("Application"));
  }

  public String[] getSelectedNotifications()
  {
    String[] selectedNotifications = new String[2];
    if (emailSettings.isNotificationDisabled())
      selectedNotifications[0] = "Never";
    if (emailSettings.isSendOnNewWorkTasks())
      selectedNotifications[1] = "Task";
    return selectedNotifications;
  }

  public boolean isNotificationCheckboxDisabled()
  {
    return emailSettings.isUseApplicationDefault();
  }

  public boolean isTaskCheckboxDisabled()
  {
    return emailSettings.isUseApplicationDefault() || emailSettings.isNotificationDisabled();
  }

  public boolean isDailyCheckboxGroupDisabled()
  {
    return emailSettings.isUseApplicationDefault() || emailSettings.isNotificationDisabled();
  }

  public void saveUserEmail()
  {
    IUser iUser = getIUser();
    iUser.setEMailLanguage(emailSettings.getLanguageLocale());
    IUserEMailNotificationSettings eMailNotificationSettings = iUser.getEMailNotificationSettings();
    eMailNotificationSettings.setUseApplicationDefault(emailSettings.isUseApplicationDefault());
    eMailNotificationSettings.setNotificationDisabled(emailSettings.isNotificationDisabled());
    eMailNotificationSettings.setSendOnNewWorkTasks(emailSettings.isSendOnNewWorkTasks());
    String[] sendDailyTasks = emailSettings.getSendDailyTasks();
    if (sendDailyTasks != null && sendDailyTasks.length > 0)
    {
      eMailNotificationSettings.setSendDailyTaskSummary(EnumSet.copyOf(
              Arrays.stream(sendDailyTasks).map(day -> Weekday.valueOf(day)).collect(Collectors.toList())));
    }
    else
    {
      eMailNotificationSettings.setSendDailyTaskSummary(EnumSet.noneOf(Weekday.class));
    }
    iUser.setEMailNotificationSettings(eMailNotificationSettings);
    FacesContext.getCurrentInstance().addMessage("emailSaveSuccess",
            new FacesMessage("User email changes saved"));
  }

  public boolean isUserMemberOfAllRole(String roleName)
  {
    return getIUser().getAllRoles().stream().filter(r -> r.getName().equals(roleName)).findAny().isPresent();
  }

  public boolean isUserMemberOfRole(String roleName)
  {
    return getIUser().getRoles().stream().filter(r -> r.getName().equals(roleName)).findAny().isPresent();
  }

  public void removeRole(String roleName)
  {
    getIUser().removeRole(getSecurityContext().findRole(roleName));
  }

  public void addRole(String roleName)
  {
    try
    {
      getIUser().addRole(getSecurityContext().findRole(roleName));
    }
    catch (Exception e)
    {
      FacesContext.getCurrentInstance().addMessage("roleMessage",
              new FacesMessage("User already member of this role"));
    }
  }

  public List<Role> getFilteredRoles()
  {
    return filteredRoles;
  }

  public void setFilteredRoles(List<Role> filteredRoles)
  {
    this.filteredRoles = filteredRoles;
  }

  private IUser getIUser()
  {
    return getSecurityContext().findUser(userName);
  }

  private ISecurityContext getSecurityContext()
  {
    return applicationBean.getSelectedIApplication().getSecurityContext();
  }
}
