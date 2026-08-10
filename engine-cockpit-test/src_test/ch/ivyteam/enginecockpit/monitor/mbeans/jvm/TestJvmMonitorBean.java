package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import software.xdev.chartjs.model.dataset.LineDataset;

import ch.ivyteam.ivy.environment.IvyTest;

@IvyTest
public class TestJvmMonitorBean {
  @Test
  public void cpuMonitor() {
    var testee = new JvmMonitorBean();

    var dataSet = testee.getCpuMonitor().getDataSets();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineDataset.class);
    var calls = (LineDataset) dataSet.get(0);
    assertThat(calls.getLabel()).isEqualTo("System");
    assertThat(calls.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(dataSet.get(1)).isInstanceOf(LineDataset.class);
    var errors = (LineDataset) dataSet.get(1);
    assertThat(errors.getLabel()).isEqualTo("Process");
    assertThat(errors.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(testee.getCpuMonitor().getInfo()).contains("CPU Load: System ", "Axon Ivy ");
  }

  @Test
  public void threadsMonitor() {
    var testee = new JvmMonitorBean();

    var dataSet = testee.getThreadsMonitor().getDataSets();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineDataset.class);
    var active = (LineDataset) dataSet.get(0);
    assertThat(active.getLabel()).isEqualTo("Active");
    assertThat(active.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(dataSet.get(1)).isInstanceOf(LineDataset.class);
    var daemons = (LineDataset) dataSet.get(1);
    assertThat(daemons.getLabel()).isEqualTo("Daemons");
    assertThat(daemons.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class)); // delta

    assertThat(testee.getThreadsMonitor().getInfo()).contains("Threads: Active ", ", Daemons  ", ", Peak ",
        ", Total Started ");
  }

  @Test
  public void classesMonitor() {
    var testee = new JvmMonitorBean();

    var dataSet = testee.getClassesMonitor().getDataSets();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineDataset.class);
    var loaded = (LineDataset) dataSet.get(0);
    assertThat(loaded.getLabel()).isEqualTo("Loaded");
    assertThat(loaded.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(dataSet.get(1)).isInstanceOf(LineDataset.class);
    var unloaded = (LineDataset) dataSet.get(1);
    assertThat(unloaded.getLabel()).isEqualTo("Unloaded");
    assertThat(unloaded.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isInstanceOf(Number.class));

    assertThat(testee.getClassesMonitor().getInfo()).contains("Classes: Loaded ", ", Unloaded  ",
        ", Total Loaded ");
  }
}
