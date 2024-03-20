package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.primefaces.model.charts.line.LineChartDataSet;

public class TestJvmMonitorBean {
  @Test
  public void cpuMonitor() {
    var testee = new JvmMonitorBean();

    var dataSet = testee.getCpuMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var calls = (LineChartDataSet)dataSet.get(0);
    assertThat(calls.getLabel()).isEqualTo("System");
    assertThat(calls.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var errors = (LineChartDataSet)dataSet.get(1);
    assertThat(errors.getLabel()).isEqualTo("Process");
    assertThat(errors.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(testee.getCpuMonitor().getInfo()).contains("CPU Load: System ", "Axon Ivy ");
  }

  @Test
  public void threadsMonitor() {
    var testee = new JvmMonitorBean();

    var dataSet = testee.getThreadsMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var active = (LineChartDataSet)dataSet.get(0);
    assertThat(active.getLabel()).isEqualTo("Active");
    assertThat(active.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var daemons = (LineChartDataSet)dataSet.get(1);
    assertThat(daemons.getLabel()).isEqualTo("Daemons");
    assertThat(daemons.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class)); // delta

    assertThat(testee.getThreadsMonitor().getInfo()).contains("Threads: Active ", ", Daemons  ", ", Peak ",
            ", Total Started ");
  }

  @Test
  public void classesMonitor() {
    var testee = new JvmMonitorBean();

    var dataSet = testee.getClassesMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var loaded = (LineChartDataSet)dataSet.get(0);
    assertThat(loaded.getLabel()).isEqualTo("Loaded");
    assertThat(loaded.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var unloaded = (LineChartDataSet)dataSet.get(1);
    assertThat(unloaded.getLabel()).isEqualTo("Unloaded");
    assertThat(unloaded.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(testee.getClassesMonitor().getInfo()).contains("Classes: Loaded ", ", Unloaded  ",
            ", Total Loaded ");
  }
}
