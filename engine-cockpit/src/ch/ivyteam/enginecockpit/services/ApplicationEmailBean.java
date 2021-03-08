package ch.ivyteam.enginecockpit.services;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.services.model.EmailSettings;
import ch.ivyteam.enginecockpit.system.ManagerBean;

@ManagedBean
@ViewScoped
public class ApplicationEmailBean
{
  private EmailSettings emailSettings;

  private ManagerBean managerBean;

  public ApplicationEmailBean()
  {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    reloadEmailSettings();
  }

  public void reloadEmailSettings()
  {
    emailSettings = new EmailSettings(managerBean.getSelectedIApplication());
    emailSettings.setNotificationCheckboxRender(false);
  }

  public EmailSettings getEmailSettings()
  {
    return emailSettings;
  }

  public void saveEmailSettings()
  {
    var iApp = managerBean.getSelectedIApplication();
    var language = emailSettings.getLanguageLocale();
    iApp.setDefaultEMailLanguage(language);
    iApp.setDefaultEMailNotifcationSettings(
            emailSettings.saveEmailSettings(iApp.getDefaultEMailNotifcationSettings()));
    FacesContext.getCurrentInstance().addMessage("emailSaveSuccess",
            new FacesMessage("User email changes saved"));
  }
}
