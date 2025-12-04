package ch.ivyteam.enginecockpit.security.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.Strings;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.TreeNodeChildren;

import ch.ivyteam.enginecockpit.commons.TableFilter;
import ch.ivyteam.enginecockpit.commons.TreeView;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.IUser;

public class RoleDataModel extends TreeView<Role> implements TableFilter {
  private static final int ROLE_CHILDREN_LIMIT = 100;
  private static final String CURRENT_ROLE_FILTER = "currentRoles";
  private int showChildLimit;
  private final ISecurityContext securityContext;
  private final boolean showMember;
  private final List<Role> roles;
  private final IUser user;
  private List<String> rolesOfUser;
  private List<SelectItem> contentFilters = List.of();
  private List<String> selectedContentFilters = List.of();

  public RoleDataModel(SecuritySystem securitySystem, boolean showMember) {
    this(securitySystem, showMember, ROLE_CHILDREN_LIMIT, null);
  }

  public RoleDataModel(SecuritySystem securitySystem, boolean showMember, int showChildLimit, IUser user) {
    this.showChildLimit = showChildLimit;
    this.user = user;
    if (user != null) {
      contentFilters = List.of(new SelectItem(CURRENT_ROLE_FILTER, Ivy.cm().co("/userDetailRoles/ShowOnlyCurrentRoles")));
    }
    this.securityContext = securitySystem.getSecurityContext();
    this.showMember = showMember;
    this.roles = securityContext.roles().all().stream()
        .map(Role::new)
        .collect(Collectors.toList());
    this.filter = "";
    reloadTree();
  }

  @Override
  @SuppressWarnings("unused")
  public void setFilter(String filter) {
    this.filter = filter;
    filteredTreeNode = new DefaultTreeNode<>("Filtered roles", null, null);
    roles.stream()
        .filter(this::containsUserThisRole)
        .filter(role -> Strings.CI.contains(role.getName(), filter))
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

  public void increaseShowChildLimitAndReloadTree(int increaseLimitBy) {
    this.showChildLimit += increaseLimitBy;
    reloadTree();
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

  public class LazyRoleTreeNode extends DefaultTreeNode<Role> {
    private final IRole role;
    private boolean childrenFetched;
    private final boolean member;

    public LazyRoleTreeNode(IRole role, boolean member, TreeNode<Role> node) {
      super(new Role(role, member), node);
      this.role = role;
      this.member = member;
      setType("role");
    }

    @Override
    public List<TreeNode<Role>> getChildren() {
      if (isLeaf()) {
        return new TreeNodeChildren<>(this);
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
      var prefiltedRoles = rolesToAdd.stream().filter(r -> containsUserThisRole(r)).toList();
      super.getChildren().addAll(prefiltedRoles.stream()
          .sorted(Comparator.comparing(IRole::getName))
          .limit(showChildLimit)
          .map(r -> new LazyRoleTreeNode(r, isMember, this))
          .collect(Collectors.toList()));
      return prefiltedRoles.size() - showChildLimit;
    }
  }

  private boolean containsUserThisRole(Role role) {
    if (rolesOfUser == null) {
      return true;
    }
    return rolesOfUser.contains(role.getName());
  }

  private boolean containsUserThisRole(IRole role) {
    if (rolesOfUser == null) {
      return true;
    }
    return rolesOfUser.contains(role.getName());
  }

  @Override
  public List<SelectItem> getContentFilters() {
    return contentFilters;
  }

  @Override
  public List<String> getSelectedContentFilters() {
    return selectedContentFilters;
  }

  @Override
  public void setSelectedContentFilters(List<String> selectedContentFilters) {
    if (this.selectedContentFilters.equals(selectedContentFilters)) {
      return;
    }
    this.selectedContentFilters = selectedContentFilters;
    if (selectedContentFilters.contains(CURRENT_ROLE_FILTER)) {
      rolesOfUser = user.getAllRoles().stream().map(IRole::getName).toList();
    } else {
      rolesOfUser = null;
    }
    reloadTree();
  }

  @Override
  public void resetSelectedContentFilters() {
    setSelectedContentFilters(List.of());
  }

  @Override
  public String getContentFilterText() {
    if (selectedContentFilters.contains(CURRENT_ROLE_FILTER)) {
      return Ivy.cm().co("/userDetailRoles/OnlyCurrentRoles");
    }
    return Ivy.cm().co("/userDetailRoles/AllRoles");
  }
}
