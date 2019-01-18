package ch.ivyteam.enginecockpit.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.enginecockpit.model.AbstractPermission;
import ch.ivyteam.enginecockpit.model.Permission;
import ch.ivyteam.enginecockpit.model.PermissionGroup;
import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.IPermissionAccess;
import ch.ivyteam.ivy.security.IPermissionGroup;
import ch.ivyteam.ivy.security.IPermissionGroupAccess;
import ch.ivyteam.ivy.security.ISecurityDescriptor;
import ch.ivyteam.ivy.security.ISecurityMember;

@ManagedBean
@ViewScoped
public class PermissionBean
{
  private TreeNode rootTreeNode;
  private TreeNode filteredRootTreeNode;
  private Map<Long, AbstractPermission> permissionMap;
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

  public String getUserMember()
  {
    return StringUtils.remove(member, "#");
  }

  public void setUserMember(String member)
  {
    setMember("#" + member);
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
    permissionMap = new HashMap<>();
    ISecurityMember iMember = getSecurityMember();
    ISecurityDescriptor securityDescriptor = getSecurityDescriptor();
    IPermissionGroup rootPermissionGroup = securityDescriptor.getSecurityDescriptorType()
            .getRootPermissionGroup();
    AbstractPermission permission = new PermissionGroup(
            securityDescriptor.getPermissionGroupAccess(rootPermissionGroup, iMember), this);
    TreeNode node = new DefaultTreeNode(permission, rootTreeNode);
    permissionMap.put(permission.getId(), permission);
    node.setExpanded(true);

    loadChildrenPermissions(node, securityDescriptor, rootPermissionGroup, iMember);
  }

  @SuppressWarnings("unused")
  private void loadChildrenPermissions(TreeNode node, ISecurityDescriptor securityDescriptor,
          IPermissionGroup permissionGroup, ISecurityMember securityMember)
  {
    IPermissionGroupAccess permissionGroupAccess = securityDescriptor
            .getPermissionGroupAccess(permissionGroup, securityMember);
    for (IPermission iPermission : permissionGroupAccess.getPermissionGroup().getPermissions())
    {
      IPermissionAccess access = securityDescriptor.getPermissionAccess(iPermission, securityMember);

      if (access.getPermission() != null)
      {
        AbstractPermission permission = new Permission(access, this);
        new DefaultTreeNode(permission, node);
        permissionMap.put(permission.getId(), permission);
      }
    }

    for (IPermissionGroup childGroup : permissionGroupAccess.getPermissionGroup().getChildGroups())
    {
      IPermissionGroupAccess childGroupAccess = securityDescriptor.getPermissionGroupAccess(childGroup,
              securityMember);

      if (childGroupAccess.getPermissionGroup() != null)
      {
        AbstractPermission permission = new PermissionGroup(childGroupAccess, this);
        TreeNode childNode = new DefaultTreeNode(permission, node);
        permissionMap.put(permission.getId(), permission);
        loadChildrenPermissions(childNode, securityDescriptor, childGroup, securityMember);
      }
    }
  }

  @SuppressWarnings("unused")
  private void filterRootTreeNode(List<TreeNode> nodes)
  {
    for (TreeNode node : nodes)
    {
      AbstractPermission permission = (AbstractPermission) node.getData();
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
 
  public void reSetRootPermissionGroup()
  {
    reSetPermissionGroup(getSecurityDescriptor().getSecurityDescriptorType()
            .getRootPermissionGroup());
  }

  private void reSetPermissionGroup(IPermissionGroup iPermissionGroup)
  {
    ISecurityDescriptor securityDescriptor = getSecurityDescriptor();
    IPermissionGroupAccess permissionGroupAccess = securityDescriptor
            .getPermissionGroupAccess(iPermissionGroup, getSecurityMember());
    PermissionGroup permissionGroup = (PermissionGroup) permissionMap.get(iPermissionGroup.getId());
    permissionGroup.setDeny(permissionGroupAccess.isDeniedAllPermissions());
    permissionGroup.setGrant(permissionGroupAccess.isGrantedAllPermissions());
    permissionGroup.setSomeDeny(permissionGroupAccess.isDeniedAnyPermission());
    permissionGroup.setSomeGrant(permissionGroupAccess.isGrantedAnyPermission());
    iPermissionGroup.getPermissions().stream().forEach(p -> reSetPermission(p));
    iPermissionGroup.getChildGroups().stream().forEach(g -> reSetPermissionGroup(g));
  }

  private void reSetPermission(IPermission iPermission)
  {
    ISecurityDescriptor securityDescriptor = getSecurityDescriptor();
    IPermissionAccess permissionAccess = securityDescriptor.getPermissionAccess(iPermission,
            getSecurityMember());
    Permission permission = (Permission) permissionMap.get(iPermission.getId());
    permission.setGrant(permissionAccess.isGranted());
    permission.setDeny(permissionAccess.isDenied());
    permission.setExplicit(permissionAccess.isExplicit());
    permission.setPermissionHolder(Optional.ofNullable(permissionAccess.getPermissionHolder()).map(r -> r.getName()).orElse(null));
  }

  public ISecurityDescriptor getSecurityDescriptor()
  {
    return applicationBean.getSelectedIApplication().getSecurityDescriptor();
  }

  public ISecurityMember getSecurityMember()
  {
    return applicationBean.getSelectedIApplication().getSecurityContext().findSecurityMember(member);
  }
}
