package ch.ivyteam.enginecockpit.services.notification;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.services.notification.NotificationChannelConfigDto.NotificationEventDto;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;
import ch.ivyteam.ivy.notification.channel.impl.NotificationChannelConfig;

@ManagedBean
@ViewScoped
public class NotificationChannelDetailBean {

  private String channelId;
  private String system;

  private ManagerBean managerBean;
  private NotificationChannelConfig notificationChannelConfig;

  private NotificationChannelConfigDto config;

  public NotificationChannelDetailBean() {
    managerBean = ManagerBean.instance();
  }

  public NotificationChannelConfigDto getConfig() {
    return config;
  }

  public void setConfig(NotificationChannelConfigDto config) {
    this.config = config;
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
    var channel = NotificationChannel.all().stream()
            .filter(notificationChannel -> notificationChannel.id().equals(this.channelId))
            .findAny()
            .orElse(null);

    if (channel == null) {
      ResponseHelper.notFound("Channel '" + channelId + "' not found");
      return;
    }

    config = NotificationChannelConfigDto.create(managerBean, channel);
    notificationChannelConfig = new NotificationChannelConfig(managerBean.getSelectedSecuritySystem().getSecurityContext(), channel);
  }

  public void save() {
    notificationChannelConfig.enabled(config.isEnabled());
    notificationChannelConfig.allEventsEnabled(config.isAllEventsEnabled());
    notificationChannelConfig.events(
            config.getEvents().stream()
                    .filter(NotificationEventDto::isEnabled)
                    .map(NotificationEventDto::getKind)
                    .toList());

    var msg = new FacesMessage(config.getDisplayName() + " notification channel changes saved");
    FacesContext.getCurrentInstance().addMessage("channelSaveSuccess", msg);
  }
}
