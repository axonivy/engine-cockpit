package ch.ivyteam.enginecockpit.system;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.security.model.User;
import ch.ivyteam.enginecockpit.setup.WizardBean.StepStatus;
import ch.ivyteam.ivy.security.administrator.AdministratorService;

@ManagedBean
@ViewScoped
public class AdministratorBean extends StepStatus {
  private final List<User> admins;
  private User editAdmin;

  public AdministratorBean() {
    admins = reloadAdmins();
  }

  private static List<User> reloadAdmins() {
    return AdministratorService.instance().allConfigured().stream()
        .map(User::new)
        .collect(Collectors.toList());
  }

  public List<User> getAdmins() {
    return admins;
  }

  public void setAdmin(User admin) {
    this.editAdmin = admin;
  }

  public void removeAdmin() {
    AdministratorService.instance().find(editAdmin.getName())
        .ifPresent(a -> AdministratorService.instance().remove(a));
    FacesContext.getCurrentInstance().addMessage("",
        new FacesMessage(FacesMessage.SEVERITY_INFO, "'" + editAdmin.getName() + "' removed successfully",
            ""));
    admins.remove(editAdmin);
  }

  public void addAdmin() {
    editAdmin = new User();
  }

  public User getAdmin() {
    return editAdmin;
  }

  @Override
  public boolean isStepOk() {
    return hasAdmins();
  }

  @Override
  public String getStepWarningMessage() {
    return "Please configure at least one admin!";
  }

  public void saveAdmin() {
    var message = new FacesMessage(FacesMessage.SEVERITY_INFO,
        "'" + editAdmin.getName() + "' modified successfully", "");
    if (!admins.contains(editAdmin)) {
      admins.add(editAdmin);
      message = new FacesMessage(FacesMessage.SEVERITY_INFO,
          "'" + editAdmin.getName() + "' added successfully", "");
    }
    FacesContext.getCurrentInstance().addMessage("", message);
    AdministratorService.instance().save(editAdmin.getAdmin());
  }

  public boolean hasAdmins() {
    return !admins.isEmpty();
  }
}
