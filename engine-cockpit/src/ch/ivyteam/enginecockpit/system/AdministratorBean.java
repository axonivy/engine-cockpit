package ch.ivyteam.enginecockpit.system;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.RestartBean;
import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.enginecockpit.setupwizard.WizardBean.StepStatus;
import ch.ivyteam.ivy.security.administrator.AdministratorService;

@ManagedBean
@ViewScoped
public class AdministratorBean extends StepStatus
{
  private List<User> admins;
  private User editAdmin;
  private RestartBean restartBean;
  
  public AdministratorBean()
  {
    admins = reloadAdmins();
    FacesContext context = FacesContext.getCurrentInstance();
    restartBean = context.getApplication().evaluateExpressionGet(context, "#{restartBean}",
            RestartBean.class);
  }
  
  private static List<User> reloadAdmins()
  {
    return AdministratorService.get().allConfigured().stream()
            .map(admin -> new User(admin))
            .collect(Collectors.toList());
  }
  
  public List<User> getAdmins()
  {
    return admins;
  }
  
  public void setAdmin(User admin)
  {
    this.editAdmin = admin;
  }
  
  public void removeAdmin()
  {
    AdministratorService.get().find(editAdmin.getName()).ifPresent(a -> AdministratorService.get().remove(a));
    FacesContext.getCurrentInstance().addMessage("",
            new FacesMessage(FacesMessage.SEVERITY_INFO, "'" + editAdmin.getName() + "' removed successfully", ""));
    admins.remove(editAdmin);
    restartBean.setRestartEngine(true);
  }
  
  public void addAdmin()
  {
    editAdmin = new User();
  }
  
  public User getAdmin()
  {
    return editAdmin;
  }
  
  @Override
  public boolean isStepOk()
  {
    return hasAdmins();
  }
  
  @Override
  public String getStepWarningMessage()
  {
    return "Please configure at least one admin!";
  }
  
  public void saveAdmin()
  {
    var message = new FacesMessage(FacesMessage.SEVERITY_INFO, "'" + editAdmin.getName() + "' modified successfully", "");
    if (!admins.contains(editAdmin))
    {
      admins.add(editAdmin);
      message = new FacesMessage(FacesMessage.SEVERITY_INFO, "'" + editAdmin.getName() + "' added successfully", "");
    }
    FacesContext.getCurrentInstance().addMessage("", message);
    AdministratorService.get().save(editAdmin.getAdmin());
    restartBean.setRestartEngine(true);
  }
  
  public boolean hasAdmins()
  {
    return !admins.isEmpty();
  }
}
