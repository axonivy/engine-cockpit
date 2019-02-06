package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.EmailSettings;
import ch.ivyteam.enginecockpit.model.Role;
import ch.ivyteam.enginecockpit.model.User;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.IUser;

@ManagedBean
@ViewScoped
public class UserDetailBean
{
  private String userName;
  private User user;
  private EmailSettings emailSettings;

  private List<Role> filteredRoles;

  private ManagerBean managerBean;

  public UserDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    user = new User();
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
    IUser iUser = getSecurityContext().findUser(userName);
    this.user = new User(iUser);
    this.emailSettings = new EmailSettings(iUser, managerBean.getSelectedIApplication().getDefaultEMailNotifcationSettings());
  }

  public User getUser()
  {
    return user;
  }

  public EmailSettings getEmailSettings()
  {
    return emailSettings;
  }

  public String creatNewUser()
  {
    getSecurityContext().createUser(user.getName(), user.getFullName(), user.getPassword(), null,
            user.getEmail(), null);
    return "users.xhtml";
  }

  public void saveUserInfos()
  {
    IUser iUser = getIUser();
    iUser.setEMailAddress(user.getEmail());
    iUser.setFullName(user.getFullName());
    if (user.getPassword() != "")
    {
      iUser.setPassword(user.getPassword());
    }
    FacesContext.getCurrentInstance().addMessage("informationSaveSuccess",
            new FacesMessage("User information changes saved"));
  }

  public String deleteSelectedUser()
  {
    getSecurityContext().deleteUser(userName);
    return "users.xhtml?faces-redirect=true";
  }

  public void saveUserEmail()
  {
    IUser iUser = getIUser();
    Locale language = emailSettings.getLanguageLocale();
    if (language.getLanguage().equals("app"))
    {
      language = null;
    }
    iUser.setEMailLanguage(language);
    iUser.setEMailNotificationSettings(
            emailSettings.saveUserEmailSettings(iUser.getEMailNotificationSettings()));
    FacesContext.getCurrentInstance().addMessage("emailSaveSuccess",
            new FacesMessage("User email changes saved"));
  }

  public boolean isUserMemberOfAllRole(String roleName)
  {
    return getIUser().getAllRoles().stream().filter(r -> r.getName().equals(roleName)).findAny().isPresent();
  }

  public boolean isUserMemberOfRole(String roleName)
  {
    return getIUser().getRoles().stream().filter(r -> r.getName().equals(roleName)).findAny().isPresent();
  }

  public void removeRole(String roleName)
  {
    getIUser().removeRole(getSecurityContext().findRole(roleName));
  }

  public void addRole(String roleName)
  {
    try
    {
      getIUser().addRole(getSecurityContext().findRole(roleName));
    }
    catch (Exception e)
    {
      FacesContext.getCurrentInstance().addMessage("roleMessage",
              new FacesMessage("User already member of this role"));
    }
  }

  public List<Role> getFilteredRoles()
  {
    return filteredRoles;
  }

  public void setFilteredRoles(List<Role> filteredRoles)
  {
    this.filteredRoles = filteredRoles;
  }

  private IUser getIUser()
  {
    return getSecurityContext().findUser(userName);
  }

  private ISecurityContext getSecurityContext()
  {
    return managerBean.getSelectedIApplication().getSecurityContext();
  }
}
