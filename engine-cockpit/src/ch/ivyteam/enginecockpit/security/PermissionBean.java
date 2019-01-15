package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.enginecockpit.model.Permission;
import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.IPermissionAccess;
import ch.ivyteam.ivy.security.IPermissionGroup;
import ch.ivyteam.ivy.security.IPermissionGroupAccess;
import ch.ivyteam.ivy.security.ISecurityDescriptor;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.IUser;

@ManagedBean
@ViewScoped
public class PermissionBean
{
  private TreeNode rootTreeNode;
  private TreeNode filteredRootTreeNode;
  private String filter = "";
  private String member;

  private ApplicationBean applicationBean;

  public PermissionBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}",
            ApplicationBean.class);
  }

  public String getMember()
  {
    return member;
  }

  public void setMember(String member)
  {
    this.member = member;
    reloadPermissions();
  }

  public TreeNode getPermissions()
  {
    if (filter.isEmpty())
    {
      return rootTreeNode;
    }
    return filteredRootTreeNode;
  }

  public void reloadPermissions()
  {
    filter = "";
    rootTreeNode = new DefaultTreeNode("Permissions", null);
    IUser user = applicationBean.getSelectedIApplication().getSecurityContext().findUser(member);
    ISecurityDescriptor securityDescriptor = applicationBean.getSelectedIApplication()
            .getSecurityDescriptor();
    IPermissionGroup rootPermissionGroup = securityDescriptor.getSecurityDescriptorType()
            .getRootPermissionGroup();
    TreeNode node = new DefaultTreeNode(
            new Permission(rootPermissionGroup.getName(), rootPermissionGroup.getId(), true), rootTreeNode);
    node.setExpanded(true);

    loadChildrenPermissions(node, securityDescriptor, rootPermissionGroup, user);
  }

  @SuppressWarnings("unused")
  private void loadChildrenPermissions(TreeNode node, ISecurityDescriptor securityDescriptor,
          IPermissionGroup permissionGroup, ISecurityMember securityMember)
  {
    IPermissionGroupAccess permissionGroupAccess = securityDescriptor
            .getPermissionGroupAccess(permissionGroup, securityMember);
    for (IPermission permission : permissionGroupAccess.getPermissionGroup().getPermissions())
    {
      IPermissionAccess access = securityDescriptor.getPermissionAccess(permission, securityMember);

      if (access.getPermission() != null)
      {
        new DefaultTreeNode(
                new Permission(access.getPermission().getName(), access.getPermission().getId(), false),
                node);
      }
    }

    for (IPermissionGroup childGroup : permissionGroupAccess.getPermissionGroup().getChildGroups())
    {
      IPermissionGroupAccess childGroupAccess = securityDescriptor.getPermissionGroupAccess(childGroup,
              securityMember);

      if (childGroupAccess.getPermissionGroup() != null)
      {
        TreeNode childNode = new DefaultTreeNode(
                new Permission(childGroup.getName(), childGroup.getId(), true), node);
        loadChildrenPermissions(childNode, securityDescriptor, childGroup, securityMember);
      }
    }
  }
  
  @SuppressWarnings("unused")
  private void filterRootTreeNode(List<TreeNode> nodes)
  {
    for (TreeNode node : nodes)
    {
      Permission permission = (Permission) node.getData();
      if (permission.getName().toLowerCase().contains(filter.toLowerCase()))
      {
        new DefaultTreeNode(permission, filteredRootTreeNode);
      }
      filterRootTreeNode(node.getChildren());
    }
  }
  
  public String getFilter()
  {
    return filter;
  }

  public void setFilter(String filter)
  {
    this.filter = filter;
    filteredRootTreeNode = new DefaultTreeNode("Filtered roles", null);
    filterRootTreeNode(rootTreeNode.getChildren());
  }

  public List<IPermission> getPermissionsList()
  {
    IUser user = applicationBean.getSelectedIApplication().getSecurityContext().findUser(member);
    return applicationBean.getSelectedIApplication().getSecurityDescriptor().getPermissionAccesses(user)
            .stream().map(a -> a.getPermission()).collect(Collectors.toList());
    // return
    // applicationBean.getSelectedIApplication().getSecurityDescriptor().getPermissions();
  }
}
