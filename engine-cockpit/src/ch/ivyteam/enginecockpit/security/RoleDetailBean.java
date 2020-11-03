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

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;
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
  private RoleDataModel roleDataModel;
  private List<Role> membersOfRole;
  private List<Role> filteredMembers;
  
  private MemberProperty roleProperties;

  private ManagerBean managerBean;
  private LdapBrowser ldapBrowser;

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
    this.roleName = URLDecoder.decode(roleName, StandardCharsets.UTF_8);
    var iRole = getSecurityContext().findRole(this.roleName);
    this.role = new Role(iRole);
    this.usersOfRole.setApp(managerBean.getSelectedIApplication());
    this.usersOfRole.setFilterRole(getIRole());
    this.usersOfRole.setFilter("");
    this.roleDataModel = new RoleDataModel(managerBean.getSelectedIApplication(), false);
    loadMembersOfRole();
    roleProperties.setMemberName(this.roleName);
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
    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    try
    {
      getIRole().createChildRole(newChildRoleName, "", "", true);
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage("Role '" + newChildRoleName + "' created successfully", ""));
    }
    catch (Exception ex)
    {
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Role '" + newChildRoleName + "' couldn't be created", ex.getMessage()));
      newChildRoleName = roleName;
    }
    return UriBuilder.fromPath("roledetail.xhtml").queryParam("roleName", newChildRoleName)
            .queryParam("faces-redirect", "true").build().toASCIIString();
  }

  public void saveRoleInfos()
  {
    var iRole = getIRole();
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
  
  public void browseLdap()
  {
    SecurityConfigDetailBean secBean = new SecurityConfigDetailBean(managerBean.getSelectedApplication().getSecuritySystemName());
    ldapBrowser.browse(secBean.getJndiConfig(secBean.getDefaultContext()), secBean.getEnableInsecureSsl());
  }
  
  public LdapBrowser getLdapBrowser()
  {
    return ldapBrowser;
  }
  
  public void chooseLdapName()
  {
    role.setExternalName(ldapBrowser.getSelectedLdapName());
  }
}
