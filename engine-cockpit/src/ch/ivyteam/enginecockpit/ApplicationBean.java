package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.ivyteam.enginecockpit.model.AbstractActivity;
import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.enginecockpit.model.EmailSettings;
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
  
  private String deletePmvName;
  private long deletePmvNameAppId;
  
  private Application newApp;
  private EmailSettings emailSettings;
  
  private ManagerBean managerBean;
  
  public ApplicationBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    reloadActivities();
    newApp = new Application();
    emailSettings = new EmailSettings(managerBean.getSelectedIApplication());
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
    for (IApplication app : managerBean.getIApplicaitons())
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
    reloadNodeState(rootTreeNode.getChildren());
  }
  
  private void reloadNodeState(List<TreeNode> nodes)
  {
    for (TreeNode node : nodes)
    {
      ((AbstractActivity) node.getData()).updateStats();
      reloadNodeState(node.getChildren());
    }
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
  
  public void createNewApplication()
  {
    IApplication app = managerBean.getManager().createApplication(newApp.getName());
    app.setDescription(newApp.getDesc());
    //TODO: remove
    IProcessModel pm = app.createProcessModel("test", "testProcessModel");
    IProcessModelVersion pmv = pm.createProcessModelVersion("test", "test PMV", "Developer", "localhost", 1);
    pmv.release();
    pm.createProcessModelVersion("test", "test PMV", "Developer", "localhost", 2);
    reloadActivities();
    managerBean.reloadApplications();
  }
  
  public EmailSettings getEmailSettings()
  {
    return emailSettings;
  }
  
  public void saveEmailSettings()
  {
    IApplication app = managerBean.getSelectedIApplication();
    Locale language = emailSettings.getLanguageLocale();
    app.setDefaultEMailLanguage(language);
    app.setDefaultEMailNotifcationSettings(
            emailSettings.saveEmailSettings(app.getDefaultEMailNotifcationSettings()));
    FacesContext.getCurrentInstance().addMessage("emailSaveSuccess",
            new FacesMessage("User email changes saved"));
  }
  
  public void deletePmvConfirm(long appId, String pmvName)
  {
    this.deletePmvNameAppId = appId;
    this.deletePmvName = pmvName;
  }
  
  public void deletePmv()
  {
    managerBean.getManager().findApplication(deletePmvNameAppId).findProcessModelVersion(deletePmvName).delete();
    FacesContext.getCurrentInstance().addMessage("applicationMessage",
            new FacesMessage("Pmv '" + deletePmvName + "' deleted successfully"));
    this.deletePmvName = "";
    this.deletePmvNameAppId = -1;
    reloadActivities();
  }
  
  public String getDeletePmvName()
  {
    return deletePmvName;
  }
  
}
