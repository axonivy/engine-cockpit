package ch.ivyteam.enginecockpit.services.notification.payload;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import ch.ivyteam.enginecockpit.services.notification.NotificationDto;

@Named
@ViewScoped
public class NotificationPayloadBean implements Serializable {

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
