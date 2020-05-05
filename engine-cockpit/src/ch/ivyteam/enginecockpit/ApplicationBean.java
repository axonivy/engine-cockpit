package ch.ivyteam.enginecockpit;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.model.AbstractActivity;
import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.enginecockpit.model.ProcessModel;
import ch.ivyteam.enginecockpit.model.ProcessModelVersion;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.application.ReleaseState;

@ManagedBean
@ViewScoped
public class ApplicationBean
{
  private TreeNode rootTreeNode;
  private TreeNode filteredRootTreeNode;
  private String filter = "";
  private boolean operating;
  
  private AbstractActivity selectedActivity;
  
  private Application newApp;
  private boolean activateNewApp;
  
  private ManagerBean managerBean;
  
  public ApplicationBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    reloadActivities();
    newApp = new Application();
    activateNewApp = true;
    selectedActivity = new Application();
    operating = false;
  }
  
  public TreeNode getActivities()
  {
    if (filter.isEmpty())
    {
      return rootTreeNode;
    }
    return filteredRootTreeNode;
  }
  
  public void reloadActivities()
  {
    filter = "";
    rootTreeNode = new DefaultTreeNode("Activities", null);
    loadApplicationTree(rootTreeNode);
  }
  
  private void loadApplicationTree(TreeNode rootNode)
  {
    for (IApplication app : managerBean.getIApplications())
    {
      TreeNode node = new DefaultTreeNode(new Application(app, this), rootNode);
      loadPmTree(app, node);
    }
  }
  
  private void loadPmTree(IApplication app, TreeNode appNode)
  {
    for (IProcessModel pm : app.getProcessModels())
    {
      TreeNode node = new DefaultTreeNode(new ProcessModel(pm, this), appNode);
      loadPmvTree(pm, node);
    }
  }
  
  @SuppressWarnings("unused")
  private void loadPmvTree(IProcessModel pm, TreeNode pmNode)
  {
    for (IProcessModelVersion pmv : pm.getProcessModelVersions())
    {
      if (pmv.getReleaseState() != ReleaseState.DELETED)
      {
        new DefaultTreeNode(new ProcessModelVersion(pmv, this), pmNode);
      }
    }
  }
  
  @SuppressWarnings("unused")
  private void filterRootTreeNode(List<TreeNode> nodes)
  {
    for (TreeNode node : nodes)
    {
      AbstractActivity activity = (AbstractActivity) node.getData();
      if (StringUtils.startsWithIgnoreCase(activity.getName(), filter))
      {
        new DefaultTreeNode(activity, filteredRootTreeNode);
      }
      filterRootTreeNode(node.getChildren());
    }
  }
  
  public void reloadActivityStates()
  {
    operating = false;
    reloadNodeState(rootTreeNode.getChildren());
  }
  
  private void reloadNodeState(List<TreeNode> nodes)
  {
    for (TreeNode node : nodes)
    {
      AbstractActivity activity = (AbstractActivity) node.getData();
      activity.updateStats();
      if (operating == false)
      {
        operating = activity.isOperating();
      }
      reloadNodeState(node.getChildren());
    }
  }
  
  public boolean isOperating()
  {
    return operating;
  }
  
  public String getFilter()
  {
    return filter;
  }

  public void setFilter(String filter)
  {
    this.filter = filter;
    filteredRootTreeNode = new DefaultTreeNode("Filtered activities", null);
    filterRootTreeNode(rootTreeNode.getChildren());
  }
  
  public List<Application> getApplications()
  {
    return managerBean.getApplications();
  }
  
  public Application getNewApplication()
  {
    return newApp;
  }
  
  public boolean getActivateNewApp()
  {
    return activateNewApp;
  }
  
  public void setActivateNewApp(boolean activateNewApp)
  {
    this.activateNewApp = activateNewApp;
  }
  
  public void createNewApplication()
  {
    try
    {
      IApplication app = managerBean.getManager().createApplication(newApp.getName());
      app.setDescription(newApp.getDesc());
      if (activateNewApp)
      {
        app.activate();
      }
      reloadActivities();
      managerBean.reloadApplications();
    }
    catch (RuntimeException ex)
    {
      FacesContext.getCurrentInstance().addMessage("applicationMessage",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
    }
  }
  
  public void setActiveActivity(AbstractActivity activity)
  {
    this.selectedActivity = activity;
  }
  
  public AbstractActivity getActiveActivity()
  {
    return selectedActivity;
  }
  
  public void delete()
  {
    if (AbstractActivity.APP.equals(selectedActivity.getActivityType()))
    {
      deleteApp();
    }
    else if (AbstractActivity.PM.equals(selectedActivity.getActivityType()))
    {
      deletePm(managerBean.getManager().findApplication(selectedActivity.getApplicationId()));
    }
    else
    {
      deletePmv(managerBean.getManager().findApplication(selectedActivity.getApplicationId()));
    }
    FacesContext.getCurrentInstance().addMessage("applicationMessage",
            new FacesMessage("'" + selectedActivity.getName() + "' deleted successfully", ""));
    selectedActivity = new Application();
    reloadActivities();
    managerBean.reloadApplications();
  }
  
  public void deleteApp()
  {
    managerBean.getManager().deleteApplication(selectedActivity.getName());
  }
  
  public void deletePm(IApplication app)
  {
    app.deleteProcessModel(selectedActivity.getName());
  }
  
  public void deletePmv(IApplication app)
  {
    app.findProcessModelVersion(selectedActivity.getName()).delete();
  }
  
}
