package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.cache;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.delta;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.derivation;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

class ExecutionCounter {
  private final ValueProvider executions;

  private final ValueProvider deltaExecutions;

  private final ValueProvider errors;
  private final ValueProvider deltaErrors;

  private final ValueProvider executionTime;
  private final ValueProvider deltaMinExecutionTime;
  private final ValueProvider deltaAvgExecutionTime;
  private final ValueProvider deltaMaxExecutionTime;

  ExecutionCounter(String objectName, String executionsName) {
    this(objectName, executionsName, "errors");
  }

  ExecutionCounter(String objectName, String executionsName, String errorName) {
    executions = attribute(objectName, executionsName, Unit.ONE);

    deltaExecutions = cache(1, delta(executions));

    errors = attribute(objectName, errorName, Unit.ONE);
    deltaErrors = cache(1, delta(errors));

    executionTime = attribute(objectName, executionsName + "TotalExecutionTimeInMicroSeconds",
            Unit.MICRO_SECONDS);
    deltaMinExecutionTime = cache(1, attribute(objectName,
            executionsName + "MinExecutionTimeDeltaInMicroSeconds", Unit.MICRO_SECONDS));
    deltaAvgExecutionTime = cache(1, derivation(executionTime, executions));
    deltaMaxExecutionTime = cache(1, attribute(objectName,
            executionsName + "MaxExecutionTimeDeltaInMicroSeconds", Unit.MICRO_SECONDS));
  }

  ValueProvider executions() {
    return executions;
  }

  ValueProvider deltaExecutions() {
    return deltaExecutions;
  }

  ValueProvider errors() {
    return errors;
  }

  ValueProvider deltaErrors() {
    return deltaErrors;
  }

  ValueProvider deltaMinExecutionTime() {
    return deltaMinExecutionTime;
  }

  ValueProvider deltaAvgExecutionTime() {
    return deltaAvgExecutionTime;
  }

  ValueProvider deltaMaxExecutionTime() {
    return deltaMaxExecutionTime;
  }

  ValueProvider executionTime() {
    return executionTime;
  }
}
