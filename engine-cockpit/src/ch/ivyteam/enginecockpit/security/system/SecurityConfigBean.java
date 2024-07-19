package ch.ivyteam.enginecockpit.security.system;

import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.security.SecurityContextRemovalCheck;
import ch.ivyteam.ivy.language.LanguageConfigurator;
import ch.ivyteam.ivy.language.LanguageManager;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityManager;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SecurityConfigBean  {

  private String name;

  private Locale language;
  private Locale formattingLanguage;
  private SecuritySystem securitySystem;
  private ManagerBean managerBean;

  public SecurityConfigBean() {
    managerBean = ManagerBean.instance();
  }

  public SecurityConfigBean(String secSystemName) {
    this();
    setSecuritySystemName(secSystemName);
  }

  public String getSecuritySystemName() {
    return name;
  }

  public void setSecuritySystemName(String secSystemName) {
    if (StringUtils.isBlank(name)) {
      name = secSystemName;
      loadSecuritySystem();
    }
  }

  private void loadSecuritySystem() {
    securitySystem = managerBean.getSecuritySystems().stream()
            .filter(system -> StringUtils.equals(system.getSecuritySystemName(), name))
            .findAny()
            .orElseThrow();

    var languageConfigurator = languageConfigurator();
    language = languageConfigurator.content();
    formattingLanguage = languageConfigurator.formatting();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Locale getLanguage() {
    return language;
  }

  public void setLanguage(Locale language) {
    this.language = language;
  }

  public Locale getFormattingLanguage() {
    return formattingLanguage;
  }

  public void setFormattingLanguage(Locale formattingLanguage) {
    this.formattingLanguage = formattingLanguage;
  }

  public List<String> getUsedByApps() {
    return securitySystem.getAppNames();
  }

  public boolean isDeletable() {
    var result = new SecurityContextRemovalCheck(securitySystem.getSecurityContext()).run();
    return result.removable();
  }

  public String getNotToDeleteReason() {
    var result = new SecurityContextRemovalCheck(securitySystem.getSecurityContext()).run();
    if (result.removable()) {
      return "Are you sure you want to delete the security system '" + name + "'?";
    }
    return result.reason();
  }

  public ISecurityContext getSecurityContext() {
    return securitySystem.getSecurityContext();
  }

  public String getLink() {
    return securitySystem.getLink();
  }

  private LanguageConfigurator languageConfigurator() {
    return LanguageManager.instance().configurator(securitySystem.getSecurityContext());
  }

  public void saveLanguages() {
    var languageConfigurator = languageConfigurator();
    languageConfigurator.content(LocaleUtils.toLocale(language));
    languageConfigurator.formatting(LocaleUtils.toLocale(formattingLanguage));
    FacesContext.getCurrentInstance().addMessage("securityLanguageSaveSuccess",
            new FacesMessage("Security System Languages saved"));
  }

  public String deleteConfiguration() {
    ISecurityManager.instance().securityContexts().delete(name);
    return "securitysystem.xhtml?faces-redirect=true";
  }
}
