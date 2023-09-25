package ch.ivyteam.enginecockpit.application;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.application.model.AbstractActivity;
import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.application.model.ProcessModel;
import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.enginecockpit.commons.TreeView;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IProcessModel;
import ch.ivyteam.ivy.application.ReleaseState;
import ch.ivyteam.ivy.application.app.NewApplication;
import ch.ivyteam.ivy.security.ISecurityManager;

@ManagedBean
@ViewScoped
public class ApplicationBean extends TreeView<AbstractActivity> {
  private boolean processing;

  private AbstractActivity selectedActivity;

  private Application newApp;
  private boolean activateNewApp;

  private ManagerBean managerBean;

  public ApplicationBean() {
    managerBean = ManagerBean.instance();
    newApp = new Application();
    activateNewApp = true;
    selectedActivity = new Application();
    processing = false;
    reloadTree();
  }

  @Override
  protected void buildTree() {
    for (var app : managerBean.getIApplications()) {
      var activity = new Application(app, this);
      var node = new DefaultTreeNode<AbstractActivity>(activity, rootTreeNode);
      loadPmTree(app, node, activity);
      activity.getState().updateChildProblems(activity);
    }
  }

  private void loadPmTree(IApplication app, TreeNode<AbstractActivity> appNode, AbstractActivity parent) {
    for (var pm : app.getProcessModels()) {
      var activity = new ProcessModel(pm, this);
      parent.addChild(activity);
      var node = new DefaultTreeNode<AbstractActivity>(activity, appNode);
      loadPmvTree(pm, node, activity);
      activity.getState().updateChildProblems(activity);
    }
  }

  @SuppressWarnings("unused")
  private void loadPmvTree(IProcessModel pm, TreeNode<AbstractActivity> pmNode, AbstractActivity parent) {
    for (var pmv : pm.getProcessModelVersions()) {
      if (pmv.getReleaseState() != ReleaseState.DELETED) {
        var activity = new ProcessModelVersion(pmv, this);
        parent.addChild(activity);
        new DefaultTreeNode<AbstractActivity>(activity, pmNode);
      }
    }
  }

  @Override
  @SuppressWarnings("unused")
  protected void filterNode(TreeNode<AbstractActivity> node) {
    var activity = node.getData();
    if (StringUtils.containsIgnoreCase(activity.getName(), filter)) {
      new DefaultTreeNode<AbstractActivity>(activity, filteredTreeNode);
    }
  }

  public void reloadActivityStates() {
    processing = false;
    reloadNodeState(rootTreeNode.getChildren());
  }

  private void reloadNodeState(List<TreeNode<AbstractActivity>> nodes) {
    for (var node : nodes) {
      var activity = node.getData();
      activity.updateStats();
      if (processing == false) {
        processing = activity.getState().isProcessing();
      }
      reloadNodeState(node.getChildren());
      activity.getState().updateChildProblems(activity);
    }
  }

  public boolean isProcessing() {
    return processing;
  }

  public List<Application> getApplications() {
    return managerBean.getApplications();
  }

  public Application getNewApplication() {
    return newApp;
  }

  public boolean getActivateNewApp() {
    return activateNewApp;
  }

  public void setActivateNewApp(boolean activateNewApp) {
    this.activateNewApp = activateNewApp;
  }

  public void createNewApplication() {
    try {
      var securityContext = ISecurityManager.instance().securityContexts().get(newApp.getSecSystem());
      var appToCreate = NewApplication.create(newApp.getName())
              .active(activateNewApp)
              .securityContext(securityContext)
              .toNewApplication();
      managerBean.apps().create(appToCreate);
      reloadTree();
      managerBean.reloadApplications();
    } catch (RuntimeException ex) {
      FacesContext.getCurrentInstance().addMessage("applicationMessage",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
    }
  }

  public void setActiveActivity(AbstractActivity activity) {
    this.selectedActivity = activity;
  }

  public AbstractActivity getActiveActivity() {
    return selectedActivity;
  }
}
