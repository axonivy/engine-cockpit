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
import ch.ivyteam.ivy.environment.Ivy;

final class NotificationChannel {
  public static final NotificationChannel NO_DATA = new NotificationChannel(null, null);

  private final Monitor deliveriesMonitor = Monitor.build()
      .name(Ivy.cm().co("/liveStats/Deliveries"))
      .title(Ivy.cm().co("/liveStats/ChannelDeliveries"))
      .icon("insert_link")
      .toMonitor();

  private final Monitor pushesTimeMonitor = Monitor.build()
      .name(Ivy.cm().co("/liveStats/Pushes"))
      .title(Ivy.cm().co("/liveStats/ChannelPushesTime"))
      .icon("timer")
      .toMonitor();

  private final Monitor locksMonitor = Monitor.build()
      .name(Ivy.cm().co("/liveStats/Locks"))
      .title(Ivy.cm().co("/liveStats/ChannelLocks"))
      .icon("timer")
      .toMonitor();

  private String label;

  NotificationChannel(ObjectName channel, String label) {
    if (channel == null) {
      deliveriesMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      locksMonitor.addInfoValue(format(Ivy.cm().co("/common/NoDataAvailable")));
      return;
    }

    this.label = label;

    var deliveries = new ExecutionCounter(channel.getCanonicalName(), "deliveries");
    deliveriesMonitor.addInfoValue(format("%5d", deliveries.deltaExecutions()));
    deliveriesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/DeliveriesMonitorTotalDeliveresValue"), deliveries.executions()));
    deliveriesMonitor.addSeries(Series.build(deliveries.deltaExecutions(), Ivy.cm().co("/liveStats/Deliveries")).toSeries());
    var isPush = isPush(channel);
    if (isPush) {
      var errors = new ExecutionCounter(channel.getCanonicalName(), "errors");
      var locks = new ExecutionCounter(channel.getCanonicalName(), "locks");
      var pushes = new ExecutionCounter(channel.getCanonicalName(), "pushes");
      deliveriesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/DeliveriesMonitorErrorsValue"), errors.deltaExecutions()));
      deliveriesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/DeliveriesMonitorTotalErrorsValue"), errors.executions()));
      deliveriesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/DeliveriesMonitorPushesValue"), pushes.deltaExecutions()));
      deliveriesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/DeliveriesMonitorTotalPushesValue"), pushes.executions()));
      deliveriesMonitor.addSeries(Series.build(errors.deltaExecutions(), Ivy.cm().co("/common/Errors")).toSeries());
      deliveriesMonitor.addSeries(Series.build(pushes.deltaExecutions(), Ivy.cm().co("/liveStats/Pushes")).toSeries());
      locksMonitor.addInfoValue(format("%5d", locks.executions()));
      locksMonitor.addSeries(Series.build(locks.executions(), Ivy.cm().co("/liveStats/Locks")).toSeries());

      pushesTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/PushesTimeMonitorMinValue"), pushes.deltaMinExecutionTime()));
      pushesTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/PushesTimeMonitorAvgValue"), pushes.deltaAvgExecutionTime()));
      pushesTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/PushesTimeMonitorMaxValue"), pushes.deltaMaxExecutionTime()));
      pushesTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/PushesTimeMonitorTotalValue"), pushes.executionTime()));
      pushesTimeMonitor.addSeries(Series.build(pushes.deltaMinExecutionTime(), Ivy.cm().co("/liveStats/PushesTimeMonitorMin")).toSeries());
      pushesTimeMonitor.addSeries(Series.build(pushes.deltaAvgExecutionTime(), Ivy.cm().co("/liveStats/PushesTimeMonitorAvg")).toSeries());
      pushesTimeMonitor.addSeries(Series.build(pushes.deltaMaxExecutionTime(), Ivy.cm().co("/liveStats/PushesTimeMonitorMax")).toSeries());
    }
  }

  private static boolean isPush(ObjectName channel) {
    try {
      return Stream
          .of(ManagementFactory.getPlatformMBeanServer().getMBeanInfo(channel).getAttributes())
          .anyMatch(attr -> "errors".equals(attr.getName()));
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
