package ch.ivyteam.enginecockpit.security;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.security.model.RoleDataModel;
import ch.ivyteam.enginecockpit.system.ManagerBean;

@Named
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
