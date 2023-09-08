package ch.ivyteam.enginecockpit.security.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import ch.ivyteam.ivy.notification.channel.NotificationChannel;
import ch.ivyteam.ivy.notification.channel.impl.NotificationChannelConfig;
import ch.ivyteam.ivy.notification.subscription.NotificationSubscription;
import ch.ivyteam.ivy.notification.subscription.impl.NotificationSubscriptionRepository;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;

public class NotificationChannelDto {

  private final NotificationChannel channel;
  private final Map<String, String> subscriptions;
  private final Map<String, String> subscriptionIcons = new HashMap<>();
  private final Map<String, String> subscriptionIconTitles = new HashMap<>();

  private final boolean allEventsEnabledByDefault;
  private final List<String> subscriptionsByDefault;

  public static final String DEFAULT = "0";
  public static final String SUBSCRIBED = "1";
  public static final String NOT_SUBSCRIBED = "2";

  private NotificationChannelDto(NotificationChannel channel, Map<String, String> events, NotificationChannelConfig config) {
    this.channel = channel;
    this.subscriptions = events;
    this.allEventsEnabledByDefault = config.allEventsEnabled();
    this.subscriptionsByDefault = config.events();
  }

  public static List<NotificationChannelDto> all(ISecurityMember subscriber, ISecurityContext securityContext) {
    var subscriptions = NotificationSubscriptionRepository.instance().findBySubscriber(subscriber).stream()
            .collect(Collectors.groupingBy(NotificationSubscription::channel));

    return NotificationChannel.all().stream()
            .filter(channel -> new NotificationChannelConfig(securityContext, channel).enabled())
            .map(channel -> mapChannel(channel,
                    subscriptions.getOrDefault(channel.id(), Collections.emptyList()),
                    securityContext))
            .toList();
  }

  private static NotificationChannelDto mapChannel(NotificationChannel channel,
          List<NotificationSubscription> subscriptions, ISecurityContext securityContext) {
    var channelSubscriptions = subscriptions.stream()
            .collect(Collectors.toMap(NotificationSubscription::kind,
                    subscription -> subscription.subscribed() ? SUBSCRIBED : NOT_SUBSCRIBED));
    var config = new NotificationChannelConfig(securityContext, channel);
    return new NotificationChannelDto(channel, channelSubscriptions, config);
  }

  public NotificationChannel getChannel() {
    return channel;
  }

  public Map<String, String> getSubscriptions() {
    return subscriptions;
  }

  public boolean isAllEventsEnabledByDefault() {
    return allEventsEnabledByDefault;
  }

  public List<String> getSubscriptionsByDefault() {
    return subscriptionsByDefault;
  }

  public String getSubscriptionIcon(String event) {
    return subscriptionIcons.get(event);
  }

  public String getSubscriptionIconTitle(String event) {
    return subscriptionIconTitles.get(event);
  }

  public void setSubscriptionIconAndTitle(String event) {
    String state = subscriptions.get(event);

    boolean subscribedByUser = state != null && state.equals(SUBSCRIBED);

    boolean useDefault = state == null || state.equals(DEFAULT);
    boolean subscribedByDefault = allEventsEnabledByDefault || subscriptionsByDefault.contains(event);

    var icon = new StringBuilder();
    var iconTitle = new StringBuilder();

    if (subscribedByUser || (useDefault && subscribedByDefault)) {
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

    subscriptionIcons.put(event, icon.toString());
    subscriptionIconTitles.put(event, iconTitle.toString());
  }
 }
