package ch.ivyteam.enginecockpit.application.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.application.ActivityOperationState;
import ch.ivyteam.ivy.application.ActivityState;
import ch.ivyteam.ivy.application.IActivity;
import ch.ivyteam.ivy.application.ReleaseState;

public class StateOfActivity {
  private String activityState;
  private String activityStateIcon;
  private String errorMessage;
  private List<String> childProblems;
  private String operation;
  private String operationIcon;
  private String releaseState = "";
  private String releaseStateIcon;
  private boolean processing;

  public StateOfActivity() {
    updateState(null);
    updateOperation(null);
  }

  public StateOfActivity(IActivity activity) {
    updateState(activity.getActivityState());
    updateOperation(activity.getActivityOperationState());
    this.errorMessage = activity.getErrorMessage();
    this.childProblems = List.of();
  }

  public String getState() {
    return activityState;
  }

  public String getStateCssClass() {
    return activityState.toLowerCase();
  }

  public String getStateIcon() {
    return activityStateIcon;
  }

  public String getOperation() {
    return operation;
  }

  public String getOperationCssClass() {
    return operation.toLowerCase();
  }

  public String getOperationIcon() {
    return operationIcon;
  }

  public String getReleaseState() {
    return releaseState;
  }

  public String getReleaseStateCssClass() {
    return releaseState.toLowerCase();
  }

  public String getReleaseStateIcon() {
    return releaseStateIcon;
  }

  public String getErrorMessage() {
    return StringUtils.isBlank(errorMessage) ? "" : "\n" + errorMessage;
  }

  public String getChildProblems() {
    var message = childProblems.stream().collect(Collectors.joining("\n"));
    return StringUtils.isBlank(message) ? "" : "\n" + message;
  }

  public boolean isProcessing() {
    return processing;
  }

  private void updateState(ActivityState update) {
    if (update == null) {
      update = ActivityState.INACTIVE;
    }
    this.activityState = update.name();
    switch (update) {
      case ACTIVE:
        this.activityStateIcon = "ti ti-circle-check";
        break;
      default:
        this.activityStateIcon = "ti ti-player-pause";
    }
  }

  private void updateOperation(ActivityOperationState update) {
    if (update == null) {
      update = ActivityOperationState.INACTIVE;
    }
    this.operation = update.name();
    this.processing = false;
    switch (update) {
      case ACTIVE:
      case LOCKED:
        this.operationIcon = "ti ti-circle-check";
        break;
      case INACTIVE:
        this.operationIcon = "ti ti-player-pause";
        break;
      case ERROR:
        this.operationIcon = "ti ti-circle-minus";
        break;
      default:
        this.operationIcon = "ti ti-refresh spinning";
        this.processing = true;
    }
  }

  public void updateReleaseState(ReleaseState update) {
    if (update == null) {
      update = ReleaseState.DELETED;
    }
    this.releaseState = update.name();
    switch (update) {
      case RELEASED:
        this.releaseStateIcon = "ti ti-circle-check";
        break;
      case DEPRECATED:
        this.releaseStateIcon = "ti ti-circle-half-vertical";
        break;
      case ARCHIVED:
        this.releaseStateIcon = "ti ti-archive";
        break;
      case CREATED:
      case PREPARED:
        this.releaseStateIcon = "ti ti-speakerphone";
        break;
      default:
        this.releaseStateIcon = "ti ti-help-circle";
    }
  }

  public boolean is(ActivityState... states) {
    return Arrays.asList(states).stream()
        .anyMatch(s -> s.name().equals(activityState));
  }

  public boolean is(ReleaseState... states) {
    return Arrays.asList(states).stream()
        .anyMatch(s -> s.name().equals(releaseState));
  }

  public static boolean is(String operation, ActivityOperationState... states) {
    return Arrays.asList(states).stream()
        .anyMatch(s -> s.name().equals(operation));
  }
}
