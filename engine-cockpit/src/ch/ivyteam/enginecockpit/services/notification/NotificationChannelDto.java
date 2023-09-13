package ch.ivyteam.enginecockpit.services.notification;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.UriBuilder;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.enginecockpit.util.DurationFormat;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;
import ch.ivyteam.ivy.notification.channel.NotificationChannelConfig;
import ch.ivyteam.ivy.notification.channel.PushNotificationChannel;
import ch.ivyteam.ivy.notification.event.NotificationEvent;
import ch.ivyteam.ivy.security.ISecurityContext;

public class NotificationChannelDto {

  private final String system;

  private final NotificationChannel channel;
  private NotificationChannelConfig config;

  private boolean enabled;
  private boolean allEvents;
  private final List<NotificationEventDto> events;

  private NotificationChannelDto(NotificationChannel channel, NotificationChannelConfig config, boolean enabled, boolean allEvents, List<NotificationEventDto> events, String system) {
    this.channel = channel;
    this.config = config;
    this.enabled = enabled;
    this.allEvents = allEvents;
    this.events = events;
    this.system = system;
  }

  public String getDisplayName() {
    return channel.displayName();
  }

  public String getDisplayIcon() {
    return channel.displayIcon();
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isAllEventsEnabled() {
    return allEvents;
  }

  public void setAllEventsEnabled(boolean allEvents) {
    this.allEvents = allEvents;
  }

  public List<NotificationEventDto> getEvents() {
    return events;
  }

  public boolean isLocked() {
    return "Locked".equals(getStateText());
  }

  public long getLocks() {
    return pushChannel().locks();
  }

  public String getNextRetryIn() {
    return DurationFormat.BLANK_SOON.fromNowTo(pushChannel().lockTimeout());
  }

  public Date getNextRetryAt() {
    return Date.from(pushChannel().lockTimeout());
  }

  public String getLastError() {
    var ex = pushChannel().lastError();
    if (ex == null) {
      return "";
    }
    return ex.getMessage();
  }

  public boolean isPush() {
    return channel instanceof PushNotificationChannel;
  }

  PushNotificationChannel pushChannel() {
    return (PushNotificationChannel)channel;
  }

  public String getStateText() {
    if (isPush()) {
      return pushChannel().isOpen() ? "Open" : "Locked";
    }
    return "";
  }

  public String getStateIcon() {
    var icon =  switch(getStateText()) {
      case "Open" -> "si si-check-circle-1 state-active";
      case "Locked" -> "si si-remove-circle state-inactive";
      default -> "";
    };
    Ivy.log().info("Icon "+icon);
    return icon;
  }

  public String getViewUrl() {
    return UriBuilder.fromPath("notification-channel-detail.xhtml")
            .queryParam("system", system)
            .queryParam("channel", channel.id())
            .build()
            .toString();
  }

  NotificationChannelConfig getConfig() {
    return config;
  }

  public static NotificationChannelDto create(ManagerBean managerBean, NotificationChannel channel) {
    ISecurityContext securityContext = managerBean.getSelectedSecuritySystem().getSecurityContext();
    var config = channel.configFor(securityContext);

    var eventKinds = NotificationEvent.all().stream()
            .map(NotificationEvent::kind)
            .toList();
    var presentEventKinds = config.events();
    var events = eventKinds.stream()
            .map(eventKind -> new NotificationEventDto(eventKind, presentEventKinds.contains(eventKind)))
            .collect(Collectors.toList());

    return new NotificationChannelDto(channel, config,
            config.enabled(), config.allEventsEnabled(), events, securityContext.getName());
  }

  public static class NotificationEventDto {

    private final String kind;
    private boolean enabled;

    public NotificationEventDto(String kind, boolean enabled) {
      this.kind = kind;
      this.enabled = enabled;
    }

    public String getKind() {
      return kind;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }
}
