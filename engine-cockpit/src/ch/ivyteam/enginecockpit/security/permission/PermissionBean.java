package ch.ivyteam.enginecockpit.security.permission;

import java.util.List;
import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.commons.TreeView;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.ISecurityDescriptor;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.internal.context.SecurityContext;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class PermissionBean extends TreeView<AbstractPermission> {

  private String securitySystemName;
  private String member;
  private SecurityContext securityContext;
  private ISecurityMember securityMember;
  private ISecurityDescriptor securityDescriptor;

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
    securityMember = securityContext.members().find(member);
    securityDescriptor = ISystemDatabasePersistencyService.instance().transaction().executeAndGet(tx -> securityContext.getSecurityDescriptor(tx));
    reloadTree();
  }

  @Override
  protected void buildTree() {
    var rootPermissionGroup = securityDescriptor.getSecurityDescriptorType().getRootPermissionGroup();
    var permission = new PermissionGroup(securityDescriptor.getPermissionGroupAccess(rootPermissionGroup, securityMember), this);
    addPermissionNode(rootTreeNode, permission);
  }

  @Override
  public void setFilter(String filter) {
    if (StringUtils.isBlank(filter))
    {
      if (StringUtils.isNotBlank(this.filter)) {
        reloadTree();
      }
      return;
    }
    filteredTreeNode = new DefaultTreeNode<AbstractPermission>("Filtered tree", null, null);
    var rootPermissionGroup = securityDescriptor.getSecurityDescriptorType().getRootPermissionGroup();
    rootPermissionGroup.getAllPermissions().stream()
            .filter(permission -> StringUtils.containsIgnoreCase(permission.getName(), filter))
            .map(permission -> {
              var access = securityDescriptor.getPermissionAccess(permission, securityMember);
              return new Permission(access, this);
            })
            .forEach(permission -> addPermissionNode(filteredTreeNode, permission));
    this.filter = filter;
  }

  @Override
  protected void filterNode(TreeNode<AbstractPermission> node) {
    // do nothing
  }

  @Override
  @SuppressWarnings("unchecked")
  public void nodeExpand(NodeExpandEvent event) {
    var node = event.getTreeNode();
    node.getChildren().clear();
    loadChildren(node);
  }

  private void loadChildren(TreeNode<AbstractPermission> node) {
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
  private void addPermissionNode(TreeNode<AbstractPermission> parent, AbstractPermission permission) {
    var node = new DefaultTreeNode<>(permission, parent);
    if (permission instanceof PermissionGroup && !node.isExpanded()) {
      new DefaultTreeNode<>(new PermissionGroup("loading..."), node);
    }
  }

  void grant(Permission permission) {
    securityDescriptor.grantPermission(permission.permission(), securityMember);
    reloadPermissionTree(permission);
  }

  void grant(PermissionGroup permissionGroup) {
    securityDescriptor.grantPermissions(permissionGroup.permissionGroup(), securityMember);
    reloadPermissionTree(permissionGroup);
  }

  void ungrant(Permission permission) {
    securityDescriptor.ungrantPermission(permission.permission(), securityMember);
    reloadPermissionTree(permission);
  }

  void ungrant(PermissionGroup permissionGroup) {
    securityDescriptor.ungrantPermissions(permissionGroup.permissionGroup(), securityMember);
    reloadPermissionTree(permissionGroup);
  }

  void deny(Permission permission) {
    securityDescriptor.denyPermission(permission.permission(), securityMember);
    reloadPermissionTree(permission);
  }

  void deny(PermissionGroup permissionGroup) {
    securityDescriptor.denyPermissions(permissionGroup.permissionGroup(), securityMember);
    reloadPermissionTree(permissionGroup);
  }

  void undeny(Permission permission) {
    securityDescriptor.undenyPermission(permission.permission(), securityMember);
    reloadPermissionTree(permission);
  }

  void undeny(PermissionGroup permissionGroup) {
    securityDescriptor.undenyPermissions(permissionGroup.permissionGroup(), securityMember);
    reloadPermissionTree(permissionGroup);
  }

  private void reloadPermissionTree(Permission permission) {
    if (StringUtils.isBlank(filter)) {
      reloadPermissionsUp(searchPermissionNode(rootTreeNode.getChildren(), permission));
    } else {
      reSetPermission(permission);
    }
  }

  private void reloadPermissionTree(PermissionGroup permissionGroup) {
    reloadPermissionsUpAndDown(searchPermissionNode(rootTreeNode.getChildren(), permissionGroup));
  }

  private void reloadPermissionsUp(TreeNode<AbstractPermission> permissionNode) {
    reSetPermission((Permission) permissionNode.getData());
    reloadPermissionGroupsUp(permissionNode.getParent());
  }

  private void reloadPermissionsUpAndDown(TreeNode<AbstractPermission> permissionGroupNode) {
    reSetPermissionGroup((PermissionGroup) permissionGroupNode.getData());
    reloadPermissionGroupsUp(permissionGroupNode.getParent());
    reloadPermissionsDown(permissionGroupNode.getChildren());
  }

  private void reloadPermissionsDown(List<TreeNode<AbstractPermission>> children) {
    if (children.isEmpty()) {
      return;
    }
    for (var child : children) {
      if (child.getData() instanceof Permission permission) {
        reSetPermission(permission);
      }
      if (child.getData() instanceof PermissionGroup permissionGroup) {
        reSetPermissionGroup(permissionGroup);
      }
      reloadPermissionsDown(child.getChildren());
    }
  }

  private void reloadPermissionGroupsUp(TreeNode<AbstractPermission> permissionGroupNode) {
    if (permissionGroupNode.getData() == null) {
      return;
    }
    reSetPermissionGroup((PermissionGroup) permissionGroupNode.getData());
    reloadPermissionGroupsUp(permissionGroupNode.getParent());
  }

  private TreeNode<AbstractPermission> searchPermissionNode(List<TreeNode<AbstractPermission>> children, AbstractPermission data) {
    for (var child : children) {
      if (data.equals(child.getData())) {
        return child;
      }
      var search = searchPermissionNode(child.getChildren(), data);
      if (search != null) {
        return search;
      }
    }
    return null;
  }

  private void reSetPermissionGroup(PermissionGroup permissionGroup) {
    if (permissionGroup.permissionGroup() == null) {
      return;
    }
    var permissionGroupAccess = securityDescriptor.getPermissionGroupAccess(permissionGroup.permissionGroup(), securityMember);
    permissionGroup.setDeny(permissionGroupAccess.isDeniedAllPermissions());
    permissionGroup.setGrant(permissionGroupAccess.isGrantedAllPermissions());
    permissionGroup.setSomeDeny(permissionGroupAccess.isDeniedAnyPermission());
    permissionGroup.setSomeGrant(permissionGroupAccess.isGrantedAnyPermission());
  }

  private void reSetPermission(Permission permission) {
    var permissionAccess = securityDescriptor.getPermissionAccess(permission.permission(), securityMember);
    permission.setGrant(permissionAccess.isGranted());
    permission.setDeny(permissionAccess.isDenied());
    permission.setExplicit(permissionAccess.isExplicit());
    permission.setPermissionHolder(
            Optional.ofNullable(permissionAccess.getPermissionHolder()).map(r -> r.getName()).orElse(null));
  }
}
