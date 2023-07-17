package ch.ivyteam.enginecockpit.services.notification.payload;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.services.notification.Notification;

@ManagedBean
@ViewScoped
public class NotificationPayloadBean {

  private Notification notification;

  public void setNotification(Notification notification) {
    this.notification = notification;
  }

  public Notification getNotification() {
    return notification;
  }

  public String getPayload() {
    if (notification == null) {
      return "";
    }
    return notification.getPayload();
  }
}
