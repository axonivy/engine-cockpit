package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import java.lang.management.ManagementFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
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
    sendMessagesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/MessageMonitorTotalValue"), sendMessages.executions()));
    sendMessagesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/MessageMonitorErrorsValue"), sendMessages.deltaErrors()));
    sendMessagesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/MessageMonitorErrorsTotalValue"), sendMessages.errors()));

    sendMessagesMonitor.addSeries(Series.build(sendMessages.deltaExecutions(), Ivy.cm().co("/liveStats/MessagesMonitorSent")).toSeries());
    sendMessagesMonitor.addSeries(Series.build(sendMessages.deltaErrors(), Ivy.cm().co("/liveStats/MonitorErrors")).toSeries());

    sendProcessingTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ProcessingTimeMonitorMinValue"), sendMessages.deltaMinExecutionTime()));
    sendProcessingTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ProcessingTimeMonitorAvgValue"), sendMessages.deltaAvgExecutionTime()));
    sendProcessingTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ProcessingTimeMonitorMaxValue"), sendMessages.deltaMaxExecutionTime()));
    sendProcessingTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ProcessingTimeMonitorTotal"), sendMessages.executionTime()));

    sendProcessingTimeMonitor.addSeries(Series.build(sendMessages.deltaMinExecutionTime(), Ivy.cm().co("/liveStats/ProcessingTimeMonitorMin")).toSeries());
    sendProcessingTimeMonitor.addSeries(Series.build(sendMessages.deltaAvgExecutionTime(), Ivy.cm().co("/liveStats/ProcessingTimeMonitorAvg")).toSeries());
    sendProcessingTimeMonitor.addSeries(Series.build(sendMessages.deltaMaxExecutionTime(), Ivy.cm().co("/liveStats/ProcessingTimeMonitorMax")).toSeries());

    receiveMessagesMonitor.addInfoValue(format("%5d", receivedMessages.deltaExecutions()));
    receiveMessagesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/MessageMonitorTotalValue"), receivedMessages.executions()));
    receiveMessagesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/MessageMonitorErrorsValue"), receivedMessages.deltaErrors()));
    receiveMessagesMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/MessageMonitorErrorsTotalValue"), receivedMessages.errors()));

    receiveMessagesMonitor.addSeries(Series.build(receivedMessages.deltaExecutions(), Ivy.cm().co("/liveStats/MessagesMonitorReceived")).toSeries());
    receiveMessagesMonitor.addSeries(Series.build(receivedMessages.deltaErrors(), Ivy.cm().co("/liveStats/MonitorErrors")).toSeries());

    receiveProcessingTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ProcessingTimeMonitorMinValue"), receivedMessages.deltaMinExecutionTime()));
    receiveProcessingTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ProcessingTimeMonitorAvgValue"), receivedMessages.deltaAvgExecutionTime()));
    receiveProcessingTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ProcessingTimeMonitorMaxValue"), receivedMessages.deltaMaxExecutionTime()));
    receiveProcessingTimeMonitor.addInfoValue(format(Ivy.cm().co("/liveStats/ProcessingTimeMonitorTotal"), receivedMessages.executionTime()));

    receiveProcessingTimeMonitor
        .addSeries(Series.build(receivedMessages.deltaMinExecutionTime(), Ivy.cm().co("/liveStats/ProcessingTimeMonitorMin")).toSeries());
    receiveProcessingTimeMonitor
        .addSeries(Series.build(receivedMessages.deltaAvgExecutionTime(), Ivy.cm().co("/liveStats/ProcessingTimeMonitorAvg")).toSeries());
    receiveProcessingTimeMonitor
        .addSeries(Series.build(receivedMessages.deltaMaxExecutionTime(), Ivy.cm().co("/liveStats/ProcessingTimeMonitorMax")).toSeries());
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
