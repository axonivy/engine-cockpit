package ch.ivyteam.enginecockpit.services.notification;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;

import ch.ivyteam.enginecockpit.util.DurationFormat;
import ch.ivyteam.ivy.notification.channel.Event;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;
import ch.ivyteam.ivy.notification.channel.NotificationChannelSystemConfig;
import ch.ivyteam.ivy.notification.channel.PushNotificationChannel;
import ch.ivyteam.ivy.security.ISecurityContext;

public class NotificationChannelDto {

  private final String system;

  private final NotificationChannel channel;
  private final NotificationChannelSystemConfig config;

  private boolean enabled;
  private boolean allEvents;
  private final List<NotificationEventDto> events;

  private NotificationChannelDto(NotificationChannel channel, NotificationChannelSystemConfig config, boolean enabled, boolean allEvents, List<NotificationEventDto> events, String system) {
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
    return loadResource(channel.displayIcon());
  }

  private static String loadResource(URI uri) {
    try {
      return IOUtils.toString(uri, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
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
    return (PushNotificationChannel) channel;
  }

  public String getStateText() {
    if (isPush()) {
      return pushChannel().isOpen() ? "Open" : "Locked";
    }
    return "";
  }

  public String getStateIcon() {
    return switch (getStateText()) {
      case "Open" -> "si si-check-circle-1 state-active";
      case "Locked" -> "si si-remove-circle state-inactive";
      default -> "";
    };
  }

  public String getViewUrl() {
    return UriBuilder.fromPath("notification-channel-detail.xhtml")
        .queryParam("system", system)
        .queryParam("channel", channel.id())
        .build()
        .toString();
  }

  NotificationChannelSystemConfig getConfig() {
    return config;
  }

  public static NotificationChannelDto instance(ISecurityContext securityContext, NotificationChannel channel) {
    var config = channel.config();
    var allEvents = Event.all();
    var presentEvents = config.events();
    var events = allEvents.stream()
        .map(event -> new NotificationEventDto(event, presentEvents.contains(event)))
        .collect(Collectors.toList());

    return new NotificationChannelDto(channel, config,
        config.enabled(), config.allEventsEnabled(), events, securityContext.getName());
  }

  public static class NotificationEventDto {

    private final Event event;
    private boolean enabled;

    public NotificationEventDto(Event event, boolean enabled) {
      this.event = event;
      this.enabled = enabled;
    }

    public Event getEvent() {
      return event;
    }

    public String getKind() {
      return event.kind();
    }

    public String getDisplayName() {
      return event.displayName(Locale.ENGLISH);
    }

    public String getDescription() {
      return event.description(Locale.ENGLISH);
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }
}
