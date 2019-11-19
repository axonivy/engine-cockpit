package ch.ivyteam.enginecockpit.setupwizard;

import static org.apache.commons.lang3.StringUtils.defaultString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.db.jdbc.ConnectionConfigurator;
import ch.ivyteam.db.jdbc.ConnectionProperty;
import ch.ivyteam.db.jdbc.DatabaseConnectionConfiguration;
import ch.ivyteam.db.jdbc.DatabaseProduct;
import ch.ivyteam.db.jdbc.JdbcDriver;
import ch.ivyteam.ivy.persistence.db.DatabasePersistencyServiceFactory;
import ch.ivyteam.ivy.server.configuration.Configuration;
import ch.ivyteam.ivy.server.configuration.system.db.ConnectionState;
import ch.ivyteam.ivy.server.configuration.system.db.IConnectionListener;
import ch.ivyteam.ivy.server.configuration.system.db.SystemDatabase;
import ch.ivyteam.ivy.server.configuration.system.db.SystemDatabaseConnectionTester;
import ch.ivyteam.util.WaitUtil;

@SuppressWarnings("restriction")
public class SystemDatabaseHelper
{
  private static final int CONNETION_TEST_TIMEOUT = 15;
  
  public DatabaseProduct product;
  public JdbcDriver driver;
  public String host;
  public String port;
  public boolean isDefaultPort;
  public String dbName;
  private String password;
  private String userName;
  private ConnectionInfo connectionInfo;
  private Configuration systemDbConfig;
  private Properties additionalProps;
  private SystemDatabaseConnectionTester tester;
  private BlockingListener connectionListener;
  
  public SystemDatabaseHelper()
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
    this.password = config.getPassword();
    this.userName = config.getUserName();
    this.additionalProps = config.getProperties();
    //TODO: additional properties
    
    connectionListener = new BlockingListener();
    this.tester = getSystemDb().getConnectionTester();
    this.tester.addConnectionListener(connectionListener);
    this.connectionInfo = ConnectionInfo.create();
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
  
  public void configChanged()
  {
    
  }
  
  public ConnectionInfo getConnectionInfo()
  {
    return connectionInfo;
  }
  
  public ConnectionState testConnection()
  {
    updateDbConfig();
    
    tester.reset();
    connectionListener.gotResult = false;
    tester.configurationChanged();
    try
    {
      WaitUtil.await(() -> connectionListener.gotResult, CONNETION_TEST_TIMEOUT, TimeUnit.SECONDS);
    }
    catch (TimeoutException ex)
    {
      String msg = "Could not connect to database within " + CONNETION_TEST_TIMEOUT + " Seconds";
      getConnectionInfo().updateConnectionStates(ConnectionState.CONNECTION_FAILED,
              new TimeoutException(msg),
              getDbProduct());
      return ConnectionState.CONNECTION_FAILED;
    }
    return tester.getConnectionState();
  }
  
  public SystemDatabase getSystemDb()
  {
    SystemDatabase.initialize(systemDbConfig);
    return SystemDatabase.getSystemDatabase();
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

    DatabaseConnectionConfiguration tempConfig = configurator.getDatabaseConnectionConfiguration(dbProps);
    DatabaseConnectionConfiguration currentConfig = systemDbConfig.getSystemDatabaseConnectionConfiguration();
    currentConfig.setConnectionUrl(tempConfig.getConnectionUrl());
    currentConfig.setDriverName(tempConfig.getDriverName());

    String configDataPw = defaultString(getPassword());
    
    String currentConfigPw = defaultString(currentConfig.getPassword());
    String fakedPassword = "*".repeat(currentConfigPw.length()); 
    
    if (!StringUtils.equals(configDataPw, fakedPassword))
    {
      currentConfig.setPassword(configDataPw);
    }
    currentConfig.setUserName(getUserName());
    currentConfig.setProperties(getAdditionalProperties());
    return currentConfig;
  }
  
  private class BlockingListener implements IConnectionListener
  {
    private boolean gotResult = false;

    @Override
    public void connectionStateChanged(ConnectionState newState)
    {
      Throwable connectionError = getSystemDb().getConnectionTester().getConnectionError();
      getConnectionInfo().updateConnectionStates(newState, connectionError, getDbProduct());
      if (newState != ConnectionState.CONNECTING)
      {
        gotResult = true;
      }
    }
  }
  
}
