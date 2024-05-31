package ch.ivyteam.enginecockpit.setup.migration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ch.ivyteam.ivy.engine.migration.MigrationClient;
import ch.ivyteam.ivy.engine.migration.MigrationEvent;

public class MigrationRunner implements MigrationClient {

  private final Map<String, Task> tasks;

  public MigrationRunner(List<Task> tasks) {
    this.tasks = tasks.stream().collect(Collectors.toMap(Task::id, t -> t));
  }

  @Override
  public void event(MigrationEvent event) {
    var dto = tasks.get(event.task().id());
    switch (event.state()) {
      case DONE -> dto.done();
      case FAILED -> dto.fail();
      case STARTED -> dto.run();
      case PROGRESS -> dto.log(event.message());
    }
  }
}
