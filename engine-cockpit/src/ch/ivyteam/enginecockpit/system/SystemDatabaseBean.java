package ch.ivyteam.enginecockpit.system;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.db.jdbc.ConnectionConfigurator;
import ch.ivyteam.db.jdbc.ConnectionProperty;
import ch.ivyteam.db.jdbc.DatabaseConnectionConfiguration;
import ch.ivyteam.db.jdbc.DatabaseProduct;
import ch.ivyteam.db.jdbc.JdbcDriver;
import ch.ivyteam.enginecockpit.setupwizard.ConnectionInfo;
import ch.ivyteam.ivy.persistence.db.DatabasePersistencyServiceFactory;
import ch.ivyteam.ivy.persistence.db.connection.ConnectionTestResult;
import ch.ivyteam.ivy.persistence.db.connection.ConnectionTester;
import ch.ivyteam.ivy.server.configuration.Configuration;
import ch.ivyteam.ivy.server.restricted.EngineMode;
import ch.ivyteam.ivy.server.restricted.MaintenanceReason;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SystemDatabaseBean
{
  public DatabaseProduct product;
  public JdbcDriver driver;
  public String host;
  public String port;
  public boolean isDefaultPort;
  public String dbName;
  private String password;
  private String realPassword;
  private String userName;
  private ConnectionInfo connectionInfo;
  private Configuration systemDbConfig;
  private Properties additionalProps;
  private String propKey;
  private String propValue;
  
  public SystemDatabaseBean()
  {
    systemDbConfig = Configuration.loadOrCreateConfiguration();
    DatabaseConnectionConfiguration config = systemDbConfig.getSystemDatabaseConnectionConfiguration();
    this.driver = JdbcDriver.forConnectionConfiguration(config).orElseThrow();
    this.product = driver.getDatabaseProduct();
    Map<ConnectionProperty, String> properties = driver.getConnectionConfigurator().getDatabaseConnectionProperties(config);
    this.host = properties.get(ConnectionProperty.HOST);
    this.port = defaultString(properties.get(ConnectionProperty.PORT));
    isDefaultPort = getDefaultPort().equals(port);
    this.dbName = findDatabaseName(properties);
    setRealPassword(config.getPassword());
    this.userName = config.getUserName();
    this.additionalProps = config.getProperties();
    this.connectionInfo = new ConnectionInfo();
  }
  
  private Set<DatabaseProduct> getSupportedDatabases()
  {
    return DatabasePersistencyServiceFactory.getSupportedDatabases();
  }
  
  private List<JdbcDriver> getSupportedDrivers()
  {
    return DatabasePersistencyServiceFactory.getSupportedJdbcDrivers().stream()
            .filter(JdbcDriver::isInstalled)
            .filter(d -> d.getDatabaseProduct() == product)
            .collect(Collectors.toList());
  }
  
  public List<String> getSupporedDatabaseNames()
  {
    return getSupportedDatabases().stream().map(DatabaseProduct::getName).collect(Collectors.toList());
  }
  
  public List<String> getSupportedDriverNames()
  {
    return getSupportedDrivers().stream().map(JdbcDriver::getName).collect(Collectors.toList());
  }
  
  public DatabaseProduct getDbProduct()
  {
    return product;
  }
  
  public String getProduct()
  {
    return product.getName();
  }
  
  public void setProduct(String product)
  {
    this.product = getSupportedDatabases().stream().filter(p -> StringUtils.equals(p.getName(), product)).findFirst().orElseThrow();
  }
  
  public String getDriver()
  {
    return driver.getName();
  }

  public void setDriver(String driver)
  {
    this.driver = getSupportedDrivers().stream().filter(d -> StringUtils.equals(d.getName(), driver)).findFirst().orElseThrow();
  }

  public String getHost()
  {
    return host;
  }

  public void setHost(String host)
  {
    this.host = host;
  }

  public String getPort()
  {
    return port;
  }

  public void setPort(String port)
  {
    this.port = port;
  }
  
  public boolean isDefaultPort()
  {
    return isDefaultPort;
  }
  
  public void setDefaultPort(boolean defaultPort)
  {
    this.isDefaultPort = defaultPort;
    this.port = defaultPort ? getDefaultPort() : port;
  }

  public String getDbName()
  {
    return dbName;
  }

  public void setDbName(String dbName)
  {
    this.dbName = dbName;
  }

  public String getPassword()
  {
    return password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  //Use for <p:password redisplay="true"> without leak the real password in the DOM
  private void setRealPassword(String realPassword)
  {
    this.password = "*".repeat(realPassword.length()); 
    this.realPassword = realPassword;
  }
  
  private String getRealPassword()
  {
    if (!StringUtils.equals(password, "*".repeat(realPassword.length())))
    {
      return password;
    }
    return realPassword;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }
  
  public Properties getAdditionalProperties()
  {
    return additionalProps;
  }
  
  public String getPropKey()
  {
    return propKey;
  }

  public void setPropKey(String propKey)
  {
    this.propKey = propKey;
  }

  public String getPropValue()
  {
    return propValue;
  }

  public void setPropValue(String propValue)
  {
    this.propValue = propValue;
  }

  public void addProp()
  {
    propKey = "";
    propValue = "";
  }
  
  public void removeProp(String key)
  {
    additionalProps.remove(key);
  }
  
  public void saveProp()
  {
    additionalProps.put(propKey, propValue);
  }

  private static String findDatabaseName(Map<ConnectionProperty, String> properties)
  {
    String dbName = properties.get(ConnectionProperty.DB_NAME);
    if (StringUtils.isNotBlank(dbName))
    {
      return dbName;
    }
    dbName = properties.get(ConnectionProperty.ORACLE_SERVICE_ID);
    if (StringUtils.isNotBlank(dbName))
    {
      return dbName;
    }
    return "";
  }
  
  public String getDefaultPort()
  {
    return driver.getConnectionConfigurator().getDefaultValue(ConnectionProperty.PORT);
  }
  
  public ConnectionInfo getConnectionInfo()
  {
    return connectionInfo;
  }
  
  public String getHelpPath()
  {
    return "installation/systemdatabase.html";
  }
  
  public boolean isHasProblem()
  {
    return EngineMode.is(EngineMode.MAINTENANCE) && 
           MaintenanceReason.isSystemDatabaseReason();
  }
  
  public String getProblemMessage()
  {
    return MaintenanceReason.getMessage();
  }
  
  public void testConnection()
  {
    ConnectionTestResult testConnection = ConnectionTester.testConnection(createConfiguration());
    connectionInfo = new ConnectionInfo(testConnection);
  }
  
  public void updateDbConfig()
  {
    DatabaseConnectionConfiguration dbConfig = createConfiguration();
    systemDbConfig.setSystemDatabaseConnectionConfiguration(dbConfig);
  }
  
  public DatabaseConnectionConfiguration createConfiguration()
  {
    ConnectionConfigurator configurator = driver.getConnectionConfigurator();
    Map<ConnectionProperty, String> dbProps = new HashMap<>();
    
    dbProps.put(ConnectionProperty.HOST, getHost());
    dbProps.put(ConnectionProperty.PORT, getPort());
    dbProps.put(ConnectionProperty.DB_NAME, getDbName());
    dbProps.put(ConnectionProperty.ORACLE_SERVICE_ID, getDbName());
    
    //configurator.getDatabaseConnectionProperties()

    DatabaseConnectionConfiguration tempConfig = configurator.getDatabaseConnectionConfiguration(dbProps);
    DatabaseConnectionConfiguration currentConfig = systemDbConfig.getSystemDatabaseConnectionConfiguration();
    currentConfig.setConnectionUrl(tempConfig.getConnectionUrl());
    currentConfig.setDriverName(tempConfig.getDriverName());
    currentConfig.setPassword(getRealPassword());
    currentConfig.setUserName(getUserName());
    currentConfig.setProperties(getAdditionalProperties());
    return currentConfig;
  }
  
}
