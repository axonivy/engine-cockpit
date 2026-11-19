package ch.ivyteam.enginecockpit.application.model;

import ch.ivyteam.ivy.application.project.ProjectState;
import ch.ivyteam.ivy.application.project.ProjectState.ProjectMode;

public class ProjectStateDto {

  private final ProjectState state;

  public ProjectStateDto(ProjectState state) {
    this.state = state;
  }

  public String getProjectState() {
    return state.mode().name();
  }

  public String getProjectStateMessage() {
    if (state.mode() == ProjectMode.ERROR) {
      return state.errorMessage();
    }
    return state.mode().message();
  }

  public String getProjectStateStyleClass() {
    return "state-badge state-project-" + state.mode().name().toLowerCase();
  }

  public String getProjectStateIcon() {
    return switch (state.mode()) {
      case UNKNOWN -> "ti ti-circle-minus";
      case OK, RUNNABLE -> "ti ti-circle-check";
      case ERROR, OUTDATED, TOO_OLD, TOO_NEW -> "ti ti-circle-x";
    };
  }
}
