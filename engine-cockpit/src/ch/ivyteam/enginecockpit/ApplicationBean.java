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
  private boolean operating;
  
  private String deleteName;
  private long deleteNameAppId;
  private String deleteType;
  
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
    operating = false;
    reloadEmailSettings();
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
      node.setExpanded(true);
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
  
  public void createNewApplication()
  {
    IApplication app = managerBean.getManager().createApplication(newApp.getName());
    app.setDescription(newApp.getDesc());
    reloadActivities();
    managerBean.reloadApplications();
  }
  
  public void reloadEmailSettings()
  {
    emailSettings = new EmailSettings(managerBean.getSelectedIApplication());
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
  
  public void deleteConfirm(long appId, String name, String type)
  {
    this.deleteNameAppId = appId;
    this.deleteName = name;
    this.deleteType = type;
  }
  
  public void delete()
  {
    if (deleteType.equals(AbstractActivity.APP))
    {
      deleteApp();
    }
    else if (deleteType.equals(AbstractActivity.PM))
    {
      deletePm(managerBean.getManager().findApplication(deleteNameAppId));
    }
    else
    {
      deletePmv(managerBean.getManager().findApplication(deleteNameAppId));
    }
    FacesContext.getCurrentInstance().addMessage("applicationMessage",
            new FacesMessage("'" + deleteName + "' deleted successfully"));
    this.deleteName = "";
    this.deleteNameAppId = -1;
    reloadActivities();
  }
  
  public void deleteApp()
  {
    managerBean.getManager().deleteApplication(deleteName);
  }
  
  public void deletePm(IApplication app)
  {
    app.deleteProcessModel(deleteName);
  }
  
  public void deletePmv(IApplication app)
  {
    app.findProcessModelVersion(deleteName).delete();
  }
  
  public String getDeleteName()
  {
    return deleteName;
  }
  
  public String getDeleteType()
  {
    return deleteType;
  }
  
}
