package ch.ivyteam.enginecockpit.security;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;

public class RoleDataModel
{
  private static final int ROLE_CHILDREN_LIMIT = 100;
  private int showChildLimit;
  private final ISecurityContext securityContext;
  private final boolean showMember;
  private final List<Role> roles;
  private String filter;
  
  private TreeNode treeRootNode;
  private TreeNode filteredTreeRootNode;

  public RoleDataModel(IApplication app, boolean showMember)
  {
    this(app, showMember, ROLE_CHILDREN_LIMIT);
  }

  public RoleDataModel(IApplication app, boolean showMember, int showChildLimit)
  {
    this.showChildLimit = showChildLimit;
    this.securityContext = app.getSecurityContext();
    this.showMember = showMember;
    this.roles = app.getSecurityContext().getRoles().stream()
            .map(role -> new Role(role)).collect(Collectors.toList());
    this.filter = "";
    buildRoleTree();
  }

  @SuppressWarnings("unused")
  public void setFilter(String filter)
  {
    this.filter = filter;
    filteredTreeRootNode = new DefaultTreeNode("Filtered roles", null);
    roles.stream().filter(role -> StringUtils.containsIgnoreCase(role.getName(), filter))
            .limit(showChildLimit)
            .forEach(role -> new DefaultTreeNode("role", role, filteredTreeRootNode));
    if (filteredTreeRootNode.getChildCount() >= showChildLimit)
    {
      new DefaultTreeNode("searchDummy", new Role("The current search has more than " + showChildLimit + " results."), filteredTreeRootNode);
    }
  }

  public String getFilter()
  {
    return filter;
  }

  public void increaseShowChildLimitAndReloadTree(int increaseLimitBy)
  {
    this.showChildLimit+=increaseLimitBy;
    buildRoleTree();
  }

  public List<Role> getList()
  {
    return roles;
  }
  
  public TreeNode getTree()
  {
    if (StringUtils.isBlank(filter))
    {
      return treeRootNode;
    }
    return filteredTreeRootNode;
  }
  
  private void buildRoleTree()
  {
    treeRootNode = new DefaultTreeNode("Roles", null);
    treeRootNode.setExpanded(true);
    var role = securityContext.getTopLevelRole();
    TreeNode node = new LazyRoleTreeNode(role, false, treeRootNode);
    node.setExpanded(true);
  }
  
  private final class LazyRoleTreeNode extends DefaultTreeNode
  {
    private final IRole role;
    private boolean childrenFetched;
    private final boolean isMember;

    public LazyRoleTreeNode(IRole role, boolean isMember, TreeNode node)
    {
      super(new Role(role, isMember), node);
      this.role = role;
      this.isMember = isMember;
      setType("role");
    }
    
    @Override
    public List<TreeNode> getChildren()
    {
      ensureChildrenFetched();
      return super.getChildren();
    }
    
    @Override
    public int getChildCount()
    {
      ensureChildrenFetched();
      return super.getChildCount();
    }
    
    @Override
    public boolean isLeaf()
    {
      ensureChildrenFetched();
      return super.isLeaf();
    }
    
    @SuppressWarnings("unused")
    private void ensureChildrenFetched()
    {
      if (childrenFetched)
      {
        return;
      }
      if (isMember)
      {
        return;
      }
      if (role == null)
      {
        return;
      }

      childrenFetched = true;
      var rolesLeft = addRolesToTree(role.getChildRoles(), false);
      if (showMember)
      {
        addRolesToTree(role.getRoleMembers(), true);
      }
      if (rolesLeft > 0)
      {
        new DefaultTreeNode("dummy", new Role(Integer.toString(rolesLeft)), this);
      }
    }

    private int addRolesToTree(List<IRole> rolesToAdd, boolean member)
    {
      super.getChildren().addAll(rolesToAdd.stream()
              .sorted(Comparator.comparing(IRole::getName))
              .limit(showChildLimit)
              .map(child -> new LazyRoleTreeNode(child, member, this))
              .collect(Collectors.toList()));
      return rolesToAdd.size() - showChildLimit;
    }
  }
}
