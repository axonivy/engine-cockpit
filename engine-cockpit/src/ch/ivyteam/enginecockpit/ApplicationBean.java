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
public class ApplicationBean extends TreeView
{
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
    newApp = new Application();
    activateNewApp = true;
    selectedActivity = new Application();
    operating = false;
    reloadTree();
  }
  
  @Override
  protected void buildTree()
  {
    for (IApplication app : managerBean.getIApplications())
    {
      TreeNode node = new DefaultTreeNode(new Application(app, this), rootTreeNode);
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
  
  @Override
  @SuppressWarnings("unused")
  protected void filterNode(TreeNode node)
  {
    AbstractActivity activity = (AbstractActivity) node.getData();
    if (StringUtils.startsWithIgnoreCase(activity.getName(), filter))
    {
      new DefaultTreeNode(activity, filteredTreeNode);
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
      reloadTree();
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
    reloadTree();
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
