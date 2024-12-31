package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.ivy.security.ISecurityContext;

public class NotificationChannelMonitor {

  private ISecurityContext securityContext;
  private NotificationChannel channel;

  public NotificationChannelMonitor(ISecurityContext securityContext, String channelId, String channelName) {
    try {
      this.securityContext = securityContext;
      var channels = searchJmx(channelId);
      channel = channels.stream()
          .map(name -> new NotificationChannel(name, channelName))
          .findFirst()
          .orElse(NotificationChannel.NO_DATA);
    } catch (MalformedObjectNameException ex) {
      channel = NotificationChannel.NO_DATA;
    }
  }

  private Set<ObjectName> searchJmx(String channelId)
      throws MalformedObjectNameException {
    return ManagementFactory.getPlatformMBeanServer().queryNames(
        new ObjectName("ivy Engine:type=Notification Channel,securityContext=" + securityContext.getName() + ",id=" + channelId),
        null);
  }

  public String getChannel() {
    return channel.label();
  }

  public Monitor getDeliveriesMonitor() {
    return channel.getDeliveriesMonitor();
  }

  public Monitor getLocksMonitor() {
    return channel.getLocksMonitor();
  }

  public Monitor getPushesTimeMonitor() {
    return channel.getPushesTimeMonitor();
  }
}
