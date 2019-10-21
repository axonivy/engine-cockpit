package ch.ivyteam.enginecockpit.services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.db.jdbc.JdbcDriver;
import ch.ivyteam.di.restricted.DiCore;
import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.ExternalDatabase;
import ch.ivyteam.enginecockpit.model.ExternalDatabase.Connection;
import ch.ivyteam.enginecockpit.model.ExternalDatabase.ExecStatement;
import ch.ivyteam.enginecockpit.services.ConnectionTestResult.IConnectionTestResult;
import ch.ivyteam.enginecockpit.services.ConnectionTestResult.TestResult;
import ch.ivyteam.enginecockpit.util.UrlUtil;
import ch.ivyteam.ivy.application.IApplicationInternal;
import ch.ivyteam.ivy.db.IExternalDatabase;
import ch.ivyteam.ivy.db.internal.ExternalDatabaseManager;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class ExternalDatabaseDetailBean extends HelpServices implements IConnectionTestResult
{
  private ExternalDatabase externalDatabase;
  private List<ExecStatement> history;
  private List<Connection> connections;
  private String databaseName;
  
  private ManagerBean managerBean;
  private String dbConfigKey;
  private ConnectionTestResult testResult;
  
  public ExternalDatabaseDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    configuration = ((IApplicationInternal) managerBean.getSelectedIApplication()).getConfiguration();
  }
  
  public String getDatabaseName()
  {
    return databaseName;
  }
  
  public void setDatabaseName(String databaseName)
  {
    this.databaseName = databaseName;
    reloadExternalDb();
  }

  private void reloadExternalDb()
  {
    externalDatabase = new ExternalDatabase(managerBean.getSelectedIEnvironment().findExternalDatabaseConfiguration(databaseName));
    dbConfigKey = "Databases." + databaseName;
    ExternalDatabaseManager dbManager = DiCore.getGlobalInjector().getInstance(ExternalDatabaseManager.class);
    IExternalDatabase externalDb = dbManager.getExternalDatabase(managerBean.getSelectedIEnvironment().findExternalDatabaseConfiguration(databaseName));
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
            .filter(name -> !StringUtils.startsWith(name, "org.hsqldb"))
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
    Map<String, String> valuesMap = new HashMap<>();
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
    return UrlUtil.getCockpitEngineGuideUrl() + "#external-database-detail";
  }
  
  public void testDbConnection()
  {
    ExternalDatabaseManager dbManager = DiCore.getGlobalInjector().getInstance(ExternalDatabaseManager.class);
    IExternalDatabase iExternalDatabase = dbManager.getExternalDatabase(managerBean.getSelectedIEnvironment().findExternalDatabaseConfiguration(databaseName));
    try
    {
      if (iExternalDatabase.getConnection().isValid(10))
      {
        testResult = new ConnectionTestResult("", 0, TestResult.SUCCESS, "Successful connected to database");
      }
      else
      {
        testResult = new ConnectionTestResult("", 0, TestResult.ERROR, "Could not connect to database");
      }
    }
    catch (NullPointerException | SQLException ex)
    {
      testResult = new ConnectionTestResult("", 0, TestResult.ERROR, "An error occurred: " + ExceptionUtils.getStackTrace(ex));
    }
  }
  
  public void saveDbConfig()
  {
    ExternalDatabase originConfig = new ExternalDatabase(managerBean.getSelectedIEnvironment().findExternalDatabaseConfiguration(databaseName));
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

}
