package ch.ivyteam.enginecockpit.services.notification;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.services.notification.NotificationChannelDto.NotificationEventDto;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;

@ManagedBean
@ViewScoped
public class NotificationChannelDetailBean {

  private String channelId;
  private String system;

  private ManagerBean managerBean;
  private NotificationChannelDto channel;

  public NotificationChannelDetailBean() {
    managerBean = ManagerBean.instance();
  }

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
    var chn = NotificationChannel.all().stream()
            .filter(notificationChannel -> notificationChannel.id().equals(this.channelId))
            .findAny()
            .orElse(null);

    if (chn == null) {
      ResponseHelper.notFound("Channel '" + channelId + "' not found");
      return;
    }

    channel = NotificationChannelDto.instance(managerBean.getSelectedSecuritySystem().getSecurityContext(), chn);
  }

  public void save() {
    var config = channel.getConfig();
    config.enabled(channel.isEnabled());
    config.allEventsEnabled(channel.isAllEventsEnabled());
    config.events(
            channel.getEvents().stream()
                    .filter(NotificationEventDto::isEnabled)
                    .map(NotificationEventDto::getKind)
                    .toList());

    var msg = new FacesMessage(channel.getDisplayName() + " notification channel changes saved");
    FacesContext.getCurrentInstance().addMessage("channelSaveSuccess", msg);
  }

  public void open() {
    channel.pushChannel().open();
  }
}
