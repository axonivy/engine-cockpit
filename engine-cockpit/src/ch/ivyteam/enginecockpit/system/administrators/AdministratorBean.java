package ch.ivyteam.enginecockpit.system.administrators;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.setup.WizardBean.StepStatus;
import ch.ivyteam.ivy.security.administrator.AdministratorService;

@ManagedBean
@ViewScoped
public class AdministratorBean extends StepStatus {

  private List<AdministratorDto> admins;
  private String filter;
  private AdministratorDto admin;

  public AdministratorBean() {
    load();
  }

  private void load() {
    admins = AdministratorService.instance().all().stream()
        .map(AdministratorDto::new)
        .collect(Collectors.toList());
  }

  public List<AdministratorDto> getAdmins() {
    return admins;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public void setAdmin(AdministratorDto admin) {
    this.admin = admin;
  }

  public void addAdmin() {
    admin = new AdministratorDto();
  }

  public AdministratorDto getAdmin() {
    return admin;
  }

  @Override
  public boolean isStepOk() {
    return hasAdmins();
  }

  @Override
  public String getStepWarningMessage() {
    return "Please configure at least one admin!";
  }

  public void createAdmin() {
    AdministratorService.instance().save(admin.toAdministrator());
    load();
    Message.info().summary("'" + admin.getName() + "' added").show();
  }
  
  public void updateAdmin() {
    AdministratorService.instance().save(admin.toAdministrator());
    Message.info().summary("'" + admin.getName() + "' updated").show();
  }
  
  public void deleteAdmin() {
    AdministratorService.instance().remove(admin.getName());
    load();
    Message.info().summary("'" + admin.getName() + "' deleted").show();
  }
  
  public boolean hasAdmins() {
    return !admins.isEmpty();
  }
}
