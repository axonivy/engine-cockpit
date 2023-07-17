package ch.ivyteam.enginecockpit.services.notification;

import java.util.Date;

public class Notification {

  private ch.ivyteam.ivy.notification.Notification n;

  private final Date createdAt;

  public Notification(ch.ivyteam.ivy.notification.Notification n) {
    this.n = n;
    this.createdAt = Date.from(n.createdAt());
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public String getKind() {
    return n.kind();
  }

  public String getReceiver() {
    return n.receiver().getMemberName();
  }

  public String getPayload() {
    return n.payload();
  }
}
