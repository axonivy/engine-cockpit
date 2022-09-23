package ch.ivyteam.enginecockpit.security.permission;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.commons.TreeView;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.IPermissionGroup;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.ISecurityDescriptor;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.internal.SecurityContext;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class PermissionBean extends TreeView<AbstractPermission> {

  private String securitySystemName;
  private String member;
  private SecurityContext securityContext;

  private Map<Long, Permission> permissionMap;
  private Map<Long, PermissionGroup> permissionGroupMap;

  public String getSecuritySystem() {
    return securitySystemName;
  }

  public void setSecuritySystem(String securitySystemName) {
    this.securitySystemName = securitySystemName;
  }

  public String getMember() {
    return member;
  }

  public void setMember(String member) {
    this.member = member;
  }

  public String getUserMember() {
    return StringUtils.removeStart(member, "#");
  }

  public void setUserMember(String member) {
    setMember("#" + member);
  }

  public void onload() {
    securityContext = (SecurityContext) ISecurityContextRepository.instance().get(securitySystemName);
    if (securityContext == null) {
      ResponseHelper.notFound("Security System '" + securitySystemName + "' not found");
      return;
    }
    reloadTree();
  }

  @Override
  protected void buildTree() {
    permissionMap = new HashMap<>();
    permissionGroupMap = new HashMap<>();
    var iMember = getSecurityMember();
    var securityDescriptor = getSecurityDescriptor();
    var rootPermissionGroup = securityDescriptor.getSecurityDescriptorType().getRootPermissionGroup();
    var permission = new PermissionGroup(securityDescriptor.getPermissionGroupAccess(rootPermissionGroup, iMember), "", this);
    var node = new DefaultTreeNode<AbstractPermission>(permission, rootTreeNode);
    permissionGroupMap.put(permission.getId(), permission);
    node.setExpanded(true);
    loadChildrenPermissions(node, securityDescriptor, rootPermissionGroup, iMember);
  }

  @SuppressWarnings("unused")
  private void loadChildrenPermissions(TreeNode<AbstractPermission> node, ISecurityDescriptor securityDescriptor,
          IPermissionGroup permissionGroup, ISecurityMember securityMember) {
    var permissionGroupAccess = securityDescriptor
            .getPermissionGroupAccess(permissionGroup, securityMember);
    for (var iPermission : permissionGroupAccess.getPermissionGroup().getPermissions()) {
      var access = securityDescriptor.getPermissionAccess(iPermission, securityMember);
      if (access.getPermission() != null) {
        var permission = permissionMap.get(access.getPermission().getId());
        if (permission == null) {
          permission = new Permission(access, node.getData().getPath(), this);
          permissionMap.put(permission.getId(), permission);
        }
        new DefaultTreeNode<>(permission, node);
      }
    }

    for (var childGroup : permissionGroupAccess.getPermissionGroup().getChildGroups()) {
      var childGroupAccess = securityDescriptor.getPermissionGroupAccess(childGroup,
              securityMember);
      if (childGroupAccess.getPermissionGroup() != null) {
        var permission = new PermissionGroup(childGroupAccess, node.getData().getPath(), this);
        var childNode = new DefaultTreeNode<AbstractPermission>(permission, node);
        permissionGroupMap.put(permission.getId(), permission);
        loadChildrenPermissions(childNode, securityDescriptor, childGroup, securityMember);
      }
    }
  }

  @Override
  @SuppressWarnings("unused")
  protected void filterNode(TreeNode<AbstractPermission> node) {
    var permission = node.getData();
    if (StringUtils.containsIgnoreCase(permission.getName(), filter)) {
      new DefaultTreeNode<>(permission, filteredTreeNode);
    }
  }

  public void reSetRootPermissionGroup() {
    reSetPermissionGroup(getSecurityDescriptor().getSecurityDescriptorType()
            .getRootPermissionGroup());
  }

  private void reSetPermissionGroup(IPermissionGroup iPermissionGroup) {
    var permissionGroupAccess = getSecurityDescriptor().getPermissionGroupAccess(iPermissionGroup, getSecurityMember());
    var permissionGroup = permissionGroupMap.get(iPermissionGroup.getId());
    permissionGroup.setDeny(permissionGroupAccess.isDeniedAllPermissions());
    permissionGroup.setGrant(permissionGroupAccess.isGrantedAllPermissions());
    permissionGroup.setSomeDeny(permissionGroupAccess.isDeniedAnyPermission());
    permissionGroup.setSomeGrant(permissionGroupAccess.isGrantedAnyPermission());
    iPermissionGroup.getPermissions().stream().forEach(p -> reSetPermission(p));
    iPermissionGroup.getChildGroups().stream().forEach(g -> reSetPermissionGroup(g));
  }

  private void reSetPermission(IPermission iPermission) {
    var permissionAccess = getSecurityDescriptor().getPermissionAccess(iPermission,
            getSecurityMember());
    var permission = permissionMap.get(iPermission.getId());
    permission.setGrant(permissionAccess.isGranted());
    permission.setDeny(permissionAccess.isDenied());
    permission.setExplicit(permissionAccess.isExplicit());
    permission.setPermissionHolder(
            Optional.ofNullable(permissionAccess.getPermissionHolder()).map(r -> r.getName()).orElse(null));
  }

  public ISecurityDescriptor getSecurityDescriptor() {
    return ISystemDatabasePersistencyService.instance().transaction().executeAndGet(tx -> securityContext.getSecurityDescriptor(tx));
  }

  public ISecurityMember getSecurityMember() {
    return securityContext.members().find(member);
  }
}
