package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.primefaces.model.charts.line.LineChartDataSet;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
public class TestSystemDatabaseMonitorBean {
  @AfterEach
  public void afterEach() {
    MBeans.unregisterAllMBeans();
  }

  @Test
  public void connectionMonitor() {
    MBeans.registerMBeanFor(new SysDb());
    var testee = new SystemDatabaseMonitorBean();

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
  public void transactionsMonitor() {
    MBeans.registerMBeanFor(new SysDb());
    var testee = new SystemDatabaseMonitorBean();

    var dataSet = testee.getTransactionsMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var transactions = (LineChartDataSet)dataSet.get(0);
    assertThat(transactions.getLabel()).isEqualTo("Transactions");
    assertThat(transactions.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var errors = (LineChartDataSet)dataSet.get(1);
    assertThat(errors.getLabel()).isEqualTo("Errors");
    assertThat(errors.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getTransactionsMonitor().getInfo())
            .isEqualTo("Transactions: -, Total 3, Errors -, Errors Total 4");
  }

  @Test
  public void processingTimeMonitor() {
    MBeans.registerMBeanFor(new SysDb());
    var testee = new SystemDatabaseMonitorBean();

    var dataSet = testee.getProcessingTimeMonitor().getModel().getData().getDataSet();
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

    assertThat(testee.getProcessingTimeMonitor().getInfo())
            .isEqualTo("Processing Time: Min 5 us, Avg -, Max 7 us, Total 6 us");
  }

  @MBean("ivy Engine:type=Database Persistency Service")
  private static final class SysDb {
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
