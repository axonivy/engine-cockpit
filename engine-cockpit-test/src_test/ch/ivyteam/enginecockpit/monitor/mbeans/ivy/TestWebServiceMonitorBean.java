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
    var testee = new WebServiceMonitorBean();
    assertThat(testee.getWebServices()).containsOnly("No Data");
    assertThat(testee.getConfigurationLink()).isEqualTo("webservices.xhtml");
    assertThat(testee.getWebService()).isEqualTo("No Data");
    assertThat(testee.getCallsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }
  
  @Test
  public void withData() throws Exception
  {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    MBeans.registerMBeanFor(new Ws("ws2 (2)"));
    var testee = new WebServiceMonitorBean();
    assertThat(testee.getWebServices()).containsOnly("test > Default > ws1", "test > Default > ws2", "No Data");
    assertThat(testee.getConfigurationLink()).isEqualTo("webservicedetail.xhtml?applicationName=test&environment=Default&webserviceId=2");
    assertThat(testee.getWebService()).isEqualTo("test > Default > ws2");
    assertThat(testee.getCallsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }

  @Test
  public void WebServiceSelectionByCombo() throws Exception
  {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    MBeans.registerMBeanFor(new Ws("ws2 (2)"));
    var testee = new WebServiceMonitorBean();
    assertThat(testee.getWebService()).isEqualTo("test > Default > ws2");
    testee.setWebService("test > Default > ws1");
    assertThat(testee.getWebService()).isEqualTo("test > Default > ws1");
    testee.setWebService("No Data");
    assertThat(testee.getWebService()).isEqualTo("No Data");
  }
  
  @Test
  public void WebServiceSelectionByNavigation() throws Exception
  {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    MBeans.registerMBeanFor(new Ws("ws2 (2)"));
    var testee = new WebServiceMonitorBean();
    assertThat(testee.getWebService()).isEqualTo("test > Default > ws2");
    testee.setApplicationName("test");
    testee.setEnvironment("Default");
    testee.setWebServiceId("1");
    assertThat(testee.getWebService()).isEqualTo("test > Default > ws1");
  }
  
  @Test
  public void callsMonitor()
  {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    var testee = new WebServiceMonitorBean();
    
    var series = testee.getCallsMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var calls = series.get(0);
    assertThat(calls.getLabel()).isEqualTo("Calls");
    assertThat(calls.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta
    
    var errors = series.get(1);
    assertThat(errors.getLabel()).isEqualTo("Errors");
    assertThat(errors.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta
    
    assertThat(testee.getCallsMonitor().getInfo()).isEqualTo("Calls: 0, Total 3, Errors 0, Errors Total 4");
  }

  @Test
  public void executionTimeMonitor()
  {
    MBeans.registerMBeanFor(new Ws("ws1 (1)"));
    var testee = new WebServiceMonitorBean();
    
    var series = testee.getExecutionTimeMonitor().getModel().getSeries();
    assertThat(series).hasSize(3);
    
    var min = series.get(0);
    assertThat(min.getLabel()).isEqualTo("Min");
    assertThat(min.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(5L)); 
    
    var avg = series.get(1);
    assertThat(avg.getLabel()).isEqualTo("Avg");
    assertThat(avg.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0)); // delta

    var max = series.get(2);
    assertThat(max.getLabel()).isEqualTo("Max");
    assertThat(max.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(7L)); 
    
    assertThat(testee.getExecutionTimeMonitor().getInfo()).isEqualTo("Execution Time: Min 5 us, Avg 0 us, Max 7 us, Total 6 us");
  }

  
  @MBean("ivy Engine:type=External Web Service,application=test,environment=Default,name=#{name}")
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
