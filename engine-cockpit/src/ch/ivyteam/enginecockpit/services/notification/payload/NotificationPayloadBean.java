package ch.ivyteam.enginecockpit.services.notification.payload;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.services.notification.NotificationDto;

@ManagedBean
@ViewScoped
public class NotificationPayloadBean {

  private NotificationDto notification;

  public void setNotification(NotificationDto notification) {
    this.notification = notification;
  }

  public NotificationDto getNotification() {
    return notification;
  }

  public String getPayload() {
    if (notification == null) {
      return "";
    }
    return notification.getPayload();
  }
}
