package ch.ivyteam.enginecockpit.setup;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.migration.MigrationEngines;
import ch.ivyteam.ivy.migration.MigrationPath;
import ch.ivyteam.ivy.migration.MigrationScenario;
import ch.ivyteam.ivy.migration.restricted.MigrateToLocalEngine;
import ch.ivyteam.ivy.migration.restricted.MigrationTaskLoader;

@ManagedBean
@ViewScoped
@SuppressWarnings("restriction")
public class MigrationBean
{
  private String pathToOldEngine;
  private MigrationEngines engines;
  private MigrationPath migrationPath;
  private MigrationScenario scenario;
  private List<Task> tasks;
  private MigrationState running;
  private CompletableFuture<String> migrationRunner;
  private MigrationRunner client;
  
  public void checkOldEngineLocation()
  {
    try
    {
      var oldEngine = Path.of(pathToOldEngine);
      engines = MigrateToLocalEngine.fromOrigin(oldEngine);
      migrationPath = MigrationPath.of(engines.getOrigin().getVersion(), engines.getTarget().getVersion());
      scenario = MigrationScenario.create(migrationPath, ()->MigrationTaskLoader.allTasks());
      reloadTasks();
      running = MigrationState.START;
    }
    catch (Exception ex)
    {
      FacesContext.getCurrentInstance().addMessage("migrateGrowl", 
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while validate location", ex.getMessage()));
    }
  }
  
  public void execute() throws Exception
  {
    running = MigrationState.RUNNING;
    migrationRunner = CompletableFuture.supplyAsync(() -> {
      try
      {
        //TODO: add cancel scenario possibility
        scenario.run(engines, client);
        return "";
      }
      catch (Exception ex)
      {
        return ex.getMessage();
      }
    });
  }
  
  private void reloadTasks()
  {
    tasks = scenario.getTasks().stream().map(Task::new).collect(Collectors.toList());
    client = new MigrationRunner(tasks);
  }
  
  public String getPathToOldEngine()
  {
    return pathToOldEngine;
  }
  
  public void setPathToOldEngine(String pathToOldEngine)
  {
    this.pathToOldEngine = pathToOldEngine;
  }
  
  public String getMigrationPath()
  {
    return migrationPath != null ? migrationPath.toString() : "";
  }
  
  public List<Task> getTasks()
  {
    return tasks;
  }
  
  public MigrationState getState() throws InterruptedException, ExecutionException
  {
    if (running == MigrationState.RUNNING && migrationRunner != null && migrationRunner.isDone())
    {
      running = MigrationState.FINISHED;
      if (StringUtils.isNotBlank(migrationRunner.get()))
      {
        FacesContext.getCurrentInstance().addMessage("migrationMessage", 
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while migration", migrationRunner.get()));
      }
      else
      {
        FacesContext.getCurrentInstance().addMessage("migrationMessage", 
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Your engine was migrated successfully. Please restart your engine!"));
      }
    }
    return running;
  }
  
  public boolean isRunnerPaused()
  {
    return client.isPaused();
  }
  
  public static enum MigrationState {
    START,
    RUNNING,
    FINISHED,
  }  
}
