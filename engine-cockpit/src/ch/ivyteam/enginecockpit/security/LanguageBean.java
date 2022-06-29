package ch.ivyteam.enginecockpit.security;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;

import ch.ivyteam.ivy.language.LanguageManager;
import ch.ivyteam.ivy.language.LanguageRepository;
import ch.ivyteam.ivy.security.ISecurityContext;

@ManagedBean
public class LanguageBean {

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
    return locale.getDisplayName(Locale.ENGLISH) + " (" + locale.toString() + ")";
  }

  private List<Locale> locales(ISecurityContext securityContext, Function<LanguageRepository, List<Locale>> supplier) {
    var languages = LanguageManager.instance().languages(securityContext);
    var locales = supplier.apply(languages).stream()
            .sorted(Comparator.comparing(Locale::getDisplayName))
            .collect(Collectors.toList());

    var l = new ArrayList<Locale>();
    l.add(Locale.ROOT);
    l.addAll(locales);
    return l;
  }
}
