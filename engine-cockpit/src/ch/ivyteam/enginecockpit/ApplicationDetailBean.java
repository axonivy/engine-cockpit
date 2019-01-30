package ch.ivyteam.enginecockpit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.ivy.application.IApplication;

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
  
  private IApplication getIApplication()
  {
    return managerBean.getIApplication(app.getId());
  }
}
