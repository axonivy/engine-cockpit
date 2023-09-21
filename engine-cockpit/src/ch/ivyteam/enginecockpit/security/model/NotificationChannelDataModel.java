package ch.ivyteam.enginecockpit.security.model;

import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import ch.ivyteam.ivy.notification.channel.NotificationSubscription;
import ch.ivyteam.ivy.notification.event.NotificationEvent;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;

public class NotificationChannelDataModel {

  private final ISecurityMember subscriber;
  private final ISecurityContext securityContext;

  private List<String> events;
  private List<NotificationChannelDto> channels;

  private NotificationChannelDataModel(ISecurityMember subscriber, ISecurityContext securityContext) {
    this.subscriber = subscriber;
    this.securityContext = securityContext;
  }

  public void onload() {
    events = new ArrayList<>(NotificationEvent.allAsString());
    channels = NotificationChannelDto.all(subscriber, securityContext, events);
  }

  public void reset() {
    channels.forEach(this::resetChannel);
    onload();
    addMessage("Notification Channels reset");
  }

  private void resetChannel(NotificationChannelDto channel) {
    channel.getSubscriptions().forEach(
            (event, subscription) -> subscription.setState(NotificationChannelSubscriptionDto.State.USE_DEFAULT));
    saveChannel(channel);
  }

  public void save() {
    channels.forEach(this::saveChannel);
    addMessage("Notification Channels changes saved");
  }

  private void saveChannel(NotificationChannelDto channel) {
    channel.getSubscriptions().entrySet().forEach(eventSubscription -> {
      var subscription = NotificationSubscription.of(subscriber, channel.getChannel(), eventSubscription.getKey());
      subscription.state(eventSubscription.getValue().getState().toDbState());
    });
  }

  private void addMessage(String msg) {
    FacesContext.getCurrentInstance().addMessage("notificationMessage", new FacesMessage(msg));
  }

  public List<NotificationChannelDto> getChannels() {
    return channels;
  }

  public List<String> getEvents() {
    return events;
  }

  public static NotificationChannelDataModel instance(ISecurityMember member, ISecurityContext securityContext) {
    var model = new NotificationChannelDataModel(member, securityContext);
    model.onload();
    return model;
  }

}
