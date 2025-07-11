package ch.ivyteam.enginecockpit.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.security.model.RoleDataModel;
import ch.ivyteam.enginecockpit.system.ManagerBean;

@ManagedBean
@ViewScoped
public class RoleBean {

  private final ManagerBean managerBean;
  private RoleDataModel roleDataModel;

  public RoleBean() {
    managerBean = ManagerBean.instance();
    reloadRoles();
  }

  public void reloadRoles() {
    roleDataModel = new RoleDataModel(managerBean.getSelectedSecuritySystem(), true);
  }

  public void increaseShowChildLimitAndReloadTree(int increaseLimitBy) {
    roleDataModel.increaseShowChildLimitAndReloadTree(increaseLimitBy);
  }

  public RoleDataModel getRoles() {
    return roleDataModel;
  }

  public String getRoleCount() {
    return managerBean.formatNumber(roleDataModel.getList().size());
  }
}
