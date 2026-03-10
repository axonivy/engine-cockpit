package ch.ivyteam.enginecockpit.adminprofile;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.Comparator;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.monitor.log.LogView;
import ch.ivyteam.enginecockpit.setup.WizardBean.StepStatus;
import ch.ivyteam.enginecockpit.system.administrators.AdministratorDto;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.language.LanguageManager;
import ch.ivyteam.ivy.language.LanguageRepository;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.ISessionInternal;
import ch.ivyteam.ivy.security.administrator.AdministratorService;

@ManagedBean
@ViewScoped
public class AdministratorProfileBean {

  private List<AdministratorDto> admins;
  private AdministratorDto loggedInAdmin;
  private final AdministratorService service;

  public AdministratorProfileBean() {
    service = AdministratorService.instance();
    load();
  }

  private void load() {
    // Load all admins from the database
    admins = service.db().all().stream()
        .map(AdministratorDto::new)
        .collect(Collectors.toList());

    // Try to set loggedInAdmin by username
    var currentUserName = ISession.current().getSessionUserName();
    if (currentUserName != null) {
      admins.stream().filter(admin -> admin.getName().equalsIgnoreCase(currentUserName)).findFirst()
          .ifPresent(admin -> this.loggedInAdmin = admin);
    }
    IO.println("Loaded admins: " + admins.stream().map(AdministratorDto::getName).collect(Collectors.joining(", ")));
    IO.println("Current session user: " + currentUserName);

    if (loggedInAdmin == null) {
      IO.println("No matching admin found for current user.");
    } else {
      IO.println("Logged in admin: " + loggedInAdmin.getName());
    }
  }

  private ISessionInternal session() {
    return (ISessionInternal) ISession.current();
  }

  // Getters and Setters
  public AdministratorDto getLoggedInAdmin() {
    return loggedInAdmin;
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

}
