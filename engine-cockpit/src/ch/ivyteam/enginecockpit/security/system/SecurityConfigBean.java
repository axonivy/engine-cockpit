package ch.ivyteam.enginecockpit.security.system;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.application.security.SecurityContextRemovalCheck;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.language.LanguageConfigurator;
import ch.ivyteam.ivy.language.LanguageManager;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityManager;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SecurityConfigBean {

  private String name;

  private Locale language;
  private Locale formattingLanguage;
  private SecuritySystem securitySystem;

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
    securitySystem = ISecurityManager.instance().securityContexts().allWithSystem().stream()
        .map(SecuritySystem::new)
        .filter(system -> Objects.equals(system.getSecuritySystemName(), name))
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
      return Ivy.cm().content("/securitySystemInfo/DeleteSecuritySystemConfirmMessage").replace("name", name).get();
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
        new FacesMessage(Ivy.cm().co("/securitySystemLanguage/SecuritySystemLanguagesSavedMessage")));
  }

  public String deleteConfiguration() {
    ISecurityManager.instance().securityContexts().delete(name);
    return "securitysystem.xhtml?faces-redirect=true";
  }

  public boolean isShowDetails() {
    return !isSystem();
  }

  public boolean isShowWorkflowLanguage() {
    return !isSystem();
  }

  private boolean isSystem() {
    return ISecurityContext.SYSTEM.equals(name);
  }
}
