package ch.ivyteam.enginecockpit.configwizard;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.db.jdbc.ConnectionProperty;
import ch.ivyteam.db.jdbc.DatabaseConnectionConfiguration;
import ch.ivyteam.db.jdbc.DatabaseProduct;
import ch.ivyteam.db.jdbc.JdbcDriver;
import ch.ivyteam.ivy.persistence.db.DatabasePersistencyServiceFactory;
import ch.ivyteam.ivy.server.configuration.Configuration;

@SuppressWarnings("restriction")
public class SystemDatabaseHelper
{
  public DatabaseProduct product;
  public JdbcDriver driver;
  public String host;
  public String port;
  public boolean isDefaultPort;
  public String dbName;
  private String password;
  private String userName;
  
  public SystemDatabaseHelper()
  {
    Configuration systemDbConfig = Configuration.loadOrCreateConfiguration();
    DatabaseConnectionConfiguration config = systemDbConfig.getSystemDatabaseConnectionConfiguration();
    this.driver = JdbcDriver.forConnectionConfiguration(config).orElseThrow();
    this.product = driver.getDatabaseProduct();
    Map<ConnectionProperty, String> properties = driver.getConnectionConfigurator().getDatabaseConnectionProperties(config);
    this.host = properties.get(ConnectionProperty.HOST);
    this.port = defaultString(properties.get(ConnectionProperty.PORT));
    isDefaultPort = getDefaultPort().equals(port);
    this.dbName = findDatabaseName(properties);
    this.password = config.getPassword();
    this.userName = config.getUserName();
    //TODO: additional properties
  }
  
  public Set<DatabaseProduct> getSupportedDatabases()
  {
    return DatabasePersistencyServiceFactory.getSupportedDatabases();
  }
  
  public List<JdbcDriver> getSupportedDrivers()
  {
    return DatabasePersistencyServiceFactory.getSupportedJdbcDrivers().stream()
            .filter(JdbcDriver::isInstalled)
            .filter(d -> d.getDatabaseProduct() == product)
            .collect(Collectors.toList());
  }
  
  public DatabaseProduct getProduct()
  {
    return product;
  }
  
  public void setProduct(DatabaseProduct product)
  {
    this.product = product;
  }
  
  public JdbcDriver getDriver()
  {
    return driver;
  }

  public void setDriver(JdbcDriver driver)
  {
    this.driver = driver;
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

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
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
  
  public void testSystemDbConnection()
  {
//    SystemDatabase.initialize(systemDbConfig);
//    SystemDatabase.getSystemDatabase().getConnectionTester().;
  }
}
