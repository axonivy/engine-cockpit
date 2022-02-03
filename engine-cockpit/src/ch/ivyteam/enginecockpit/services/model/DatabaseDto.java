package ch.ivyteam.enginecockpit.services.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.ivy.db.Database;
import ch.ivyteam.ivy.db.IExternalDatabaseRuntimeConnection;
import ch.ivyteam.ivy.db.IStatementExecution;

public class DatabaseDto implements IService {
  private String name;
  private String url;
  private String driver;
  private String userName;
  private String password;
  private int maxConnections;
  private Map<String, String> properties;
  private boolean passwordChanged;

  public DatabaseDto(Database db) {
    name = db.name();
    url = db.url();
    driver = db.driver();
    userName = db.user();
    password = db.password();
    maxConnections = db.maxConnections();
    properties = new HashMap<>(db.properties());
    passwordChanged = false;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @Override
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
    if (StringUtils.isNotBlank(password)) {
      passwordChanged = true;
    }
  }

  @Override
  public boolean passwordChanged() {
    return passwordChanged;
  }

  public int getMaxConnections() {
    return maxConnections;
  }

  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  public Map<String, String> getProperties() {
    return properties;
  }

  public static class ExecStatement {
    private String time;
    private String execTime;
    private String resultTime;
    private String sql;
    private String element;
    private long rowsAffected;

    public ExecStatement(IStatementExecution statement) {
      time = DateUtil.formatDate(statement.getExecutionTimestamp());
      execTime = (double) statement.getExecutionTimeInMicroSeconds() / 1000 + "ms";
      resultTime = (double) statement.getReadingResultTimeInMicroSeconds() / 1000 + "ms";
      sql = statement.getSql();
      element = statement.getDatabaseElement().getProcessElementId();
      rowsAffected = statement.getRowsAffected();
    }

    public String getTime() {
      return time;
    }

    public String getExecTime() {
      return execTime;
    }

    public String getResultTime() {
      return resultTime;
    }

    public String getSql() {
      return sql;
    }

    public String getElement() {
      return element;
    }

    public long getRowsAffected() {
      return rowsAffected;
    }
  }

  public static class Connection {
    private String lastUsed;
    private boolean inUse;

    public Connection(IExternalDatabaseRuntimeConnection conn) {
      lastUsed = DateUtil.formatDate(conn.getLastUsed());
      inUse = conn.isInUse();
    }

    public String getLastUsed() {
      return lastUsed;
    }

    public boolean isInUse() {
      return inUse;
    }
  }

}
