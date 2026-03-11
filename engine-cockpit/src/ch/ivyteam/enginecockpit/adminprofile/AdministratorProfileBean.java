package ch.ivyteam.enginecockpit.adminprofile;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

  private List<AdministratorProfileDTO> admins;
  private AdministratorProfileDTO loggedInAdmin;
  private boolean isAdmin;
  private final AdministratorService service;

  public AdministratorProfileBean() {
    service = AdministratorService.instance();
    load();
  }

  private void load() {
    // Load all admins from the database
    admins = service.db().all().stream()
        .map(AdministratorProfileDTO::new)
        .collect(Collectors.toList());
    
    // Load the current logged in session user and get username
    var currentUserName = ISession.current().getSessionUserName();

    // Try to set loggedInAdmin and isAdmin by comparing usernames
    if (currentUserName != null) {
      admins.stream().filter(admin -> admin.getUserName().equalsIgnoreCase(currentUserName)).findFirst()
          .ifPresentOrElse((admin -> {
            this.loggedInAdmin = admin;
            this.isAdmin = true;
          }), () -> {
            this.isAdmin = false;
          });
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
