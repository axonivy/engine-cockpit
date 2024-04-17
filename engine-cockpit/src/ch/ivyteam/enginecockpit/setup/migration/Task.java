package ch.ivyteam.enginecockpit.setup.migration;

import java.util.ArrayList;
import java.util.List;

import ch.ivyteam.ivy.migration.MigrationTask;
import ch.ivyteam.ivy.migration.input.Quest;

@SuppressWarnings("restriction")
public class Task {

  private final String name;
  private final String description;
  private String state;
  private String stateIcon;
  private final MigrationTask task;
  private final List<Question> questions;

  public Task(MigrationTask task) {
    this.task = task;
    this.name = task.name();
    this.description = task.description();
    this.stateIcon = "navigation-menu-horizontal";
    this.questions = new ArrayList<>();
  }

  public void run() {
    state = "running";
    stateIcon = "button-refresh-arrows si-is-spinning";
  }

  public void done() {
    state = "done";
    stateIcon = "check-circle-1";
  }

  public void fail() {
    state = "fail";
    stateIcon = "remove-circle";
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
