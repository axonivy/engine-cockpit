package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.cache;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.delta;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.derivation;

import ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider;

class ExecutionCounter
{
  private final MValueProvider executions;

  private final MValueProvider deltaExecutions;

  private final MValueProvider errors;
  private final MValueProvider deltaErrors;
  
  private final MValueProvider executionTime;
  private final MValueProvider deltaMinExecutionTime;
  private final MValueProvider deltaAvgExecutionTime;
  private final MValueProvider deltaMaxExecutionTime;

  ExecutionCounter(String objectName, String executionsName)
  {
    executions = attribute(objectName, executionsName);
    
    deltaExecutions = cache(1, delta(executions));
    
    errors = attribute(objectName, "errors");
    deltaErrors = cache(1, delta(errors));
    
    executionTime = attribute(objectName, executionsName+"TotalExecutionTimeInMicroSeconds");
    deltaMinExecutionTime = cache(1, attribute(objectName, executionsName+"MinExecutionTimeDeltaInMicroSeconds"));
    deltaAvgExecutionTime = cache(1, derivation(executionTime, executions));
    deltaMaxExecutionTime = cache(1, attribute(objectName, executionsName+"MaxExecutionTimeDeltaInMicroSeconds"));
  }

  MValueProvider executions()
  {
    return executions;
  }

  MValueProvider deltaExecutions()
  {
    return deltaExecutions;
  }

  MValueProvider errors()
  {
    return errors;
  }
  
  MValueProvider deltaErrors()
  {
    return deltaErrors;
  }

  MValueProvider deltaMinExecutionTime()
  {    
    return deltaMinExecutionTime;
  }

  MValueProvider deltaAvgExecutionTime()
  {
    return deltaAvgExecutionTime;
  }

  MValueProvider deltaMaxExecutionTime()
  {
    return deltaMaxExecutionTime;
  }

  MValueProvider executionTime()
  {
    return executionTime;
  }
}
