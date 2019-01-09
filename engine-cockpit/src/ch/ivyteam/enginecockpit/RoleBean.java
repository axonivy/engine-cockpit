package ch.ivyteam.enginecockpit;

import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.security.IRole;

@ManagedBean
@SessionScoped
public class RoleBean {
	private TreeNode treeRootNode;
	private DefaultTreeNode selectedRole;
	private String filter = "";
	
	private ApplicationBean applicationBean;
	
	public RoleBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}", ApplicationBean.class);
	}
	
	public TreeNode getRolesForAppId(long appId) {
		IApplication app = applicationBean.getIApplication(appId);
		treeRootNode = new DefaultTreeNode(new Role("Roles"), null);
		if (filter.isEmpty()) {
			IRole role = app.getSecurityContext().getTopLevelRole();
			TreeNode node = new DefaultTreeNode(new Role(role.getName()), treeRootNode);
			node.setExpanded(true);
			buildRolesTree(role, node);
			return treeRootNode;
		} else {
			return filterIRoles(app);
		}
	}
	
	private void buildRolesTree(IRole parentRole, TreeNode rootNode) {
		for (IRole role : parentRole.getChildRoles()) {
			TreeNode node = new DefaultTreeNode(new Role(role.getName()), rootNode);
			buildRolesTree(role, node);
		}
	}
	
	private TreeNode filterIRoles(IApplication app) {
		for (IRole role : app.getSecurityContext().getRoles().stream()
				.filter(r -> r.getName().toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList())) {
			new DefaultTreeNode(new Role(role.getName()), treeRootNode);
		}
		return treeRootNode;
	}
	
	public DefaultTreeNode getSelectedRole() {
        return selectedRole;
    }
 
    public void setSelectedRole(DefaultTreeNode selectedRole) {
        this.selectedRole = selectedRole;
    }
    
    public void onSelect(NodeSelectEvent event) {
        FacesMessage msg = new FacesMessage("Role Selected", ((Role) event.getTreeNode().getData()).getName());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public String getFilter() {
    	return filter;
    }
    
    public void setFilter(String filter) {
    	this.filter = filter;
    }
    
    public void filterUpdate() {
    	
    }
}
