package ch.ivyteam.enginecockpit;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.security.IRole;

@ManagedBean
@ViewScoped
public class RoleBean
{
  private TreeNode treeRootNode;
  private TreeNode filteredTreeRootNode;
  private String filter = "";

  private ApplicationBean applicationBean;

  public RoleBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}",
            ApplicationBean.class);
    reloadRoles();
  }

  @SuppressWarnings("unused")
  public void reloadRoles()
  {
    filter = "";
    treeRootNode = new DefaultTreeNode(new Role("Roles"), null);
    IApplication app = applicationBean.getSelectedIApplication();
    // TODO: remove
    if (app == null)
    {
      TreeNode node = new DefaultTreeNode(new Role("role1"), treeRootNode);
      node.setExpanded(true);
      new DefaultTreeNode(new Role("role2"), node);
      return;
    }
    if (filter.isEmpty())
    {
      IRole role = app.getSecurityContext().getTopLevelRole();
      TreeNode node = new DefaultTreeNode(new Role(role.getName()), treeRootNode);
      node.setExpanded(true);
      buildRolesTree(role, node);
    }
  }

  public TreeNode getRoles()
  {
    if (filter.isEmpty())
    {
      return treeRootNode;
    }
    return filteredTreeRootNode;
  }

  private void buildRolesTree(IRole parentRole, TreeNode rootNode)
  {
    for (IRole role : parentRole.getChildRoles())
    {
      TreeNode node = new DefaultTreeNode(new Role(role.getName()), rootNode);
      buildRolesTree(role, node);
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
