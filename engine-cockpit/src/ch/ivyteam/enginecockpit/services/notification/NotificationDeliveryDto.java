package ch.ivyteam.enginecockpit.services.notification;

import java.time.Instant;
import java.util.Date;

import ch.ivyteam.ivy.notification.delivery.NotificationDelivery;

public class NotificationDeliveryDto {

  private final NotificationDelivery delivery;
  private final Date deliveredAt;

  public NotificationDeliveryDto(NotificationDelivery delivery) {
    this.delivery = delivery;
    this.deliveredAt = toDate(delivery.deliveredAt());
  }

  private static Date toDate(Instant instant) {
    if (instant == null) {
      return null;
    }
    return Date.from(instant);
  }

  public String getId() {
    return delivery.uuid();
  }

  public String getChannel() {
    return delivery.channel();
  }

  public String getReceiver() {
    return delivery.receiver().getMemberName();
  }

  public Date getDeliveredAt() {
    return deliveredAt;
  }
}
