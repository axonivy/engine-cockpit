package ch.ivyteam.enginecockpit.monitor.mbeans.tomcat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
public class TestRequestMonitorBean {
  @BeforeEach
  public void beforeEach() {
    MBeans.registerMBeanFor(new Connector("http"));
    MBeans.registerMBeanFor(new Connector("https"));
    MBeans.registerMBeanFor(new ProtocolHandler(8080, "http"));
    MBeans.registerMBeanFor(new ProtocolHandler(8443, "https"));
  }

  @AfterEach
  public void afterEach() {
    MBeans.unregisterAllMBeans();
  }

  @Test
  public void requestMonitor() {
    var testee = new RequestMonitorBean();

    var series = testee.getRequestsMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);

    var http = series.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    var https = series.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getRequestsMonitor().getInfo())
            .isEqualTo("Requests: Http -, Http Total 300, Https -, Https Total 300");
  }

  @Test
  public void errorsMonitor() {
    var testee = new RequestMonitorBean();

    var series = testee.getErrorsMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);

    var http = series.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    var https = series.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getErrorsMonitor().getInfo())
            .isEqualTo("Errors: Http -, Http Total 4, Https -, Https Total 4");
  }

  @Test
  public void bytesMonitor() {
    var testee = new RequestMonitorBean();

    var series = testee.getBytesMonitor().getModel().getSeries();
    assertThat(series).hasSize(4);

    var httpSent = series.get(0);
    assertThat(httpSent.getLabel()).isEqualTo("Http Sent");
    assertThat(httpSent.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    var httpReceived = series.get(1);
    assertThat(httpReceived.getLabel()).isEqualTo("Http Received");
    assertThat(httpReceived.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    var httpsSent = series.get(2);
    assertThat(httpsSent.getLabel()).isEqualTo("Https Sent");
    assertThat(httpsSent.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    var httpsReceived = series.get(3);
    assertThat(httpsReceived.getLabel()).isEqualTo("Https Received");
    assertThat(httpsReceived.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getBytesMonitor().getInfo()).isEqualTo(
            "Bytes: Http Sent -/1000 B, Http Received -/2000 B, Https Sent -/1000 B, Https Received -/2000 B");
  }

  @Test
  public void processingMonitor() {
    var testee = new RequestMonitorBean();

    var series = testee.getProcessingTimeMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);

    var http = series.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    var https = series.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getProcessingTimeMonitor().getInfo())
            .isEqualTo("Processing Time: Http -, Http Total 5000 ms, Https -, Https Total 5000 ms");
  }

  @Test
  public void connectionsMonitor() {
    var testee = new RequestMonitorBean();

    var series = testee.getConnectionsMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);

    var http = series.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(2.0));

    var https = series.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(1.0));

    assertThat(testee.getConnectionsMonitor().getInfo())
            .isEqualTo("Connections: Http 2, Http Max 8192, Https 1, Https Max 4096");
  }

  @MBean("ivy:type=GlobalRequestProcessor,name=#{name}")
  private static final class Connector {
    private final String name;

    public Connector(String name) {
      this.name = name;
    }

    @SuppressWarnings("unused")
    public String getName() {
      return name;
    }

    @MAttribute
    public long getBytesSent() {
      return 1000;
    }

    @MAttribute
    public long getBytesReceived() {
      return 2000;
    }

    @MAttribute
    public long getRequestCount() {
      return 300;
    }

    @MAttribute
    public long getErrorCount() {
      return 4;
    }

    @MAttribute
    public long getProcessingTime() {
      return 5000;
    }
  }

  @MBean("ivy:type=ProtocolHandler,port=#{port}")
  private static final class ProtocolHandler {
    private final int port;
    private final String protocol;

    public ProtocolHandler(int port, String protocol) {
      this.port = port;
      this.protocol = protocol;
    }

    @MAttribute
    public String getName() {
      return "\"" + protocol + "-nio-" + port + "\"";
    }

    @MAttribute
    public long getConnectionCount() {
      return protocol.equals("http") ? 2 : 1;
    }

    @MAttribute
    public long getMaxConnections() {
      return protocol.equals("http") ? 8192 : 4096;
    }
  }

}
