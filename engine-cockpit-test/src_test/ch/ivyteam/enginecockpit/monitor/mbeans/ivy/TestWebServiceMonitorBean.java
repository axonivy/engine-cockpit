package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
public class TestWebServiceMonitorBean
{ 
  @AfterEach
  public void afterEach()
  {
    MBeans.unregisterAllMBeans();
  }
  
  @Test
  public void noData()
  {
    var testee = new WebServiceMonitor();
    assertThat(testee.getWebService()).isEqualTo("No Data");
    assertThat(testee.getCallsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }
  
  @Test
  public void withData() throws Exception
  {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    MBeans.registerMBeanFor(new Ws("ws2 (2)"));
    var testee = new WebServiceMonitor("test", "Default", "1");
    assertThat(testee.getWebService()).isEqualTo("test > Default > ws1");
    assertThat(testee.getCallsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
    testee = new WebServiceMonitor("test", "Default", "2");
    assertThat(testee.getWebService()).isEqualTo("test > Default > ws2");
    assertThat(testee.getCallsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }

  @Test
  public void callsMonitor()
  {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    var testee = new WebServiceMonitor("test", "Default", "1");
    
    var series = testee.getCallsMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var calls = series.get(0);
    assertThat(calls.getLabel()).isEqualTo("Calls");
    assertThat(calls.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta
    
    var errors = series.get(1);
    assertThat(errors.getLabel()).isEqualTo("Errors");
    assertThat(errors.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta
    
    assertThat(testee.getCallsMonitor().getInfo()).isEqualTo("Calls: -, Total 3, Errors -, Errors Total 4");
  }

  @Test
  public void executionTimeMonitor()
  {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    var testee = new WebServiceMonitor("test", "Default", "1");
    
    var series = testee.getExecutionTimeMonitor().getModel().getSeries();
    assertThat(series).hasSize(3);
    
    var min = series.get(0);
    assertThat(min.getLabel()).isEqualTo("Min");
    assertThat(min.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(5.0D)); 
    
    var avg = series.get(1);
    assertThat(avg.getLabel()).isEqualTo("Avg");
    assertThat(avg.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0.0D)); // delta

    var max = series.get(2);
    assertThat(max.getLabel()).isEqualTo("Max");
    assertThat(max.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(7.0D)); 
    
    assertThat(testee.getExecutionTimeMonitor().getInfo()).isEqualTo("Execution Time: Min 5 us, Avg -, Max 7 us, Total 6 us");
  }

  
  @MBean("ivy Engine:type=External Web Service,application=test,environment=Default,name=\"#{name}\"")
  private static final class Ws
  {    
    private final String name;

    public Ws(String name)
    {
      this.name = name;
    }

    @SuppressWarnings("unused")
    private String getName()
    {
      return name;
    }
    
    @MAttribute
    public long getCalls()
    {
      return 3;
    }
    
    @MAttribute
    public long getErrors()
    {
      return 4;
    }
    
    @MAttribute
    public long getCallsMinExecutionTimeDeltaInMicroSeconds()
    {
      return 5;
    }

    @MAttribute
    public long getCallsTotalExecutionTimeInMicroSeconds()
    {
      return 6;
    }

    @MAttribute
    public long getCallsMaxExecutionTimeDeltaInMicroSeconds()
    {
      return 7;
    }

  }
}
