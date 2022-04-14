package ch.ivyteam.enginecockpit.configuration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.commons.TreeView;
import ch.ivyteam.enginecockpit.configuration.model.BusinessCalendar;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.calendar.IBusinessCalendarConfiguration;
import ch.ivyteam.ivy.scripting.objects.Tree;

@ManagedBean
@ViewScoped
@SuppressWarnings("removal")
public class BusinessCalendarBean extends TreeView<BusinessCalendar> {

  private ManagerBean managerBean;
  private BusinessCalendar activeCalendar;
  private String calendarSelection;
  private String environmentCalendar;

  public BusinessCalendarBean() {
    managerBean = ManagerBean.instance();
    environmentCalendar = managerBean.getSelectedIApplication().getActualEnvironment().getBusinessCalendar().getName();
    reloadTree();
  }

  @Override
  protected void buildTree() {
    var rootTree = managerBean.getSelectedIApplication().getBusinessCalendarSettings()
            .getAllBusinessCalendarConfigurations();
    var node = new DefaultTreeNode<>(findCalendar(rootTree.getInfo()), rootTreeNode);
    node.setExpanded(true);
    buildCalendarTree(rootTree, node);
  }

  private void buildCalendarTree(Tree rootTree, TreeNode<BusinessCalendar> rootNode) {
    for (var child : rootTree.getChildren()) {
      var node = new DefaultTreeNode<>(findCalendar(child.getInfo()), rootNode);
      node.setExpanded(true);
      buildCalendarTree(child, node);
    }
  }

  private BusinessCalendar findCalendar(String name) {
    var businessCalendar = new BusinessCalendar(getBusinessCalendarConfiguration(name));
    managerBean.getSelectedIApplication().getEnvironments().stream()
            .filter(e -> StringUtils.equals(e.getBusinessCalendar().getName(), businessCalendar.getName()))
            .forEach(e -> businessCalendar.addEnvironment(e.getName()));
    return businessCalendar;
  }

  public BusinessCalendar getActiveCalendar() {
    return activeCalendar;
  }

  public void setCalendarSelection(String calendarSelection) {
    this.calendarSelection = calendarSelection;
  }

  public String getCalendarSelection() {
    return calendarSelection;
  }

  public void onload() {
    var config = getBusinessCalendarConfiguration(calendarSelection);
    this.activeCalendar = new BusinessCalendar(config);
  }

  public String getEnvironmentCalendar() {
    return environmentCalendar;
  }

  @Override
  @SuppressWarnings("unused")
  protected void filterNode(TreeNode<BusinessCalendar> node) {
    var calendar = node.getData();
    if (StringUtils.containsIgnoreCase(calendar.getName(), filter)) {
      new DefaultTreeNode<>(calendar, filteredTreeNode);
    }
  }

  private IBusinessCalendarConfiguration getBusinessCalendarConfiguration(String calendarName) {
    var app = managerBean.getSelectedIApplication();
    var settings = app.getBusinessCalendarSettings();
    var calConfiguration = settings.findBusinessCalendarConfiguration(calendarName);
    if (calConfiguration == null) {
      calConfiguration = settings.getRootBusinessCalendarConfiguration();
    }
    return calConfiguration;
  }
}
