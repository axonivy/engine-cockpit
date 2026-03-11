package ch.ivyteam.enginecockpit.adminprofile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import ch.ivyteam.ivy.language.LanguageManager;
import ch.ivyteam.ivy.language.LanguageRepository;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.ISessionInternal;
import ch.ivyteam.ivy.security.administrator.AdministratorService;

@ManagedBean
@ViewScoped
public class AdministratorProfileBean {

  private List<AdministratorProfileDTO> admins;
  private AdministratorProfileDTO loggedInAdmin;
  private String isAdmin;
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

    // Debug print
    IO.println("Loaded admins: "
        + admins.stream().map(AdministratorProfileDTO::getUserName).collect(Collectors.joining(", ")));
    IO.println("Current session user: " + currentUserName);

    // Try to set loggedInAdmin and isAdmin by comparing usernames
    if (currentUserName != null) {
      admins.stream().filter(admin -> admin.getUserName().equalsIgnoreCase(currentUserName)).findFirst()
          .ifPresentOrElse((admin -> {
            IO.println("Found matching admin for current user: %s (%s)".formatted(admin.getUserName(), admin.getFullName()));
            this.loggedInAdmin = admin;
            this.isAdmin = "isAdmin";
          }), () -> {
            IO.println("No matching admin found for current user.");
            this.isAdmin = "notAdmin";
          });
    }
  }

  private ISessionInternal session() {
    return (ISessionInternal) ISession.current();
  }

  // Getters and Setters
  public AdministratorProfileDTO getLoggedInAdmin() {
    return loggedInAdmin;
  }

  public String getIsAdmin() {
    return isAdmin;
  }

  // Locales and Languages
  public List<Locale> getContentLanguages() {
    return locales(LanguageRepository::allContent);
  }

  public List<Locale> getFormattingLanguages() {
    return locales(LanguageRepository::allFormatting);
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

  private List<Locale> locales(Function<LanguageRepository, List<Locale>> loader) {
    var locales = loader.apply(LanguageManager.instance().languages(ISession.current().getSecurityContext())).stream()
        // .sorted(Comparator.comparing(this::localeToDisplayName,String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());
    var l = new ArrayList<Locale>();
    l.add(Locale.ROOT);
    l.addAll(locales);
    return l;
  }

  // Use AdministratorService to save changes to the loggedInAdmin, based on
  // current state from the form
  public void save() {
    service.config().save(loggedInAdmin.toAdministrator());
  }

}
