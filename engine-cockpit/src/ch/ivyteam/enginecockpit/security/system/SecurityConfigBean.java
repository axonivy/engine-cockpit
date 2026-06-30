package ch.ivyteam.enginecockpit.security.system;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.application.model.App;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.ivy.application.security.SecurityContextRemovalCheck;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.language.LanguageConfigurator;
import ch.ivyteam.ivy.language.LanguageManager;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityManager;

@Named
@ViewScoped
public class SecurityConfigBean implements Serializable {

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

  public List<App> getUsedByApps() {
    return securitySystem.getApps();
  }

  public String getApplicationDetailLink(App app) {
    return UriBuilder.fromPath("application.xhtml")
        .queryParam("context", getSecuritySystemName())
        .queryParam("app", app.name())
          .queryParam("appVersion", app.version())
          .build()
          .toString();
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
