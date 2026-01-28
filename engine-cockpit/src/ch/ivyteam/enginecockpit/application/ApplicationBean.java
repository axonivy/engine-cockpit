package ch.ivyteam.enginecockpit.application;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.Strings;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.application.model.AppTreeItem;
import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.enginecockpit.commons.TreeView;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.application.app.NewApplication;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityManager;

@ManagedBean
@ViewScoped
public class ApplicationBean extends TreeView<AppTreeItem> {
  private boolean processing;

  private AppTreeItem selectedActivity;

  private final Application newApp;
  private boolean activateNewApp;

  private final ManagerBean managerBean;

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
      var node = new DefaultTreeNode<AppTreeItem>(activity, rootTreeNode);
      loadPmvTree(app, node);
    }
  }

  private void loadPmvTree(IApplication app, TreeNode<AppTreeItem> pmNode) {
    for (var pmv : app.getProcessModelVersions().toList()) {      
      var activity = new ProcessModelVersion(pmv, this);
      new DefaultTreeNode<AppTreeItem>(activity, pmNode);
    }
  }

  @Override
  protected void filterNode(TreeNode<AppTreeItem> node) {
    var activity = node.getData();
    if (Strings.CI.contains(activity.getName(), filter)) {
      new DefaultTreeNode<>(activity, filteredTreeNode);
    }
  }

  public void reloadActivityStates() {
    processing = false;
    reloadNodeState(rootTreeNode.getChildren());
  }

  private void reloadNodeState(List<TreeNode<AppTreeItem>> nodes) {
    for (var node : nodes) {
      var activity = node.getData();
      activity.updateStats();
      if (!processing) {
        processing = activity.getState().isProcessing();
      }
      reloadNodeState(node.getChildren());
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
          .toNewApplication();
      IApplicationRepository.of(securityContext).create(appToCreate);
      reloadTree();
      managerBean.reloadApplications();
    } catch (RuntimeException ex) {
      FacesContext.getCurrentInstance().addMessage("applicationMessage",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/common/Error"), ex.getMessage()));
    }
  }

  public void setActiveActivity(AppTreeItem activity) {
    this.selectedActivity = activity;
  }

  public AppTreeItem getActiveActivity() {
    return selectedActivity;
  }

  @Override
  protected String dataIdentifier(AppTreeItem data) {
    var id = Long.toString(data.getApplicationId());
    if (data.isPmv()) {
      id += "-" + Long.toString(data.getProcessModelVersionId());
    }
    return id;
  }
}
