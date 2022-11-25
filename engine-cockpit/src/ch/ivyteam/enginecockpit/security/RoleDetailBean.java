package ch.ivyteam.enginecockpit.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.security.ldapbrowser.LdapBrowser;
import ch.ivyteam.enginecockpit.security.model.MemberProperty;
import ch.ivyteam.enginecockpit.security.model.Role;
import ch.ivyteam.enginecockpit.security.model.RoleDataModel;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.security.model.User;
import ch.ivyteam.enginecockpit.security.model.UserDataModel;
import ch.ivyteam.enginecockpit.security.system.SecurityLdapBean;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityConstants;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.query.UserQuery;
import ch.ivyteam.ivy.security.role.NewRole;
import ch.ivyteam.ivy.workflow.TaskState;
import ch.ivyteam.ivy.workflow.query.TaskQuery;

@ManagedBean
@ViewScoped
public class RoleDetailBean {

  private String securitySystemName;
  private ISecurityContext securityContext;

  private String roleName;
  private String newChildRoleName;
  private User roleUser;
  private String roleMemberName;
  private Role role;

  private UserDataModel usersOfRole;
  private RoleDataModel roleDataModel;
  private List<Role> membersOfRole;
  private List<Role> filteredMembers;

  private MemberProperty roleProperties;

  private LdapBrowser ldapBrowser;
  private long userCount;
  private long runningTaskCount;
  private long directTaskCount;
  private long userInheritCont;

  public String getSecuritySystem() {
    return securitySystemName;
  }

  public void setSecuritySystem(String securitySystemName) {
    this.securitySystemName = securitySystemName;
  }

  public String getName() {
    return roleName;
  }

  public void setName(String roleName) {
    this.roleName = roleName;
  }

  public void onload() {
    securityContext = ISecurityContextRepository.instance().get(securitySystemName);
    if (securityContext == null) {
      ResponseHelper.notFound("Security System '" + securitySystemName + "' not found");
      return;
    }
    var securitySystem = new SecuritySystem(securityContext);
    roleProperties = new MemberProperty().new RoleProperty();
    usersOfRole = new UserDataModel(securitySystem);
    ldapBrowser = new LdapBrowser();

    var iRole = securityContext.roles().find(roleName);
    if (iRole == null) {
      ResponseHelper.notFound("Role '" + roleName + "' not found");
      return;
    }

    this.role = new Role(iRole);
    this.usersOfRole.setSecuritySystem(securitySystem);
    this.usersOfRole.setFilterRole(getIRole());
    this.usersOfRole.setFilter("");
    this.roleDataModel = new RoleDataModel(securitySystem, false);
    loadMembersOfRole();
    userCount = securityContext.users().query().where().hasRoleAssigned(iRole).executor().count();
    userInheritCont = securityContext.users().query().where().hasRole(iRole).executor().count();
    runningTaskCount = TaskQuery.create().where().state().isEqual(TaskState.CREATED)
            .or().state().isEqual(TaskState.RESUMED)
            .or().state().isEqual(TaskState.PARKED)
            .andOverall().activatorId().isEqual(iRole.getSecurityMemberId()).executor().count();
    directTaskCount = TaskQuery.create().where().state().isEqual(TaskState.CREATED)
            .or().state().isEqual(TaskState.SUSPENDED)
            .or().state().isEqual(TaskState.RESUMED)
            .or().state().isEqual(TaskState.PARKED)
            .andOverall().activatorId().isEqual(iRole.getSecurityMemberId()).executor().count();
    roleProperties.setMemberName(this.roleName);
  }

  public String getUsersOfRoleFilter() {
    return usersOfRole.getFilter();
  }

  public void setUsersOfRoleFilter(String filter) {
    this.usersOfRole.setFilter(filter);
  }

  public String getNewChildRoleName() {
    return newChildRoleName;
  }

