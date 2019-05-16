package ch.ivyteam.enginecockpit;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.model.BusinessCalendar;
import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.ivy.application.calendar.IBusinessCalendarConfiguration;
import ch.ivyteam.ivy.scripting.objects.Tree;

@ManagedBean
@ViewScoped
public class BusinessCalendarBean
{
  private ManagerBean managerBean;
  private TreeNode treeRootNode;
  private TreeNode filteredTreeRootNode;
  private String filter;
  private BusinessCalendar activeCalendar;

  public BusinessCalendarBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);
    reloadBusinessCalendar();
  }
  
  public void reloadBusinessCalendar()
  {
    treeRootNode = new DefaultTreeNode("Roles", null);
    loadCalendarTree(treeRootNode);
    
    setActiveCalendar("cal2");
  }

  private void loadCalendarTree(TreeNode rootNode)
  {
    Tree rootTree = managerBean.getSelectedIApplication().getBusinessCalendarSettings().getAllBusinessCalendarConfigurations();
    TreeNode node = new DefaultTreeNode(new BusinessCalendar(findCalendar(rootTree.getInfo())).getName(), rootNode);
    node.setExpanded(true);
    buildCalendarTree(rootTree, node);
  }
  
  private IBusinessCalendarConfiguration findCalendar(String name)
  {
    return managerBean.getSelectedIApplication().getBusinessCalendarSettings().findBusinessCalendarConfiguration(name);
  }
  
  private void buildCalendarTree(Tree rootTree, TreeNode rootNode)
  {
    for (Tree child : rootTree.getChildren())
    {
      TreeNode node = new DefaultTreeNode(new BusinessCalendar(findCalendar(child.getInfo())).getName(), rootNode);
      buildCalendarTree(child, node);
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
  
  public BusinessCalendar getActiveCalendar()
  {
    return activeCalendar;
  }
  
  public void setActiveCalendar(String selectedCalendar)
  {
    this.activeCalendar = new BusinessCalendar(managerBean.getSelectedIApplication().getBusinessCalendarSettings().findBusinessCalendarConfiguration(selectedCalendar));
  }
  
  public TreeNode getRootNode()
  {
    return treeRootNode;
  }
}
