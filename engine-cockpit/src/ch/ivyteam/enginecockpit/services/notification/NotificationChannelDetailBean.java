package ch.ivyteam.enginecockpit.services.notification;

import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.dynamic.config.ConfigProperty;
import ch.ivyteam.enginecockpit.dynamic.config.ConfigPropertyGroup;
import ch.ivyteam.enginecockpit.dynamic.config.DynamicConfig;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.NotificationChannelMonitor;
import ch.ivyteam.enginecockpit.services.notification.NotificationChannelDto.NotificationEventDto;
import ch.ivyteam.ivy.configuration.configurator.ConfiguratorMetadataProvider;
import ch.ivyteam.ivy.configuration.meta.Metadata;
import ch.ivyteam.ivy.configuration.restricted.ConfigKey;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class NotificationChannelDetailBean {

  private static final String CONFIG_PREFIX = "Config.";

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
      ResponseHelper.notFound("Could not find security context " + system);
      return;
    }

    var notificationChannel = NotificationChannel.all(securityContext).stream()
            .filter(c -> c.id().equals(this.channelId))
            .findAny()
            .orElse(null);

    if (notificationChannel == null) {
      ResponseHelper.notFound("Channel '" + channelId + "' not found");
      return;
    }

    channel = NotificationChannelDto.instance(securityContext, notificationChannel);
    liveStats = new NotificationChannelMonitor(securityContext, this.channelId, channel.getDisplayName());
    var properties = new ConfiguratorMetadataProvider(notificationChannel.config().configurator()).get().entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(CONFIG_PREFIX))
            .map(entry -> toConfigProperty(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

    var propertyGroups = ConfigPropertyGroup.toGroups(properties);
    this.dynamicConfig = new DynamicConfig(propertyGroups, this::setProp);
  }

  private void setProp(ConfigKey key, String value) {
    var k = key.unquoted();
    this.channel.getConfig().config().setProperty(k, value);
  }

  private ConfigProperty toConfigProperty(String key, Metadata metadata) {
    var config = channel.getConfig().config();
    key = StringUtils.removeStart(key, CONFIG_PREFIX);
    var value = config.getProperty(key);
    return new ConfigProperty(null, key, value, Map.of(), metadata);
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
              .map(NotificationEventDto::getKind)
              .toList();
      config.events(events);
      Message.info("msgs", "Successfully saved");
    } catch (Exception ex) {
      Message.error("msgs", ex);
    }
  }

  public void open() {
    channel.pushChannel().open();
  }

  public NotificationChannelMonitor getLiveStats() {
    return liveStats;
  }
}
