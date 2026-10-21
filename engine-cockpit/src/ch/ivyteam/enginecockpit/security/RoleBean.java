package ch.ivyteam.enginecockpit.security;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import ch.ivyteam.enginecockpit.security.model.RoleDataModel;
import ch.ivyteam.enginecockpit.system.ManagerBean;

@Named
@ViewScoped
public class RoleBean implements Serializable {

  @Inject
  private ManagerBean managerBean;
  private RoleDataModel roleDataModel;

  @PostConstruct
  public void init() {
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
