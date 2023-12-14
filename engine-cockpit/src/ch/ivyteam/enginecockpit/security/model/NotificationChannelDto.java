package ch.ivyteam.enginecockpit.security.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;
import ch.ivyteam.ivy.notification.channel.NotificationSubscription;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;

public class NotificationChannelDto {

  private final NotificationChannel channel;
  private final Map<String, NotificationChannelSubscriptionDto> subscriptions;

  private NotificationChannelDto(NotificationChannel channel,
          Map<String, NotificationChannelSubscriptionDto> subscriptions) {
    this.channel = channel;
    this.subscriptions = subscriptions;
  }

  public static List<NotificationChannelDto> all(ISecurityMember subscriber, ISecurityContext securityContext,
          List<String> events) {
    var channels = NotificationChannel.all(securityContext).stream()
            .filter(channel -> channel.config().enabled())
            .map(channel -> toChannel(subscriber, channel))
            .toList();
    channels.forEach(channel -> events.forEach(event -> channel.setSubscriptionIconAndTitle(event)));
    return channels;
  }

  private static NotificationChannelDto toChannel(ISecurityMember subscriber, NotificationChannel channel) {
    var subscriptions = channel.configFor(subscriber).subscriptions().stream()
            .collect(Collectors.toMap(
                    NotificationSubscription::event,
                    subscription -> new NotificationChannelSubscriptionDto(
                            subscription.state(),
                            subscription.isSubscribedByDefault())));
    return new NotificationChannelDto(channel, subscriptions);
  }

  public NotificationChannel getChannel() {
    return channel;
  }

  public Map<String, NotificationChannelSubscriptionDto> getSubscriptions() {
    return subscriptions;
  }

  public NotificationChannelSubscriptionDto getSubscription(String event) {
    return subscriptions.get(event);
  }

  public void setSubscriptionIconAndTitle(String event) {
    var subscription = subscriptions.get(event);
    var state = subscription.getState();

    boolean subscribedByUser = state.equals(NotificationChannelSubscriptionDto.State.SUBSCRIBED);

    boolean useDefault = state.equals(NotificationChannelSubscriptionDto.State.USE_DEFAULT);

    var icon = new StringBuilder();
    var iconTitle = new StringBuilder();

    if (subscribedByUser || (useDefault && subscription.isSubscribedByDefault())) {
      icon.append("check-circle-1 state-active");
      iconTitle.append("Subscribed");
    } else {
      icon.append("remove-circle state-inactive");
      iconTitle.append("Not subscribed");
    }

    if (useDefault) {
      icon.append(" light");
      iconTitle.append(" by default");
    }

    subscription.setIcon(icon.toString());
    subscription.setTitle(iconTitle.toString());
  }
 }
