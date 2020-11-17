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
import ch.ivyteam.ivy.application.ReleaseState;

@ManagedBean
@ViewScoped
public class ApplicationBean extends TreeView
{
  private boolean processing;

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
    processing = false;
    reloadTree();
  }

  @Override
  protected void buildTree()
  {
    for (var app : managerBean.getIApplications())
    {
      var activity = new Application(app, this);
      var node = new DefaultTreeNode(activity, rootTreeNode);
      loadPmTree(app, node, activity);
      activity.getState().updateChildProblems(activity);
    }
  }

  private void loadPmTree(IApplication app, TreeNode appNode, AbstractActivity parent)
  {
    for (var pm : app.getProcessModels())
    {
      var activity = new ProcessModel(pm, this);
      parent.addChild(activity);
      var node = new DefaultTreeNode(activity, appNode);
      loadPmvTree(pm, node, activity);
      activity.getState().updateChildProblems(activity);
    }
  }

  @SuppressWarnings("unused")
  private void loadPmvTree(IProcessModel pm, TreeNode pmNode, AbstractActivity parent)
  {
    for (var pmv : pm.getProcessModelVersions())
    {
      if (pmv.getReleaseState() != ReleaseState.DELETED)
      {
        var activity = new ProcessModelVersion(pmv, this);
        parent.addChild(activity);
        new DefaultTreeNode(activity, pmNode);
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
    processing = false;
    reloadNodeState(rootTreeNode.getChildren());
  }

  private void reloadNodeState(List<TreeNode> nodes)
  {
    for (TreeNode node : nodes)
    {
      AbstractActivity activity = (AbstractActivity) node.getData();
      activity.updateStats();
      if (processing == false)
      {
        processing = activity.getState().isProcessing();
      }
      reloadNodeState(node.getChildren());
      activity.getState().updateChildProblems(activity);
    }
  }

  public boolean isProcessing()
  {
    return processing;
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

}
