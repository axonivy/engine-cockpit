package ch.ivyteam.enginecockpit;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.enginecockpit.model.Property;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.system.IProperty;

@ManagedBean
@ViewScoped
public class ApplicationDetailBean
{
  private String appName;
  private Application app;
  private SecuritySystem security;
  private List<Property> properties;
  
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
    List<IProperty> configurationProperties = getIApplication().getConfigurationProperties();
    properties = configurationProperties.stream().filter(p -> !p.getValue().isEmpty())
            .map(p -> new Property(p)).collect(Collectors.toList());
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
  
  public List<Property> getProperties()
  {
    return properties;
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
