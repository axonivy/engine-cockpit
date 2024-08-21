package ch.ivyteam.enginecockpit.services.model;

import java.util.Date;

import ch.ivyteam.enginecockpit.util.DateUtil;

public class ExecHistoryStatement {
    private final String executionTimestamp;
    private final String executionTimeInMicroSeconds;
    private final String processElementId;

    public ExecHistoryStatement(Date executionTimestamp, long executionTimeInMicroSeconds, String processElementId) {
        this.executionTimestamp = DateUtil.formatDate(executionTimestamp);
        this.executionTimeInMicroSeconds = (double) executionTimeInMicroSeconds / 1000 + "ms";;
        this.processElementId = processElementId;
    }

    public String getExecutionTimestamp() {
        return executionTimestamp;
    }

    public String getExecutionTimeInMicroSeconds() {
        return executionTimeInMicroSeconds;
    }

    public String getProcessElementId() {
        return processElementId;
    }
}
