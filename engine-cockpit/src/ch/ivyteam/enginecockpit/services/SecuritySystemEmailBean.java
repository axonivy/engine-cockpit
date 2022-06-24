package ch.ivyteam.enginecockpit.services;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.services.model.EmailSettings;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.language.LanguageConfigurator;
import ch.ivyteam.ivy.security.email.EmailNotificationConfigurator;

@ManagedBean
@ViewScoped
public class SecuritySystemEmailBean {

  private ManagerBean managerBean;
  private EmailSettings emailSettings;

  public SecuritySystemEmailBean() {
    managerBean = ManagerBean.instance();
    reloadEmailSettings();
  }

  public void reloadEmailSettings() {
    emailSettings = new EmailSettings(managerBean.getSelectedSecuritySystem().getSecurityContext());
    emailSettings.setNotificationCheckboxRender(false);
  }

  public EmailSettings getEmailSettings() {
    return emailSettings;
  }

  public void saveEmailSettings() {
    var context = managerBean.getSelectedSecuritySystem().getSecurityContext();
    new LanguageConfigurator(context).content(emailSettings.getLanguageLocale());
    var configurator = new EmailNotificationConfigurator(context);
    configurator.settings(emailSettings.saveEmailSettings(configurator.settings()));
    var msg = new FacesMessage("User email changes saved");
    FacesContext.getCurrentInstance().addMessage("emailSaveSuccess", msg);
  }
}
