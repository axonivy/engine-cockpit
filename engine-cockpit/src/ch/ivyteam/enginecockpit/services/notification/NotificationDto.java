package ch.ivyteam.enginecockpit.services.notification;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.ws.rs.core.UriBuilder;

import ch.ivyteam.enginecockpit.application.model.ProcessModelVersion;
import ch.ivyteam.enginecockpit.security.model.SecurityMember;
import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.notification.Notification;
import ch.ivyteam.ivy.notification.channel.Event;
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

  public String getEvent() {
    return Event.ofKind(notification.kind()).displayName(Locale.ENGLISH);
  }

  public List<SecurityMember> getReceivers() {
    return notification
        .receivers()
        .stream()
        .map(SecurityMember::createFor)
        .limit(20)
        .toList();
  }

  public boolean isReceiversConcat() {
    return notification.receivers().size() > 20;
  }

  public String getPmv() {
    return notification.pmv()
        .map(IProcessModelVersion::getVersionName)
        .orElse("");
  }

  public String getPmvIcon() {
    return pmv()
        .map(ProcessModelVersion::getIcon)
        .orElse("");
  }

  public String getPmvUri() {
    return pmv()
        .map(ProcessModelVersion::getDetailView)
        .orElse("");
  }

  public String getTemplate() {
    return notification.template();
  }

  public String getPayload() {
    return notification.payload();
  }

  public NotificationDeliveryRepository getDeliveries() {
    return notification.deliveries();
  }

  public String getViewUrl() {
    return UriBuilder.fromPath("notificationDeliveries.xhtml")
            .queryParam("system", notification.securityContext().getName())
            .queryParam("id", notification.uuid())
            .build()
            .toString();
  }

  public void reusher() {
    notification.reusher();
  }

  public void retry() {
    notification.retry();
  }

  private Optional<ProcessModelVersion> pmv() {
    return notification.pmv()
        .map(ProcessModelVersion::new);
  }
}
