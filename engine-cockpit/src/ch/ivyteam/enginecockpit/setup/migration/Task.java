package ch.ivyteam.enginecockpit.setup.migration;

import ch.ivyteam.ivy.engine.migration.MigrationTask;

public class Task {

  private final String id;
  private final String name;
  private final String description;
  private String state;
  private String stateIcon;
  private final MigrationTask task;
  private String script;
  private int version;

  public Task(MigrationTask task) {
    this.id = task.id();
    this.task = task;
    this.version = task.version();
    this.name = task.name();
    this.description = task.description();
    this.stateIcon = "time-clock-circle";
    this.script = task.script();
  }

  String id() {
    return id;
  }

  public void run() {
    state = "running";
    stateIcon = "button-refresh-arrows si-is-spinning";
  }

  public String getScript() {
    return script;
  }

  public void done() {
    state = "done";
    stateIcon = "check-circle-1 state-active";
  }

  public void fail() {
    state = "fail";
    stateIcon = "remove-circle state-inactive";
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getState() {
    return state;
  }

  public String getStateIcon() {
    return stateIcon;
  }

  int getVersion() {
    return version;
  }

  MigrationTask getTask() {
    return task;
  }
}
