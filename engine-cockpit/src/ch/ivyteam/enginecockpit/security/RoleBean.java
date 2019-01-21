package ch.ivyteam.enginecockpit.security;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.security.IRole;

@ManagedBean
@ViewScoped
public class RoleBean
{
  private TreeNode treeRootNode;
  private TreeNode filteredTreeRootNode;
  private TreeNode treeRootNodeWithMembers;
  private String filter = "";

  private ApplicationBean applicationBean;

  public RoleBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}",
            ApplicationBean.class);
    reloadRoles();
  }

  public void reloadRoles()
  {
    filter = "";
    treeRootNode = new DefaultTreeNode("Roles", null);
    loadRoleTree(treeRootNode, false);
    reloadRolesWithMembers();
  }
  
  public void reloadRolesWithMembers()
  {
    treeRootNodeWithMembers = new DefaultTreeNode("RolesWithMembers", null);
    loadRoleTree(treeRootNodeWithMembers, true);
  }
  
  public TreeNode getRoles()
  {
    if (filter.isEmpty())
    {
      return treeRootNode;
    }
    return filteredTreeRootNode;
  }
  
  public TreeNode getRolesWithMembers()
  {
    if (filter.isEmpty())
    {
      return treeRootNodeWithMembers;
    }
    return filteredTreeRootNode;
  }

  @SuppressWarnings("unused")
  private void loadRoleTree(TreeNode rootNode, boolean renderMembers)
  {
    IApplication app = applicationBean.getSelectedIApplication();
    IRole role = app.getSecurityContext().getTopLevelRole();
    TreeNode node = new DefaultTreeNode(new Role(role), rootNode);
    node.setExpanded(true);
    buildRolesTree(role, node, renderMembers);
  }
  
  @SuppressWarnings("unused")
  private void buildRolesTree(IRole parentRole, TreeNode rootNode, boolean renderMembers)
  {
    for (IRole role : parentRole.getChildRoles())
    {
      TreeNode node = new DefaultTreeNode(new Role(role), rootNode);
      buildRolesTree(role, node, renderMembers);
    }
    if (renderMembers) {
      for (IRole role : parentRole.getRoleMembers())
      {
        new DefaultTreeNode(new Role(role, true), rootNode);
      }
    }
  }

  @SuppressWarnings("unused")
  private void filterTreeRootNode(List<TreeNode> nodes)
  {
    for (TreeNode node : nodes)
    {
      Role role = (Role) node.getData();
      if (role.getName().toLowerCase().contains(filter))
      {
        new DefaultTreeNode(role, filteredTreeRootNode);
      }
      filterTreeRootNode(node.getChildren());
    }
  }

  public String getFilter()
  {
    return filter;
  }

  public void setFilter(String filter)
  {
    this.filter = filter.toLowerCase();
    filteredTreeRootNode = new DefaultTreeNode(new Role("Filtered roles"), null);
    filterTreeRootNode(treeRootNode.getChildren());
  }

}
