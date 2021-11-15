package ch.ivyteam.enginecockpit.configuration;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.ManagerBean;
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
  private String environmentCalendar;

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
    environmentCalendar = managerBean.getSelectedIApplication().getActualEnvironment().getBusinessCalendar().getName();
    loadCalendarTree(treeRootNode);
  }

  private void loadCalendarTree(TreeNode rootNode)
  {
    Tree rootTree = managerBean.getSelectedIApplication().getBusinessCalendarSettings().getAllBusinessCalendarConfigurations();
    TreeNode node = new DefaultTreeNode(findCalendar(rootTree.getInfo()), rootNode);
    node.setExpanded(true);
    buildCalendarTree(rootTree, node);
  }
  
  private void buildCalendarTree(Tree rootTree, TreeNode rootNode)
  {
    for (Tree child : rootTree.getChildren())
    {
      TreeNode node = new DefaultTreeNode(findCalendar(child.getInfo()), rootNode);
      node.setExpanded(true);
      buildCalendarTree(child, node);
    }
  }

  private BusinessCalendar findCalendar(String name)
  {
    BusinessCalendar businessCalendar = new BusinessCalendar(getBusinessCalendarConfiguration(name));
    managerBean.getSelectedIApplication().getEnvironments().stream()
            .filter(e -> StringUtils.equals(e.getBusinessCalendar().getName(), businessCalendar.getName()))
            .forEach(e -> businessCalendar.addEnvironment(e.getName()));
    return businessCalendar;
  }
  
  public BusinessCalendar getActiveCalendar()
  {
    return activeCalendar;
  }
  
  public void setActiveCalendar()
  {
    this.activeCalendar = new BusinessCalendar(getBusinessCalendarConfiguration(calendarSelection));
  }
  
  public void setCalendarSelection(String calendarSelection)
  {
    this.calendarSelection = calendarSelection;
    setActiveCalendar();
  }
  
  public String getCalendarSelection()
  {
    return calendarSelection;
  }
  
  public String getEnvironmentCalendar()
  {
    return environmentCalendar;
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
      BusinessCalendar calendar = (BusinessCalendar) node.getData();
      if (StringUtils.containsIgnoreCase(calendar.getName(), filter))
      {
        new DefaultTreeNode(calendar, filteredTreeRootNode);
      }
      filterTreeRootNode(node.getChildren());
    }
  }
  
  private IBusinessCalendarConfiguration getBusinessCalendarConfiguration(String calendarName)
  {
    IBusinessCalendarConfiguration calConfiguration = managerBean.getSelectedIApplication().getBusinessCalendarSettings().findBusinessCalendarConfiguration(calendarName);
    
    if (calConfiguration == null)
    {
      calConfiguration = managerBean.getSelectedIApplication().getBusinessCalendarSettings().getRootBusinessCalendarConfiguration();
    }
    
    return calConfiguration;
  }
}
