package ch.ivyteam.enginecockpit.security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

@ManagedBean
@ViewScoped
public class PermissionBean
{
  private TreeNode rootTreeNode;
  private TreeNode filteredRootTreeNode;
  private Map<Long, Permission> permissionMap;
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
    permissionMap = new HashMap<>();
    ISecurityMember iMember = getSecurityMember();
    ISecurityDescriptor securityDescriptor = getSecurityDescriptor();
    IPermissionGroup rootPermissionGroup = securityDescriptor.getSecurityDescriptorType()
            .getRootPermissionGroup();
    Permission permission = new Permission(rootPermissionGroup, 
            securityDescriptor.getPermissionGroupAccess(rootPermissionGroup, iMember));
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
        Permission permission = new Permission(access);
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
        Permission permission = new Permission(childGroup, childGroupAccess);
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

  public void toggleGrant(long id, boolean grant, boolean group)
  {
    if(group) {
      toggleGrantGroup(id, grant);
    }
    else 
    {
      toggleGrantPermission(id, grant);
    }
  }
  
  public void toggleGrantGroup(long id, boolean grant)
  {
    IPermissionGroup permissionGroup = findPermissionGroup(id, getSecurityDescriptor()
            .getSecurityDescriptorType().getRootPermissionGroup());
    if(!grant) 
    {
      getSecurityDescriptor().grantPermissions(permissionGroup, getSecurityMember());
    }
    else
    {
      getSecurityDescriptor().ungrantPermissions(permissionGroup, getSecurityMember());
    }
    reSetPermissionGroup(permissionGroup);
  }

  private void toggleGrantPermission(long id, boolean grant)
  {
    Optional<IPermission> permission = findPermission(id);
    if(permission.isPresent()) 
    {
      if(!grant) 
      {
        getSecurityDescriptor().grantPermission(permission.get(), getSecurityMember());
      }
      else
      {
        getSecurityDescriptor().ungrantPermission(permission.get(), getSecurityMember());
      }
      reSetSinglePermission(permission.get());
    }
  }
  
  public void toggleDeny(long id, boolean deny, boolean group)
  {
    if(group)
    {
      toggleDenyGroup(id, deny);
    }
    else
    {
      toggleDenyPermission(id, deny);
    }
  }
  
  public void toggleDenyGroup(long id, boolean deny)
  {
    IPermissionGroup permissionGroup = findPermissionGroup(id, getSecurityDescriptor()
            .getSecurityDescriptorType().getRootPermissionGroup());
    if(!deny) 
    {
      getSecurityDescriptor().denyPermissions(permissionGroup, getSecurityMember());
    }
    else
    {
      getSecurityDescriptor().undenyPermissions(permissionGroup, getSecurityMember());
    }
    reSetPermissionGroup(permissionGroup);
  }

  private void toggleDenyPermission(long id, boolean deny)
  {
    Optional<IPermission> permission = findPermission(id);
    if(permission.isPresent())
    {
      if(!deny) 
      {
        getSecurityDescriptor().denyPermission(permission.get(), getSecurityMember());
      }
      else
      {
        getSecurityDescriptor().undenyPermission(permission.get(), getSecurityMember());
      }
      reSetSinglePermission(permission.get());
    }
  }
  
  private void reSetPermissionGroup(IPermissionGroup permissionGroup)
  {
    ISecurityDescriptor securityDescriptor = getSecurityDescriptor();
    IPermissionGroupAccess permissionGroupAccess = securityDescriptor.getPermissionGroupAccess(permissionGroup, getSecurityMember());
    permissionMap.get(permissionGroup.getId()).setDeny(permissionGroupAccess.isDeniedAllPermissions());
    permissionMap.get(permissionGroup.getId()).setGrant(permissionGroupAccess.isGrantedAllPermissions());
    permissionGroup.getAllPermissions().stream().forEach(p -> reSetSinglePermission(p));
  }
  
  private void reSetSinglePermission(IPermission iPermission)
  {
    ISecurityDescriptor securityDescriptor = getSecurityDescriptor();
    IPermissionAccess permissionAccess = securityDescriptor.getPermissionAccess(iPermission, getSecurityMember());
    permissionMap.get(iPermission.getId()).setGrant(permissionAccess.isGranted());
    permissionMap.get(iPermission.getId()).setDeny(permissionAccess.isDenied());
    permissionMap.get(iPermission.getId()).setExplicit(permissionAccess.isExplicit());
  }
  
  private ISecurityDescriptor getSecurityDescriptor()
  {
    return applicationBean.getSelectedIApplication().getSecurityDescriptor();
  }
  
  private Optional<IPermission> findPermission(long id)
  {
    return getSecurityDescriptor().getPermissions().stream().filter(p -> p.getId() == id).findAny();
  }
  
  private IPermissionGroup findPermissionGroup(long id, IPermissionGroup permissionGroup)
  {
    if(permissionGroup.getId() == id)
    {
      return permissionGroup;
    }
    for(IPermissionGroup childGroup : permissionGroup.getChildGroups())
    {
      IPermissionGroup group = findPermissionGroup(id, childGroup);
      if (group != null)
      {
        return group;
      }
    }
    return null;
  }
  
  private ISecurityMember getSecurityMember()
  {
    return applicationBean.getSelectedIApplication().getSecurityContext().findUser(member);
  }
}
