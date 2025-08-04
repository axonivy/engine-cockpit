package ch.ivyteam.enginecockpit.security;

import java.util.Arrays;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.security.model.MemberProperty;
import ch.ivyteam.enginecockpit.security.model.NotificationChannelDataModel;
import ch.ivyteam.enginecockpit.security.model.Role;
import ch.ivyteam.enginecockpit.security.model.RoleDataModel;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.security.model.User;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.workflow.IWorkflowContext;
import ch.ivyteam.ivy.workflow.TaskState;
import ch.ivyteam.ivy.workflow.query.CaseQuery;
import ch.ivyteam.ivy.workflow.query.TaskQuery;

@ManagedBean
@ViewScoped
public class UserDetailBean {

  private String userName;
  private User user;
  private MemberProperty userProperties;

  private List<Role> filteredRoles;

  private String securitySystemName;
  private ISecurityContext securityContext;
  private long canWorkOn;
  private long personalTasks;
  private long startedCases;
  private long workingOn;
  private RoleDataModel roleDataModel;
  private NotificationChannelDataModel notificationChannelDataModel;

  private UserSynch userSynch;

  public UserSynch getUserSynch() {
    return userSynch;
  }

  public String getSecuritySystem() {
    return securitySystemName;
  }

  public void setSecuritySystem(String securitySystem) {
    this.securitySystemName = securitySystem;
  }

  public String getName() {
    return userName;
  }

  public void setName(String userName) {
    this.userName = userName;
  }

  public void onload() {
    securityContext = ISecurityContextRepository.instance().get(securitySystemName);
    if (securityContext == null) {
      ResponseHelper.notFound(
          Ivy.cms().co("/userDetailInformation/NotFoundSecuritySystemMessage", Arrays.asList(securitySystemName)));
      return;
    }

    var iUser = securityContext.users().find(userName);
    if (iUser == null) {
      ResponseHelper
          .notFound(Ivy.cms().co("/userDetailInformation/NotFoundUserMessage", Arrays.asList(securitySystemName)));
      return;
    }

    userSynch = new UserSynch(securityContext, userName);
    this.user = new User(iUser);
    roleDataModel = new RoleDataModel(new SecuritySystem(securityContext), false, 20);
    var caseQueryExecutor = IWorkflowContext.of(securityContext).getCaseQueryExecutor();
    startedCases = CaseQuery.create(caseQueryExecutor).where().isBusinessCase().and().creatorId()
        .isEqual(iUser.getSecurityMemberId()).executor().count();
    var taskQueryExecutor = IWorkflowContext.of(securityContext).getTaskQueryExecutor();
    workingOn = TaskQuery.create(taskQueryExecutor).where().state().isEqual(TaskState.CREATED)
        .or().state().isEqual(TaskState.RESUMED)
        .or().state().isEqual(TaskState.PARKED)
        .andOverall().workerId().isEqual(iUser.getSecurityMemberId()).executor().count();
    personalTasks = TaskQuery.create(taskQueryExecutor).where().state().isEqual(TaskState.CREATED)
        .or().state().isEqual(TaskState.SUSPENDED)
        .or().state().isEqual(TaskState.RESUMED)
        .or().state().isEqual(TaskState.PARKED)
        .andOverall().activatorId().isEqual(iUser.getSecurityMemberId()).executor().count();
    canWorkOn = TaskQuery.create(taskQueryExecutor).where().canWorkOn(iUser).executor().count();

    notificationChannelDataModel = NotificationChannelDataModel.instance(iUser, securityContext);
    userProperties = new MemberProperty().new UserProperty();
    userProperties.setMemberName(userName);
  }

  public User getUser() {
    return user;
  }

  public void saveUserInfos() {
    var iUser = getIUser();
    if (!user.isExternal()) {
      iUser.setEMailAddress(user.getEmail());
      iUser.setFullName(user.getFullName());
      if (user.getPassword() != "") {
        iUser.setPassword(user.getPassword());
      }
    }
    iUser.setLanguage(user.getLanguage());
    iUser.setFormattingLanguage(user.getFormattingLanguage());
    var msg = new FacesMessage(Ivy.cm().co("/userDetailInformation/UserInformationChangesSavedMessage"));
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess", msg);
  }

