package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;

@ManagedBean
@ViewScoped
public class RoleDetailBean {
	private String roleName;
	private String newChildRoleName;
	private Role role;
	
	private ApplicationBean applicationBean;
	
	public RoleDetailBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}", ApplicationBean.class);
		role = new Role();
	}
	
	public String getRoleName() {
		return roleName;
	}
	
	public void setRoleName(String roleName) {
		this.roleName = roleName;
		IRole iRole = getSecurityContext().findRole(roleName);
		this.role = new Role(iRole);
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
	
	public void saveRoleInfos() {
		IRole iRole = getSecurityContext().findRole(role.getName());
		iRole.setDisplayDescriptionTemplate(role.getDescription());
		iRole.setDisplayNameTemplate(role.getDisplayName());
		iRole.setExternalSecurityName(role.getExternalName());
		FacesContext.getCurrentInstance().addMessage("informationSaveSuccess", new FacesMessage("Role information changes saved"));
	}
    
    public List<User> getUsersOfRole() {
    	return getSecurityContext().findRole(roleName).getAllUsers().stream().map(u -> new User(u)).collect(Collectors.toList());
    }
    
    public void removeUser(String userName) {
    	getSecurityContext().findUser(userName).removeRole(getSecurityContext().findRole(roleName));
    }
    
    public void addUser(String userName) {
    	getSecurityContext().findUser(userName).addRole(getSecurityContext().findRole(roleName));
    }
	
	private ISecurityContext getSecurityContext() {
		return applicationBean.getSelectedIApplication().getSecurityContext();
	}
}
