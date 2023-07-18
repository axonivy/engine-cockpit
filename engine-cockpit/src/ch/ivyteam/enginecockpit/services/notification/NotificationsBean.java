package ch.ivyteam.enginecockpit.services.notification;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.system.ManagerBean;

@ManagedBean
@ViewScoped
public class NotificationsBean {

  private NotificationsDataModel dataModel;

  public NotificationsDataModel getDataModel() {
    return dataModel;
  }

  public void onload() {
    dataModel = new NotificationsDataModel(ManagerBean.instance().getSelectedSecuritySystem());
  }
}
