package ch.ivyteam.enginecockpit.services.notification;

import java.time.Instant;
import java.util.Date;
import javax.ws.rs.core.UriBuilder;
import ch.ivyteam.enginecockpit.security.model.SecurityMember;
import ch.ivyteam.enginecockpit.util.DurationFormat;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;
import ch.ivyteam.ivy.notification.delivery.NotificationDelivery;

public class NotificationDeliveryDto {

  private final NotificationDelivery delivery;
  private final Date deliveredAt;
  private final Date readAt;

  public NotificationDeliveryDto(NotificationDelivery delivery) {
    this.delivery = delivery;
    this.deliveredAt = toDate(delivery.deliveredAt());
    this.readAt = toDate(delivery.readAt());
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
    return NotificationChannel.byId(delivery.channel())
        .map(channel -> channel.displayName())
        .orElse(delivery.channel());
  }

  public String getChannelIcon() {
    return NotificationChannel.byId(delivery.channel())
        .map(channel -> channel.displayIcon())
        .orElse("");
  }

  public String getChannelUri() {
    return UriBuilder.fromPath("notification-channel-detail.xhtml")
        .queryParam("system", delivery.receiver().getSecurityContext().getName())
        .queryParam("channel", delivery.channel())
        .build()
        .toString();
  }

  public String getReceiver() {
    return delivery.receiver().getName();
  }

  public String getReceiverIcon() {
    return SecurityMember.createFor(delivery.receiver()).getCssIconClass();
  }

  public String getReceiverUri() {
    return SecurityMember.createFor(delivery.receiver()).getViewUrl();
  }

  public Date getDeliveredAt() {
    return deliveredAt;
  }

  public Date getReadAt() {
    return readAt;
  }

  public boolean isHidden() {
    return delivery.hidden();
  }

  public String getDeliveryState() {
    return delivery.deliveryState().toString();
  }

  public String getError() {
    return delivery.error();
  }

  public Date getNextRetryAt() {
    var nextRetry = delivery.errorTimeoutTimestamp();
    if (nextRetry == null) {
      return null;
    }
    return Date.from(nextRetry);
  }

  public String getNextRetryIn() {
    return DurationFormat.BLANK_SOON.fromNowTo(delivery.errorTimeoutTimestamp());
  }

  public int getNumberOfErrors() {
    return delivery.numberOfErrors();
  }
}