  public void setNewChildRoleName(String name) {
    this.newChildRoleName = name;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public void createNewChildRole() throws IOException {
    var existingRole = getIRole().findChildRole(newChildRoleName);
    if (existingRole != null) {
      var u = new Role(newChildRoleName).getViewUrl(securitySystemName);
      FacesContext.getCurrentInstance().getExternalContext().redirect(u);
      return;
    }

    var faces = FacesContext.getCurrentInstance();
    faces.getExternalContext().getFlash().setKeepMessages(true);
    try {
      var newRole = NewRole.create(newChildRoleName)
              .parentRole(getIRole())
              .toNewRole();
      securityContext.roles().create(newRole);
      var msg = new FacesMessage("Role '" + newChildRoleName + "' created successfully", "");
      faces.addMessage("msgs", msg);
      var u = new Role(newChildRoleName).getViewUrl(securitySystemName);
      faces.getExternalContext().redirect(u);
    } catch (Exception ex) {
      var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Role '" + newChildRoleName + "' couldn't be created", ex.getMessage());
      faces.addMessage("msgs", msg);
      newChildRoleName = roleName;
    }
  }

  public void saveRoleInfos() {
    var iRole = getIRole();
    iRole.setDisplayDescriptionTemplate(role.getDescription());
    iRole.setDisplayNameTemplate(role.getDisplayName());
    iRole.setExternalName(role.getExternalName());
    var msg = new FacesMessage("Role information changes saved");
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess", msg);
  }

  public String deleteRole() {
    getIRole().delete();
    return "roles.xhtml?faces-redirect=true";
  }

  public UserDataModel getUsersOfRole() {
    return usersOfRole;
  }

  public void removeUser(String userName) {
    var user = securityContext.users().find(userName);
    user.removeRole(getIRole());
  }

  public void addUser() {
    if (roleUser == null) {
      return;
    }
    securityContext.users()
            .findById(roleUser.getSecurityMemberId())
            .addRole(getIRole());
    roleUser = null;
  }

  public User getRoleUser() {
    return roleUser;
  }

  public void setRoleUser(User roleUser) {
    this.roleUser = roleUser;
  }

  public boolean hasRoleAssigned(String userName) {
    var iRole = getIRole();
    if (iRole == null) {
      throw new IllegalStateException("IRole not found: " + roleName);
    }
    return iRole.users().assignedPaged().stream()
            .anyMatch(u -> u.getName().equals(userName));
  }

  public List<User> searchUser(String query) {
    var hasRole = UserQuery.create().where().hasRoleAssigned(getIRole());
    var dbQuery = "%" + query + "%";
    var searchFilter = UserQuery.create().where()
            .name().isLikeIgnoreCase(dbQuery)
            .or()
            .fullName().isLikeIgnoreCase(dbQuery)
            .or()
            .eMailAddress().isLikeIgnoreCase(dbQuery);

    return securityContext.users().query()
            .where()
            .not(hasRole)
            .and(searchFilter)
            .executor()
            .resultsPaged(10)
            .map(User::new)
            .page(1);
  }

  private IRole getIRole() {
    return getIRole(roleName);
  }

  private IRole getIRole(String name) {
    return securityContext.roles().find(name);
  }

  private void loadMembersOfRole() {
    membersOfRole = getIRole().getRoleMembers().stream()
            .map(r -> new Role(r))
            .collect(Collectors.toList());
  }

  public List<Role> getMembersOfRole() {
    return membersOfRole;
  }

  public boolean isRoleMemberOfRole(String name) {
    return name.equals(roleName) || membersOfRole.stream().filter(r -> r.getName().equals(name)).findAny().isPresent();
  }

  public List<Role> getFilteredMembers() {
    return filteredMembers;
  }

  public void setFilteredMembers(List<Role> filteredMembers) {
    this.filteredMembers = filteredMembers;
  }

  public void addMember() {
    if (roleMemberName.isEmpty()) {
      return;
    }
    getIRole().addRoleMember(getIRole(roleMemberName));
    roleMemberName = "";
    loadMembersOfRole();
  }

  public void removeMember(String member) {
    getIRole().removeRoleMember(getIRole(member));
    loadMembersOfRole();
  }

  public String getRoleMemberName() {
    return roleMemberName;
  }

  public void setRoleMemberName(String roleMemberName) {
    this.roleMemberName = roleMemberName;
  }

  public List<Role> searchMember(String query) {
    return roleDataModel.getList().stream()
            .filter(m -> StringUtils.containsIgnoreCase(m.getName(), query) && !isRoleMemberOfRole(m.getName()))
            .limit(10).collect(Collectors.toList());
  }

  public MemberProperty getMemberProperty() {
    return roleProperties;
  }

  public boolean isManaged() {
    return ISecurityConstants.TOP_LEVEL_ROLE_NAME.equals(getName()) || (!SecuritySystem.isIvySecuritySystem(securityContext) && getRole().isManaged());
  }

  public void browseLdap() {
    var secBean = new SecurityLdapBean();
    secBean.setSecuritySystemName(securityContext.getName());
    ldapBrowser.browse(secBean.getJndiConfig(secBean.getDefaultContext()), secBean.getEnableInsecureSsl(), role.getExternalName());
  }

  public LdapBrowser getLdapBrowser() {
    return ldapBrowser;
  }

  public void chooseLdapName() {
    role.setExternalName(ldapBrowser.getSelectedNameString());
  }

  public String getUserCount() {
    return ManagerBean.instance().formatNumber(userCount);
  }

  public String getUserInheritCount() {
    return ManagerBean.instance().formatNumber(userInheritCont);
  }

  public String getRunningTaskCount() {
    return ManagerBean.instance().formatNumber(runningTaskCount);
  }

  public String getDirectTaskCount() {
    return ManagerBean.instance().formatNumber(directTaskCount);
  }

  public boolean isLdapBrowserDisabled() {
    return isRootRole() || !SecuritySystem.isJndiSecuritySystem(securityContext);
  }

  private boolean isRootRole() {
    return getIRole().getParent() == null;
  }
}
