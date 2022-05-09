package ch.ivyteam.enginecockpit.monitor.performance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.ToLongFunction;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.inject.AbstractModule;

import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.ivy.bpm.engine.restricted.IBpmEngineManager;
import ch.ivyteam.ivy.bpm.engine.restricted.statistic.IProcessElementExecutionStatistic;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
class TestPerformanceBean {

  private static final double ONE_MILLION = 1_000_000.0f;
  private TstExecutionStatistic statistic;
  private PerformanceBean bean;

  @BeforeEach
  void beforeEach() {
    DiCore.installModules(new AbstractModule() {
      @Override
      protected void configure() {
        super.configure();
        bind(IBpmEngineManager.class).to(TstBpmEngineManager.class);
      }
    });
    statistic = (TstExecutionStatistic) DiCore.getGlobalInjector().getInstance(IBpmEngineManager.class).getExecutionStatistic();
    bean = new PerformanceBean();
  }

  @AfterEach
  void afterEach() {
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", false);
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Interval", "300");

    DiCore.reset();
  }

  @Test
  void isNotStartable() {
    assertThat(bean.isNotStartable()).isFalse();
    statistic.isRunning = true;
    assertThat(bean.isNotStartable()).isTrue();
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", true);
    assertThat(bean.isNotStartable()).isTrue();
    statistic.isRunning = false;
    assertThat(bean.isNotStartable()).isTrue();
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", false);
    assertThat(bean.isNotStartable()).isFalse();
  }

  @Test
  void isNotStopable() {
    assertThat(bean.isNotStoppable()).isTrue();
    statistic.isRunning = true;
    assertThat(bean.isNotStoppable()).isFalse();
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", true);
    assertThat(bean.isNotStoppable()).isTrue();
    statistic.isRunning = false;
    assertThat(bean.isNotStoppable()).isTrue();
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", false);
    assertThat(bean.isNotStoppable()).isTrue();
  }

  @Test
  void isNotCleanable() {
    assertThat(bean.isNotCleanable()).isTrue();
    statistic.statistic = TstExecutionStatistic.TWO_ELEMENT;
    bean.refresh();
    assertThat(bean.isNotCleanable()).isFalse();
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", true);
    assertThat(bean.isNotCleanable()).isTrue();
    statistic.statistic = TstExecutionStatistic.EMPTY;
    bean.refresh();
    assertThat(bean.isNotCleanable()).isTrue();
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", false);
    assertThat(bean.isNotCleanable()).isTrue();
  }

  @Test
  void isNotRefreshable() {
    assertThat(bean.isNotRefreshable()).isTrue();
    statistic.isRunning = true;
    assertThat(bean.isNotRefreshable()).isFalse();
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", true);
    assertThat(bean.isNotRefreshable()).isFalse();
    statistic.isRunning = false;
    assertThat(bean.isNotRefreshable()).isTrue();
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", false);
    assertThat(bean.isNotRefreshable()).isTrue();
  }

  @Test
  void isLoggingActive() {
    assertThat(bean.isLoggingActive()).isFalse();
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", true);
    assertThat(bean.isNotRefreshable()).isTrue();
    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Active", false);
    assertThat(bean.isLoggingActive()).isFalse();
  }

  @Test
  void getLoggingInterval() {
    assertThat(bean.getLoggingInterval()).isEqualTo("5 minutes");

    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Interval", "120");
    assertThat(bean.getLoggingInterval()).isEqualTo("2 minutes");

    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Interval", "30");
    assertThat(bean.getLoggingInterval()).isEqualTo("30 seconds");

    IConfiguration.instance().set("ProcessEngine.FiringStatistic.Interval", "1");
    assertThat(bean.getLoggingInterval()).isEqualTo("1 second");

  }

  @Test
  void start() {
    assertThat(statistic.startCalled).isFalse();
    bean.start();
    assertThat(statistic.startCalled).isTrue();
  }

  @Test
  void stop() {
    assertThat(statistic.stopCalled).isFalse();
    bean.stop();
    assertThat(statistic.stopCalled).isTrue();
  }

  @Test
  void clear() {
    statistic.statistic = TstExecutionStatistic.TWO_ELEMENT;
    bean.refresh();

    assertThat(bean.getProcessElements()).isNotEmpty();
    assertThat(statistic.clearCalled).isFalse();

    statistic.statistic = TstExecutionStatistic.EMPTY;
    bean.clear();

    assertThat(statistic.clearCalled).isTrue();
    assertThat(bean.getProcessElements()).isEmpty();
  }

  @Test
  void refresh() {
    assertThat(bean.getProcessElements()).isEmpty();

    statistic.statistic = TstExecutionStatistic.TWO_ELEMENT;
    bean.refresh();

    assertThat(bean.getProcessElements()).isNotEmpty();
  }

  @Test
  void filter() {
    assertThat(bean.getFilter()).isNull();
    bean.setFilter("Hello");
    assertThat(bean.getFilter()).isEqualTo("Hello");
  }

  @Test
  void filteredProcessElements() {
    var processElements = bean.getProcessElements();

    assertThat(bean.getFilteredProcessElements()).isNull();
    bean.setFilteredProcessElements(processElements);
    assertThat(bean.getFilteredProcessElements()).isSameAs(processElements);
  }

