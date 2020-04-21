package ch.ivyteam.enginecockpit.monitor.mbeans.tomcat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
public class TestRequestMonitorBean
{    
  @BeforeEach
  public void beforeEach()
  {
    MBeans.registerMBeanFor(new Connector("http"));
    MBeans.registerMBeanFor(new Connector("https"));
  }
  
  @AfterEach
  public void afterEach()
  {
    MBeans.unregisterAllMBeans();
  }

  @Test
  public void requestMonitor()
  {
    var testee = new RequestMonitorBean();
    
    var series = testee.getRequestsMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var http = series.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta 
    
    var https = series.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta
    
    assertThat(testee.getRequestsMonitor().getInfo()).isEqualTo("Requests: Http 0, Http Total 300, Https 0, Https Total 300");
  }

  @Test
  public void errorsMonitor()
  {
    var testee = new RequestMonitorBean();
    
    var series = testee.getErrorsMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var http = series.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta 
    
    var https = series.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta
    
    assertThat(testee.getErrorsMonitor().getInfo()).isEqualTo("Errors: Http 0, Http Total 4, Https 0, Https Total 4");
  }
  
  @Test
  public void bytesMonitor()
  {
    var testee = new RequestMonitorBean();
    
    var series = testee.getBytesMonitor().getModel().getSeries();
    assertThat(series).hasSize(4);
    
    var httpSent = series.get(0);
    assertThat(httpSent.getLabel()).isEqualTo("Http Sent");
    assertThat(httpSent.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta 
    
    var httpReceived = series.get(1);
    assertThat(httpReceived.getLabel()).isEqualTo("Http Received");
    assertThat(httpReceived.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta
    
    var httpsSent = series.get(2);
    assertThat(httpsSent.getLabel()).isEqualTo("Https Sent");
    assertThat(httpsSent.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta 
    
    var httpsReceived = series.get(3);
    assertThat(httpsReceived.getLabel()).isEqualTo("Https Received");
    assertThat(httpsReceived.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta

    assertThat(testee.getBytesMonitor().getInfo()).isEqualTo("Bytes: Http Sent 0/0 [kB], Http Received 0/1 [kB], Https Sent 0/0 [kB], Https Received 0/1 [kB]");
  }

  @Test
  public void processingMonitor()
  {
    var testee = new RequestMonitorBean();
    
    var series = testee.getProcessingTimeMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var http = series.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0)); // delta 
    
    var https = series.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0)); // delta
    
    assertThat(testee.getProcessingTimeMonitor().getInfo()).isEqualTo("Processing Time: Http 0 ms, Http Total 5000 ms, Https 0 ms, Https Total 5000 ms");
  }
  
  @MBean("ivy:type=GlobalRequestProcessor,name=#{name}")
  private static final class Connector
  {
    private final String name;

    public Connector(String name)
    {
      this.name = name;
    }
    
    @SuppressWarnings("unused")
    public String getName()
    {
      return name;      
    }
    
    @MAttribute
    public long getBytesSent()
    {
      return 1000;
    }

    @MAttribute
    public long getBytesReceived()
    {
      return 2000;
    }

    @MAttribute
    public long getRequestCount()
    {
      return 300;
    }

    @MAttribute
    public long getErrorCount()
    {
      return 4;
    }

    @MAttribute
    public long getProcessingTime()
    {
      return 5000;
    }
  }
}
