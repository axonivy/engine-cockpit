package ch.ivyteam.enginecockpit.setup.migration;

import java.util.ArrayList;
import java.util.List;

import ch.ivyteam.ivy.engine.migration.MigrationTask;
import ch.ivyteam.ivy.engine.migration.input.Quest;

public class Task {

  private final String name;
  private final String description;
  private String state;
  private String stateIcon;
  private final MigrationTask task;
  private final List<Question> questions;
  private String script;
  private int version;

  public Task(MigrationTask task) {
    this.task = task;
    this.version = task.version();
    this.name = task.name();
    this.description = task.description();
    this.stateIcon = "navigation-menu-horizontal";
    this.questions = new ArrayList<>();
    this.script = task.script();
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

  public void question(Quest<?> quest, String diff) {
    questions.add(new Question(quest, diff));
  }

  public List<Question> getQuestions() {
    return questions;
  }

  String answer() {
    if (questions.size() > 0) {
      return questions.get(questions.size() - 1).getAnswer();
    }
    return null;
  }
}
