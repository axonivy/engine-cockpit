package ch.ivyteam.enginecockpit.services.notification;

import java.util.Date;

import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.ivy.notification.Notification;
import ch.ivyteam.ivy.notification.delivery.NotificationDeliveryRepository;

public class NotificationDto {

  private Notification notification;
  private final Date createdAt;

  public NotificationDto(Notification notification) {
    this.notification = notification;
    this.createdAt = Date.from(notification.createdAt());
  }

  public String getId() {
    return notification.uuid();
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public String getKind() {
    return notification.kind();
  }

  public String getReceiver() {
    return notification.receiver().getMemberName();
  }

  public String getPayload() {
    return notification.payload();
  }

  public NotificationDeliveryRepository getDeliveries() {
    return notification.deliveries();
  }

  public String getViewUrl() {
    return UriBuilder.fromPath("notification.xhtml")
            .queryParam("system", notification.securityContext().getName())
            .queryParam("id", notification.uuid())
            .build()
            .toString();
  }
}
