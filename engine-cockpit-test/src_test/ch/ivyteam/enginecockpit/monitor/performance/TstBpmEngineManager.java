package ch.ivyteam.enginecockpit.monitor.performance;

import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import ch.ivyteam.ivy.application.IProcessModelVersion;
import ch.ivyteam.ivy.bpm.engine.restricted.IBpmEngine;
import ch.ivyteam.ivy.bpm.engine.restricted.IBpmEngineManager;
import ch.ivyteam.ivy.bpm.engine.restricted.IBpmEngineStarter;
import ch.ivyteam.ivy.bpm.engine.restricted.event.IEventPublisher;
import ch.ivyteam.ivy.bpm.engine.restricted.statistic.IExecutionStatistic;
import ch.ivyteam.ivy.request.IRequest;
import ch.ivyteam.ivy.request.IResponse;
import ch.ivyteam.ivy.request.RequestException;

@SuppressWarnings("restriction")
@Singleton
final class TstBpmEngineManager implements IBpmEngineManager {

  private IExecutionStatistic executionStatistic = new TstExecutionStatistic();

  @Override
  public void handleRequest(IRequest request, IResponse response) throws RequestException {
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public IBpmEngine getBpmEngine(IProcessModelVersion processModelVersion) {
    return null;
  }

  @Override
  public IBpmEngine getBpmEngineIfExists(IProcessModelVersion pmv) {
    return null;
  }

  @Override
  public Set<IBpmEngine> getRequiredBpmEngines(IProcessModelVersion processModelVersion) {
    return null;
  }

  @Override
  public List<IBpmEngine> getBpmEngines() {
    return null;
  }

  @Override
  public IEventPublisher getEventPublisher() {
    return null;
  }

  @Override
  public IExecutionStatistic getExecutionStatistic() {
    return executionStatistic;
  }

  @Override
  public void setBpmEngineStarter(IBpmEngineStarter bpmEngineStarter) {
  }

  @Override
  public void requestEngineStart(IProcessModelVersion processModelVersion) {
  }
}
