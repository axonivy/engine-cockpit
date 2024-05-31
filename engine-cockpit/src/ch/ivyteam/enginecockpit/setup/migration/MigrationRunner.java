package ch.ivyteam.enginecockpit.setup.migration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.ivyteam.ivy.engine.migration.MigrationClient;
import ch.ivyteam.ivy.engine.migration.MigrationEvent;

public class MigrationRunner implements MigrationClient {

  private final Map<String, Task> tasks;
  private String summary;

  public MigrationRunner(List<Task> tasks) {
    this.tasks = tasks.stream().collect(Collectors.toMap(Task::id, t -> t));
  }

  public String summary() {
    return summary;
  }

  public long taskCountAll() {
    return tasks.size();
  }

  public long taskCountDone() {
    return tasks.values().stream()
            .filter(t -> t.isDone())
            .count();
  }

  @Override
  public void event(MigrationEvent event) {
    switch (event.state()) {
      case STARTED -> task(event).run();
      case PROGRESS -> task(event).log(event.message());
      case DONE -> task(event).done();
      case FAILED -> task(event).fail();
      case FINISHED -> summary = event.message();
    }
  }

  private Task task(MigrationEvent event) {
    return tasks.get(event.task().id());
  }
}
