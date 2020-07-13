package ch.ivyteam.enginecockpit.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;

@ManagedBean
@ViewScoped
public class UserBean
{
  private UserDataModel userDataModel;
  private ManagerBean managerBean;

  public UserBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);
    userDataModel = new UserDataModel();
    reloadUsers();
  }

  public void reloadUsers()
  {
    IApplication app = managerBean.getSelectedIApplication();
    userDataModel.setApp(app);
    userDataModel.setFilter("");
    userDataModel.loadContentFilters(managerBean.isIvySecuritySystem());
  }

  public UserDataModel getUserDataModel()
  {
    return userDataModel;
  }
  
  public String getFilter()
  {
    return userDataModel.getFilter();
  }

  public void setFilter(String filter)
  {
    this.userDataModel.setFilter(filter);
  }

  public String getUserCount()
  {
    var count = managerBean.getSelectedIApplication().getSecurityContext().users().count();
    return String.valueOf(count);
  }

}
