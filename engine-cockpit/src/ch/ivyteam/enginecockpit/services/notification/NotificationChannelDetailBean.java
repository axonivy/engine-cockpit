package ch.ivyteam.enginecockpit.services.notification;

import java.util.Arrays;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.dynamic.config.DynamicConfig;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.NotificationChannelMonitor;
import ch.ivyteam.enginecockpit.services.notification.NotificationChannelDto.NotificationEventDto;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@ManagedBean
@ViewScoped
public class NotificationChannelDetailBean {

  private String channelId;
  private String system;

  private NotificationChannelDto channel;
  private NotificationChannelMonitor liveStats;

  private DynamicConfig dynamicConfig;

  public NotificationChannelDto getChannel() {
    return channel;
  }

  public String getChannelId() {
    return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getSystem() {
    return system;
  }

  public void setSystem(String system) {
    this.system = system;
  }

  public void onload() {
    var securityContext = ISecurityContextRepository.instance().get(system);
    if (securityContext == null) {
      ResponseHelper
          .notFound(Ivy.cms().co("/notificationChannelDetail/NotFoundSecurityContext", Arrays.asList(system)));
      return;
    }

    var notificationChannel = NotificationChannel.all(securityContext).stream()
        .filter(c -> c.id().equals(this.channelId))
        .findAny()
        .orElse(null);

    if (notificationChannel == null) {
      ResponseHelper.notFound(Ivy.cms().co("/notificationChannelDetail/NotFoundChannel", Arrays.asList(channelId)));
      return;
    }

    channel = NotificationChannelDto.instance(securityContext, notificationChannel);
    liveStats = new NotificationChannelMonitor(securityContext, this.channelId, channel.getDisplayName());
    dynamicConfig = DynamicConfig.create()
        .configurator(notificationChannel.configurator())
        .config(channel.getConfig().config())
        .toDynamicConfig();
  }

  public DynamicConfig getDynamicConfig() {
    return dynamicConfig;
  }

  public void save() {
    try {
      var config = channel.getConfig();
      config.enabled(channel.isEnabled());
      config.allEventsEnabled(channel.isAllEventsEnabled());
      var events = channel.getEvents().stream()
          .filter(NotificationEventDto::isEnabled)
          .map(NotificationEventDto::getEvent)
          .toList();
      config.events(events);
      Message.info()
          .clientId("msgs")
          .summary(Ivy.cm().co("/notificationChannelDetail/SuccessfullySavedMessage"))
          .show();
    } catch (Exception ex) {
      Message.error()
          .clientId("msgs")
          .exception(ex)
          .show();
    }
  }

  public void open() {
    channel.pushChannel().open();
  }

  public NotificationChannelMonitor getLiveStats() {
    return liveStats;
  }
}
