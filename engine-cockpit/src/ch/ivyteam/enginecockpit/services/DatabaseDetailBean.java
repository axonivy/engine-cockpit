package ch.ivyteam.enginecockpit.services;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.db.jdbc.DatabaseUtil;
import ch.ivyteam.db.jdbc.JdbcDriver;
import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.DatabaseMonitor;
import ch.ivyteam.enginecockpit.services.help.HelpServices;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.TestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.services.model.DatabaseDto;
import ch.ivyteam.enginecockpit.services.model.DatabaseDto.Connection;
import ch.ivyteam.enginecockpit.services.model.DatabaseDto.ExecStatement;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.enginecockpit.system.SystemDatabaseBean;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.db.Database;
import ch.ivyteam.ivy.db.Database.Builder;
import ch.ivyteam.ivy.db.Databases;
import ch.ivyteam.ivy.db.IExternalDatabaseManager;

@ManagedBean
@ViewScoped
public class DatabaseDetailBean extends HelpServices implements IConnectionTestResult
{
  private DatabaseDto database;
  private Property activeProperty;
  private List<ExecStatement> history;
  private List<Connection> connections;
  private String databaseName;
  
  private ManagerBean managerBean;
  private ConnectionTestResult testResult;
  
  private final ConnectionTestWrapper connectionTest;
  private DatabaseMonitor liveStats;
  private Databases databases;
  
  public DatabaseDetailBean()
  {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    connectionTest = new ConnectionTestWrapper();
  }
  
  public String getDatabaseName()
  {
    return databaseName;
  }
  
  public void setDatabaseName(String databaseName)
  {
    if (this.databaseName == null)
    {
      this.databaseName = databaseName;
      databases = Databases.of(managerBean.getSelectedIApplication(), managerBean.getSelectedEnvironment());
      reloadExternalDb();
      liveStats = new DatabaseMonitor(managerBean.getSelectedApplicationName(), managerBean.getSelectedEnvironment(), databaseName);
    }
  }

  private void reloadExternalDb()
  {
    var app = managerBean.getSelectedIApplication();
    var env = managerBean.getSelectedEnvironment();
    var db = databases.find(databaseName);
    database = new DatabaseDto(db);
    var externalDb = IExternalDatabaseManager.instance().getExternalDatabase(app, db, env);
    history = externalDb.getExecutionHistory().stream()
            .map(statement -> new ExecStatement(statement))
            .collect(Collectors.toList());
    connections = externalDb.getConnections().stream()
            .map(conn -> new Connection(conn))
            .collect(Collectors.toList());
  }
  
  public DatabaseDto getDatabase()
  {
    return database;
  }
  
  public void setProperty(String key)
  {
    this.activeProperty = new Property();
    if (key != null)
    {
      this.activeProperty = new Property(key, database.getProperties().get(key));
    }
  }
  
  public Property getProperty()
  {
    return activeProperty;
  }
  
  public void saveProperty()
  {
    var props = database.getProperties();
    props.put(activeProperty.getName(), activeProperty.getValue());
    saveDatabase(dbBuilder().properties(props));
    reloadExternalDb();
  }
  
  public void removeProperty(String key)
  {
    var props = database.getProperties();
    props.remove(key);
    saveDatabase(dbBuilder().properties(props));
    reloadExternalDb();
  }

  public List<Connection> getConnections()
  {
    return connections;
  }
  
  public List<ExecStatement> getExecutionHistory()
  {
    return history;
  }

  public List<String> completeDriver(String value)
  {
    return JdbcDriver.all().stream()
            .filter(driver -> driver.isInstalled())
            .map(driver -> driver.getDriverName())
            .filter(name -> !StringUtils.startsWith(name, SystemDatabaseBean.HSQL_DB))
            .filter(name -> StringUtils.startsWith(name, value))
            .distinct()
            .collect(Collectors.toList());
  }

  @Override
  public String getTitle()
  {
    return "Database '" + databaseName + "'";
  }
  
  @Override
  public String getGuideText()
  {
    return "To edit your Database overwrite your app.yaml file. For example copy and paste the snippet below.";
  }
  
  @Override
  public String getYaml()
  {
    var valuesMap = new HashMap<String, String>();
    valuesMap.put("name", database.getName());
    valuesMap.put("url", database.getUrl());
    valuesMap.put("driver", database.getDriver());
    valuesMap.put("username", database.getUserName());
    valuesMap.put("maxConnections", String.valueOf(database.getMaxConnections()));
    valuesMap.put("properties", parsePropertiesToYaml(database.getProperties()));
    String templateString = readTemplateString("database.yaml");
    StrSubstitutor strSubstitutor = new StrSubstitutor(valuesMap);
    return strSubstitutor.replace(templateString);
  }
  
  @Override
  public String getHelpUrl()
  {
    return UrlUtil.getCockpitEngineGuideUrl() + "services.html#external-database-detail";
  }
  
  public void testDbConnection()
  {
    testResult = (ConnectionTestResult) connectionTest.test(() -> testConnection());
  }
  
  private ConnectionTestResult testConnection()
  {
    var dbConfig = databases.find(databaseName).config();
    try (var connection =  DatabaseUtil.openConnection(dbConfig))
    {
      var metaData = connection.getMetaData();
      var productName = metaData.getDatabaseProductName();
      var productVersion = metaData.getDatabaseProductVersion();
      var jdbcVersion = metaData.getJDBCMajorVersion();
      return new ConnectionTestResult("", 0, TestResult.SUCCESS, "Successfully connected to database: " + productName
              + " (" + productVersion + ") with JDBC Version " + jdbcVersion);
    }
    catch (Exception ex)
    {
      return new ConnectionTestResult("", 0, TestResult.ERROR, "An error occurred: " + ExceptionUtils.getStackTrace(ex));
    }
  }

  public void saveDbConfig()
  {
    connectionTest.stop();
    var dbBuilder = dbBuilder()
            .url(database.getUrl())
            .driver(database.getDriver())
            .user(database.getUserName())
            .maxConnections(database.getMaxConnections());
    if (database.passwordChanged())
    {
      dbBuilder.password(database.getPassword());
    }
    saveDatabase(dbBuilder);
    FacesContext.getCurrentInstance().addMessage("databaseConfigMsg", 
            new FacesMessage("Database configuration saved", ""));
    reloadExternalDb();
  }
  
  public void resetDbConfig()
  {
    connectionTest.stop();
    databases.remove(databaseName);
    FacesContext.getCurrentInstance().addMessage("databaseConfigMsg", 
            new FacesMessage("Database configuration reset", ""));
    reloadExternalDb();
  }

  @Override
  public ConnectionTestResult getResult()
  {
    return testResult;
  }
  
  public DatabaseMonitor getLiveStats()
  {
    return liveStats;
  }
  
  private Builder dbBuilder()
  {
    var originDb = databases.find(databaseName);
    return Database.create(databaseName)
            .url(originDb.url())
            .driver(originDb.driver())
            .user(originDb.user())
            .password(originDb.password())
            .maxConnections(originDb.maxConnections())
            .properties(originDb.properties());
  }
  
  private void saveDatabase(Builder dbBuilder)
  {
    databases.set(dbBuilder.toDatabase());
  }

}
