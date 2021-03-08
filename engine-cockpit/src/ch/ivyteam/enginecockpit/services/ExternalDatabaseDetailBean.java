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
import ch.ivyteam.enginecockpit.monitor.mbeans.ivy.ExternalDatabaseMonitor;
import ch.ivyteam.enginecockpit.services.help.HelpServices;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestResult.TestResult;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.services.model.ExternalDatabase;
import ch.ivyteam.enginecockpit.services.model.ExternalDatabase.Connection;
import ch.ivyteam.enginecockpit.services.model.ExternalDatabase.ExecStatement;
import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.enginecockpit.system.SystemDatabaseBean;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.db.IExternalDatabaseManager;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class ExternalDatabaseDetailBean extends HelpServices implements IConnectionTestResult
{
  private ExternalDatabase externalDatabase;
  private Property activeProperty;
  private List<ExecStatement> history;
  private List<Connection> connections;
  private String databaseName;
  
  private ManagerBean managerBean;
  private String dbConfigKey;
  private ConnectionTestResult testResult;
  
  private final ConnectionTestWrapper connectionTest;
  private ExternalDatabaseMonitor liveStats;
  
  public ExternalDatabaseDetailBean()
  {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    configuration = ((IApplicationInternal) managerBean.getSelectedIApplication()).getConfiguration();
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
      reloadExternalDb();
      liveStats = new ExternalDatabaseMonitor(managerBean.getSelectedApplicationName(), managerBean.getSelectedIApplication().getActiveEnvironment(), databaseName);
    }
  }

  private void reloadExternalDb()
  {
    externalDatabase = new ExternalDatabase(managerBean.getSelectedIEnvironment().findExternalDatabaseConfiguration(databaseName));
    dbConfigKey = "Databases." + databaseName;
    
    var externalDb = IExternalDatabaseManager.instance()
            .getExternalDatabase(managerBean.getSelectedIEnvironment().findExternalDatabaseConfiguration(databaseName));
    history = externalDb.getExecutionHistory().stream()
            .map(statement -> new ExecStatement(statement))
            .collect(Collectors.toList());
    connections = externalDb.getConnections().stream()
            .map(conn -> new Connection(conn))
            .collect(Collectors.toList());
  }
  
  public ExternalDatabase getExternalDatabase()
  {
    return externalDatabase;
  }
  
  public void setProperty(Property property)
  {
    if (property == null)
    {
      property = new Property();
    }
    this.activeProperty = property;
  }
  
  public Property getProperty()
  {
    return activeProperty;
  }
  
  public void saveProperty()
  {
    configuration.set(propertyConfigKey(activeProperty.getName()), activeProperty.getValue());
    reloadExternalDb();
  }
  
  public void removeProperty(Property property)
  {
    configuration.remove(propertyConfigKey(property.getName()));
    reloadExternalDb();
  }

  private String propertyConfigKey(String propertyName)
  {
    return dbConfigKey + ".Properties." + propertyName;
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
    return "External Database '" + databaseName + "'";
  }
  
  @Override
  public String getGuideText()
  {
    return "To edit your External Database overwrite your app.yaml file. For example copy and paste the snippet below.";
  }
  
  @Override
  public String getYaml()
  {
    var valuesMap = new HashMap<String, String>();
    valuesMap.put("name", externalDatabase.getName());
    valuesMap.put("url", externalDatabase.getUrl());
    valuesMap.put("driver", externalDatabase.getDriver());
    valuesMap.put("username", externalDatabase.getUserName());
    valuesMap.put("maxConnections", String.valueOf(externalDatabase.getMaxConnections()));
    valuesMap.put("properties", parsePropertiesToYaml(externalDatabase.getProperties()));
    String templateString = readTemplateString("externaldatabase.yaml");
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
    var dbConfig = managerBean.getSelectedIEnvironment().findExternalDatabaseConfiguration(databaseName).getDatabaseConnectionConfiguration();
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
    var originConfig = new ExternalDatabase(managerBean.getSelectedIEnvironment().findExternalDatabaseConfiguration(databaseName));
    setIfChanged(dbConfigKey + ".Url", externalDatabase.getUrl(), originConfig.getUrl());
    setIfChanged(dbConfigKey + ".Driver", externalDatabase.getDriver(), originConfig.getDriver());
    setIfChanged(dbConfigKey + ".UserName", externalDatabase.getUserName(), originConfig.getUserName());
    setIfPwChanged(dbConfigKey + ".Password", externalDatabase);
    setIfChanged(dbConfigKey + ".MaxConnections", externalDatabase.getMaxConnections(), originConfig.getMaxConnections());
    FacesContext.getCurrentInstance().addMessage("databaseConfigMsg", 
            new FacesMessage("Database configuration saved", ""));
    reloadExternalDb();
  }
  
  public void resetDbConfig()
  {
    connectionTest.stop();
    configuration.remove(dbConfigKey);
    FacesContext.getCurrentInstance().addMessage("databaseConfigMsg", 
            new FacesMessage("Database configuration reset", ""));
    reloadExternalDb();
  }

  @Override
  public ConnectionTestResult getResult()
  {
    return testResult;
  }
  
  public ExternalDatabaseMonitor getLiveStats()
  {
    return liveStats;
  }

}