  @Test
  void getProcessElements() {
    assertThat(bean.getProcessElements()).isEmpty();

    statistic.statistic = TstExecutionStatistic.TWO_ELEMENT;
    bean.refresh();

    assertThat(bean.getProcessElements()).hasSize(2);

    var uiStat = bean.getProcessElements().get(0);
    var stat = TstExecutionStatistic.TWO_ELEMENT[0];
    assertThat(uiStat.getOrder()).isEqualTo(stat.getExecutionOrder());

    assertThat(uiStat.getApplication()).isEqualTo(stat.getApplicationName());
    assertThat(uiStat.getProcessModel()).isEqualTo(stat.getModelName());
    assertThat(uiStat.getVersion()).isEqualTo(stat.getVersionName());

    assertThat(uiStat.getProcess()).isEqualTo(stat.getProcessElement().getTopLevelProcess().getFullQualifiedName());
    assertThat(uiStat.getName()).isEqualTo(stat.getProcessElement().getName());
    assertThat(uiStat.getId()).isEqualTo(stat.getProcessElement().getId().getRawPid());
    assertThat(uiStat.getType()).isEqualTo(stat.getProcessElement().getType());

    assertThat(uiStat.getTotalExecutionTime()).isEqualTo((stat.getProcessEngineExecutionTime() / ONE_MILLION + stat.getBackgroundExecutionTime() / ONE_MILLION));

    assertThat(uiStat.getInternalExecutions()).isEqualTo(stat.getExecutions());
    assertThat(uiStat.getTotalInternalExecutionTime()).isEqualTo(stat.getProcessEngineExecutionTime() / ONE_MILLION);
    assertThat(uiStat.getMinInternalExecutionTime()).isEqualTo(stat.getMinProcessEngineExecutionTime() / ONE_MILLION);
    assertThat(uiStat.getAvgInternalExecutionTime()).isEqualTo(stat.getProcessEngineExecutionTime() / ONE_MILLION / stat.getExecutions());
    assertThat(uiStat.getMaxInternalExecutionTime()).isEqualTo(stat.getMaxProcessEngineExecutionTime() / ONE_MILLION);

    assertThat(uiStat.getExternalExecutions()).isEqualTo(stat.getBackgroundExecutions());
    assertThat(uiStat.getTotalExternalExecutionTime()).isEqualTo(stat.getBackgroundExecutionTime() / ONE_MILLION);
    assertThat(uiStat.getMinExternalExecutionTime()).isEqualTo(stat.getMinBackgroundExecutionTime() / ONE_MILLION);
    assertThat(uiStat.getAvgExternalExecutionTime()).isEqualTo(stat.getBackgroundExecutionTime() / ONE_MILLION / stat.getBackgroundExecutions());
    assertThat(uiStat.getMaxExternalExecutionTime()).isEqualTo(stat.getMaxBackgroundExecutionTime() / ONE_MILLION);
  }

  @Test
  void background() {
    assertThat(bean.getProcessElements()).isEmpty();

    statistic.statistic = TstExecutionStatistic.TWO_ELEMENT;
    bean.refresh();

    assertThat(bean.getProcessElements()).hasSize(2);
    var uiStat = bean.getProcessElements().get(0);

    assertThat(uiStat.getTotalExecutionTimeBackground())
        .isEqualTo(background(stat -> stat.getProcessEngineExecutionTime() + stat.getBackgroundExecutionTime()));

    assertThat(uiStat.getInternalExecutionsBackground())
        .isEqualTo(background(IProcessElementExecutionStatistic::getExecutions));
    assertThat(uiStat.getTotalInternalExecutionTimeBackground())
         .isEqualTo(background(IProcessElementExecutionStatistic::getProcessEngineExecutionTime));
    assertThat(uiStat.getMinInternalExecutionTimeBackground())
        .isEqualTo(background(IProcessElementExecutionStatistic::getMinProcessEngineExecutionTime));
    assertThat(uiStat.getAvgInternalExecutionTimeBackground())
        .isEqualTo(background(stat -> stat.getProcessEngineExecutionTime() / stat.getExecutions()));
    assertThat(uiStat.getMaxInternalExecutionTimeBackground())
        .isEqualTo(background(IProcessElementExecutionStatistic::getMaxProcessEngineExecutionTime));

    assertThat(uiStat.getExternalExecutionsBackground())
        .isEqualTo(background(IProcessElementExecutionStatistic::getBackgroundExecutions));
    assertThat(uiStat.getTotalExternalExecutionTimeBackground())
        .isEqualTo(background(IProcessElementExecutionStatistic::getBackgroundExecutionTime));
    assertThat(uiStat.getMinExternalExecutionTimeBackground())
        .isEqualTo(background(IProcessElementExecutionStatistic::getMinBackgroundExecutionTime));
    assertThat(uiStat.getAvgExternalExecutionTimeBackground())
        .isEqualTo(background(stat -> stat.getBackgroundExecutionTime() / stat.getBackgroundExecutions()));
    assertThat(uiStat.getMaxExternalExecutionTimeBackground())
        .isEqualTo(background(IProcessElementExecutionStatistic::getMaxBackgroundExecutionTime));
  }

  private String background(ToLongFunction<IProcessElementExecutionStatistic> mapping) {
    var max = Stream.of(TstExecutionStatistic.TWO_ELEMENT).mapToLong(mapping).max().getAsLong();
    var value = mapping.applyAsLong(TstExecutionStatistic.TWO_ELEMENT[0]);
    var percentage = value * 100 / max;
    var end = Math.min(percentage+ 20, 100);
    var color = 120 - percentage * 120 / 100;
    return "linear-gradient(90deg, hsl(" + color + ", 100%, 70%) " + percentage + "%, var(--surface-a, #FFFFFF) "+ end +"%)";
  }
}
