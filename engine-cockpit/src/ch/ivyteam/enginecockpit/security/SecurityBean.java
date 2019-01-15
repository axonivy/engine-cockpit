package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ApplicationBean;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.ivy.security.ISecurityContext;

@ManagedBean
@ViewScoped
public class SecurityBean
{
  private List<SecuritySystem> systems;

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
    applicationBean.getManager().findApplication(appName).getSecurityContext().triggerSynchronization();
  }

  public boolean syncRunning(long securityContextId)
  {
    Optional<ISecurityContext> context = applicationBean.getIApplicaitons().stream()
            .map(app -> app.getSecurityContext())
            .filter(c -> c.getId() == securityContextId)
            .findAny();
    if (context.isPresent())
    {
      return context.get().isSynchronizationRunning();
    }
    return false;
  }

}
