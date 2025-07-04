package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.primefaces.model.charts.line.LineChartDataSet;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
public class TestEmailMonitorBean {
  @AfterEach
  public void afterEach() {
    MBeans.unregisterAllMBeans();
  }

  @Test
  public void transactionsMonitor() {
    MBeans.registerMBeanFor(new Mail());
    var testee = new MailClientMonitorBean();

    var dataSet = testee.getSentMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(2);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var mails = (LineChartDataSet) dataSet.get(0);
    assertThat(mails.getLabel()).isEqualTo("Mails");
    assertThat(mails.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var errors = (LineChartDataSet) dataSet.get(1);
    assertThat(errors.getLabel()).isEqualTo("Errors");
    assertThat(errors.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(testee.getSentMonitor().getInfo()).isEqualTo("Mails Sent: Count -/3 Errors -/4");
  }

  @Test
  public void processingTimeMonitor() {
    MBeans.registerMBeanFor(new Mail());
    var testee = new MailClientMonitorBean();

    var dataSet = testee.getExecutionTimeMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(3);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var min = (LineChartDataSet) dataSet.get(0);
    assertThat(min.getLabel()).isEqualTo("Min");
    assertThat(min.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(5.0D));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var avg = (LineChartDataSet) dataSet.get(1);
    assertThat(avg.getLabel()).isEqualTo("Avg");
    assertThat(avg.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(0.0D)); // delta

    assertThat(dataSet.get(2)).isInstanceOf(LineChartDataSet.class);
    var max = (LineChartDataSet) dataSet.get(2);
    assertThat(max.getLabel()).isEqualTo("Max");
    assertThat(max.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(7.0D));

    assertThat(testee.getExecutionTimeMonitor().getInfo())
        .isEqualTo("Execution Time: Min 5 us Avg - Max 7 us Total 6 us");
  }

  @MBean("ivy Engine:name=External Mail Server")
  private static final class Mail {
    @MAttribute
    public long getSentMails() {
      return 3;
    }

    @MAttribute
    public long getErrors() {
      return 4;
    }

    @MAttribute
    public long getSentMailsMinExecutionTimeDeltaInMicroSeconds() {
      return 5;
    }

    @MAttribute
    public long getSentMailsTotalExecutionTimeInMicroSeconds() {
      return 6;
    }

    @MAttribute
    public long getSentMailsMaxExecutionTimeDeltaInMicroSeconds() {
      return 7;
    }
  }
}
