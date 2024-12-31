package ch.ivyteam.enginecockpit.monitor.performance;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import ch.ivyteam.ivy.bpm.engine.restricted.model.ErrorCatcher;
import ch.ivyteam.ivy.bpm.engine.restricted.model.IConnectorService;
import ch.ivyteam.ivy.bpm.engine.restricted.model.IExecutionModel;
import ch.ivyteam.ivy.bpm.engine.restricted.model.IProcess;
import ch.ivyteam.ivy.bpm.engine.restricted.model.IProcessElement;
import ch.ivyteam.ivy.bpm.engine.restricted.model.ITopLevelProcess;
import ch.ivyteam.ivy.bpm.engine.restricted.model.InputName;
import ch.ivyteam.ivy.bpm.engine.restricted.model.OutputName;
import ch.ivyteam.ivy.bpm.engine.restricted.model.service.FormatService;
import ch.ivyteam.ivy.bpm.engine.restricted.statistic.IProcessElementExecutionStatistic;
import ch.ivyteam.ivy.process.model.ProcessKind;
import ch.ivyteam.ivy.process.model.value.PID;

@SuppressWarnings("restriction")
final class TstProcessElementExecStat implements IProcessElementExecutionStatistic {
  private static final Random RANDOM = new Random();
  private static final long ONE_MILLION = 1_000_000l;
  private static int nextOrder = 0;

  private final int order = nextOrder++;

  private final long executions = RANDOM.nextInt(1000);
  private final long internalExecTime = RANDOM.nextInt(1000) * ONE_MILLION;
  private final long maxExternalExecTime = RANDOM.nextInt(1000) * ONE_MILLION;
  private final long minExternalExecTime = RANDOM.nextInt(1000) * ONE_MILLION;

  private final long externalExecutions = RANDOM.nextInt(1000);
  private final long externalExecTime = RANDOM.nextInt(1000) * ONE_MILLION;
  private final long maxInternalExecTime = RANDOM.nextInt(1000) * ONE_MILLION;
  private final long minInternalExecTime = RANDOM.nextInt(1000) * ONE_MILLION;

  @Override
  public int getExecutionOrder() {
    return order;
  }

  @Override
  public String getApplicationName() {
    return "app";
  }

  @Override
  public String getModelName() {
    return "pm";
  }

  @Override
  public String getVersionName() {
    return "1";
  }

  @Override
  public long getExecutions() {
    return executions;
  }

  @Override
  public long getProcessEngineExecutionTime() {
    return internalExecTime;
  }

  @Override
  public long getMinProcessEngineExecutionTime() {
    return minInternalExecTime;
  }

  @Override
  public long getMaxProcessEngineExecutionTime() {
    return maxInternalExecTime;
  }

  @Override
  public long getBackgroundExecutionTime() {
    return externalExecTime;
  }

  @Override
  public long getMinBackgroundExecutionTime() {
    return minExternalExecTime;
  }

  @Override
  public long getMaxBackgroundExecutionTime() {
    return maxExternalExecTime;
  }

  @Override
  public long getBackgroundExecutions() {
    return externalExecutions;
  }

  @Override
  public IProcessElement getProcessElement() {
    return new TstProcessElement();
  }

  private static final class TstProcessElement implements IProcessElement {

    @Override
    public PID getId() {
      return PID.of("5872394-f1");
    }

    @Override
    public String getName() {
      return "element name";
    }

    @Override
    public String getType() {
      return "Script";
    }

    @Override
    public IProcess getProcess() {
      return null;
    }

    @Override
    public ITopLevelProcess getTopLevelProcess() {
      return new TstProcess();
    }

    @Override
    public List<OutputName> getFlowOutputNames() {
      return null;
    }

    @Override
    public List<InputName> getFlowInputNames() {
      return null;
    }

    @Override
    public FormatService format() {
      return null;
    }

    @Override
    public IConnectorService connect(OutputName outputName) {
      return null;
    }

    @Override
    public IConnectorService connect() {
      return null;
    }

    @Override
    public Object getConfig() {
      return null;
    }

    @Override
    public void addErrorCatcher(ErrorCatcher errorCatcher) {}

    @Override
    public String toDisplayString() {
      return null;
    }
  }

  private static class TstProcess implements ITopLevelProcess {

    @Override
    public PID getId() {
      return null;
    }

    @Override
    public String getName() {
      return null;
    }

    @Override
    public IExecutionModel getExecutionModel() {
      return null;
    }

    @Override
    public IProcess getParentProcess() {
      return null;
    }

    @Override
    public Collection<IProcessElement> getProcessElements() {
      return null;
    }

    @Override
    public Collection<ErrorCatcher> getErrorCatchers() {
      return null;
    }

    @Override
    public IProcessElement getProcessElementWithId(PID pid) {
      return null;
    }

    @Override
    public String toDisplayString() {
      return null;
    }

    @Override
    public String getFullQualifiedName() {
      return "Process Name";
    }

    @Override
    public ProcessKind getKind() {
      return null;
    }
  }

}
