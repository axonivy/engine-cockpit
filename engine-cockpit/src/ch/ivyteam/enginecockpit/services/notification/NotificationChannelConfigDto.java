package ch.ivyteam.enginecockpit.services.notification;

import java.util.List;
import java.util.stream.Collectors;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;
import ch.ivyteam.ivy.notification.event.NotificationEvent;
import ch.ivyteam.ivy.security.ISecurityContext;

public class NotificationChannelConfigDto {

  private final String system;

  private final String channelId;
  private final String displayName;
  private final String displayIcon;

  private boolean enabled;
  private boolean allEvents;
  private final List<NotificationEventDto> events;

  private NotificationChannelConfigDto(String channelId, String displayName, String displayIcon,
          boolean enabled, boolean allEvents, List<NotificationEventDto> events, String system) {
    this.channelId = channelId;
    this.displayName = displayName;
    this.displayIcon = displayIcon;
    this.enabled = enabled;
    this.allEvents = allEvents;
    this.events = events;
    this.system = system;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDisplayicon() {
    return displayIcon;
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

  public String getViewUrl() {
    return String.format("notification-channel-detail.xhtml?system=%s&channel=%s", system, channelId);
  }

  public static NotificationChannelConfigDto create(ManagerBean managerBean, NotificationChannel channel) {
    ISecurityContext securityContext = managerBean.getSelectedSecuritySystem().getSecurityContext();
    var config = channel.configFor(securityContext);

    var eventKinds = NotificationEvent.all().stream()
            .map(NotificationEvent::kind)
            .toList();
    var presentEventKinds = config.events();
    var events = eventKinds.stream()
            .map(eventKind -> new NotificationEventDto(eventKind, presentEventKinds.contains(eventKind)))
            .collect(Collectors.toList());

    return new NotificationChannelConfigDto(channel.id(), channel.displayName(), channel.displayIcon(),
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
