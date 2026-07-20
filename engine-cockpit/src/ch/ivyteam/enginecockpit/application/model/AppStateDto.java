package ch.ivyteam.enginecockpit.application.model;

import java.util.function.Consumer;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.application.app.state.ActivityState;
import ch.ivyteam.ivy.application.app.state.AppState;
import ch.ivyteam.ivy.application.app.state.ReleaseState;
import ch.ivyteam.ivy.environment.Ivy;

public class AppStateDto {

  private final AppState state;

  public AppStateDto(AppState state) {
    this.state = state;
  }

  public ReleaseState getReleaseState() {
    return state.releaseState();
  }

  public String getReleaseStateLabel() {
    return state.releaseState().toString();
  }

  public String getReleaseStateStyleClass() {
    return "state-badge state-app-" + state.releaseState().name().toLowerCase();
  }

  public String getReleaseStateIcon() {
    return switch (state.releaseState()) {
      case RELEASED -> "ti ti-circle-check";
      case DEPRECATED -> "ti ti-circle-half-vertical";
      case ARCHIVED -> "ti ti-archive";
      case CREATED, PREPARED -> "ti ti-speakerphone";
    };
  }

  public String getActivityStateLabel() {
    return state.activityState().toString();
  }

  public String getActivityStateStyleClass() {
    return "state-badge state-app-" + state.activityState().name().toLowerCase();
  }

  public String getActivityStateIcon() {
    return switch (state.activityState()) {
      case ACTIVE -> "ti ti-player-play";
      case INACTIVE -> "ti ti-player-stop";
    };
  }

  public boolean isDeactivatable() {
    return state.canChangeTo(ActivityState.INACTIVE);
  }

  public boolean isActivatable() {
    return state.canChangeTo(ActivityState.ACTIVE);
  }

  public boolean isReleasable() {
    return state.canChangeTo(ReleaseState.RELEASED);
  }

  public boolean isArchiveable() {
    return state.canChangeTo(ReleaseState.ARCHIVED);
  }

  public boolean isDeprecatable() {
    return state.canChangeTo(ReleaseState.DEPRECATED);
  }

  public void activate() {
    execute(AppState::activate, "activate");
  }

  public void deactivate() {
    execute(AppState::deactivate, "deactivate");
  }

  public void release() {
    execute(AppState::release, "release");
  }

  public void deprecate() {
    execute(AppState::deprecate, "deprecate");
  }

  public void archive() {
    execute(AppState::archive, "archive");
  }

  private void execute(Consumer<AppState> operation, String actionKey) {
    try {
      operation.accept(state);
    } catch (RuntimeException ex) {
      Message.error()
          .clientId("applicationMessage")
          .summary(Ivy.cm().co("/common/Error"))
          .detail(ex.getMessage())
          .exception(ex)
          .show();
    }
  }

  public String getActivityState() {
    return state.activityState().name();
  }

  public String getActivityStateCssClass() {
    return state.activityState().name().toLowerCase();
  }

  public String getActivityOperationState() {
    return state.activityOperationState().name();
  }

  public String getActivityOperationStateCssClass() {
    return state.activityOperationState().name().toLowerCase();
  }

  public String getActivityOperationStateIcon() {
    return switch (state.activityOperationState()) {
      case ACTIVE -> "ti ti-circle-check";
      case INACTIVE -> "ti ti-player-pause";
      case ERROR -> "ti ti-circle-minus";
      default -> "ti ti-refresh spinning";
    };
  }

  public boolean isActivityStateProcessing() {
    return switch (state.activityOperationState()) {
      case ACTIVE, INACTIVE, ERROR -> false;
      default -> true;
    };
  }
}
