package ch.ivyteam.enginecockpit.services.notification;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;

import java.io.Serializable;

import ch.ivyteam.enginecockpit.system.ManagerBean;

@Named
@ViewScoped
public class NotificationsBean implements Serializable {

  private NotificationsDataModel dataModel;

  public NotificationsDataModel getDataModel() {
    return dataModel;
  }

  public void onload() {
    dataModel = new NotificationsDataModel(ManagerBean.instance().getSelectedSecuritySystem());
  }
}
