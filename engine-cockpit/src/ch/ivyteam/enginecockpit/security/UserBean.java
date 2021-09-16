package ch.ivyteam.enginecockpit.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.security.model.UserDataModel;
import ch.ivyteam.enginecockpit.system.ManagerBean;

@ManagedBean
@ViewScoped
public class UserBean {
  private UserDataModel userDataModel;
  private ManagerBean managerBean;

  public UserBean() {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    userDataModel = new UserDataModel();
    reloadUsers();
  }

  public void reloadUsers() {
    var app = managerBean.getSelectedIApplication();
    userDataModel.setApp(app);
    userDataModel.setFilter("");
    userDataModel.loadContentFilters(managerBean.isIvySecuritySystem());
  }

  public UserDataModel getUserDataModel() {
    return userDataModel;
  }

  public String getUserCount() {
    return managerBean
            .formatNumber(managerBean.getSelectedIApplication().getSecurityContext().users().count());
  }

}
