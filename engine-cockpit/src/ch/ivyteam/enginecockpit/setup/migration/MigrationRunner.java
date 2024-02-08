package ch.ivyteam.enginecockpit.setup.migration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.migration.MigrationClient;
import ch.ivyteam.ivy.migration.MigrationLog;
import ch.ivyteam.ivy.migration.MigrationLog.MigrationLog4j;
import ch.ivyteam.ivy.migration.MigrationTask;
import ch.ivyteam.ivy.migration.MigrationTaskEvent;
import ch.ivyteam.ivy.migration.input.Option;
import ch.ivyteam.ivy.migration.input.Quest;
import ch.ivyteam.ivy.migration.restricted.FileChoice;
import ch.ivyteam.ivy.migration.restricted.diff.TextContentComparison;

public class MigrationRunner implements MigrationClient {

  private List<Task> tasks;
  private boolean paused = false;

  public MigrationRunner(List<Task> tasks) {
    this.tasks = tasks;
  }

  public boolean isPaused() {
    return paused;
  }

  @Override
  public MigrationLog log() {
    return new MigrationLog4j();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T choose(Quest<T> quest) {
    var dto = findRunningDTO();
    if (quest instanceof FileChoice) {
      var autoSelect = ((FileChoice) quest).autoSelect(log());
      if (autoSelect.isPresent()) {
        return (T) autoSelect.get();
      }
    }
    show(dto, quest);
    return select(quest.options, dto);
  }

  private <T> T select(List<Option<T>> options, Task dto) {
    while (StringUtils.isBlank(dto.answer())) {
      try {
        paused = true;
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
    paused = false;
    return matchToString(options, dto.answer());
  }

  private static <T> T matchToString(List<Option<T>> options, String input) {
    if (StringUtils.isBlank(input)) {
      return options.get(0).value;
    }
    for (Option<T> opt : options) {
      if (input.equalsIgnoreCase(opt.name)) {
        return opt.value;
      }
    }
    return null;
  }

  private final <T> void show(Task dto, Quest<T> quest) {
    var diff = "";
    if (quest instanceof FileChoice) {
      diff = ((FileChoice) quest).getDiff().flatMap(content -> getChange(content)).orElse("");
    }
    dto.question(quest, diff);
  }

  @Override
  public void event(MigrationEvent event) {
    if (event instanceof MigrationTaskEvent) {
      var taskEvent = (MigrationTaskEvent) event;
      var dto = findDTOByTask(taskEvent.getTask());
      switch (taskEvent.getState()) {
        case DONE:
          dto.done();
          break;
        case FAILED:
          dto.fail();
          break;
        case STARTED:
          dto.run();
          break;
        default:
          break;
      }
    }
  }

  private Task findDTOByTask(MigrationTask task) {
    return tasks.stream().filter(t -> t.getTask().equals(task)).findFirst().get();
  }

  private Task findRunningDTO() {
    return tasks.stream().filter(t -> StringUtils.contains(t.getStateIcon(), "si-is-spinning")).findFirst()
            .get();
  }

  protected Optional<String> getChange(TextContentComparison compare) {
    var change = compare.getChangedLines();
    change = change.lines()
            .map(line -> fixWindowsPath(line))
            .collect(Collectors.joining("\n"));
    return Optional.of(change);
  }

  private static String fixWindowsPath(String line) {
    if (line.startsWith("--- ") || line.startsWith("+++ ")) {
      return line.replace("\\", "/");
    }
    return line;
  }
}
