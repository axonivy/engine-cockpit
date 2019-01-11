package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.IPermissionAccess;
import ch.ivyteam.ivy.security.IPermissionGroup;
import ch.ivyteam.ivy.security.IPermissionGroupAccess;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityDescriptor;
import ch.ivyteam.ivy.security.ISecurityDescriptorType;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.IUser;

@ManagedBean
@ViewScoped
public class PermissionBean {
	private DefaultTreeNode rootTreeNode;
	
	private ApplicationBean applicationBean;
	
	public PermissionBean() {
		FacesContext context = FacesContext.getCurrentInstance();
		applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}", ApplicationBean.class);
	}
	
	public TreeNode getPermissions() {
		rootTreeNode = new DefaultTreeNode("Permissions", null);
		IUser user = applicationBean.getSelectedIApplication().getSecurityContext().findUser("Developer");
		ISecurityDescriptor securityDescriptor = applicationBean.getSelectedIApplication().getSecurityDescriptor();
		IPermissionGroup rootPermissionGroup = securityDescriptor.getSecurityDescriptorType().getRootPermissionGroup();
		TreeNode rootNode = new DefaultTreeNode(rootPermissionGroup, rootTreeNode);
		
		loadChildrenPermissions(rootNode, securityDescriptor, rootPermissionGroup, user);
		
		return rootTreeNode;
	}
	
	private void loadChildrenPermissions(TreeNode node, ISecurityDescriptor securityDescriptor, IPermissionGroup permissionGroup, ISecurityMember securityMember) {
		IPermissionGroupAccess permissionGroupAccess = securityDescriptor.getPermissionGroupAccess(permissionGroup, securityMember);
		for (IPermission permission : permissionGroupAccess.getPermissionGroup().getPermissions()) {
	        IPermissionAccess access = securityDescriptor.getPermissionAccess(permission, securityMember);

	        if (access.getPermission() != null) {
	        	new DefaultTreeNode(access.getPermission(), node);
	        }
	    }
		
		for (IPermissionGroup childGroup : permissionGroupAccess.getPermissionGroup().getChildGroups()) {
	        IPermissionGroupAccess childGroupAccess =
	            securityDescriptor.getPermissionGroupAccess(childGroup, securityMember);

	        if (childGroupAccess.getPermissionGroup() != null) {
	        	TreeNode childNode = new DefaultTreeNode(childGroup, node);
	        	loadChildrenPermissions(childNode, securityDescriptor, childGroup, securityMember);
	        }
	    }
	}
	
	public List<IPermission> getPermissionsList() {
		IUser user = applicationBean.getSelectedIApplication().getSecurityContext().findUser("Developer");
		return applicationBean.getSelectedIApplication().getSecurityDescriptor().getPermissionAccesses(user).stream().map(a -> a.getPermission()).collect(Collectors.toList());
		//return applicationBean.getSelectedIApplication().getSecurityDescriptor().getPermissions();
	}
}
