package ch.ivyteam.enginecockpit.security;

import java.util.Arrays;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.security.model.RoleDataModel;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.environment.Ivy;

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

  public String getExternalRoleMessage(String roleName) {
    return Ivy.cms().co("/roles/ExternalRoleNameMessage", Arrays.asList(roleName));
  }

  public String getShowMoreRoleLeftMessage(String roleName) {
    return Ivy.cms().co("/roles/ShowMoreRoleLeftMessage", Arrays.asList(roleName));
  }
}
