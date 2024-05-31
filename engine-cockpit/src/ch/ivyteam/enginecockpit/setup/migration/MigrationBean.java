package ch.ivyteam.enginecockpit.setup.migration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import ch.ivyteam.enginecockpit.commons.Message;
import ch.ivyteam.ivy.engine.migration.EngineMigrator;
import ch.ivyteam.ivy.engine.migration.EngineMigrator.Check;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.licence.NewLicenceFileInstaller;

@ManagedBean
@ApplicationScoped
public class MigrationBean {

  private String pathToOldEngine;
  private EngineMigrator migrator;
  private List<Task> tasks;
  private MigrationState running;
  private CompletableFuture<Exception> asyncRunner;
  private MigrationRunner client;
  private EngineMigrator.Result result;
  private boolean writeToTmp = false;
  private UploadedFile uploadedLicenceFile;
  private Set<String> showLogs = new HashSet<>();

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

  public void show(Task task) {
    showLogs.add(task.id());
  }

  public boolean showLog(Task task) {
    return showLogs.contains(task.id());
  }

  public UploadedFile getUploadedLicenceFile() {
    return uploadedLicenceFile;
  }

  public void setUploadedLicenceFile(UploadedFile file) {
    this.uploadedLicenceFile = file;
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

  public boolean licenceCheck() {
    if (result == null) {
      return false;
    }
    return result.success();
  }

  public boolean isValidLicence() {
    return migrator.checkLicence().success();
  }

  public String getInvalidLicenceMessage() {
    return migrator.checkLicence().message();
  }

  public String getLicence() {
    var lic = migrator.checkLicence().licence();
    if (lic == null) {
      return "";
    }
    try {
      return lic.save().toAsciiOnlyString();
    } catch (IOException ex) {
      return "Error while reading licence: " + ex.getMessage();
    }
  }

  public String getStartMigrationButtonIcon() {
    return switch(running) {
      case START -> "si si-controls-play";
      case RUNNING -> "si si-button-refresh-arrows si-is-spinning";
      case FINISHED -> "si si-check-1";
    };
  }

  public String getStartMigrationButtonName() {
    return switch(running) {
      case START -> "Start";
      case RUNNING -> "Running " + client.taskCountDone() + "/" + client.taskCountAll();
      case FINISHED -> "Done";
    };
  }

  public void handleUploadLicence(FileUploadEvent event) {
    try (var in = event.getFile().getInputStream()) {
      NewLicenceFileInstaller.install(event.getFile().getFileName(), in);
    } catch (Exception ex) {
      Message.error().clientId("licenceMessage").summary(ex.getMessage()).show();
    }
  }

  private static List<Task> loadTasks(EngineMigrator.Result result) {
    if (result.success()) {
      return result.tasks().stream()
              .map(Task::new)
              .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  public void execute() throws Exception {
    running = MigrationState.RUNNING;
    asyncRunner = CompletableFuture.supplyAsync(() -> {
      try {
        migrator.run(client, writeToTmp);
        return null;
      } catch (Exception ex) {
        return ex;
      }
    });
  }

  public EngineMigrator.Result getResult() {
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

  public String getConfigurationTmpDir() {
    return migrator.tmpConfigDir().toString();
  }

  public boolean isNotLastCheck(Check check) {
    var checks = result.checks();
    return !checks.get(checks.size() - 1).equals(check);
  }

  public String getSummary() {
    Ivy.log().info(client.summary());
    return client.summary();
  }

  public MigrationState getState() throws InterruptedException, ExecutionException {
    if (running == MigrationState.RUNNING && asyncRunner != null && asyncRunner.isDone()) {
      running = MigrationState.FINISHED;
      var exception = asyncRunner.get();
      if (exception == null) {
        Message.info()
                .clientId("migrationMessage")
                .summary("The Axon Ivy Engine migration was successful. Click on Finish.")
                .show();
      } else {
        Message.error()
                .clientId("migrationMessage")
                .summary("Error while migration")
                .exception(exception)
                .show();
      }
    }
    return running;
  }

  public static enum MigrationState {
    START, RUNNING, FINISHED,
  }
}
