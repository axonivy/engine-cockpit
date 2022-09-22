package ch.ivyteam.enginecockpit.security;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.security.model.UserDataModel;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.security.ISecurityContext;

@ManagedBean
@ViewScoped
public class UserBean {

  private UserDataModel userDataModel;
  private ManagerBean managerBean;
  private NewUser newUser;
  private UserSynch userSynch;

  public UserBean() {
    managerBean = ManagerBean.instance();
    userDataModel = new UserDataModel(managerBean.getSelectedSecuritySystem());
    reloadUsers();
  }

  public void reloadUsers() {
    newUser = new NewUser(managerBean.getSelectedSecuritySystem().getSecurityContext());
    userSynch = new UserSynch(managerBean.getSelectedSecuritySystem().getSecurityContext());
    userDataModel.setSecuritySystem(managerBean.getSelectedSecuritySystem());
    userDataModel.setFilter("");
    userDataModel.loadContentFilters(managerBean.isIvySecuritySystemForSelectedSecuritySystem());
  }

  public UserDataModel getUserDataModel() {
    return userDataModel;
  }

  public String getUserCount() {
    return managerBean.formatNumber(managerBean.getSelectedSecuritySystem().getSecurityContext().users().count());
  }

  public UserSynch getUserSynch() {
    return userSynch;
  }

  public NewUser getNewUser() {
    return newUser;
  }

  public static final class NewUser {

    private ISecurityContext securityContext;
    private String name;
    private String email;
    private String fullName;
    private String password;

    public NewUser(ISecurityContext securityContext) {
      this.securityContext = securityContext;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getFullName() {
      return fullName;
    }

    public void setFullName(String fullName) {
      this.fullName = fullName;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String creatNewUser() {
      var newUser = ch.ivyteam.ivy.security.user.NewUser
              .create(name)
              .fullName(fullName)
              .password(password)
              .mailAddress(email)
              .toNewUser();
      try {
        securityContext.users().create(newUser);
        var msg = new FacesMessage("User '" + newUser.getName() + "' created successfully", "");
        FacesContext.getCurrentInstance().addMessage("msgs", msg);
      } catch (Exception ex) {
        var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "User '" + newUser.getName() + "' couldn't be created", ex.getMessage());
        FacesContext.getCurrentInstance().addMessage("msgs", msg);
      }
      return "users.xhtml";
    }
  }
}
