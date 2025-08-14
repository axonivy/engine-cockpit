package ch.ivyteam.enginecockpit.system.administrators;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.monitor.log.LogView;
import ch.ivyteam.enginecockpit.setup.WizardBean.StepStatus;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.administrator.AdministratorService;

@ManagedBean
@ViewScoped
public class AdministratorBean extends StepStatus {

  private List<AdministratorDto> admins;
  private String filter;
  private AdministratorDto admin;
  private final AdministratorService service;

  public AdministratorBean() {
    service = AdministratorService.instance();
    load();
  }

  private void load() {
    admins = service.db().all().stream()
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
    service.config().save(admin.toAdministrator());
    load();
    Message.info().summary("'" + admin.getName() + "' added").show();
  }

  public void updateAdmin() {
    service.config().save(admin.toAdministrator());
    Message.info().summary("'" + admin.getName() + "' updated").show();
  }

  public void deleteAdmin() {
    service.db().delete(admin.getName());
    service.config().delete(admin.getName());
    load();
    Message.info().summary("'" + admin.getName() + "' deleted").show();
  }

  public boolean hasAdmins() {
    return !admins.isEmpty();
  }

  public boolean isDeleteButtonDisabled(AdministratorDto dto) {
    if (ISession.current().getSessionUserName().equalsIgnoreCase(dto.getName())) {
      return true;
    }
    if (dto.isExternal()) {
      return true;
    }
    return false;
  }

  public boolean hasIdentityProvider() {
    return service.hasIdentityProvider();
  }

  public boolean isSyncRunning() {
    return service.isSynchRunning();
  }

  public void triggerSynch() {
    service.triggerSynch();
  }

  public String getSynchLogUri() {
    return LogView.uri().fileName("usersynch").toUri();
  }

  public String getConfigurationUri() {
    return "security-detail.xhtml?securitySystemName=system";
  }

  public String getDeleteAdminDialogHeader() {
    return Ivy.cms().co("/administrators/DeleteAdminDialogHeader",
        Arrays.asList(admin != null ? admin.getName() : ""));
  }

  public String getDeleteAdminDialogMessage() {
    return Ivy.cms().co("/administrators/DeleteAdminDialogMessage",
        Arrays.asList(admin != null ? admin.getName() : ""));
  }
}
