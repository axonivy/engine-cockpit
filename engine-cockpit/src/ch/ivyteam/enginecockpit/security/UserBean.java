package ch.ivyteam.enginecockpit.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.security.model.UserDataModel;
import ch.ivyteam.enginecockpit.system.ManagerBean;

@ManagedBean
@ViewScoped
public class UserBean {

  private UserDataModel userDataModel;
  private ManagerBean managerBean;

  public UserBean() {
    managerBean = ManagerBean.instance();
    userDataModel = new UserDataModel(managerBean.getSelectedSecuritySystem());
    reloadUsers();
  }

  public void reloadUsers() {
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
}
