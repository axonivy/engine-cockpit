package ch.ivyteam.enginecockpit.services.notification;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import ch.ivyteam.enginecockpit.system.ManagerBean;

@Named
@ViewScoped
public class NotificationsBean implements Serializable {

  @Inject
  private ManagerBean managerBean;

  private NotificationsDataModel dataModel;

  public NotificationsDataModel getDataModel() {
    return dataModel;
  }

  public void onload() {
    dataModel = new NotificationsDataModel(managerBean.getSelectedSecuritySystem());
  }
}
