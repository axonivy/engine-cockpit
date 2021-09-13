package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestMemoryMonitorBean {
  @Test
  public void heapMemoryMonitor() {
    var testee = new MemoryMonitorBean();

    var series = testee.getHeapMemoryMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);

    var calls = series.get(0);
    assertThat(calls.getLabel()).isEqualTo("Used");
    assertThat(calls.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class));

    var errors = series.get(1);
    assertThat(errors.getLabel()).isEqualTo("Committed");
    assertThat(errors.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class));

    assertThat(testee.getHeapMemoryMonitor().getInfo()).contains("Heap Memory: Used", ", Committed ",
            ", Init ", ", Max ");
  }

  @Test
  public void nonHeapMemoryMonitor() {
    var testee = new MemoryMonitorBean();

    var series = testee.getNonHeapMemoryMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);

    var min = series.get(0);
    assertThat(min.getLabel()).isEqualTo("Used");
    assertThat(min.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class));

    var avg = series.get(1);
    assertThat(avg.getLabel()).isEqualTo("Committed");
    assertThat(avg.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class)); // delta

    assertThat(testee.getNonHeapMemoryMonitor().getInfo()).contains("Non Heap Memory: Used", ", Committed ");
  }

  @Test
  public void garbageCollectorsMonitor() {
    var testee = new MemoryMonitorBean();

    var series = testee.getGarbageCollectorsMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);

    var young = series.get(0);
    assertThat(young.getLabel()).isEqualTo("G1 Young Generation");
    assertThat(young.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class));

    var old = series.get(1);
    assertThat(old.getLabel()).isEqualTo("G1 Old Generation");
    assertThat(old.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class));

    assertThat(testee.getGarbageCollectorsMonitor().getInfo())
            .contains("Garbage Collection: G1 Young Generation ", "Count", ", G1 Old Generation", "Count");
  }
}
