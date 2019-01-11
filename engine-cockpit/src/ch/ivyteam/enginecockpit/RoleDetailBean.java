package ch.ivyteam.enginecockpit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.Role;
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
	
	public void createNewChildRole() {
    	getSecurityContext().findRole(role.getName()).createChildRole(newChildRoleName, "", "", false);;
    }
    
    public String deleteRole() {
    	getSecurityContext().findRole(role.getName()).delete();
    	return "roles.xhtml";
    }
	
	private ISecurityContext getSecurityContext() {
		return applicationBean.getSelectedIApplication().getSecurityContext();
	}
}
