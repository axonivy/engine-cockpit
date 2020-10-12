package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.application.IExternalDatabaseConfiguration;
import ch.ivyteam.ivy.db.IExternalDatabaseRuntimeConnection;
import ch.ivyteam.ivy.db.IStatementExecution;
import ch.ivyteam.util.Property;

public class ExternalDatabase implements IService
{
  private String name;
  private String url;
  private String driver;
  private String userName;
  private String password;
  private int maxConnections;
  private List<Property> properties;
  private boolean passwordChanged;
  
  public ExternalDatabase(IExternalDatabaseConfiguration externalDatabase)
  {
    name = externalDatabase.getUserFriendlyName();
    url = externalDatabase.getDatabaseConnectionConfiguration().getConnectionUrl();
    driver = externalDatabase.getDatabaseConnectionConfiguration().getDriverName();
    userName = externalDatabase.getDatabaseConnectionConfiguration().getUserName();
    password = externalDatabase.getDatabaseConnectionConfiguration().getPassword();
    maxConnections = externalDatabase.getMaxConnections();
    properties = externalDatabase.getDatabaseConnectionConfiguration().getProperties().entrySet().stream()
            .map(e -> new Property(e.getKey().toString(), e.getValue().toString()))
            .collect(Collectors.toList());
    passwordChanged = false;
  }

  public String getName()
  {
    return name;
  }

  public String getUrl()
  {
    return url;
  }
  
  public void setUrl(String url)
  {
    this.url = url;
  }
  
  public String getDriver()
  {
    return driver;
  }
  
  public void setDriver(String driver)
  {
    this.driver = driver;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }
  
  @Override
  public String getPassword()
  {
    return password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
    if (StringUtils.isNotBlank(password))
    {
      passwordChanged = true;
    }
  }
  
  @Override
  public boolean passwordChanged()
  {
    return passwordChanged;
  }

  public int getMaxConnections()
  {
    return maxConnections;
  }
  
  public void setMaxConnections(int maxConnections)
  {
    this.maxConnections = maxConnections;
  }

  public List<Property> getProperties()
  {
    return properties;
  }
  
  public static class ExecStatement
  {
    private String time;
    private String execTime;
    private String resultTime;
    private String sql;
    private String element;

    public ExecStatement(IStatementExecution statement)
    {
      time = DateUtil.formatDate(statement.getExecutionTimestamp());
      execTime = (double) statement.getExecutionTimeInMicroSeconds() / 1000 + "ms";
      resultTime = (double) statement.getReadingResultTimeInMicroSeconds() / 1000 + "ms";
      sql = statement.getSql();
      element = statement.getDatabaseElement().getProcessElementId();
    }

    public String getTime()
    {
      return time;
    }

    public String getExecTime()
    {
      return execTime;
    }

    public String getResultTime()
    {
      return resultTime;
    }

    public String getSql()
    {
      return sql;
    }

    public String getElement()
    {
      return element;
    }
  }
  
  public static class Connection
  {
    private String lastUsed;
    private boolean inUse;

    public Connection(IExternalDatabaseRuntimeConnection conn)
    {
      lastUsed = DateUtil.formatDate(conn.getLastUsed());
      inUse = conn.isInUse();
    }

    public String getLastUsed()
    {
      return lastUsed;
    }

    public boolean isInUse()
    {
      return inUse;
    }
  }
  
}
