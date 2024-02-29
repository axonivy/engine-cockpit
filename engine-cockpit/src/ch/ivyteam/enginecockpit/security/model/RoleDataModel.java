package ch.ivyteam.enginecockpit.security.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import ch.ivyteam.enginecockpit.commons.TreeView;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;

public class RoleDataModel extends TreeView<Role> {
  private static final int ROLE_CHILDREN_LIMIT = 100;
  private final int showChildLimit;
  private final ISecurityContext securityContext;
  private final boolean showMember;
  private final List<Role> roles;

  public RoleDataModel(SecuritySystem securitySystem, boolean showMember) {
    this (securitySystem, showMember, ROLE_CHILDREN_LIMIT);
  }

  public RoleDataModel(SecuritySystem securitySystem, boolean showMember, int showChildLimit) {
    this.showChildLimit = showChildLimit;
    this.securityContext = securitySystem.getSecurityContext();
    this.showMember = showMember;
    this.roles = securityContext.roles().all().stream()
            .map(role -> new Role(role))
            .collect(Collectors.toList());
    this.filter = "";
    reloadTree();
  }

  @Override
  @SuppressWarnings("unused")
  public void setFilter(String filter) {
    this.filter = filter;
    filteredTreeNode = new DefaultTreeNode<>("Filtered roles", null, null);
    roles.stream().filter(role -> StringUtils.containsIgnoreCase(role.getName(), filter))
            .limit(showChildLimit)
            .forEach(role -> new DefaultTreeNode<>("role", role, filteredTreeNode));
    if (filteredTreeNode.getChildCount() >= showChildLimit) {
      new DefaultTreeNode<>("searchDummy",
              new Role("", "The current search has more than " + showChildLimit + " results."),
              filteredTreeNode);
    }
  }

  @Override
  protected void filterNode(TreeNode<Role> node) {
    // Not used because of overridden setFilter
  }

  public List<Role> getList() {
    return roles;
  }

  @Override
  protected void buildTree() {
    rootTreeNode.setExpanded(true);
    var role = securityContext.roles().topLevel();
    var node = new LazyRoleTreeNode(role, false, rootTreeNode);
    node.setExpanded(true);
  }

  public int showChildLimit() {
    return showChildLimit;
  }

  public class LazyRoleTreeNode extends DefaultTreeNode<Role> {
    private IRole role;
    private boolean childrenFetched;
    private boolean member;

    public LazyRoleTreeNode(IRole role, boolean member, TreeNode<Role> node) {
      super(new Role(role, member), node);
      this.role = role;
      this.member = member;
      setType("role");
    }

    @Override
    public List<TreeNode<Role>> getChildren() {
      if (isLeaf()) {
        return new ArrayList<>();
      }
      ensureChildrenFetched();
      return super.getChildren();
    }

    @Override
    public int getChildCount() {
      if (isLeaf()) {
        return 0;
      }
      ensureChildrenFetched();
      return super.getChildCount();
    }

    @Override
    public boolean isLeaf() {
      ensureChildrenFetched();
      return super.isLeaf();
    }

    @SuppressWarnings("unused")
    private void ensureChildrenFetched() {
      if (childrenFetched) {
        return;
      }
      if (member) {
        return;
      }
      if (role == null) {
        return;
      }
      childrenFetched = true;
      var rolesLeft = addRolesToTree(role.getChildRoles(), false);
      if (showMember) {
        addRolesToTree(role.getRoleMembers(), true);
      }
      if (rolesLeft > 0) {
        new DefaultTreeNode<>("dummy", new Role("", Integer.toString(rolesLeft)), this);
      }
    }

    private int addRolesToTree(List<IRole> rolesToAdd, boolean isMember) {
      super.getChildren().addAll(rolesToAdd.stream()
              .sorted(Comparator.comparing(IRole::getName))
              .limit(showChildLimit)
              .map(child -> new LazyRoleTreeNode(child, isMember, this))
              .collect(Collectors.toList()));
      return rolesToAdd.size() - showChildLimit;
    }
  }
}
