package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.TreeView;
import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;

public class RoleDataModel extends TreeView
{
  private static final int ROLE_CHILDREN_LIMIT = 100;
  private ISecurityContext securityContext;
  private boolean showMember;
  private List<Role> roles;

  public RoleDataModel(IApplication app, boolean showMember)
  {
    this.securityContext = app.getSecurityContext();
    this.showMember = showMember;
    this.roles = app.getSecurityContext().getRoles().stream()
            .map(role -> new Role(role)).collect(Collectors.toList());
    this.filter = "";
    reloadTree();
  }

  @Override
  @SuppressWarnings("unused")
  public void setFilter(String filter)
  {
    this.filter = filter;
    filteredTreeNode = new DefaultTreeNode("Filtered roles", null);
    roles.stream().filter(role -> StringUtils.containsIgnoreCase(role.getName(), filter))
            .limit(ROLE_CHILDREN_LIMIT)
            .forEach(role -> new DefaultTreeNode("role", role, filteredTreeNode));
    if (filteredTreeNode.getChildCount() >= ROLE_CHILDREN_LIMIT)
    {
      new DefaultTreeNode("dummy", new Role("The current search has more than " + ROLE_CHILDREN_LIMIT + " results."), filteredTreeNode);
    }
  }
  
  @Override
  protected void filterNode(TreeNode node)
  {
    // Not used because of overridden setFilter
  }
  
  public List<Role> getList()
  {
    return roles;
  }
  
  @Override
  protected void buildTree()
  {
    rootTreeNode.setExpanded(true);
    var role = securityContext.getTopLevelRole();
    TreeNode node = new LazyRoleTreeNode(role, false, rootTreeNode);
    node.setExpanded(true);
  }
  
  public class LazyRoleTreeNode extends DefaultTreeNode
  {
    private IRole role;
    private boolean childrenFetched;
    private boolean member;

    public LazyRoleTreeNode(IRole role, boolean member, TreeNode node)
    {
      super(new Role(role, member), node);
      this.role = role;
      this.member = member;
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
      if (member)
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
        new DefaultTreeNode("dummy", new Role("Please use the search to find a specific role (" + rolesLeft + " more roles)"), this);
      }
    }

    private int addRolesToTree(List<IRole> rolesToAdd, boolean isMember)
    {
      super.getChildren().addAll(rolesToAdd.stream()
              .limit(ROLE_CHILDREN_LIMIT)
              .map(child -> new LazyRoleTreeNode(child, isMember, this))
              .collect(Collectors.toList()));
      return rolesToAdd.size() - ROLE_CHILDREN_LIMIT;
    }
  }
}
