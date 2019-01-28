package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;

@ManagedBean
@ViewScoped
public class RoleDetailBean
{
  private String roleName;
  private String newChildRoleName;
  private String roleUserName;
  private Role role;

  private List<User> usersOfRole;
  private List<User> filteredUsers;
  private List<Role> membersOfRole;
  private List<Role> filteredMembers;

  private ApplicationBean applicationBean;

  public RoleDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}",
            ApplicationBean.class);
    role = new Role();
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
    loadUsersOfRole();
    loadMembersOfRole();
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

  public List<User> getUsersOfRole()
  {
    return usersOfRole;
  }

  public boolean isUserMemberOfRole(String userName)
  {
    return !usersOfRole.stream().filter(u -> u.getName().equals(userName)).findAny().isPresent();
  }

  public void removeUser(String userName)
  {
    getSecurityContext().findUser(userName).removeRole(getIRole());
    loadUsersOfRole();
  }

  public void addUser()
  {
    getSecurityContext().findUser(roleUserName).addRole(getIRole());
    roleUserName = "";
    loadUsersOfRole();
  }
  
  public String getRoleUserName()
  {
    return roleUserName;
  }

  public void setRoleUserName(String roleUserName)
  {
    this.roleUserName = roleUserName;
  }

  public List<User> getFilteredUsers()
  {
    return filteredUsers;
  }

  public void setFilteredUsers(List<User> filteredUsers)
  {
    this.filteredUsers = filteredUsers;
  }

  private void loadUsersOfRole()
  {
    usersOfRole = getIRole().getUsers().stream().map(u -> new User(u))
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

  public void addMember(String member)
  {
    getIRole().addRoleMember(getIRole(member));
    loadMembersOfRole();
  }

  public void removeMember(String member)
  {
    getIRole().removeRoleMember(getIRole(member));
    loadMembersOfRole();
  }

  private ISecurityContext getSecurityContext()
  {
    return applicationBean.getSelectedIApplication().getSecurityContext();
  }
}
