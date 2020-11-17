package ch.ivyteam.enginecockpit.monitor.mbeans.jvm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestJvmMonitorBean
{    
  @Test
  public void cpuMonitor()
  {
    var testee = new JvmMonitorBean();
    
    var series = testee.getCpuMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var calls = series.get(0);
    assertThat(calls.getLabel()).isEqualTo("System");
    assertThat(calls.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class)); 
    
    var errors = series.get(1);
    assertThat(errors.getLabel()).isEqualTo("Process");
    assertThat(errors.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class));
    
    assertThat(testee.getCpuMonitor().getInfo()).contains("CPU Load: System ", "Axon.ivy ");
  }

  @Test
  public void threadsMonitor()
  {
    var testee = new JvmMonitorBean();
    
    var series = testee.getThreadsMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var active = series.get(0);
    assertThat(active.getLabel()).isEqualTo("Active");
    assertThat(active.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class)); 
    
    var daemons = series.get(1);
    assertThat(daemons.getLabel()).isEqualTo("Daemons");
    assertThat(daemons.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class)); // delta
    
    assertThat(testee.getThreadsMonitor().getInfo()).contains("Threads: Active ", ", Daemons  ", ", Peak ", ", Total Started ");
  }
  
  @Test
  public void classesMonitor()
  {
    var testee = new JvmMonitorBean();
    
    var series = testee.getClassesMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var loaded = series.get(0);
    assertThat(loaded.getLabel()).isEqualTo("Loaded");
    assertThat(loaded.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class)); 
    
    var unloaded = series.get(1);
    assertThat(unloaded.getLabel()).isEqualTo("Unloaded");
    assertThat(unloaded.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isInstanceOf(Number.class));
    
    assertThat(testee.getClassesMonitor().getInfo()).contains("Classes: Loaded ", ", Unloaded  ", ", Total Loaded ");
  }
}
