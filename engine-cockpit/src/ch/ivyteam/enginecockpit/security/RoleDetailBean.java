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
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.query.UserQuery;

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
  private List<Role> membersOfRole;
  private List<Role> filteredMembers;
  
  private MemberProperty roleProperties;

  private ManagerBean managerBean;
  private RoleBean roleBean;

  public RoleDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);
    roleBean = context.getApplication().evaluateExpressionGet(context, "#{roleBean}", RoleBean.class);
    role = new Role();
    roleProperties = new MemberProperty().new RoleProperty();
    usersOfRole = new UserDataModel();
  }

  public String getRoleName()
  {
    return roleName;
  }

  public void setRoleName(String roleName)
  {
    this.roleName = roleName;
    IRole iRole = getSecurityContext().findRole(roleName);
    this.role = new Role(iRole);
    this.usersOfRole.setApp(managerBean.getSelectedIApplication());
    this.usersOfRole.setFilterRole(getIRole());
    this.usersOfRole.setFilter("");
    loadMembersOfRole();
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
    IRole iRole = getSecurityContext().findRole(role.getName());
    iRole.setDisplayDescriptionTemplate(role.getDescription());
    iRole.setDisplayNameTemplate(role.getDisplayName());
    iRole.setExternalSecurityName(role.getExternalName());
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
    IUser user = getSecurityContext().findUser(userName);
    user.removeRole(getIRole());
  }

  public void addUser()
  {
    if (roleUserName.isEmpty())
    {
      return;
    }
    getSecurityContext().findUser(roleUserName).addRole(getIRole());
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
    return getSecurityContext().getUserQueryExecutor().createUserQuery()
            .where()
                .not(hasRole)
              .and()
                .name().isLikeIgnoreCase(query + "%")
            .executor().results(0, 10)
            .stream()
            .map(User::new)
            .collect(Collectors.toList());
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
    return membersOfRole.stream().filter(r -> r.getName().equals(name)).findAny().isPresent()
            || name.equals(roleName);
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
    List<Role> search = roleBean.getRolesFlat().stream()
            .filter(m -> StringUtils.startsWithIgnoreCase(m.getName(), query) && !isRoleMemberOfRole(m.getName()))
            .limit(10).collect(Collectors.toList());
    return search;
  }

  private ISecurityContext getSecurityContext()
  {
    return managerBean.getSelectedIApplication().getSecurityContext();
  }
  
  public MemberProperty getMemberProperty()
  {
    return roleProperties;
  }
}
