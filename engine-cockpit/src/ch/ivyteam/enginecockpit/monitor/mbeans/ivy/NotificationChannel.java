package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;
import java.lang.management.ManagementFactory;
import java.util.stream.Stream;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;

final class NotificationChannel {
  public static final NotificationChannel NO_DATA = new NotificationChannel(null, null);

  private final Monitor deliveriesMonitor = Monitor.build()
          .name("Deliveries")
          .title("Channel Deliveries")
          .icon("insert_link")
          .toMonitor();

  private final Monitor pushesTimeMonitor = Monitor.build()
          .name("Pushes")
          .title("Channel Pushes Time")
          .icon("timer")
          .toMonitor();

  private final Monitor locksMonitor = Monitor.build()
          .name("Locks")
          .title("Channel Locks")
          .icon("timer")
          .toMonitor();

  private String label;

  NotificationChannel(ObjectName channel, String label) {
    if (channel == null) {
      deliveriesMonitor.addInfoValue(format("No data available"));
      locksMonitor.addInfoValue(format("No data available"));
      return;
    }

    this.label = label;

    var deliveries = new ExecutionCounter(channel.getCanonicalName(), "deliveries");
    deliveriesMonitor.addInfoValue(format("%5d", deliveries.deltaExecutions()));
    deliveriesMonitor.addInfoValue(format("Total Deliveres %5d", deliveries.executions()));
    deliveriesMonitor.addSeries(Series.build(deliveries.deltaExecutions(), "Deliveres").toSeries());
    var isPush = isPush(channel);
    if (isPush) {
      var errors = new ExecutionCounter(channel.getCanonicalName(), "errors");
      var locks = new ExecutionCounter(channel.getCanonicalName(), "locks");
      var pushes = new ExecutionCounter(channel.getCanonicalName(), "pushes");
      deliveriesMonitor.addInfoValue(format("Errors %5d", errors.deltaExecutions()));
      deliveriesMonitor.addInfoValue(format("Total Errors %5d", errors.executions()));
      deliveriesMonitor.addInfoValue(format("Pushes %5d", pushes.deltaExecutions()));
      deliveriesMonitor.addInfoValue(format("Total Pushes %5d", pushes.executions()));
      deliveriesMonitor.addSeries(Series.build(errors.deltaExecutions(), "Errors").toSeries());
      deliveriesMonitor.addSeries(Series.build(pushes.deltaExecutions(), "Pushes").toSeries());
      locksMonitor.addInfoValue(format("%5d", locks.executions()));
      locksMonitor.addSeries(Series.build(locks.executions(), "Locks").toSeries());

      pushesTimeMonitor.addInfoValue(format("Min %t", pushes.deltaMinExecutionTime()));
      pushesTimeMonitor.addInfoValue(format("Avg %t", pushes.deltaAvgExecutionTime()));
      pushesTimeMonitor.addInfoValue(format("Max %t", pushes.deltaMaxExecutionTime()));
      pushesTimeMonitor.addInfoValue(format("Total %t", pushes.executionTime()));
      pushesTimeMonitor.addSeries(Series.build(pushes.deltaMinExecutionTime(), "Min").toSeries());
      pushesTimeMonitor.addSeries(Series.build(pushes.deltaAvgExecutionTime(), "Avg").toSeries());
      pushesTimeMonitor.addSeries(Series.build(pushes.deltaMaxExecutionTime(), "Max").toSeries());
    }
  }

  private static boolean isPush(ObjectName channel) {
    try {
      return Stream
          .of(ManagementFactory.getPlatformMBeanServer().getMBeanInfo(channel).getAttributes())
          .anyMatch(attr -> attr.getName().equals("errors"));
    } catch (IntrospectionException | InstanceNotFoundException | ReflectionException ex) {
      return false;
    }
  }

  String label() {
    return label;
  }

  Monitor getDeliveriesMonitor() {
    return deliveriesMonitor;
  }

  Monitor getLocksMonitor() {
    return locksMonitor;
  }

  Monitor getPushesTimeMonitor() {
    return pushesTimeMonitor;
  }
}
