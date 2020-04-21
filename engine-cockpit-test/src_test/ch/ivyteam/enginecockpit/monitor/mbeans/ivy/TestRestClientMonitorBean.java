package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
public class TestRestClientMonitorBean
{ 
  @AfterEach
  public void afterEach()
  {
    MBeans.unregisterAllMBeans();
  }
  
  @Test
  public void noData()
  {
    var testee = new RestClientMonitorBean();
    assertThat(testee.getRestClients()).containsOnly("No Data");
    assertThat(testee.getConfigurationLink()).isEqualTo("restclients.xhtml");
    assertThat(testee.getRestClient()).isEqualTo("No Data");
    assertThat(testee.getCallsMonitor()).isNotNull();
    assertThat(testee.getConnectionsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }
  
  @Test
  public void withData() throws Exception
  {
    MBeans.registerMBeanFor(new Client("client1"));
    MBeans.registerMBeanFor(new Client("client2"));
    var testee = new RestClientMonitorBean();
    assertThat(testee.getRestClients()).containsOnly("test > Default > client1", "test > Default > client2", "No Data");
    assertThat(testee.getConfigurationLink()).isEqualTo("restclientdetail.xhtml?applicationName=test&environment=Default&restClientName=client1");
    assertThat(testee.getRestClient()).isEqualTo("test > Default > client1");
    assertThat(testee.getCallsMonitor()).isNotNull();
    assertThat(testee.getConnectionsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }

  @Test
  public void restClientSelectionByCombo() throws Exception
  {
    MBeans.registerMBeanFor(new Client("client1"));
    MBeans.registerMBeanFor(new Client("client2"));
    var testee = new RestClientMonitorBean();
    assertThat(testee.getRestClient()).isEqualTo("test > Default > client1");
    testee.setRestClient("test > Default > client2");
    assertThat(testee.getRestClient()).isEqualTo("test > Default > client2");
    testee.setRestClient("No Data");
    assertThat(testee.getRestClient()).isEqualTo("No Data");
  }
  
  @Test
  public void restClientSelectionByNavigation() throws Exception
  {
    MBeans.registerMBeanFor(new Client("client1"));
    MBeans.registerMBeanFor(new Client("client2"));
    var testee = new RestClientMonitorBean();
    assertThat(testee.getRestClient()).isEqualTo("test > Default > client1");
    testee.setApplicationName("test");
    testee.setEnvironment("Default");
    testee.setRestClientName("client2");
    assertThat(testee.getRestClient()).isEqualTo("test > Default > client2");
  }
  
  @Test
  public void connectionMonitor()
  {
    MBeans.registerMBeanFor(new Client("client1"));
    var testee = new RestClientMonitorBean();
    
    var series = testee.getConnectionsMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var openConnections = series.get(0);
    assertThat(openConnections.getLabel()).isEqualTo("Open");
    assertThat(openConnections.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(2));
    
    var usedConnections = series.get(1);
    assertThat(usedConnections.getLabel()).isEqualTo("Used");
    assertThat(usedConnections.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(1));    
    
    assertThat(testee.getConnectionsMonitor().getInfo()).isEqualTo("Connections: Used 1, Open 2, Max 50");
  }

  @Test
  public void callsMonitor()
  {
    MBeans.registerMBeanFor(new Client("client1"));
    var testee = new RestClientMonitorBean();
    
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
    MBeans.registerMBeanFor(new Client("client1"));
    var testee = new RestClientMonitorBean();
    
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

  
  @MBean("ivy Engine:type=External REST Web Service,application=test,environment=Default,name=#{name}")
  private static final class Client
  {    
    private final String name;

    public Client(String name)
    {
      this.name = name;
    }

    @SuppressWarnings("unused")
    private String getName()
    {
      return name;
    }
    
    @MAttribute
    public int getOpenConnections()
    {
      return 2;
    }
    
    @MAttribute
    public int getUsedConnections()
    {
      return 1;
    }

    @MAttribute
    public int getMaxConnections()
    {
      return 50;
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
