package ch.ivyteam.enginecockpit.setup.migration;

import ch.ivyteam.ivy.engine.migration.MigrationTask;

public class Task {

  private static final String DONE = "done";
  private final String id;
  private final String name;
  private final String description;
  private String state;
  private String stateIcon;
  private final MigrationTask task;
  private final String script;
  private final int version;
  private String log = "";

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
    stateIcon = "ti ti-refresh spinning";
  }

  public String getScript() {
    return script;
  }

  public void done() {
    state = DONE;
    stateIcon = "ti ti-circle-check state-active";
  }

  public boolean isDone() {
    return DONE.equals(state);
  }

  public void fail() {
    state = "fail";
    stateIcon = "ti ti-circle-minus state-inactive";
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

  public String getLog() {
    return log;
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

  public void log(String message) {
    log += message + "\n";
  }
}
