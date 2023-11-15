package ch.ivyteam.enginecockpit.security;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.AbstractPermission;
import ch.ivyteam.enginecockpit.model.Permission;
import ch.ivyteam.enginecockpit.model.PermissionGroup;
import ch.ivyteam.ivy.security.ISecurityDescriptor;
import ch.ivyteam.ivy.security.ISecurityMember;

@ManagedBean
@ViewScoped
public class PermissionBean
{
  private TreeNode rootTreeNode;
  private TreeNode filteredRootTreeNode;
  private String filter = "";
  private String member;

  private ManagerBean managerBean;
  private ISecurityMember securityMember;
  private ISecurityDescriptor securityDescriptor;

  public PermissionBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }

  public String getMember()
  {
    return member;
  }

  public void setMember(String member)
  {
    this.member = URLDecoder.decode(member, StandardCharsets.UTF_8);
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
    securityMember = managerBean.getSelectedIApplication().getSecurityContext().findSecurityMember(member);
    securityDescriptor = managerBean.getSelectedIApplication().getSecurityDescriptor();
    var rootPermissionGroup = securityDescriptor.getSecurityDescriptorType().getRootPermissionGroup();
    var permission = new PermissionGroup(securityDescriptor.getPermissionGroupAccess(rootPermissionGroup, securityMember), this);
    addPermissionNode(rootTreeNode, permission);
  }

  public String getFilter()
  {
    return filter;
  }

  public void setFilter(String filter)
  {
    if (StringUtils.isBlank(filter)) 
    {
      if (StringUtils.isNotBlank(this.filter)) 
      {
        reloadPermissions();
      }
      return;
    }
    filteredRootTreeNode = new DefaultTreeNode("Filtered roles", null);
    var rootPermissionGroup = securityDescriptor.getSecurityDescriptorType().getRootPermissionGroup();
    rootPermissionGroup.getAllPermissions().stream()
            .filter(permission -> StringUtils.containsIgnoreCase(permission.getName(), filter))
            .map(permission -> {
              var access = securityDescriptor.getPermissionAccess(permission, securityMember);
              return new Permission(access, this);
            })
            .forEach(permission -> addPermissionNode(filteredRootTreeNode, permission));
    this.filter = filter;
  }
  
  public void nodeExpand(NodeExpandEvent event) 
  {
    var node = event.getTreeNode();
    node.setExpanded(true);
    node.getChildren().clear();
    loadChildren(node);
  }
  
  private void loadChildren(TreeNode node) 
  {
    var group = (PermissionGroup) node.getData();
    group.permissionGroup().getChildGroups().forEach(childGroup -> {
      var access = securityDescriptor.getPermissionGroupAccess(childGroup, securityMember);
      addPermissionNode(node, new PermissionGroup(access, this));
    });
    group.permissionGroup().getPermissions().forEach(childPermission -> {
      var access = securityDescriptor.getPermissionAccess(childPermission, securityMember);
      addPermissionNode(node, new Permission(access, this));
    });
  }
  
  @SuppressWarnings("unused")
  private void addPermissionNode(TreeNode parent, AbstractPermission permission) 
  {
    var node = new DefaultTreeNode(permission, parent);
    if (permission instanceof PermissionGroup && !node.isExpanded()) 
    {
      new DefaultTreeNode(new PermissionGroup("loading..."), node);
    }
  }
 
  public void grant(Permission permission) 
  {
    securityDescriptor.grantPermission(permission.permission(), securityMember);
    reloadPermissionTree(permission);
  }

  public void grant(PermissionGroup permissionGroup) 
  {
    securityDescriptor.grantPermissions(permissionGroup.permissionGroup(), securityMember);
    reloadPermissionTree(permissionGroup);
  }

  public void ungrant(Permission permission) 
  {
    securityDescriptor.ungrantPermission(permission.permission(), securityMember);
    reloadPermissionTree(permission);
  }

  public void ungrant(PermissionGroup permissionGroup) 
  {
    securityDescriptor.ungrantPermissions(permissionGroup.permissionGroup(), securityMember);
    reloadPermissionTree(permissionGroup);
  }

  public void deny(Permission permission) 
  {
    securityDescriptor.denyPermission(permission.permission(), securityMember);
    reloadPermissionTree(permission);
  }

  public void deny(PermissionGroup permissionGroup) 
  {
    securityDescriptor.denyPermissions(permissionGroup.permissionGroup(), securityMember);
    reloadPermissionTree(permissionGroup);
  }

  public void undeny(Permission permission) 
  {
    securityDescriptor.undenyPermission(permission.permission(), securityMember);
    reloadPermissionTree(permission);
  }

  public void undeny(PermissionGroup permissionGroup) 
  {
    securityDescriptor.undenyPermissions(permissionGroup.permissionGroup(), securityMember);
    reloadPermissionTree(permissionGroup);
  }

  public void reloadPermissionTree(Permission permission) 
  {
    if (StringUtils.isBlank(filter)) 
    {
      reloadPermissionsUp(searchPermissionNode(rootTreeNode.getChildren(), permission));
    } 
    else 
    {
      reSetPermission(permission);
    }
  }

  public void reloadPermissionTree(PermissionGroup permissionGroup) 
  {
    reloadPermissionsUpAndDown(searchPermissionNode(rootTreeNode.getChildren(), permissionGroup));
  }

  private void reloadPermissionsUp(TreeNode permissionNode) 
  {
    reSetPermission((Permission) permissionNode.getData());
    reloadPermissionGroupsUp(permissionNode.getParent());
  }

  private void reloadPermissionsUpAndDown(TreeNode permissionGroupNode) 
  {
    reSetPermissionGroup((PermissionGroup) permissionGroupNode.getData());
    reloadPermissionGroupsUp(permissionGroupNode.getParent());
    reloadPermissionsDown(permissionGroupNode.getChildren());
  }

  private void reloadPermissionsDown(List<TreeNode> children) 
  {
    if (children.isEmpty()) 
    {
      return;
    }
    for (var child : children) 
    {
      if (child.getData() instanceof Permission) 
      {
        reSetPermission((Permission) child.getData());
      }
      if (child.getData() instanceof PermissionGroup) 
      {
        reSetPermissionGroup((PermissionGroup) child.getData());
      }
      reloadPermissionsDown(child.getChildren());
    }
  }

  private void reloadPermissionGroupsUp(TreeNode permissionGroupNode) 
  {
    if (permissionGroupNode.getData() == null || !(permissionGroupNode.getData() instanceof PermissionGroup)) 
    {
      return;
    }
    reSetPermissionGroup((PermissionGroup) permissionGroupNode.getData());
    reloadPermissionGroupsUp(permissionGroupNode.getParent());
  }

  public TreeNode searchPermissionNode(List<TreeNode> children, AbstractPermission data) 
  {
    for (var child : children) 
    {
      if (data.equals(child.getData())) 
      {
        return child;
      }
      var search = searchPermissionNode(child.getChildren(), data);
      if (search != null) 
      {
        return search;
      }
    }
    return null;
  }

  private void reSetPermissionGroup(PermissionGroup permissionGroup) 
  {
    if (permissionGroup.permissionGroup() == null) 
    {
      return;
    }
    var permissionGroupAccess = securityDescriptor.getPermissionGroupAccess(permissionGroup.permissionGroup(), securityMember);
    permissionGroup.setDeny(permissionGroupAccess.isDeniedAllPermissions());
    permissionGroup.setGrant(permissionGroupAccess.isGrantedAllPermissions());
    permissionGroup.setSomeDeny(permissionGroupAccess.isDeniedAnyPermission());
    permissionGroup.setSomeGrant(permissionGroupAccess.isGrantedAnyPermission());
  }

  private void reSetPermission(Permission permission) 
  {
    var permissionAccess = securityDescriptor.getPermissionAccess(permission.permission(), securityMember);
    permission.setGrant(permissionAccess.isGranted());
    permission.setDeny(permissionAccess.isDenied());
    permission.setExplicit(permissionAccess.isExplicit());
    permission.setPermissionHolder(
            Optional.ofNullable(permissionAccess.getPermissionHolder()).map(r -> r.getName()).orElse(null));
  }
}
