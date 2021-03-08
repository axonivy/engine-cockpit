package ch.ivyteam.enginecockpit.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.security.model.RoleDataModel;
import ch.ivyteam.enginecockpit.system.ManagerBean;

@ManagedBean
@ViewScoped
public class RoleBean
{
  private ManagerBean managerBean;
  private RoleDataModel roleDataModel;

  public RoleBean()
  {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    reloadRoles();
  }

  public void reloadRoles()
  {
    roleDataModel = new RoleDataModel(managerBean.getSelectedIApplication(), true);
  }
  
  public RoleDataModel getRoles()
  {
    return roleDataModel;
  }
  
  public String getRoleCount()
  {
    return managerBean.formatNumber(roleDataModel.getList().size());
  }
  
}
