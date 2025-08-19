package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import java.lang.management.ManagementFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.util.CmsUtil;
import ch.ivyteam.ivy.environment.Ivy;

@ManagedBean
@ViewScoped
public class ClusterMonitorBean {
  private static final ObjectName CLUSTER_CHANNEL;

  static {
    try {
      CLUSTER_CHANNEL = new ObjectName("ivy Engine:type=Cluster Channel");
    } catch (MalformedObjectNameException ex) {
      throw new IllegalArgumentException("Wrong object name", ex);
    }
  }

  private final Monitor sendMessagesMonitor;
  private final Monitor sendProcessingTimeMonitor;
  private final Monitor receiveMessagesMonitor;
  private final Monitor receiveProcessingTimeMonitor;

  public ClusterMonitorBean() {
    sendMessagesMonitor = Monitor.build().name(Ivy.cm().co("/liveStats/SentMessagesMonitor")).icon("dns").toMonitor();
    sendProcessingTimeMonitor = Monitor.build().name(Ivy.cm().co("/liveStats/SendProcessingTimeMonitor")).icon("timer")
        .yAxisLabel(Ivy.cm().co("/common/Time")).toMonitor();
    receiveMessagesMonitor =
        Monitor.build().name(Ivy.cm().co("/liveStats/ReceivedMessagesMonitor")).icon("dns").toMonitor();
    receiveProcessingTimeMonitor = Monitor.build().name(Ivy.cm().co("/liveStats/ReceiveProcessingTimeMonitor"))
        .icon("timer").yAxisLabel(Ivy.cm().co("/common/Time")).toMonitor();

    if (ManagementFactory.getPlatformMBeanServer().queryNames(CLUSTER_CHANNEL, null).isEmpty()) {
      return;
    }
    var sendMessages = new ExecutionCounter(CLUSTER_CHANNEL.getCanonicalName(), "sendMessages", "sendErrors");
    var receivedMessages = new ExecutionCounter(CLUSTER_CHANNEL.getCanonicalName(), "receiveMessages",
        "receiveErrors");

    sendMessagesMonitor.addInfoValue(format("%5d", sendMessages.deltaExecutions()));
    sendMessagesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Total", "Total") + " %5d", sendMessages.executions()));
    sendMessagesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Errors", "Errors") + " %5d", sendMessages.deltaErrors()));
    sendMessagesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/ErrorsTotal", "Errors Total") + " %5d", sendMessages.errors()));

    sendMessagesMonitor.addSeries(Series.build(sendMessages.deltaExecutions(), CmsUtil.coWithDefault("/liveStats/Sent", "Sent")).toSeries());
    sendMessagesMonitor.addSeries(Series.build(sendMessages.deltaErrors(), CmsUtil.coWithDefault("/common/Errors", "Errors")).toSeries());

    sendProcessingTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Min", "Min") + " %t", sendMessages.deltaMinExecutionTime()));
    sendProcessingTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Avg", "Avg") + " %t", sendMessages.deltaAvgExecutionTime()));
    sendProcessingTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Max", "Max") + " %t", sendMessages.deltaMaxExecutionTime()));
    sendProcessingTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Total", "Total") + " %t", sendMessages.executionTime()));

    sendProcessingTimeMonitor.addSeries(Series.build(sendMessages.deltaMinExecutionTime(), CmsUtil.coWithDefault("/liveStats/Min", "Min")).toSeries());
    sendProcessingTimeMonitor.addSeries(Series.build(sendMessages.deltaAvgExecutionTime(), CmsUtil.coWithDefault("/liveStats/Avg", "Avg")).toSeries());
    sendProcessingTimeMonitor.addSeries(Series.build(sendMessages.deltaMaxExecutionTime(), CmsUtil.coWithDefault("/liveStats/Min", "Max")).toSeries());

    receiveMessagesMonitor.addInfoValue(format("%5d", receivedMessages.deltaExecutions()));
    receiveMessagesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Total", "Total") + " %5d", receivedMessages.executions()));
    receiveMessagesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Errors", "Errors") + " %5d", receivedMessages.deltaErrors()));
    receiveMessagesMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/ErrorsTotal", "Errors Total") + " %5d", receivedMessages.errors()));

    receiveMessagesMonitor.addSeries(Series.build(receivedMessages.deltaExecutions(), CmsUtil.coWithDefault("/liveStats/Received", "Received")).toSeries());
    receiveMessagesMonitor.addSeries(Series.build(receivedMessages.deltaErrors(), CmsUtil.coWithDefault("/common/Errors", "Errors")).toSeries());

    receiveProcessingTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Min", "Min") + " %t", receivedMessages.deltaMinExecutionTime()));
    receiveProcessingTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Avg", "Avg") + " %t", receivedMessages.deltaAvgExecutionTime()));
    receiveProcessingTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/liveStats/Max", "Max") + " %t", receivedMessages.deltaMaxExecutionTime()));
    receiveProcessingTimeMonitor.addInfoValue(format(CmsUtil.coWithDefault("/common/Total", "Total") + " %t", receivedMessages.executionTime()));

    receiveProcessingTimeMonitor
        .addSeries(Series.build(receivedMessages.deltaMinExecutionTime(), Ivy.cm().co("/liveStats/Min")).toSeries());
    receiveProcessingTimeMonitor
        .addSeries(Series.build(receivedMessages.deltaAvgExecutionTime(), Ivy.cm().co("/liveStats/Avg")).toSeries());
    receiveProcessingTimeMonitor
        .addSeries(Series.build(receivedMessages.deltaMaxExecutionTime(), Ivy.cm().co("/liveStats/Max")).toSeries());
  }

  public Monitor getSendMessagesMonitor() {
    return sendMessagesMonitor;
  }

  public Monitor getSendProcessingTimeMonitor() {
    return sendProcessingTimeMonitor;
  }

  public Monitor getReceiveMessagesMonitor() {
    return receiveMessagesMonitor;
  }

  public Monitor getReceiveProcessingTimeMonitor() {
    return receiveProcessingTimeMonitor;
  }
}
