package ch.ivyteam.enginecockpit.system.administrators;

import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.administrator.Administrator;
import ch.ivyteam.ivy.security.administrator.AdministratorService;

public class AdministratorLanguageUpdater {

  private final AdministratorService service;

  public AdministratorLanguageUpdater(AdministratorService service) {
    this.service = service;
  }

  public static AdministratorLanguageUpdater instance() {
    return new AdministratorLanguageUpdater(AdministratorService.instance());
  }

  public void saveLanguage(Administrator admin, boolean managed) {
    if (managed) {
      saveToSystemUser(admin);
    } else {
      service.config().save(admin);
    }
  }

  private void saveToSystemUser(Administrator admin) {
    var system = ISecurityContextRepository.instance().getSystem();
    var user = system.users().find(admin.username());
    if (user == null) {
      return;
    }
    user.setLanguage(admin.language());
    user.setFormattingLanguage(admin.formattingLanguage());
  }
}