  public String deleteSelectedUser() {
    securityContext.users().delete(userName);
    return "users.xhtml?faces-redirect=true";
  }

  public void increaseShowChildLimitAndReloadTree(int increaseLimitBy) {
    roleDataModel.increaseShowChildLimitAndReloadTree(increaseLimitBy);
  }

  public void disableUser() {
    getIUser().disable();
  }

  public boolean isUserDisabled() {
    return !isUserEnabled();
  }

  public void enableUser() {
    getIUser().enable();
  }

  public boolean isUserEnabled() {
    return getIUser().isEnabled();
  }

  public RoleDataModel getRoles() {
    return roleDataModel;
  }

  public NotificationChannelDataModel getNotificationChannels() {
    return notificationChannelDataModel;
  }

  public void removeRole(String roleName) {
    getIUser().removeRole(securityContext.roles().find(roleName));
  }

  public void addRole(String roleName) {
    try {
      getIUser().addRole(securityContext.roles().find(roleName));
    } catch (Exception e) {
      FacesContext.getCurrentInstance().addMessage("roleMessage",
          new FacesMessage(Ivy.cm().co("/userDetailInformation/AddRoleFailedMessage")));
    }
  }

  public List<Role> getFilteredRoles() {
    return filteredRoles;
  }

  public void setFilteredRoles(List<Role> filteredRoles) {
    this.filteredRoles = filteredRoles;
  }

  private IUser getIUser() {
    return securityContext.users().find(userName);
  }

  public MemberProperty getMemberProperty() {
    return userProperties;
  }

  public String userDeleteHint() {
    if (personalTasks != 0) {
      return Ivy.cms().co("/userDetailInformation/UserDeleteHintMessageWithPersonalTasks",
          Arrays.asList(userName, getPersonalTasks()));
    }
    return Ivy.cm().co("/userDetailInformation/UserDeleteHintMessage");
  }

  public String getSecuritySystemName() {
    return securitySystemName;
  }

  public String getCanWorkOn() {
    return ManagerBean.instance().formatNumber(canWorkOn);
  }

  public String getPersonalTasks() {
    return ManagerBean.instance().formatNumber(personalTasks);
  }

  public String getStartedCases() {
    return ManagerBean.instance().formatNumber(startedCases);
  }

  public String getWorkingOn() {
    return ManagerBean.instance().formatNumber(workingOn);
  }

  public ISecurityContext getSecurityContext() {
    return securityContext;
  }

  public boolean isIvySecuritySystem() {
    return SecuritySystem.isIvySecuritySystem(securityContext);
  }

  public boolean isManaged() {
    return getIUser().isExternal();
  }

  public boolean isUserMemberOfButNotDirect(Role role) {
    var hasRole = getIUser().getAllRoles().stream().anyMatch(r -> r.getName().equals(role.getName()));
    return hasRole && !hasUserRoleDirect(role);
  }

  public boolean isUserDirectMemberOf(Role role) {
    return hasUserRoleDirect(role);
  }

  public boolean isAddRoleButtonDisabled(Role role) {
    if (role.isTopLevel()) {
      return true;
    }
    return hasUserRoleDirect(role);
  }

  public boolean isRemoveRoleButtonDisabled(Role role) {
    if (role.isTopLevel()) {
      return true;
    }
    return !hasUserRoleDirect(role);
  }

  private boolean hasUserRoleDirect(Role role) {
    return getIUser().getRoles().stream().anyMatch(r -> r.getName().equals(role.getName()));
  }

  public boolean isRoleManaged(Role role) {
    return role.isManaged() && isIvySecuritySystem();
  }

  public String getShowMoreRoleLeftMessage(String roleName) {
    return Ivy.cms().co("/usersDetailRoles/ShowMoreRoleLeftMessage", Arrays.asList(roleName));
  }
}
