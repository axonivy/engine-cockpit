package ch.ivyteam.enginecockpit.adminprofile;

import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.ISessionInternal;
import ch.ivyteam.ivy.security.administrator.AdministratorService;

@ManagedBean
@ViewScoped
public class AdministratorProfileBean {

  private AdministratorProfileDTO loggedInAdmin;
  private boolean isAdmin;
  private final AdministratorService service;

  public AdministratorProfileBean() {
    service = AdministratorService.instance();
    isAdmin = false; // default is false until proven
    load();
  }

  private void load() {
    
    // Load the current logged in session username. Must match admin username
    var currentUserName = ISession.current().getSessionUserName();
    
    // Try to set loggedInAdmin and isAdmin by comparing usernames
    if (currentUserName != null) {
      service.db().all().stream()
        .map(AdministratorProfileDTO::new)
        .filter(adminDTO -> adminDTO.getUserName().equalsIgnoreCase(currentUserName))
        .findFirst()
        .ifPresent(adminDTO -> {
          this.loggedInAdmin = adminDTO;
          this.isAdmin = true;
          }
        );
    }
  }

  public ISecurityContext getSecurityContext() {
    return ISecurityContextRepository.instance().getSystem();
  }

  // Getters and Setters
  public AdministratorProfileDTO getLoggedInAdmin() {
    return loggedInAdmin;
  }

  public boolean getIsAdmin() {
    return isAdmin;
  }

  // Current language and formatting
  private ISessionInternal session() {
    return (ISessionInternal) ISession.current();
  }

  public Locale getCurrentContentLocale() {
    return session().getContentLocale();
  }

  public String getCurrentContentLocaleSource() {
    return session().getContentLocaleInfo().source();
  }

  public Locale getCurrentFormattingLocale() {
    return session().getFormattingLocale();
  }

  public String getCurrentFormattingLocaleSource() {
    return session().getFormattingLocaleInfo().source();
  }

  // Use AdministratorService to save changes to the loggedInAdmin, based on
  // current state from the form
  public void save() {
    service.config().save(loggedInAdmin.toAdministrator());
    Message.info().summary(Ivy.cm().content("/administrators/AdminUpdatedMessage").replace("name", loggedInAdmin.getUserName()).get()).show();
  }

}
