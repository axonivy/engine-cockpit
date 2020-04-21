package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
public class TestExternalDatabaseMonitorBean
{ 
  @AfterEach
  public void afterEach()
  {
    MBeans.unregisterAllMBeans();
  }
  
  @Test
  public void noData()
  {
    var testee = new ExternalDatabaseMonitorBean();
    assertThat(testee.getExternalDatabases()).containsOnly("No Data");
    assertThat(testee.getConfigurationLink()).isEqualTo("externaldatabases.xhtml");
    assertThat(testee.getExternalDatabase()).isEqualTo("No Data");
    assertThat(testee.getQueriesMonitor()).isNotNull();
    assertThat(testee.getConnectionsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }
  
  @Test
  public void withData() throws Exception
  {
    MBeans.registerMBeanFor(new Db("db1"));
    MBeans.registerMBeanFor(new Db("db2"));
    var testee = new ExternalDatabaseMonitorBean();
    assertThat(testee.getExternalDatabases()).containsOnly("test > Default > db1", "test > Default > db2", "No Data");
    assertThat(testee.getConfigurationLink()).isEqualTo("externaldatabasedetail.xhtml?applicationName=test&environment=Default&databaseName=db2");
    assertThat(testee.getExternalDatabase()).isEqualTo("test > Default > db2");
    assertThat(testee.getQueriesMonitor()).isNotNull();
    assertThat(testee.getConnectionsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }

  @Test
  public void ExternalDatabaseSelectionByCombo() throws Exception
  {
    MBeans.registerMBeanFor(new Db("db1"));
    MBeans.registerMBeanFor(new Db("db2"));
    var testee = new ExternalDatabaseMonitorBean();
    assertThat(testee.getExternalDatabase()).isEqualTo("test > Default > db2");
    testee.setExternalDatabase("test > Default > db1");
    assertThat(testee.getExternalDatabase()).isEqualTo("test > Default > db1");
    testee.setExternalDatabase("No Data");
    assertThat(testee.getExternalDatabase()).isEqualTo("No Data");
  }
  
  @Test
  public void ExternalDatabaseSelectionByNavigation() throws Exception
  {
    MBeans.registerMBeanFor(new Db("db1"));
    MBeans.registerMBeanFor(new Db("db2"));
    var testee = new ExternalDatabaseMonitorBean();
    assertThat(testee.getExternalDatabase()).isEqualTo("test > Default > db2");
    testee.setApplicationName("test");
    testee.setEnvironment("Default");
    testee.setDatabaseName("db1");
    assertThat(testee.getExternalDatabase()).isEqualTo("test > Default > db1");
  }
  
  @Test
  public void connectionMonitor()
  {
    MBeans.registerMBeanFor(new Db("db1"));
    var testee = new ExternalDatabaseMonitorBean();
    
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
    MBeans.registerMBeanFor(new Db("db1"));
    var testee = new ExternalDatabaseMonitorBean();
    
    var series = testee.getQueriesMonitor().getModel().getSeries();
    assertThat(series).hasSize(2);
    
    var queries = series.get(0);
    assertThat(queries.getLabel()).isEqualTo("Queries");
    assertThat(queries.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta
    
    var errors = series.get(1);
    assertThat(errors.getLabel()).isEqualTo("Errors");
    assertThat(errors.getData()).hasSize(1).allSatisfy((t, v) -> assertThat(v).isEqualTo(0L)); // delta
    
    assertThat(testee.getQueriesMonitor().getInfo()).isEqualTo("Queries: 0, Total 3, Errors 0, Errors Total 4");
  }

  @Test
  public void executionTimeMonitor()
  {
    MBeans.registerMBeanFor(new Db("db1"));
    var testee = new ExternalDatabaseMonitorBean();
    
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

  
  @MBean("ivy Engine:type=External Database,application=test,environment=Default,name=#{name}")
  private static final class Db
  {    
    private final String name;

    public Db(String name)
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
    public long getTransactions()
    {
      return 3;
    }
    
    @MAttribute
    public long getErrors()
    {
      return 4;
    }
    
    @MAttribute
    public long getTransactionsMinExecutionTimeDeltaInMicroSeconds()
    {
      return 5;
    }

    @MAttribute
    public long getTransactionsTotalExecutionTimeInMicroSeconds()
    {
      return 6;
    }

    @MAttribute
    public long getTransactionsMaxExecutionTimeDeltaInMicroSeconds()
    {
      return 7;
    }
  }
}
