package ch.ivyteam.enginecockpit.security;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.ldapbrowser.LdapBrowser;
import ch.ivyteam.enginecockpit.security.model.MemberProperty;
import ch.ivyteam.enginecockpit.security.model.Role;
import ch.ivyteam.enginecockpit.security.model.RoleDataModel;
import ch.ivyteam.enginecockpit.security.model.User;
import ch.ivyteam.enginecockpit.security.model.UserDataModel;
import ch.ivyteam.enginecockpit.security.system.SecurityConfigDetailBean;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityConstants;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.query.UserQuery;
import ch.ivyteam.ivy.workflow.TaskState;
import ch.ivyteam.ivy.workflow.query.TaskQuery;

@ManagedBean
@ViewScoped
public class RoleDetailBean {
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

  private ManagerBean managerBean;
  private LdapBrowser ldapBrowser;
  private long userCount;
  private long runningTaskCount;
  private long directTaskCount;
  private long userInheritCont;

  public RoleDetailBean() {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    roleProperties = new MemberProperty().new RoleProperty();
    usersOfRole = new UserDataModel(managerBean.getSelectedIApplication());
    ldapBrowser = new LdapBrowser();
  }

  public String getRoleName() {
    return roleName;
  }

  public void setRoleName(String roleName) {
    this.roleName = URLDecoder.decode(roleName, StandardCharsets.UTF_8);
    var iRole = getSecurityContext().findRole(this.roleName);
    this.role = new Role(iRole);
    this.usersOfRole.setApp(managerBean.getSelectedIApplication());
    this.usersOfRole.setFilterRole(getIRole());
    this.usersOfRole.setFilter("");
    this.roleDataModel = new RoleDataModel(managerBean.getSelectedIApplication(), false);
    loadMembersOfRole();
    userCount = managerBean.getSelectedIApplication().getSecurityContext().users().query().where()
            .hasRoleAssigned(iRole).executor().count();
    userInheritCont = managerBean.getSelectedIApplication().getSecurityContext().users().query().where()
            .hasRole(iRole).executor().count();
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

  public String createNewChildRole() {
    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    try {
      getIRole().createChildRole(newChildRoleName, "", "", true);
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage("Role '" + newChildRoleName + "' created successfully", ""));
    } catch (Exception ex) {
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage(FacesMessage.SEVERITY_ERROR,
                      "Role '" + newChildRoleName + "' couldn't be created", ex.getMessage()));
      newChildRoleName = roleName;
    }
    return UriBuilder.fromPath("roledetail.xhtml").queryParam("roleName", newChildRoleName)
            .queryParam("faces-redirect", "true").build().toASCIIString();
  }

  public void saveRoleInfos() {
    var iRole = getIRole();
    iRole.setDisplayDescriptionTemplate(role.getDescription());
    iRole.setDisplayNameTemplate(role.getDisplayName());
    iRole.setExternalName(role.getExternalName());
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess",
            new FacesMessage("Role information changes saved"));
  }

  public String deleteRole() {
    getIRole().delete();
    return "roles.xhtml?faces-redirect=true";
  }

  public UserDataModel getUsersOfRole() {
    return usersOfRole;
  }

  public void removeUser(String userName) {
    var user = getSecurityContext().users().find(userName);
    user.removeRole(getIRole());
  }

  public void addUser() {
    if (roleUser == null) {
      return;
    }
    getSecurityContext().users().find(roleUser.getId()).addRole(getIRole());
    roleUser = null;
  }

  public User getRoleUser() {
    return roleUser;
  }

  public void setRoleUser(User roleUser) {
    this.roleUser = roleUser;
  }

  public boolean hasRoleAssigned(String userName) {
    return getIRole().users().assignedPaged().stream().anyMatch(u -> u.getName().equals(userName));
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

    return getSecurityContext().users().query()
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
    return getSecurityContext().findRole(name);
  }

  private void loadMembersOfRole() {
    membersOfRole = getIRole().getRoleMembers().stream().map(r -> new Role(r)).collect(Collectors.toList());
  }

  public List<Role> getMembersOfRole() {
    return membersOfRole;
  }

  public boolean isRoleMemberOfRole(String name) {
    return name.equals(roleName) ||
            membersOfRole.stream().filter(r -> r.getName().equals(name)).findAny().isPresent();
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
            .filter(m -> StringUtils.containsIgnoreCase(m.getName(), query)
                    && !isRoleMemberOfRole(m.getName()))
            .limit(10).collect(Collectors.toList());
  }

  private ISecurityContext getSecurityContext() {
    return managerBean.getSelectedIApplication().getSecurityContext();
  }

  public MemberProperty getMemberProperty() {
    return roleProperties;
  }

  public boolean isManaged() {
    return ISecurityConstants.TOP_LEVEL_ROLE_NAME.equals(getRoleName())
            || (!managerBean.isIvySecuritySystem() && getRole().isManaged());
  }

  public void browseLdap() {
    var secBean = new SecurityConfigDetailBean(managerBean.getSelectedApplication().getSecuritySystemName());
    ldapBrowser.browse(secBean.getJndiConfig(secBean.getDefaultContext()), secBean.getEnableInsecureSsl(),
            role.getExternalName());
  }

  public LdapBrowser getLdapBrowser() {
    return ldapBrowser;
  }

  public void chooseLdapName() {
    role.setExternalName(ldapBrowser.getSelectedLdapName());
  }

  public String getUserCount() {
    return managerBean.formatNumber(userCount);
  }

  public String getUserInheritCount() {
    return managerBean.formatNumber(userInheritCont);
  }

  public String getRunningTaskCount() {
    return managerBean.formatNumber(runningTaskCount);
  }

  public String getDirectTaskCount() {
    return managerBean.formatNumber(directTaskCount);
  }
}
