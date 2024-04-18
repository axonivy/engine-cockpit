package ch.ivyteam.enginecockpit.setup.migration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.migration.EngineMigrator;
import ch.ivyteam.ivy.migration.EngineMigrator.Result;

@ManagedBean
@ViewScoped
public class MigrationBean {

  private String pathToOldEngine;
  private EngineMigrator migrator;
  private List<Task> tasks;
  private MigrationState running;
  private CompletableFuture<String> migrationRunner;
  private MigrationRunner client;
  private Result result;
  private boolean writeToTmp = false;

  public void checkOldEngineLocation() {
    try {
      migrator = new EngineMigrator(pathToOldEngine);
      result = migrator.check();
      tasks = loadTasks(result);
      client = new MigrationRunner(tasks);
      running = MigrationState.START;
    } catch (Exception ex) {
      Message.error()
              .clientId("migrateGrowl")
              .summary("Error while validate location")
              .exception(ex)
              .show();
    }
  }

  public boolean isInPlace() {
    if (result == null) {
      return false;
    }
    if (!result.success()) {
      return false;
    }
    return result.mode() == EngineMigrator.Mode.IN_PLACE;
  }

  public boolean isWriteToTmp() {
    return writeToTmp;
  }

  public void setWriteToTmp(boolean writeToTmp) {
    this.writeToTmp = writeToTmp;
  }

  private static List<Task> loadTasks(Result result) {
    if (result.success()) {
      return result.tasks().stream().map(Task::new).collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  public void execute() throws Exception {
    running = MigrationState.RUNNING;
    migrationRunner = CompletableFuture.supplyAsync(() -> {
      try {
        migrator.run(client, writeToTmp);
        return "";
      } catch (Exception ex) {
        return ex.getMessage();
      }
    });
  }

  public Result getResult() {
    return result;
  }

  public String getPathToOldEngine() {
    return pathToOldEngine;
  }

  public void setPathToOldEngine(String pathToOldEngine) {
    this.pathToOldEngine = pathToOldEngine;
  }

  public List<Task> getTasks() {
    return tasks;
  }

  public MigrationState getState() throws InterruptedException, ExecutionException {
    if (running == MigrationState.RUNNING && migrationRunner != null && migrationRunner.isDone()) {
      running = MigrationState.FINISHED;
      if (StringUtils.isNotBlank(migrationRunner.get())) {
        Message.error()
                .clientId("migrationMessage")
                .summary("Error while migration")
                .detail(migrationRunner.get())
                .show();
      }
    }
    return running;
  }

  public boolean isRunnerPaused() {
    return client.isPaused();
  }

  public static enum MigrationState {
    START, RUNNING, FINISHED,
  }
}
