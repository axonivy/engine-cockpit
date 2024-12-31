package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.primefaces.model.charts.line.LineChartDataSet;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
class TestWebServiceMonitorBean {

  @AfterEach
  void afterEach() {
    MBeans.unregisterAllMBeans();
  }

  @Test
  void noData() {
    var testee = new WebServiceMonitor();
    assertThat(testee.getWebService()).isEqualTo("No Data");
    assertThat(testee.getCallsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }

  @Test
  void withData() throws Exception {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    MBeans.registerMBeanFor(new Ws("ws2 (2)"));
    var testee = new WebServiceMonitor("test", "1");
    assertThat(testee.getWebService()).isEqualTo("test > ws1");
    assertThat(testee.getCallsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
    testee = new WebServiceMonitor("test", "2");
    assertThat(testee.getWebService()).isEqualTo("test > ws2");
    assertThat(testee.getCallsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }

  @Test
  void callsMonitor() {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    var testee = new WebServiceMonitor("test", "1");

    var dataSet = testee.getCallsMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var calls = (LineChartDataSet) dataSet.get(0);
    assertThat(calls.getLabel()).isEqualTo("Calls");
    assertThat(calls.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var errors = (LineChartDataSet) dataSet.get(1);
    assertThat(errors.getLabel()).isEqualTo("Errors");
    assertThat(errors.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getCallsMonitor().getInfo()).isEqualTo("Calls: -, Total 3, Errors -, Errors Total 4");
  }

  @Test
  void executionTimeMonitor() {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    var testee = new WebServiceMonitor("test", "1");

    var dataSet = testee.getExecutionTimeMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(3);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var min = (LineChartDataSet) dataSet.get(0);
    assertThat(min.getLabel()).isEqualTo("Min");
    assertThat(min.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(5.0D));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var avg = (LineChartDataSet) dataSet.get(1);
    assertThat(avg.getLabel()).isEqualTo("Avg");
    assertThat(avg.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(2)).isInstanceOf(LineChartDataSet.class);
    var max = (LineChartDataSet) dataSet.get(2);
    assertThat(max.getLabel()).isEqualTo("Max");
    assertThat(max.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(7.0D));

    assertThat(testee.getExecutionTimeMonitor().getInfo())
        .isEqualTo("Execution Time: Min 5 us, Avg -, Max 7 us, Total 6 us");
  }

  @MBean("ivy Engine:type=External Web Service,application=test,name=\"#{name}\"")
  private static final class Ws {
    private final String name;

    public Ws(String name) {
      this.name = name;
    }

    @SuppressWarnings("unused")
    private String getName() {
      return name;
    }

    @MAttribute
    public long getCalls() {
      return 3;
    }

    @MAttribute
    public long getErrors() {
      return 4;
    }

    @MAttribute
    public long getCallsMinExecutionTimeDeltaInMicroSeconds() {
      return 5;
    }

    @MAttribute
    public long getCallsTotalExecutionTimeInMicroSeconds() {
      return 6;
    }

    @MAttribute
    public long getCallsMaxExecutionTimeDeltaInMicroSeconds() {
      return 7;
    }

  }
}
