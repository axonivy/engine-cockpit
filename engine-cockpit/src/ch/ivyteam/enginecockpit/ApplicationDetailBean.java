package ch.ivyteam.enginecockpit;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import ch.ivyteam.enginecockpit.model.Application;
import ch.ivyteam.enginecockpit.model.Property;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.system.IProperty;

@ManagedBean
@ViewScoped
public class ApplicationDetailBean
{
  private String appName;
  private Application app;
  private SecuritySystem security;
  private List<Property> properties;
  private List<String> environments;
  
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
    security = initSecuritySystem(appName);
    List<IProperty> configurationProperties = getIApplication().getConfigurationProperties();
    properties = configurationProperties.stream().filter(p -> !p.getValue().isEmpty())
            .map(p -> new Property(p)).collect(Collectors.toList());
    environments = managerBean.getIApplication(app.getId()).getEnvironmentsSortedByName()
            .stream().map(e -> e.getName()).collect(Collectors.toList());
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
    managerBean.reloadApplications();
    return "applications.xhtml?faces-redirect=true";
  }
  
  public int getSessionCount()
  {
    return getIApplication().getSecurityContext().getSessions().size();
  }
  
  public int getUsersCount()
  {
    return security.getUsersCount();
  }
  
  public long getCasesCount()
  {
    return app.getRunningCasesCount();
  }
  
  public int getPmCount()
  {
    return getIApplication().getProcessModels().stream()
            .mapToInt(pm -> pm.getProcessModelVersions().size()).sum();
  }
  
  public List<String> getEnvironments()
  {
    return environments;
  }
  
  public void saveApplicationInfos()
  {
    managerBean.getIApplication(app.getId()).setActiveEnvironment(app.getActiveEnv());
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess",
            new FacesMessage("Active Environment change saved"));
  }

  private IApplication getIApplication()
  {
    return managerBean.getIApplication(app.getId());
  }

  private SecuritySystem initSecuritySystem(String applicationName)
  {
    return new SecuritySystem(getSecuritySystemName(applicationName), Optional.of(getIApplication().getSecurityContext()), Arrays.asList(applicationName));
  }
  
  private String getSecuritySystemName(String appName)
  {
    return IConfiguration.get().get("Applications." + appName + ".SecuritySystem").orElse("");
  }

  public void setSecuritySystem(ValueChangeEvent event)
  {
    app.setSecuritySystem(event.getNewValue().toString());
    security = initSecuritySystem(getAppName());
  }
}
