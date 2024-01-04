package ch.ivyteam.enginecockpit.services.notification;

import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.dynamic.config.ConfigProperty;
import ch.ivyteam.enginecockpit.dynamic.config.ConfigPropertyGroup;
import ch.ivyteam.enginecockpit.dynamic.config.DynamicConfig;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.NotificationChannelMonitor;
import ch.ivyteam.enginecockpit.services.notification.NotificationChannelDto.NotificationEventDto;
import ch.ivyteam.ivy.configuration.configurator.ConfiguratorMetadataProvider;
import ch.ivyteam.ivy.configuration.meta.Metadata;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@ManagedBean
@ViewScoped
public class NotificationChannelDetailBean {

  private String channelId;
  private String system;

  private NotificationChannelDto channel;
  private NotificationChannelMonitor liveStats;

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

    var configurator = notificationChannel.config().configurator();

    var properties = new ConfiguratorMetadataProvider(configurator).get().entrySet().stream()
            .map(entry -> toConfigProperty(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    this.dynamicConfig = new DynamicConfig(ConfigPropertyGroup.toGroups(properties), securityContext);
  }

  private DynamicConfig dynamicConfig;

  private ConfigProperty toConfigProperty(String key, Metadata metadata) {
    var config = channel.getConfig().config();
    var value = "";
    Map<String, String> keyValue = Map.of();
    if (metadata.isKeyValue()) {
      //keyValue = new HashMap<>(config.getPropertyAsKeyValue(key));
    } else {
      value = config.getProperty(key);
    }
    return new ConfigProperty(null, key, value, keyValue, metadata);
  }

  public DynamicConfig getDynamicConfig() {
    return dynamicConfig;
  }

  public void save() {
    try {
      var config = channel.getConfig();
      config.enabled(channel.isEnabled());
      config.allEventsEnabled(channel.isAllEventsEnabled());
      config.events(
              channel.getEvents().stream()
                      .filter(NotificationEventDto::isEnabled)
                      .map(NotificationEventDto::getKind)
                      .toList());

      var msg = new FacesMessage(channel.getDisplayName() + " notification channel changes saved", "");
      FacesContext.getCurrentInstance().addMessage("msgs", msg);
    } catch (IllegalArgumentException ex) {
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage()));
    }
  }

  public void open() {
    channel.pushChannel().open();
  }

  public NotificationChannelMonitor getLiveStats() {
    return liveStats;
  }
}
