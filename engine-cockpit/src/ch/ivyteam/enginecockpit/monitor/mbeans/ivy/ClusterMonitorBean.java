package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;

@ManagedBean
@ViewScoped
public class ClusterMonitorBean
{
  private static final ObjectName CLUSTER_CHANNEL;

  static
  {
    try
    {
      CLUSTER_CHANNEL = new ObjectName("ivy Engine:type=Cluster Channel");
    }
    catch (MalformedObjectNameException ex)
    {
      throw new IllegalArgumentException("Wrong object name", ex);
    }
  }

  private final Monitor sendMessagesMonitor;
  private final Monitor sendProcessingTimeMonitor;
  private final Monitor receiveMessagesMonitor;
  private final Monitor receiveProcessingTimeMonitor;

  public ClusterMonitorBean()
  {
    sendMessagesMonitor = Monitor.build().name("Send Messages").icon("dns").toMonitor();
    sendProcessingTimeMonitor = Monitor.build().name("Send Processing Time").icon("timer").yAxisLabel("Time")
            .toMonitor();
    receiveMessagesMonitor = Monitor.build().name("Receive Messages").icon("dns").toMonitor();
    receiveProcessingTimeMonitor = Monitor.build().name("Receive Processing Time").icon("timer")
            .yAxisLabel("Time").toMonitor();

    var sendMessages = new ExecutionCounter(CLUSTER_CHANNEL.getCanonicalName(), "sendMessages", "sendErrors");
    var receivedMessages = new ExecutionCounter(CLUSTER_CHANNEL.getCanonicalName(), "receiveMessages", "receiveErrors");

    sendMessagesMonitor.addInfoValue(format("%5d", sendMessages.deltaExecutions()));
    sendMessagesMonitor.addInfoValue(format("Total %5d", sendMessages.executions()));
    sendMessagesMonitor.addInfoValue(format("Errors %5d", sendMessages.deltaErrors()));
    sendMessagesMonitor.addInfoValue(format("Errors Total %5d", sendMessages.errors()));

    sendMessagesMonitor.addSeries(Series.build(sendMessages.deltaExecutions(), "Sent").toSeries());
    sendMessagesMonitor.addSeries(Series.build(sendMessages.deltaErrors(), "Errors").toSeries());

    sendProcessingTimeMonitor.addInfoValue(format("Min %t", sendMessages.deltaMinExecutionTime()));
    sendProcessingTimeMonitor.addInfoValue(format("Avg %t", sendMessages.deltaAvgExecutionTime()));
    sendProcessingTimeMonitor.addInfoValue(format("Max %t", sendMessages.deltaMaxExecutionTime()));
    sendProcessingTimeMonitor.addInfoValue(format("Total %t", sendMessages.executionTime()));

    sendProcessingTimeMonitor.addSeries(Series.build(sendMessages.deltaMinExecutionTime(), "Min").toSeries());
    sendProcessingTimeMonitor.addSeries(Series.build(sendMessages.deltaAvgExecutionTime(), "Avg").toSeries());
    sendProcessingTimeMonitor.addSeries(Series.build(sendMessages.deltaMaxExecutionTime(), "Max").toSeries());

    receiveMessagesMonitor.addInfoValue(format("%5d", receivedMessages.deltaExecutions()));
    receiveMessagesMonitor.addInfoValue(format("Total %5d", receivedMessages.executions()));
    receiveMessagesMonitor.addInfoValue(format("Errors %5d", receivedMessages.deltaErrors()));
    receiveMessagesMonitor.addInfoValue(format("Errors Total %5d", receivedMessages.errors()));

    receiveMessagesMonitor.addSeries(Series.build(receivedMessages.deltaExecutions(), "Received").toSeries());
    receiveMessagesMonitor.addSeries(Series.build(receivedMessages.deltaErrors(), "Errors").toSeries());

    receiveProcessingTimeMonitor.addInfoValue(format("Min %t", receivedMessages.deltaMinExecutionTime()));
    receiveProcessingTimeMonitor.addInfoValue(format("Avg %t", receivedMessages.deltaAvgExecutionTime()));
    receiveProcessingTimeMonitor.addInfoValue(format("Max %t", receivedMessages.deltaMaxExecutionTime()));
    receiveProcessingTimeMonitor.addInfoValue(format("Total %t", receivedMessages.executionTime()));

    receiveProcessingTimeMonitor
            .addSeries(Series.build(receivedMessages.deltaMinExecutionTime(), "Min").toSeries());
    receiveProcessingTimeMonitor
            .addSeries(Series.build(receivedMessages.deltaAvgExecutionTime(), "Avg").toSeries());
    receiveProcessingTimeMonitor
            .addSeries(Series.build(receivedMessages.deltaMaxExecutionTime(), "Max").toSeries());
  }

  public Monitor getSendMessagesMonitor()
  {
    return sendMessagesMonitor;
  }

  public Monitor getSendProcessingTimeMonitor()
  {
    return sendProcessingTimeMonitor;
  }

  public Monitor getReceiveMessagesMonitor()
  {
    return receiveMessagesMonitor;
  }

  public Monitor getReceiveProcessingTimeMonitor()
  {
    return receiveProcessingTimeMonitor;
  }
}
