package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.enginecockpit.util.SynchronizationLogger;

@ManagedBean
@ViewScoped
public class SecurityBean
{
  private List<SecuritySystem> systems;
  
  private SynchronizationLogger synchronizationLogger = new SynchronizationLogger();

  private ApplicationBean applicationBean;

  public SecurityBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    applicationBean = context.getApplication().evaluateExpressionGet(context, "#{applicationBean}",
            ApplicationBean.class);
    systems = applicationBean.getIApplicaitons().stream()
            .map(app -> new SecuritySystem(app.getSecurityContext(), app.getName()))
            .collect(Collectors.toList());
  }

  public List<SecuritySystem> getSecuritySystems()
  {
    return systems;
  }

  public void triggerSynchronization(String appName)
  {
    applicationBean.getManager().findApplication(appName).getSecurityContext()
            .triggerSynchronization(synchronizationLogger);
  }

  public boolean isSyncRunning(String appName)
  {
    return applicationBean.getManager().findApplication(appName).getSecurityContext().isSynchronizationRunning();
  }
  
  public boolean isAnySyncRunningOrNewLog()
  {
    return applicationBean.getIApplicaitons().stream()
            .filter(app -> app.getSecurityContext().isSynchronizationRunning() == true)
            .findAny().isPresent() || synchronizationLogger.isNewLogAwailabe();
  }
  
  public String getLogs()
  {
    StringBuilder sb = new StringBuilder();
    synchronizationLogger.getSynchronizationLogMessages().stream().forEach(msg -> sb.append(msg).append("\n"));
    if(sb.length() > 2) {
      sb.setLength(sb.length() - 2);
    }
    return sb.toString();
  }

}
