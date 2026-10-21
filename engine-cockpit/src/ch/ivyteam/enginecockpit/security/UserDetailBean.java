package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.function.LongSupplier;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.commons.LazyCount;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.security.model.MemberProperty;
import ch.ivyteam.enginecockpit.security.model.NotificationChannelDataModel;
import ch.ivyteam.enginecockpit.security.model.Role;
import ch.ivyteam.enginecockpit.security.model.RoleDataModel;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.security.model.User;
import ch.ivyteam.enginecockpit.system.ManagerBean;
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
  private final LazyCount canWorkOn = new LazyCount();
  private final LazyCount personalTasks = new LazyCount();
  private final LazyCount startedCases = new LazyCount();
  private final LazyCount workingOn = new LazyCount();
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
      ResponseHelper.notFound("Security System '" + securitySystemName + "' not found");
      return;
    }

    var iUser = securityContext.users().find(userName);
    if (iUser == null) {
      ResponseHelper.notFound("User '" + userName + "' not found");
      return;
    }

    userSynch = new UserSynch(securityContext, userName);
    this.user = new User(iUser);
    roleDataModel = new RoleDataModel(new SecuritySystem(securityContext), false, 20, iUser);
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
    var msg = new FacesMessage("User information changes saved");
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
          new FacesMessage("User already member of this role"));
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
    var message = "";
    var taskCount = getPersonalTasks();
    if ("-".equals(taskCount)) {
      message += "Could not load the user's personal task count. Deleting this user may affect personal tasks. ";
    } else if (!ManagerBean.instance().formatNumber(0).equals(taskCount)) {
      message += "The user '" + userName + "' has " + taskCount + " personal tasks. "
          + "If you delete this user, no other user can work on these tasks. ";
    }
    return message
        + "You may want to disable this user instead of deleting it, you could lose part of the workflow history.";
  }

  public String getSecuritySystemName() {
    return securitySystemName;
  }

  public String getCanWorkOn() {
    return formatCount(canWorkOn, "available task count", () -> {
      var taskQueryExecutor = IWorkflowContext.of(securityContext).getTaskQueryExecutor();
      return TaskQuery.create(taskQueryExecutor).where().canWorkOn(getIUser()).executor().count();
    });
  }

  public String getPersonalTasks() {
    return formatCount(personalTasks, "personal task count", () -> {
      var taskQueryExecutor = IWorkflowContext.of(securityContext).getTaskQueryExecutor();
      return TaskQuery.create(taskQueryExecutor).where().state().isEqual(TaskState.CREATED)
          .or().state().isEqual(TaskState.SUSPENDED)
          .or().state().isEqual(TaskState.RESUMED)
          .or().state().isEqual(TaskState.PARKED)
          .andOverall().activatorId().isEqual(getIUser().getSecurityMemberId()).executor().count();
    });
  }

  public String getStartedCases() {
    return formatCount(startedCases, "started case count", () -> {
      var caseQueryExecutor = IWorkflowContext.of(securityContext).getCaseQueryExecutor();
      return CaseQuery.create(caseQueryExecutor).where().isBusinessCase().and().creatorId()
          .isEqual(getIUser().getSecurityMemberId()).executor().count();
    });
  }

  public String getWorkingOn() {
    return formatCount(workingOn, "working task count", () -> {
      var taskQueryExecutor = IWorkflowContext.of(securityContext).getTaskQueryExecutor();
      return TaskQuery.create(taskQueryExecutor).where().state().isEqual(TaskState.CREATED)
          .or().state().isEqual(TaskState.RESUMED)
          .or().state().isEqual(TaskState.PARKED)
          .andOverall().workerId().isEqual(getIUser().getSecurityMemberId()).executor().count();
    });
  }

  private String formatCount(LazyCount count, String description, LongSupplier query) {
    var value = count.getCount(description + " for user '" + userName + "'", query);
    return value == null ? "-" : ManagerBean.instance().formatNumber(value);
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
}
