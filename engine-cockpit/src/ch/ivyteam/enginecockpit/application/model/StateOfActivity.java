package ch.ivyteam.enginecockpit.application.model;

import java.util.ArrayList;
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
        this.activityStateIcon = "check-circle-1";
        break;
      default:
        this.activityStateIcon = "button-pause";
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
        this.operationIcon = "check-circle-1";
        break;
      case INACTIVE:
        this.operationIcon = "button-pause";
        break;
      case ERROR:
        this.operationIcon = "remove-circle";
        break;
      default:
        this.operationIcon = "button-refresh-arrows si-is-spinning";
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
        this.releaseStateIcon = "check-circle-1";
        break;
      case DEPRECATED:
        this.releaseStateIcon = "delete";
        break;
      case ARCHIVED:
        this.releaseStateIcon = "archive";
        break;
      case CREATED:
      case PREPARED:
        this.releaseStateIcon = "advertising-megaphone-2";
        break;
      default:
        this.releaseStateIcon = "question-circle";
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

  public void updateChildProblems(AbstractActivity activity) {
    if (is(activity.getState().operation, ActivityOperationState.ACTIVE, ActivityOperationState.LOCKED)) {
      this.childProblems = checkChildrenForProblemStates(activity);
    }
  }

  private static List<String> checkChildrenForProblemStates(AbstractActivity activity) {
    if (activity == null) {
      return List.of();
    }
    var problems = new ArrayList<String>();
    for (var child : activity.children) {
      if (is(child.getState().operation, ActivityOperationState.INACTIVE, ActivityOperationState.ERROR)
              && isNotArchived(child)) {
        problems.add(child.getName() + ": " + child.getState().operation);
      }
      problems.addAll(checkChildrenForProblemStates(child));
    }
    return problems;
  }

  private static boolean isNotArchived(AbstractActivity child) {
    return !(child.isPmv() && child.getState().is(ReleaseState.ARCHIVED));
  }
}
