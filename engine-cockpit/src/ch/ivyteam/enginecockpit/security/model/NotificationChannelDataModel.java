package ch.ivyteam.enginecockpit.security.model;

import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import ch.ivyteam.ivy.notification.event.NotificationEvent;
import ch.ivyteam.ivy.notification.subscription.NewNotificationSubscription;
import ch.ivyteam.ivy.notification.subscription.impl.NotificationSubscriptionRepository;
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
    events = NotificationEvent.all().stream()
            .map(NotificationEvent::kind)
            .toList();
    channels = NotificationChannelDto.all(subscriber, securityContext);
  }

  public void reset() {
    channels.forEach(this::resetChannel);
    addMessage("Notification Channels reset");
  }

  private void resetChannel(NotificationChannelDto channel) {
    channel.getSubscriptions().replaceAll((event, state) -> state = NotificationChannelDto.DEFAULT);
    saveChannel(channel);
  }

  public void save() {
    channels.forEach(this::saveChannel);
    addMessage("Notification Channels changes saved");
  }

  private void saveChannel(NotificationChannelDto channel) {
    channel.getSubscriptions().forEach((event, state) -> {
      saveSubscription(channel, event, state);
    });
  }

  private void saveSubscription(NotificationChannelDto channel, String event, String state) {
    var subscriptions = NotificationSubscriptionRepository.instance();
    if (state.equals(NotificationChannelDto.DEFAULT)) {
      subscriptions.delete(subscriber, channel.getChannel(), event);
      return;
    }

    var subscription = subscriptions.find(subscriber, channel.getChannel(), event);
    if (subscription != null) {
      subscription.subscribed(state.equals(NotificationChannelDto.SUBSCRIBED));
      return;
    }

    subscriptions.create(NewNotificationSubscription.create()
            .channel(channel.getChannel().id())
            .kind(event)
            .subscriber(subscriber)
            .subscribed(state.equals(NotificationChannelDto.SUBSCRIBED))
            .toNewNotificationSubscription());
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
