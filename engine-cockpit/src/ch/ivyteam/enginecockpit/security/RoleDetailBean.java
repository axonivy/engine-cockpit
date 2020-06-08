package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityConstants;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.query.UserQuery;
import ch.ivyteam.ivy.workflow.TaskState;
import ch.ivyteam.ivy.workflow.query.TaskQuery;

@ManagedBean
@ViewScoped
public class RoleDetailBean
{
  private String roleName;
  private String newChildRoleName;
  private String roleUserName;
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

  public RoleDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);
    roleProperties = new MemberProperty().new RoleProperty();
    usersOfRole = new UserDataModel();
    ldapBrowser = new LdapBrowser();
  }

  public String getRoleName()
  {
    return roleName;
  }

  public void setRoleName(String roleName)
  {
    this.roleName = roleName;
    var iRole = getSecurityContext().findRole(roleName);
    this.role = new Role(iRole);
    this.usersOfRole.setApp(managerBean.getSelectedIApplication());
    this.usersOfRole.setFilterRole(getIRole());
    this.usersOfRole.setFilter("");
    this.roleDataModel = new RoleDataModel(managerBean.getSelectedIApplication(), false);
    loadMembersOfRole();
    userCount = managerBean.getSelectedIApplication().getSecurityContext().users().query().where().hasRoleAssigned(iRole).executor().count();
    userInheritCont = managerBean.getSelectedIApplication().getSecurityContext().users().query().where().hasRole(iRole).executor().count();
    runningTaskCount = TaskQuery.create().where().state().isEqual(TaskState.CREATED)
            .or().state().isEqual(TaskState.RESUMED)
            .or().state().isEqual(TaskState.PARKED)
            .andOverall().activatorRoleId().isEqual(iRole.getId()).executor().count();
    directTaskCount = TaskQuery.create().where().state().isEqual(TaskState.CREATED)
            .or().state().isEqual(TaskState.SUSPENDED)
            .or().state().isEqual(TaskState.RESUMED)
            .or().state().isEqual(TaskState.PARKED)
            .andOverall().activatorRoleId().isEqual(iRole.getId()).executor().count();
  }

  public String getUsersOfRoleFilter()
  {
    return usersOfRole.getFilter();
  }

  public void setUsersOfRoleFilter(String filter)
  {
    this.usersOfRole.setFilter(filter);
  }

  public String getNewChildRoleName()
  {
    return newChildRoleName;
  }

  public void setNewChildRoleName(String name)
  {
    this.newChildRoleName = name;
  }

  public Role getRole()
  {
    return role;
  }

  public void setRole(Role role)
  {
    this.role = role;
  }

  public String createNewChildRole()
  {
    getIRole().createChildRole(newChildRoleName, "", "", true);
    return "roledetail.xhtml?roleName=" + newChildRoleName + "&faces-redirect=true";
  }

  public void saveRoleInfos()
  {
    var iRole = getIRole();
    iRole.setDisplayDescriptionTemplate(role.getDescription());
    iRole.setDisplayNameTemplate(role.getDisplayName());
    iRole.setExternalName(role.getExternalName());
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess",
            new FacesMessage("Role information changes saved"));
  }

  public String deleteRole()
  {
    getIRole().delete();
    return "roles.xhtml?faces-redirect=true";
  }

  public UserDataModel getUsersOfRole()
  {
    return usersOfRole;
  }

  public void removeUser(String userName)
  {
    var user = getSecurityContext().users().find(userName);
    user.removeRole(getIRole());
  }

  public void addUser()
  {
    if (roleUserName.isEmpty())
    {
      return;
    }
    getSecurityContext().users().find(roleUserName).addRole(getIRole());
    roleUserName = "";
  }
  
  public String getRoleUserName()
  {
    return roleUserName;
  }

  public void setRoleUserName(String roleUserName)
  {
    this.roleUserName = roleUserName;
  }
  
  public List<User> searchUser(String query)
  {
    var hasRole = UserQuery.create().where().hasRole(getIRole());
    return getSecurityContext().users().query()
            .where()
                .not(hasRole)
              .and()
                .name().isLikeIgnoreCase(query + "%")
            .executor()
            .resultsPaged(10)
            .map(User::new)
            .page(1);
  }

  private IRole getIRole()
  {
    return getIRole(roleName);
  }

  private IRole getIRole(String name)
  {
    return getSecurityContext().findRole(name);
  }

  private void loadMembersOfRole()
  {
    membersOfRole = getIRole().getRoleMembers().stream().map(r -> new Role(r)).collect(Collectors.toList());
  }

  public List<Role> getMembersOfRole()
  {
    return membersOfRole;
  }

  public boolean isRoleMemberOfRole(String name)
  {
    return name.equals(roleName) || 
            membersOfRole.stream().filter(r -> r.getName().equals(name)).findAny().isPresent();
  }

  public List<Role> getFilteredMembers()
  {
    return filteredMembers;
  }

  public void setFilteredMembers(List<Role> filteredMembers)
  {
    this.filteredMembers = filteredMembers;
  }

  public void addMember()
  {
    if (roleMemberName.isEmpty())
    {
      return;
    }
    getIRole().addRoleMember(getIRole(roleMemberName));
    roleMemberName = "";
    loadMembersOfRole();
  }

  public void removeMember(String member)
  {
    getIRole().removeRoleMember(getIRole(member));
    loadMembersOfRole();
  }
  
  public String getRoleMemberName()
  {
    return roleMemberName;
  }

  public void setRoleMemberName(String roleMemberName)
  {
    this.roleMemberName = roleMemberName;
  }
  
  public List<Role> searchMember(String query)
  {
    return roleDataModel.getList().stream()
            .filter(m -> StringUtils.startsWithIgnoreCase(m.getName(), query) && !isRoleMemberOfRole(m.getName()))
            .limit(10).collect(Collectors.toList());
  }

  private ISecurityContext getSecurityContext()
  {
    return managerBean.getSelectedIApplication().getSecurityContext();
  }
  
  public MemberProperty getMemberProperty()
  {
    return roleProperties;
  }
  
  public boolean isManaged()
  {
    return getRoleName().equals(ISecurityConstants.TOP_LEVEL_ROLE_NAME) || 
      (!managerBean.isIvySecuritySystem() && getRole().isManaged());
  }
  
  public void browseLdap()
  {
    SecurityConfigDetailBean secBean = new SecurityConfigDetailBean(managerBean.getSelectedApplication().getSecuritySystemName());
    ldapBrowser.browse(secBean.getJndiConfig(secBean.getDefaultContext()));
  }
  
  public LdapBrowser getLdapBrowser()
  {
    return ldapBrowser;
  }
  
  public void chooseLdapName()
  {
    role.setExternalName(ldapBrowser.getSelectedLdapName());
  }
  
  public long getUserCount()
  {
    return userCount;
  }
  
  public long getUserInheritCount()
  {
    return userInheritCont;
  }
  
  public long getRunningTaskCount()
  {
    return runningTaskCount;
  }
  
  public long getDirectTaskCount()
  {
    return directTaskCount;
  }
}
