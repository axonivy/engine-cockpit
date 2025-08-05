package ch.ivyteam.enginecockpit.monitor.mbeans.tomcat;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.delta;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.derivation;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.Strings;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

@ManagedBean
@ViewScoped
public class RequestMonitorBean {
  private final Monitor requestsMonitor = Monitor.build().name("Requests").icon("network-signal").toMonitor();
  private final Monitor errorsMonitor = Monitor.build().name("Errors").icon("global-warming-globe-fire")
      .toMonitor();
  private final Monitor bytesMonitor = Monitor.build().name("Bytes").icon("cd").yAxisLabel("Bytes")
      .toMonitor();
  private final Monitor processingTimeMonitor = Monitor.build().name("Processing Time")
      .icon("optimization-timer").yAxisLabel("Time").toMonitor();
  private final Monitor connectionsMonitor = Monitor.build().name("Connections").icon("insert_link-timer")
      .yAxisLabel("Connections").toMonitor();

  public RequestMonitorBean() {
    try {
      Set<ObjectName> requestProcessors = ManagementFactory.getPlatformMBeanServer()
          .queryNames(new ObjectName("ivy:type=GlobalRequestProcessor,name=*"), null);
      for (ObjectName requestProcessor : requestProcessors) {
        setupRequestProcessingMonitors(requestProcessor);
      }
      Set<ObjectName> protocolHandlers = ManagementFactory.getPlatformMBeanServer()
          .queryNames(new ObjectName("ivy:type=ProtocolHandler,port=*"), null);
      for (ObjectName protocolHandler : protocolHandlers) {
        setupProtocolHandlerMonitors(protocolHandler);
      }

    } catch (MalformedObjectNameException ex) {
      throw new RuntimeException(ex);
    }
  }

  private void setupRequestProcessingMonitors(ObjectName requestProcessor) {
    String label = requestProcessor.getKeyProperty("name");
    label = StringUtils.substringBefore(label, "-");
    label = Strings.CS.removeStart(label, "\"");
    label = StringUtils.capitalize(label);
    setupRequestsMonitor(requestProcessor, label);
    setupErrorsMonitor(requestProcessor, label);
    setupBytesMonitor(requestProcessor, label);
    setupProcessTimeMonitor(requestProcessor, label);
  }

  private void setupProtocolHandlerMonitors(ObjectName protocolHandler) {
    String label = attribute(protocolHandler, "name", Unit.ONE).nextValue().toString();
    label = StringUtils.substringBefore(label, "-");
    label = Strings.CS.removeStart(label, "\"");
    label = StringUtils.capitalize(label);
    setupConnectionsMonitor(protocolHandler, label);
  }

  private void setupRequestsMonitor(ObjectName requestProcessor, String label) {
    requestsMonitor.addInfoValue(format(label + " %5d", deltaRequestCount(requestProcessor)));
    requestsMonitor.addInfoValue(format(label + " Total %5d", requestCount(requestProcessor)));
    requestsMonitor.addSeries(Series.build(deltaRequestCount(requestProcessor), label).toSeries());
  }

  private void setupErrorsMonitor(ObjectName requestProcessor, String label) {
    errorsMonitor.addInfoValue(format(label + " %5d", deltaErrorCount(requestProcessor)));
    errorsMonitor.addInfoValue(format(label + " Total %5d", errorCount(requestProcessor)));
    errorsMonitor.addSeries(Series.build(deltaErrorCount(requestProcessor), label).toSeries());
  }

  private void setupBytesMonitor(ObjectName requestProcessor, String label) {
    bytesMonitor.addInfoValue(
        format(label + " Sent %5d/%5d", deltaBytesSent(requestProcessor), bytesSent(requestProcessor)));
    bytesMonitor.addInfoValue(format(label + " Received %5d/%5d", deltaBytesReceived(requestProcessor),
        bytesReceived(requestProcessor)));

    bytesMonitor.addSeries(Series.build(deltaBytesSent(requestProcessor), label + " Sent").toSeries());
    bytesMonitor
        .addSeries(Series.build(deltaBytesReceived(requestProcessor), label + " Received").toSeries());
  }

  private void setupProcessTimeMonitor(ObjectName requestProcessor, String label) {
    processingTimeMonitor.addInfoValue(format(label + " %t", deltaProcessingTime(requestProcessor)));
    processingTimeMonitor.addInfoValue(format(label + " Total %t", processingTime(requestProcessor)));
    processingTimeMonitor.addSeries(Series.build(deltaProcessingTime(requestProcessor), label).toSeries());
  }

  private void setupConnectionsMonitor(ObjectName protocolHandler, String label) {
    connectionsMonitor.addInfoValue(format(label + " %5d", openConnections(protocolHandler)));
    connectionsMonitor.addInfoValue(format(label + " Max %5d", maxConnections(protocolHandler)));
    connectionsMonitor.addSeries(Series.build(openConnections(protocolHandler), label).toSeries());
  }

  public Monitor getRequestsMonitor() {
    return requestsMonitor;
  }

  public Monitor getErrorsMonitor() {
    return errorsMonitor;
  }

  public Monitor getBytesMonitor() {
    return bytesMonitor;
  }

  public Monitor getProcessingTimeMonitor() {
    return processingTimeMonitor;
  }

  public Monitor getConnectionsMonitor() {
    return connectionsMonitor;
  }

  private ValueProvider deltaProcessingTime(ObjectName requestProcessor) {
    return derivation(
        processingTime(requestProcessor),
        attribute(requestProcessor, "requestCount", Unit.ONE));
  }

  private ValueProvider processingTime(ObjectName requestProcessor) {
    return attribute(requestProcessor, "processingTime", Unit.MILLI_SECONDS);
  }

  private ValueProvider deltaErrorCount(ObjectName requestProcessor) {
    return delta(errorCount(requestProcessor));
  }

  private ValueProvider errorCount(ObjectName requestProcessor) {
    return attribute(requestProcessor, "errorCount", Unit.ONE);
  }

  private ValueProvider deltaRequestCount(ObjectName requestProcessor) {
    return delta(requestCount(requestProcessor));
  }

  private ValueProvider requestCount(ObjectName requestProcessor) {
    return attribute(requestProcessor, "requestCount", Unit.ONE);
  }

  private ValueProvider deltaBytesSent(ObjectName requestProcessor) {
    return delta(bytesSent(requestProcessor));
  }

  private ValueProvider bytesSent(ObjectName requestProcessor) {
    return attribute(requestProcessor, "bytesSent", Unit.BYTES);
  }

  private ValueProvider deltaBytesReceived(ObjectName requestProcessor) {
    return delta(bytesReceived(requestProcessor));
  }

  private ValueProvider bytesReceived(ObjectName requestProcessor) {
    return attribute(requestProcessor, "bytesReceived", Unit.BYTES);
  }

  private ValueProvider openConnections(ObjectName protocolHandler) {
    return attribute(protocolHandler, "connectionCount", Unit.ONE);
  }

  private ValueProvider maxConnections(ObjectName protocolHandler) {
    return attribute(protocolHandler, "maxConnections", Unit.ONE);
  }
}
