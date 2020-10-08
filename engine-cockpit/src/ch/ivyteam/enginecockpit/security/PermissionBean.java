package ch.ivyteam.enginecockpit.security;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.TreeView;
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
public class PermissionBean extends TreeView
{
  private Map<Long, Permission> permissionMap;
  private Map<Long, PermissionGroup> permissionGroupMap;
  private String member;

  private ManagerBean managerBean;

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
    reloadTree();
  }

  public String getUserMember()
  {
    return StringUtils.remove(member, "#");
  }

  public void setUserMember(String member)
  {
    setMember("#" + member);
  }

  @Override
  protected void buildTree()
  {
    permissionMap = new HashMap<>();
    permissionGroupMap = new HashMap<>();
    ISecurityMember iMember = getSecurityMember();
    ISecurityDescriptor securityDescriptor = getSecurityDescriptor();
    IPermissionGroup rootPermissionGroup = securityDescriptor.getSecurityDescriptorType()
            .getRootPermissionGroup();
    PermissionGroup permission = new PermissionGroup(
            securityDescriptor.getPermissionGroupAccess(rootPermissionGroup, iMember), "", this);
    TreeNode node = new DefaultTreeNode(permission, rootTreeNode);
    permissionGroupMap.put(permission.getId(), permission);
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
        Permission permission = new Permission(access, ((AbstractPermission) node.getData()).getPath(), this);
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
        PermissionGroup permission = new PermissionGroup(childGroupAccess, ((AbstractPermission) node.getData()).getPath(), this);
        TreeNode childNode = new DefaultTreeNode(permission, node);
        permissionGroupMap.put(permission.getId(), permission);
        loadChildrenPermissions(childNode, securityDescriptor, childGroup, securityMember);
      }
    }
  }

  @Override
  @SuppressWarnings("unused")
  protected void filterNode(TreeNode node)
  {
    AbstractPermission permission = (AbstractPermission) node.getData();
    if (permission.getName().toLowerCase().contains(filter.toLowerCase()))
    {
      new DefaultTreeNode(permission, filteredTreeNode);
    }
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
    PermissionGroup permissionGroup = permissionGroupMap.get(iPermissionGroup.getId());
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
    Permission permission = permissionMap.get(iPermission.getId());
    permission.setGrant(permissionAccess.isGranted());
    permission.setDeny(permissionAccess.isDenied());
    permission.setExplicit(permissionAccess.isExplicit());
    permission.setPermissionHolder(Optional.ofNullable(permissionAccess.getPermissionHolder()).map(r -> r.getName()).orElse(null));
  }

  public ISecurityDescriptor getSecurityDescriptor()
  {
    return managerBean.getSelectedIApplication().getSecurityDescriptor();
  }

  public ISecurityMember getSecurityMember()
  {
    return managerBean.getSelectedIApplication().getSecurityContext().findSecurityMember(member);
  }
}
