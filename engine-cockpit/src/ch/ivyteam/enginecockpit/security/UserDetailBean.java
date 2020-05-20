package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.EmailSettings;
import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.synch.UserSynchResult;
import ch.ivyteam.ivy.security.synch.UserSynchResult.SynchStatus;
import ch.ivyteam.ivy.security.user.NewUser;
import ch.ivyteam.ivy.workflow.TaskState;
import ch.ivyteam.ivy.workflow.query.CaseQuery;
import ch.ivyteam.ivy.workflow.query.TaskQuery;

@ManagedBean
@ViewScoped
public class UserDetailBean
{
  private String userName;
  private User user;
  private EmailSettings emailSettings;
  private MemberProperty userProperties;
  private String userSynchName;
  private String synchLog;

  private List<Role> filteredRoles;

  private ManagerBean managerBean;
  private String securitySystemName;
  private long canWorkOn;
  private long personalTasks;
  private long startedCases;
  private long workingOn;

  public UserDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);
    user = new User();
    userProperties = new MemberProperty().new UserProperty();
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
    this.userSynchName = userName;
    IUser iUser = getSecurityContext().users().find(userName);
    this.user = new User(iUser);
    this.emailSettings = new EmailSettings(iUser, managerBean.getSelectedIApplication().getDefaultEMailNotifcationSettings());
    this.securitySystemName = managerBean.getSelectedApplication().getSecuritySystemName();
    startedCases = CaseQuery.create().where().isBusinessCase().and().creatorUserId().isEqual(iUser.getId()).executor().count();
    workingOn = TaskQuery.create().where().state().isEqual(TaskState.CREATED)
            .or().state().isEqual(TaskState.RESUMED)
            .or().state().isEqual(TaskState.PARKED)
            .andOverall().workerUserId().isEqual(iUser.getId()).executor().count();
    personalTasks = TaskQuery.create().where().state().isEqual(TaskState.CREATED)
            .or().state().isEqual(TaskState.SUSPENDED)
            .or().state().isEqual(TaskState.RESUMED)
            .or().state().isEqual(TaskState.PARKED)
            .andOverall().activatorUserId().isEqual(iUser.getId()).executor().count();
    canWorkOn = TaskQuery.create().where().canWorkOn(iUser).executor().count();
  }

  public String getUserSynchName()
  {
    return userSynchName;
  }

  public void setUserSynchName(String userName)
  {
    this.userSynchName = userName;
  }
  
  public void resetSynchInfo()
  {
    if (StringUtils.isEmpty(userName))
    {
      this.userSynchName = null;
    }
    this.synchLog = null;
  }

  public User getUser()
  {
    return user;
  }

  public EmailSettings getEmailSettings()
  {
    return emailSettings;
  }

  public String creatNewUser()
  {
    NewUser newUser = NewUser
        .create(user.getName())
        .fullName(user.getFullName())
        .password(user.getPassword())
        .mailAddress(user.getEmail())
        .toNewUser();
    getSecurityContext().users().create(newUser);
    return "users.xhtml";
  }

  public void saveUserInfos()
  {
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

  public void synchUser()
  {
    UserSynchResult synchResult = getSecurityContext().synchronizeUser(userSynchName);
    if (synchResult.getStatus() == SynchStatus.SUCCESS)
    {
      user = new User(synchResult.getUser());      
    }
    synchLog = synchResult.getSynchLog();
  }

  public String getSynchLog()
  {
    return synchLog;
  }

  public String deleteSelectedUser()
  {
    getSecurityContext().users().delete(userName);
    return "users.xhtml?faces-redirect=true";
  }

  public void disableUser()
  {
    getIUser().disable();
  }

  public boolean isUserDisabled()
  {
    return !isUserEnabled();
  }

  public void enableUser()
  {
    getIUser().enable();
  }

  public boolean isUserEnabled()
  {
    return getIUser().isEnabled();
  }

  public void saveUserEmail()
  {
    IUser iUser = getIUser();
    Locale language = emailSettings.getLanguageLocale();
    if (language.getLanguage().equals("app"))
    {
      language = null;
    }
    iUser.setEMailLanguage(language);
    iUser.setEMailNotificationSettings(
            emailSettings.saveUserEmailSettings(iUser.getEMailNotificationSettings()));
    FacesContext.getCurrentInstance().addMessage("emailSaveSuccess",
            new FacesMessage("User email changes saved"));
  }

  public boolean isUserMemberOfAllRole(String roleName)
  {
    return getIUser().getAllRoles().stream().anyMatch(r -> r.getName().equals(roleName));
  }

  public boolean isUserMemberOfRole(String roleName)
  {
    return getIUser().getRoles().stream().anyMatch(r -> r.getName().equals(roleName));
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
    return getSecurityContext().users().find(userName);
  }

  private ISecurityContext getSecurityContext()
  {
    return managerBean.getSelectedIApplication().getSecurityContext();
  }
  
  public MemberProperty getMemberProperty()
  {
    return userProperties;
  }
  
  public String userDeleteHint()
  {
    String message = "";
    if (getPersonalTasks() != 0)
    {
      message += "The user '" + getUserName() + "' has " + getPersonalTasks() + " personal tasks. "
              + "If you delete this user, no other user can work on these tasks. ";
    }
    return message + "You may want to disable this user instead of deleting it, you could lose part of the workflow history.";
  }
  
  public String getSecuritySystemName()
  {
    return securitySystemName;
  }
  
  public long getCanWorkOn()
  {
    return canWorkOn;
  }
  
  public long getPersonalTasks()
  {
    return personalTasks;
  }
  
  public long getStartedCases()
  {
    return startedCases;
  }
  
  public long getWorkingOn()
  {
    return workingOn;
  }
}
