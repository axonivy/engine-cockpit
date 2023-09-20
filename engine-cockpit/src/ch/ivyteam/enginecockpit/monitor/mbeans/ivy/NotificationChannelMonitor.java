package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import java.lang.management.ManagementFactory;
import java.util.Set;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;

public class NotificationChannelMonitor {

  private NotificationChannel channel;

  public NotificationChannelMonitor(String channelId, String channelName) {
    try {
      var channels = searchJmx(channelId);
      channel = channels.stream()
              .map(name -> new NotificationChannel(name, channelName))
              .findFirst()
              .orElse(NotificationChannel.NO_DATA);
    } catch (MalformedObjectNameException ex) {
      channel = NotificationChannel.NO_DATA;
    }
  }

  private static Set<ObjectName> searchJmx(String channelId)
          throws MalformedObjectNameException {
    return ManagementFactory.getPlatformMBeanServer().queryNames(
            new ObjectName("ivy Engine:type=Notification Channel,id=" + channelId),
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
