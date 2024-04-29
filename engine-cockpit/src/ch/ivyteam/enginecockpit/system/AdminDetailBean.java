package ch.ivyteam.enginecockpit.system;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.security.model.NotificationChannelDataModel;
import ch.ivyteam.enginecockpit.security.model.User;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.IUser;

@ManagedBean
@ViewScoped
public class AdminDetailBean {

  private String userName;
  private User user;

  private String securitySystemName;
  private ISecurityContext securityContext;
  private NotificationChannelDataModel notificationChannelDataModel;

  public String getSecuritySystem() {
    return securitySystemName;
  }

  public void setSecuritySystem(String securitySystem) {
    this.securitySystemName = securitySystem;
  }

  public String getName() {
    return userName;
  }

  public void setName(String userName) {
    this.userName = userName;
  }

  public void onload() {
    securityContext = ISecurityContextRepository.instance().get(securitySystemName);
    if (securityContext == null) {
      ResponseHelper.notFound("Security System '" + securitySystemName + "' not found");
      return;
    }

    var iUser = securityContext.users().find(userName);
    if (iUser == null) {
      ResponseHelper.notFound("Admin '" + userName + "' not found");
      return;
    }

    this.user = new User(iUser);
    notificationChannelDataModel = NotificationChannelDataModel.instance(iUser, securityContext);
  }

  public User getAdmin() {
    return user;
  }

  public void saveAdminInfos() {
    var iUser = getIUser();
    iUser.setEMailAddress(user.getEmail());
    iUser.setFullName(user.getFullName());
    if (user.getPassword() != "") {
      iUser.setPassword(user.getPassword());
    }
    iUser.setLanguage(user.getLanguage());
    iUser.setFormattingLanguage(user.getFormattingLanguage());
    var msg = new FacesMessage("Admin information changes saved");
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess", msg);
  }

  public String deleteSelectedAdmin() {
    securityContext.users().delete(userName);
    return "admins.xhtml?faces-redirect=true";
  }

  public NotificationChannelDataModel getNotificationChannels() {
    return notificationChannelDataModel;
  }

  private IUser getIUser() {
    return securityContext.users().find(userName);
  }

  public String getSecuritySystemName() {
    return securitySystemName;
  }

  public ISecurityContext getSecurityContext() {
    return securityContext;
  }
}
