package ch.ivyteam.enginecockpit;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.model.BusinessCalendar;
import ch.ivyteam.ivy.application.calendar.IBusinessCalendarConfiguration;
import ch.ivyteam.ivy.scripting.objects.Tree;

@ManagedBean
@ViewScoped
public class BusinessCalendarBean
{
  private ManagerBean managerBean;
  private TreeNode treeRootNode;
  private BusinessCalendar activeCalendar;
  private String calendarSelection;

  private String filter = "";
  private TreeNode filteredTreeRootNode;

  public BusinessCalendarBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);
    reloadBusinessCalendar();
  }
  
  public void reloadBusinessCalendar()
  {
    treeRootNode = new DefaultTreeNode("Calendars", null);
    loadCalendarTree(treeRootNode);
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

  public BusinessCalendar getActiveCalendar()
  {
    return activeCalendar;
  }
  
  public void setActiveCalendar()
  {
    this.activeCalendar = new BusinessCalendar(managerBean.getSelectedIApplication().getBusinessCalendarSettings().findBusinessCalendarConfiguration(calendarSelection));
  }
  
  public void setCalendarSelection(String calendarSelection)
  {
    this.calendarSelection = calendarSelection;
  }
  
  public String getCalendarSelection()
  {
    return calendarSelection;
  }
  
  public TreeNode getRootNode()
  {
    if (filter.isEmpty())
    {
      return treeRootNode;
    }
    return filteredTreeRootNode;
  }
  
  public String getFilter()
  {
    return filter;
  }
  
  public void setFilter(String filter)
  {
    this.filter = filter.toLowerCase();
    filteredTreeRootNode = new DefaultTreeNode("Filtered calendars", null);
    filterTreeRootNode(treeRootNode.getChildren());
  }
  
  @SuppressWarnings("unused")
  private void filterTreeRootNode(List<TreeNode> nodes)
  {
    for (TreeNode node : nodes)
    {
      String calendar = (String) node.getData();
      if (calendar.toLowerCase().contains(filter))
      {
        new DefaultTreeNode(node, filteredTreeRootNode);
      }
      filterTreeRootNode(node.getChildren());
    }
  }
  
}
