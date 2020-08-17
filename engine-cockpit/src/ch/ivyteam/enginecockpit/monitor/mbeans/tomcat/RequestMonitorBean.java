package ch.ivyteam.enginecockpit.monitor.mbeans.tomcat;

import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.delta;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.derivation;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.format;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.scale;

import java.lang.management.ManagementFactory;
import java.util.Set;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang.StringUtils;

import ch.ivyteam.enginecockpit.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MMonitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MSeries;
import ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider;

@ManagedBean
@ViewScoped
public class RequestMonitorBean
{
  private final MMonitor requestsMonitor = MMonitor.build().name("Requests").icon("rss_feed").toMonitor();
  private final MMonitor errorsMonitor = MMonitor.build().name("Errors").icon("error_outline").toMonitor();
  private final MMonitor bytesMonitor = MMonitor.build().name("Bytes").icon("network_check").yAxisLabel("Bytes [kb]").toMonitor();
  private final MMonitor processingTimeMonitor = MMonitor.build().name("Processing Time").icon("timer").yAxisLabel("Time [ms]").toMonitor();
  
  public RequestMonitorBean()
  {
    try
    {
      Set<ObjectName> requestProcessors = ManagementFactory.getPlatformMBeanServer().queryNames(new ObjectName("ivy:type=GlobalRequestProcessor,name=*"), null);
      for (ObjectName requestProcessor : requestProcessors)
      {
        setupRequestProcessingMonitors(requestProcessor);      
      }
    }
    catch(MalformedObjectNameException ex)
    {
      throw new RuntimeException(ex);
    }
  }

  private void setupRequestProcessingMonitors(ObjectName requestProcessor)
  {
    String label = requestProcessor.getKeyProperty("name");
    label = StringUtils.substringBefore(label, "-");
    label = StringUtils.removeStart(label, "\"");
    label = StringUtils.capitalize(label);
    setupRequestsMonitor(requestProcessor, label);
    setupErrorsMonitor(requestProcessor, label);
    setupBytesMonitor(requestProcessor, label);
    setupProcessTimeMonitor(requestProcessor, label);
  }

  private void setupRequestsMonitor(ObjectName requestProcessor, String label)
  {
    requestsMonitor.addInfoValue(format(label +" %d", deltaRequestCount(requestProcessor)));
    requestsMonitor.addInfoValue(format(label +" Total %d", requestCount(requestProcessor)));
    requestsMonitor.addSeries(new MSeries(deltaRequestCount(requestProcessor), label));
  }

  private void setupErrorsMonitor(ObjectName requestProcessor, String label)
  {
    errorsMonitor.addInfoValue(format(label +" %d", deltaErrorCount(requestProcessor)));
    errorsMonitor.addInfoValue(format(label +" Total %d", errorCount(requestProcessor)));
    errorsMonitor.addSeries(new MSeries(deltaErrorCount(requestProcessor), label));
  }

  private void setupBytesMonitor(ObjectName requestProcessor, String label)
  {
    bytesMonitor.addInfoValue(format(label +" Sent %d/%d [kB]", deltaBytesSent(requestProcessor), bytesSent(requestProcessor)));
    bytesMonitor.addInfoValue(format(label +" Received %d/%d [kB]", deltaBytesReceived(requestProcessor), bytesReceived(requestProcessor)));
  
    bytesMonitor.addSeries(new MSeries(deltaBytesSent(requestProcessor), label+" Sent"));
    bytesMonitor.addSeries(new MSeries(deltaBytesReceived(requestProcessor), label+" Received"));
  }

  private void setupProcessTimeMonitor(ObjectName requestProcessor, String label)
  {
    processingTimeMonitor.addInfoValue(format(label +" %d ms", deltaProcessingTime(requestProcessor)));
    processingTimeMonitor.addInfoValue(format(label +" Total %d ms", processingTime(requestProcessor)));
    processingTimeMonitor.addSeries(new MSeries(deltaProcessingTime(requestProcessor), label));
  }

  public Monitor getRequestsMonitor()
  {
    return requestsMonitor;
  }
  
  public Monitor getErrorsMonitor()
  {
    return errorsMonitor;
  }
  
  public Monitor getBytesMonitor()
  {
    return bytesMonitor;
  }

  public Monitor getProcessingTimeMonitor()
  {
    return processingTimeMonitor;
  }
  
  private MValueProvider deltaProcessingTime(ObjectName requestProcessor)
  {
    return derivation(
        processingTime(requestProcessor),
        attribute(requestProcessor, "requestCount")
     );
  }

  private MValueProvider processingTime(ObjectName requestProcessor)
  {
    return attribute(requestProcessor, "processingTime");
  }

  private MValueProvider deltaErrorCount(ObjectName requestProcessor)
  {
    return delta(errorCount(requestProcessor));
  }

  private MValueProvider errorCount(ObjectName requestProcessor)
  {
    return attribute(requestProcessor, "errorCount");
  }

  private MValueProvider deltaRequestCount(ObjectName requestProcessor)
  {
    return delta(requestCount(requestProcessor));
  }

  private MValueProvider requestCount(ObjectName requestProcessor)
  {
    return attribute(requestProcessor, "requestCount");
  }

  private MValueProvider deltaBytesSent(ObjectName requestProcessor)
  {
    return delta(bytesSent(requestProcessor));
  }

  private MValueProvider bytesSent(ObjectName requestProcessor)
  {
    return scale(attribute(requestProcessor, "bytesSent"), 1024);
  }

  private MValueProvider deltaBytesReceived(ObjectName requestProcessor)
  {
    return delta(bytesReceived(requestProcessor));
  }

  private MValueProvider bytesReceived(ObjectName requestProcessor)
  {
    return scale(attribute(requestProcessor, "bytesReceived"), 1024);
  }

  
}
