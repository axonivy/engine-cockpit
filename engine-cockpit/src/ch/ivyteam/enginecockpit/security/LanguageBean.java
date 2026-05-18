package ch.ivyteam.enginecockpit.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.language.LanguageManager;
import ch.ivyteam.ivy.language.LanguageRepository;
import ch.ivyteam.ivy.security.ISecurityContext;

@Named
@ViewScoped
public class LanguageBean implements Serializable {

  public List<Locale> getContentLanguages(ISecurityContext securityContext) {
    return locales(securityContext, LanguageRepository::allContent);
  }

  public List<Locale> getFormattingLanguages(ISecurityContext securityContext) {
    return locales(securityContext, LanguageRepository::allFormatting);
  }

  public String toDisplayName(Locale locale) {
    if (Locale.ROOT.equals(locale)) {
      return "";
    }
    return locale.getDisplayLanguage(Ivy.session().getContentLocale()) + " (" + locale.toString() + ")";
  }

  private List<Locale> locales(ISecurityContext securityContext, Function<LanguageRepository, List<Locale>> supplier) {
    var languages = LanguageManager.instance().languages(securityContext);
    var locales = supplier.apply(languages).stream()
        .sorted(Comparator.comparing(this::toDisplayName, String.CASE_INSENSITIVE_ORDER))
        .collect(Collectors.toList());

    var l = new ArrayList<Locale>();
    l.add(Locale.ROOT);
    l.addAll(locales);
    return l;
  }
}
