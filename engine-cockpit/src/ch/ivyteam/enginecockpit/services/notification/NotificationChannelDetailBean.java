package ch.ivyteam.enginecockpit.services.notification;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.NotificationChannelMonitor;
import ch.ivyteam.enginecockpit.services.notification.NotificationChannelDto.NotificationEventDto;
import ch.ivyteam.enginecockpit.system.ManagerBean;
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
    liveStats = new NotificationChannelMonitor(this.channelId, channel.getDisplayName());
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
