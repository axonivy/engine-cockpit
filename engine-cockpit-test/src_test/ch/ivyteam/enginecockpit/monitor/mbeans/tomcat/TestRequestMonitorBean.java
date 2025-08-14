package ch.ivyteam.enginecockpit.monitor.mbeans.tomcat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.model.charts.line.LineChartDataSet;

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

    var dataSet = testee.getRequestsMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var http = (LineChartDataSet) dataSet.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var https = (LineChartDataSet) dataSet.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getRequestsMonitor().getInfo())
        .isEqualTo("Requests: Http -, Http Total 300, Https -, Https Total 300");
  }

  @Test
  public void errorsMonitor() {
    var testee = new RequestMonitorBean();

    var dataSet = testee.getErrorsMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var http = (LineChartDataSet) dataSet.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var https = (LineChartDataSet) dataSet.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getErrorsMonitor().getInfo())
        .isEqualTo("Errors: Http -, Http Total 4, Https -, Https Total 4");
  }

  @Test
  public void bytesMonitor() {
    var testee = new RequestMonitorBean();

    var dataSet = testee.getBytesMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(4);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var httpSent = (LineChartDataSet) dataSet.get(0);
    assertThat(httpSent.getLabel()).isEqualTo("Http Sent");
    assertThat(httpSent.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var httpReceived = (LineChartDataSet) dataSet.get(1);
    assertThat(httpReceived.getLabel()).isEqualTo("Http Received");
    assertThat(httpReceived.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(2)).isInstanceOf(LineChartDataSet.class);
    var httpsSent = (LineChartDataSet) dataSet.get(2);
    assertThat(httpsSent.getLabel()).isEqualTo("Https Sent");
    assertThat(httpsSent.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(3)).isInstanceOf(LineChartDataSet.class);
    var httpsReceived = (LineChartDataSet) dataSet.get(3);
    assertThat(httpsReceived.getLabel()).isEqualTo("Https Received");
    assertThat(httpsReceived.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getBytesMonitor().getInfo()).isEqualTo(
        "Bytes: Http Sent -/1000 B, Http Received -/2000 B, Https Sent -/1000 B, Https Received -/2000 B");
  }

  @Test
  public void processingMonitor() {
    var testee = new RequestMonitorBean();

    var dataSet = testee.getProcessingTimeMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var http = (LineChartDataSet) dataSet.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var https = (LineChartDataSet) dataSet.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getProcessingTimeMonitor().getInfo())
        .isEqualTo("Processing Time: Http -, Http Total 5000 ms, Https -, Https Total 5000 ms");
  }

  @Test
  public void connectionsMonitor() {
    var testee = new RequestMonitorBean();

    var dataSet = testee.getConnectionsMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var http = (LineChartDataSet) dataSet.get(0);
    assertThat(http.getLabel()).isEqualTo("Http");
    assertThat(http.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(2.0));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var https = (LineChartDataSet) dataSet.get(1);
    assertThat(https.getLabel()).isEqualTo("Https");
    assertThat(https.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(1.0));

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
      return "http".equals(protocol) ? 2 : 1;
    }

    @MAttribute
    public long getMaxConnections() {
      return "http".equals(protocol) ? 8192 : 4096;
    }
  }

}
