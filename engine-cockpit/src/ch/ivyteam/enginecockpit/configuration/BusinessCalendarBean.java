package ch.ivyteam.enginecockpit.configuration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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
public class BusinessCalendarBean extends TreeView {
  private ManagerBean managerBean;
  private BusinessCalendar activeCalendar;
  private String calendarSelection;
  private String environmentCalendar;

  public BusinessCalendarBean() {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    environmentCalendar = managerBean.getSelectedIApplication().getActualEnvironment().getBusinessCalendar()
            .getName();
    reloadTree();
  }

  @Override
  protected void buildTree() {
    var rootTree = managerBean.getSelectedIApplication().getBusinessCalendarSettings()
            .getAllBusinessCalendarConfigurations();
    var node = new DefaultTreeNode(findCalendar(rootTree.getInfo()), rootTreeNode);
    node.setExpanded(true);
    buildCalendarTree(rootTree, node);
  }

  private void buildCalendarTree(Tree rootTree, TreeNode rootNode) {
    for (var child : rootTree.getChildren()) {
      var node = new DefaultTreeNode(findCalendar(child.getInfo()), rootNode);
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

  public void setActiveCalendar() {
    this.activeCalendar = new BusinessCalendar(getBusinessCalendarConfiguration(calendarSelection));
  }

  public void setCalendarSelection(String calendarSelection) {
    this.calendarSelection = calendarSelection;
    setActiveCalendar();
  }

  public String getCalendarSelection() {
    return calendarSelection;
  }

  public String getEnvironmentCalendar() {
    return environmentCalendar;
  }

  @Override
  @SuppressWarnings("unused")
  protected void filterNode(TreeNode node) {
    var calendar = (BusinessCalendar) node.getData();
    if (calendar.getName().toLowerCase().contains(filter)) {
      new DefaultTreeNode(calendar, filteredTreeNode);
    }
  }

  private IBusinessCalendarConfiguration getBusinessCalendarConfiguration(String calendarName) {
    var calConfiguration = managerBean.getSelectedIApplication().getBusinessCalendarSettings()
            .findBusinessCalendarConfiguration(calendarName);
    if (calConfiguration == null) {
      calConfiguration = managerBean.getSelectedIApplication().getBusinessCalendarSettings()
              .getRootBusinessCalendarConfiguration();
    }
    return calConfiguration;
  }
}
