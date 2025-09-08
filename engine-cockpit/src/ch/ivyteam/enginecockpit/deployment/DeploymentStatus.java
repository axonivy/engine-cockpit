package ch.ivyteam.enginecockpit.deployment;

import ch.ivyteam.ivy.environment.Ivy;

public class DeploymentStatus {
  public enum State {
    IDLE, RUNNING, SUCCESS, ERROR
  }

  private State state = State.IDLE;

  public void idle() {
    this.state = State.IDLE;
  }

  public void running() {
    this.state = State.RUNNING;
  }

  public void success() {
    this.state = State.SUCCESS;
  }

  public void error() {
    this.state = State.ERROR;
  }

  public State getState() {
    return state;
  }

  public boolean isVisible() {
    return state != State.IDLE;
  }

  public String getText() {
    return switch (state) {
      case RUNNING -> Ivy.cm().co("/common/Running");
      case SUCCESS -> Ivy.cm().co("/common/Success");
      case ERROR -> Ivy.cm().co("/common/Error");
      default -> "";
    };
  }

  public String getIconClass() {
    return switch (state) {
      case RUNNING -> "si-button-refresh-arrows si-is-spinning";
      case SUCCESS -> "si-check-circle-1";
      case ERROR -> "si-alert-circle";
      default -> "";
    };
  }

  public String getStyleClass() {
    return switch (state) {
      case RUNNING -> "status-running";
      case SUCCESS -> "status-success";
      case ERROR -> "status-error";
      default -> "";
    };
  }
}
