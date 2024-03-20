package ch.ivyteam.enginecockpit.monitor.mbeans.tomcat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.model.charts.line.LineChartDataSet;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@SuppressWarnings("restriction")
public class TestSessionMonitorBean {
  @BeforeEach
  public void beforeEach() {
    MBeans.registerMBeanFor(new Manager("app1"));
    MBeans.registerMBeanFor(new Manager("app2"));
    MBeans.registerMBeanFor(new SecurityManager());
  }

  @AfterEach
  public void afterEach() {
    MBeans.unregisterAllMBeans();
  }

  @Test
  public void sessionsMonitor() {
    var testee = new SessionMonitorBean();

    var dataSet = testee.getSessionsMonitor().getModel().getData().getDataSet();
    assertThat(dataSet).hasSize(3);

    assertThat(dataSet.get(0)).isInstanceOf(LineChartDataSet.class);
    var licensedSessions = (LineChartDataSet)dataSet.get(0);
    assertThat(licensedSessions.getLabel()).isEqualTo("Licensed Sessions");
    assertThat(licensedSessions.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(5.0D));

    assertThat(dataSet.get(1)).isInstanceOf(LineChartDataSet.class);
    var sessions = (LineChartDataSet)dataSet.get(1);
    assertThat(sessions.getLabel()).isEqualTo("Sessions");
    assertThat(sessions.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(7.0D));

    assertThat(dataSet.get(2)).isInstanceOf(LineChartDataSet.class);
    var httpSessions = (LineChartDataSet)dataSet.get(2);
    assertThat(httpSessions.getLabel()).isEqualTo("Http Sessions");
    assertThat(httpSessions.getData()).hasSize(1).allSatisfy(v -> assertThat(v).isEqualTo(6.0D));

    assertThat(testee.getSessionsMonitor().getInfo())
            .isEqualTo("Sessions: Licensed Sessions 5, Sessions 7, Http Sessions 6, Licensed Users 100");
  }

  @MBean("ivy:type=Manager,host=localhost,context=#{context}")
  private static final class Manager {
    private final String context;

    public Manager(String context) {
      this.context = context;
    }

    @SuppressWarnings("unused")
    public String getContext() {
      return context;
    }

    @MAttribute
    public long getActiveSessions() {
      return 3;
    }
  }

  @MBean("ivy Engine:type=Security Manager")
  private static final class SecurityManager {
    @MAttribute
    public long getLicensedSessions() {
      return 5;
    }

    @MAttribute
    public long getSessions() {
      return 7;
    }

    @MAttribute
    public long getLicensedUsers() {
      return 100;
    }
  }
}
