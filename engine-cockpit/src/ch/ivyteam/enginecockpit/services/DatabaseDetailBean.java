package ch.ivyteam.enginecockpit.services;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.db.jdbc.DatabaseConnectionConfiguration;
import ch.ivyteam.db.jdbc.DatabaseUtil;
import ch.ivyteam.db.jdbc.JdbcDriver;
import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.DatabaseMonitor;
import ch.ivyteam.enginecockpit.services.help.HelpServices;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.TestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.services.model.DatabaseDto;
import ch.ivyteam.enginecockpit.services.model.DatabaseDto.Connection;
import ch.ivyteam.enginecockpit.services.model.DatabaseDto.ExecStatement;
import ch.ivyteam.enginecockpit.system.SystemDatabaseBean;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.db.Database.Builder;
import ch.ivyteam.ivy.db.Databases;
import ch.ivyteam.ivy.db.IExternalDatabaseManager;

@ManagedBean
@ViewScoped
public class DatabaseDetailBean extends HelpServices implements IConnectionTestResult, PropertyEditor {

  private DatabaseDto database;
  private List<ExecStatement> history;
  private List<Connection> connections;
  private String databaseName;

  private String appName;
  private IApplication app;
  private ConnectionTestResult testResult;

  private final ConnectionTestWrapper connectionTest;
  private DatabaseMonitor liveStats;
  private Databases databases;
  private Property activeProperty;

  public DatabaseDetailBean() {
    connectionTest = new ConnectionTestWrapper();
  }

  public void setApp(String appName) {
    this.appName = appName;
  }

  public String getApp() {
    return appName;
  }

  public String getName() {
    return databaseName;
  }

  public void setName(String databaseName) {
    this.databaseName = databaseName;
  }

  public void onload() {
    app = IApplicationRepository.instance().findByName(appName).orElse(null);
    if (app == null) {
      ResponseHelper.notFound("Application '" + appName + "' not found");
      return;
    }

    databases = Databases.of(app);
    reloadExternalDb();
    liveStats = new DatabaseMonitor(app.getName(), databaseName);
  }

  public String getViewUrl() {
    return database.getViewUrl(appName);
  }

  private void reloadExternalDb() {
    var db = databases.find(databaseName);
    if (db == null) {
      ResponseHelper.notFound("Database '" + databaseName + "' not found");
      return;
    }

    database = new DatabaseDto(db);
    var externalDb = IExternalDatabaseManager.instance()
        .getExternalDatabaseApplicationContext(app)
        .getExternalDatabase(db.name());
    history = externalDb.getExecutionHistory().stream()
        .map(ExecStatement::new)
        .collect(Collectors.toList());
    connections = externalDb.getConnections().stream()
        .map(Connection::new)
        .collect(Collectors.toList());
  }

  public DatabaseDto getDatabase() {
    return database;
  }

  @Override
  public List<Property> getProperties() {
    return database.getProperties();
  }

  @Override
  public void saveProperty(boolean isNewProperty) {
    if (!isNewProperty || !isExistingProperty()) {
      saveDatabase(dbBuilder().property(getProperty().getName(), getProperty().getValue()));
    }
    reloadExternalDb();
  }

  public void removeProperty(String name) {
    databases.remove(database.getName() + "." + "Properties" + "." + name);
    reloadExternalDb();
  }

  public List<Connection> getConnections() {
    return connections;
  }

  public List<ExecStatement> getExecutionHistory() {
    return history;
  }

  public List<String> completeDriver(String value) {
    return JdbcDriver.all().stream()
        .filter(JdbcDriver::isInstalled)
        .map(JdbcDriver::getDriverName)
        .filter(name -> !Strings.CS.startsWith(name, SystemDatabaseBean.HSQL_DB))
        .filter(name -> Strings.CS.startsWith(name, value))
        .distinct()
        .collect(Collectors.toList());
  }

  @Override
  public String getTitle() {
    return "Database '" + databaseName + "'";
  }

