package ch.ivyteam.enginecockpit.services.notification;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.system.ManagerBean;

@ManagedBean
@ViewScoped
public class NotificationBean {

  private NotificationDataModel dataModel;

  public NotificationDataModel getDataModel() {
    return dataModel;
  }

  public void onload() {
    dataModel = new NotificationDataModel(ManagerBean.instance().getSelectedSecuritySystem());
  }
}
