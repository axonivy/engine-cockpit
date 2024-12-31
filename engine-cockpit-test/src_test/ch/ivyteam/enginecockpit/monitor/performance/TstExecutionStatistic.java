package ch.ivyteam.enginecockpit.monitor.performance;

import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.bpm.engine.restricted.statistic.IExecutionStatistic;
import ch.ivyteam.ivy.bpm.engine.restricted.statistic.IProcessElementExecutionStatistic;
import ch.ivyteam.ivy.process.model.value.PID;

@SuppressWarnings("restriction")
class TstExecutionStatistic implements IExecutionStatistic {

  static final IProcessElementExecutionStatistic[] EMPTY = {};
  static final IProcessElementExecutionStatistic[] TWO_ELEMENT = {new TstProcessElementExecStat(), new TstProcessElementExecStat()};
  boolean isRunning;
  IProcessElementExecutionStatistic[] statistic = EMPTY;
  boolean startCalled;
  boolean stopCalled;
  boolean clearCalled;

  @Override
  public IProcessElementExecutionStatistic[] getProcessElementExecutionStatistic() {
    return statistic;
  }

  @Override
  public void start() {
    startCalled = true;
  }

  @Override
  public void stop() {
    stopCalled = true;
  }

  @Override
  public boolean isRunning() {
    return isRunning;
  }

  @Override
  public void clear() {
    clearCalled = true;
  }

  @Override
  public IProcessElementExecutionStatistic getFirstProcessElementExecutionStatistic(
      IProcessModelVersion processModelVersion, PID processElementId) {
    return null;
  }
}