  @Override
  public String getGuideText() {
    return "To edit your Database overwrite your app.yaml file. For example copy and paste the snippet below.";
  }

  @SuppressWarnings("deprecation")
  @Override
  public String getYaml() {
    var valuesMap = new HashMap<String, String>();
    valuesMap.put("name", database.getName());
    valuesMap.put("url", database.getUrl());
    valuesMap.put("driver", database.getDriver());
    valuesMap.put("username", database.getUserName());
    valuesMap.put("maxConnections", String.valueOf(database.getMaxConnections()));
    valuesMap.put("properties", parsePropertiesToYaml(getProperties()));
    String templateString = readTemplateString("database.yaml");
    var strSubstitutor = new org.apache.commons.lang3.text.StrSubstitutor(valuesMap);
    return strSubstitutor.replace(templateString);
  }

  @Override
  public String getHelpUrl() {
    return UrlUtil.getCockpitEngineGuideUrl() + "services.html#external-database-detail";
  }

  public void testDbConnection() {
    testResult = (ConnectionTestResult) connectionTest.test(this::testConnection);
  }

  private ConnectionTestResult testConnection() {
    var dbConfig = prepareDatabaseConnection();
    return databases.getInDeclaringPmvContext(databaseName, () -> testConnection(dbConfig));
  }

  private ConnectionTestResult testConnection(DatabaseConnectionConfiguration dbConfig) {
    try (var connection = DatabaseUtil.openConnection(dbConfig)) {
      var metaData = connection.getMetaData();
      var productName = metaData.getDatabaseProductName();
      var productVersion = metaData.getDatabaseProductVersion();
      var jdbcVersion = metaData.getJDBCMajorVersion();
      return new ConnectionTestResult("", 0, TestResult.SUCCESS,
          "Successfully connected to database: " + productName
              + " (" + productVersion + ") with JDBC Version " + jdbcVersion);
    } catch (Exception ex) {
      return new ConnectionTestResult("", 0, TestResult.ERROR,
          "An error occurred: " + ExceptionUtils.getStackTrace(ex));
    }
  }

  private DatabaseConnectionConfiguration prepareDatabaseConnection() {
    var dbConfig = databases.find(databaseName).config();
    dbConfig.setConnectionUrl(database.getUrl());
    dbConfig.setDriverName(database.getDriver());
    dbConfig.setUserName(database.getUserName());
    if (StringUtils.isNotBlank(database.getPassword())) {
      dbConfig.setPassword(database.getPassword());
    }
    return dbConfig;
  }

  public void saveDbConfig() {
    connectionTest.stop();
    var dbBuilder = dbBuilder()
        .url(database.getUrl())
        .driver(database.getDriver())
        .user(database.getUserName())
        .maxConnections(database.getMaxConnections());
    if (database.passwordChanged()) {
      dbBuilder.password(database.getPassword());
    }
    saveDatabase(dbBuilder);
    FacesContext.getCurrentInstance().addMessage("databaseConfigMsg",
        new FacesMessage("Database configuration saved", ""));
    reloadExternalDb();
  }

  public void resetDbConfig() {
    connectionTest.stop();
    databases.remove(databaseName);
    FacesContext.getCurrentInstance().addMessage("databaseConfigMsg",
        new FacesMessage("Database configuration reset", ""));
    reloadExternalDb();
  }

  @Override
  public ConnectionTestResult getResult() {
    return testResult;
  }

  public void reset() {
    testResult = null;
  }

  public DatabaseMonitor getLiveStats() {
    return liveStats;
  }

  private Builder dbBuilder() {
    return databases.find(databaseName).toBuilder();
  }

  private void saveDatabase(Builder dbBuilder) {
    databases.set(dbBuilder.toDatabase());
  }

  @Override
  public Property getProperty() {
    return activeProperty;
  }

  @Override
  public void setProperty(String key) {
    this.activeProperty = findProperty(key);
  }

}
