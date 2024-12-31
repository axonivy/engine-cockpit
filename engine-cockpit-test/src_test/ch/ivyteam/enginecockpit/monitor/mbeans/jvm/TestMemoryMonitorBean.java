package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.primefaces.model.charts.line.LineChartDataSet;

public class TestMemoryMonitorBean {
  @Test
  public void heapMemoryMonitor() {
    var testee = new MemoryMonitorBean();

    var dataSet = testee.getHeapMemoryMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var calls = (LineChartDataSet) dataSet.get(0);
    assertThat(calls.getLabel()).isEqualTo("Used");
    assertThat(calls.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var errors = (LineChartDataSet) dataSet.get(1);
    assertThat(errors.getLabel()).isEqualTo("Committed");
    assertThat(errors.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(testee.getHeapMemoryMonitor().getInfo()).contains("Heap Memory: Used", ", Committed ",
        ", Init ", ", Max ");
  }

  @Test
  public void nonHeapMemoryMonitor() {
    var testee = new MemoryMonitorBean();

    var dataSet = testee.getNonHeapMemoryMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var min = (LineChartDataSet) dataSet.get(0);
    assertThat(min.getLabel()).isEqualTo("Used");
    assertThat(min.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var avg = (LineChartDataSet) dataSet.get(1);
    assertThat(avg.getLabel()).isEqualTo("Committed");
    assertThat(avg.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class)); // delta

    assertThat(testee.getNonHeapMemoryMonitor().getInfo()).contains("Non Heap Memory: Used", ", Committed ");
  }

  @Test
  public void garbageCollectorsMonitor() {
    var testee = new MemoryMonitorBean();

    var dataSet = testee.getGarbageCollectorsMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(3);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var young = (LineChartDataSet) dataSet.get(0);
    assertThat(young.getLabel()).isEqualTo("G1 Young Generation");
    assertThat(young.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var old = (LineChartDataSet) dataSet.get(1);
    assertThat(old.getLabel()).isEqualTo("G1 Old Generation");
    assertThat(old.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(dataSet.get(2)).isInstanceOf(LineChartDataSet.class);
    var concurrent = (LineChartDataSet) dataSet.get(2);
    assertThat(concurrent.getLabel()).isEqualTo("G1 Concurrent GC");
    assertThat(concurrent.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(testee.getGarbageCollectorsMonitor().getInfo())
        .contains("Garbage Collection: G1 Young Generation ", "Count", ", G1 Old Generation", "Count");
  }
}
