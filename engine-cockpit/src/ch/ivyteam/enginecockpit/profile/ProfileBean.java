package ch.ivyteam.enginecockpit.profile;

import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.ISessionInternal;
import ch.ivyteam.ivy.security.administrator.AdministratorService;

@ManagedBean
@ViewScoped
public class ProfileBean {

  private ProfileDTO loggedInAdmin;
  private final AdministratorService service;

  public ProfileBean() {
    service = AdministratorService.instance();
  }

  public void load() {
    
    // Load the current logged in session username. Must match admin username
    var currentUserName = ISession.current().getSessionUserName();
    
    // Try to set loggedInAdmin by comparing username
    if (currentUserName != null) {
      service.db().all().stream()
        .filter(admin -> currentUserName.equalsIgnoreCase(admin.username())) // equalsIgnoreCase is fine, currentUserName is not-null here
        .findAny()
        .map(ProfileDTO::new)
        .ifPresentOrElse(
          adminDTO -> loggedInAdmin = adminDTO,
          () -> ResponseHelper.notFound("Could not find admin with name " + currentUserName)
        );
    }
  }

  public ISecurityContext getSecurityContext() {
    return ISecurityContextRepository.instance().getSystem();
  }

  // Getters and Setters
  public ProfileDTO getLoggedInAdmin() {
    return loggedInAdmin;
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

  // Use the admin service to save changes to the loggedInAdmin, based on
  // current state from the form
  public void save() {
    service.config().save(loggedInAdmin.toAdmin());
    Message.info().summary(Ivy.cm().content("/administrators/AdminUpdatedMessage").replace("name", loggedInAdmin.getUserName()).get()).show();
  }

}
