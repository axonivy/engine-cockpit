package ch.ivyteam.enginecockpit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.Ivy;

@ManagedBean
@ViewScoped
public class ApplicationDetailBean
{
  private String appName;
  private Application app;
  private SecuritySystem security;
  
  private ManagerBean managerBean;
  
  public ApplicationDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }
  
  public void setAppName(String appName)
  {
    this.appName = appName;
    app = managerBean.getApplications().stream().filter(a -> a.getName().equals(appName)).findFirst().get();
    security = new SecuritySystem(getIApplication().getSecurityContext(), appName);
  }
  
  public String getAppName()
  {
    return appName;
  }
  
  public Application getApplication()
  {
    return app;
  }
  
  public SecuritySystem getSecuritySystem() 
  {
    return security;
  }
  
  public String deleteApplication()
  {
    managerBean.getManager().deleteApplication(appName);
    return "applications.xhtml?faces-redirect=true";
  }
  
  public int getSessionCount()
  {
    return getIApplication().getSecurityContext().getSessions().size();
  }
  
  public int getUsersCount()
  {
    return getIApplication().getSecurityContext().getUsers().size();
  }
  
  public long getCasesCount()
  {
    return getIApplication().getProcessModels().stream()
            .flatMap(pm -> pm.getProcessModelVersions().stream())
            .mapToLong(pmv -> Ivy.wf().getRunningCasesCount(pmv)).sum();
  }
  
  public int getPmCount()
  {
    return getIApplication().getProcessModels().stream()
            .mapToInt(pm -> pm.getProcessModelVersions().size()).sum();
  }
  
  private IApplication getIApplication()
  {
    return managerBean.getIApplication(app.getId());
  }
}
