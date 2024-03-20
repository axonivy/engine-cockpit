package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.primefaces.model.charts.line.LineChartDataSet;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
class TestDatabaseMonitorBean {

  @AfterEach
  void afterEach() {
    MBeans.unregisterAllMBeans();
  }

  @Test
  void noData() {
    var testee = new DatabaseMonitor();
    assertThat(testee.getDatabase()).isEqualTo("No Data");
    assertThat(testee.getQueriesMonitor()).isNotNull();
    assertThat(testee.getConnectionsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }

  @Test
  void withData() throws Exception {
    MBeans.registerMBeanFor(new Db("db1"));
    MBeans.registerMBeanFor(new Db("db2"));
    var testee = new DatabaseMonitor("test", "db1");
    assertThat(testee.getDatabase()).isEqualTo("test > db1");
    assertThat(testee.getQueriesMonitor()).isNotNull();
    assertThat(testee.getConnectionsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
    testee = new DatabaseMonitor("test", "db2");
    assertThat(testee.getDatabase()).isEqualTo("test > db2");
    assertThat(testee.getQueriesMonitor()).isNotNull();
    assertThat(testee.getConnectionsMonitor()).isNotNull();
    assertThat(testee.getExecutionTimeMonitor()).isNotNull();
  }

  @Test
  void connectionMonitor() {
    MBeans.registerMBeanFor(new Db("db1"));
    var testee = new DatabaseMonitor("test", "db1");

    var dataSet = testee.getConnectionsMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var openConnections = (LineChartDataSet)dataSet.get(0);
    assertThat(openConnections.getLabel()).isEqualTo("Open");
    assertThat(openConnections.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(2.0D));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var usedConnections = (LineChartDataSet)dataSet.get(1);
    assertThat(usedConnections.getLabel()).isEqualTo("Used");
    assertThat(usedConnections.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(1.0D));

    assertThat(testee.getConnectionsMonitor().getInfo()).isEqualTo("Connections: Used 1, Open 2, Max 50");
  }

  @Test
  void callsMonitor() {
    MBeans.registerMBeanFor(new Db("db1"));
    var testee = new DatabaseMonitor("test", "db1");

    var dataSet = testee.getQueriesMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var queries = (LineChartDataSet)dataSet.get(0);
    assertThat(queries.getLabel()).isEqualTo("Queries");
    assertThat(queries.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var errors = (LineChartDataSet)dataSet.get(1);
    assertThat(errors.getLabel()).isEqualTo("Errors");
    assertThat(errors.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getQueriesMonitor().getInfo())
            .isEqualTo("Queries: -, Total 3, Errors -, Errors Total 4");
  }

  @Test
  void executionTimeMonitor() {
    MBeans.registerMBeanFor(new Db("db1"));
    var testee = new DatabaseMonitor("test", "db1");

    var dataSet = testee.getExecutionTimeMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(3);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var min = (LineChartDataSet)dataSet.get(0);
    assertThat(min.getLabel()).isEqualTo("Min");
    assertThat(min.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(5.0D));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var avg = (LineChartDataSet)dataSet.get(1);
    assertThat(avg.getLabel()).isEqualTo("Avg");
    assertThat(avg.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(2)).isInstanceOf(LineChartDataSet.class);
    var max = (LineChartDataSet)dataSet.get(2);
    assertThat(max.getLabel()).isEqualTo("Max");
    assertThat(max.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(7.0D));

    assertThat(testee.getExecutionTimeMonitor().getInfo())
            .isEqualTo("Execution Time: Min 5 us, Avg -, Max 7 us, Total 6 us");
  }

  @MBean("ivy Engine:type=External Database,application=test,name=#{name}")
  private static final class Db {
    private final String name;

    public Db(String name) {
      this.name = name;
    }

    @SuppressWarnings("unused")
    private String getName() {
      return name;
    }

    @MAttribute
    public int getOpenConnections() {
      return 2;
    }

    @MAttribute
    public int getUsedConnections() {
      return 1;
    }

    @MAttribute
    public int getMaxConnections() {
      return 50;
    }

    @MAttribute
    public long getTransactions() {
      return 3;
    }

    @MAttribute
    public long getErrors() {
      return 4;
    }

    @MAttribute
    public long getTransactionsMinExecutionTimeDeltaInMicroSeconds() {
      return 5;
    }

    @MAttribute
    public long getTransactionsTotalExecutionTimeInMicroSeconds() {
      return 6;
    }

    @MAttribute
    public long getTransactionsMaxExecutionTimeDeltaInMicroSeconds() {
      return 7;
    }
  }
}
