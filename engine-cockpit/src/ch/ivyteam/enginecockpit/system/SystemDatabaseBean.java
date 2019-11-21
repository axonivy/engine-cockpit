package ch.ivyteam.enginecockpit.system;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

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
  private DatabaseProduct product;
  private JdbcDriver driver;
  private List<SystemDbConnectionProperty> connectionProperties;
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
    this.connectionProperties = getConnectionPropertiesList(config);
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
    setDriver(getSupportedDriverNames().get(0));
  }
  
  public String getDriver()
  {
    return driver.getName();
  }

  public void setDriver(String driver)
  {
    this.driver = getSupportedDrivers().stream().filter(d -> StringUtils.equals(d.getName(), driver)).findFirst().orElseThrow();
    this.connectionProperties = mergeConnectionProperies(connectionProperties, getConnectionPropertiesList());
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
  
  public void saveConfiguration()
  {
    updateDbConfig();
  }
  
  public Collection<SystemDbConnectionProperty> getConnectionProperties()
  {
    return connectionProperties;
  }
  
  
  private List<SystemDbConnectionProperty> getConnectionPropertiesList(DatabaseConnectionConfiguration config)
  {
    return driver.getConnectionConfigurator().getDatabaseConnectionProperties(config).entrySet().stream()
            .map(e -> new SystemDbConnectionProperty(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
  }
  
  private List<SystemDbConnectionProperty> getConnectionPropertiesList()
  {
    return driver.getConnectionConfigurator().getDatabaseConnectionProperties().stream()
            .map(p -> new SystemDbConnectionProperty(p, driver.getConnectionConfigurator().getDefaultValue(p)))
            .collect(Collectors.toList());
  }
  
  private static List<SystemDbConnectionProperty> mergeConnectionProperies(
          List<SystemDbConnectionProperty> oldProps,
          List<SystemDbConnectionProperty> newProps)
  {
    oldProps.forEach(old -> {
      newProps.forEach(p -> {
        if (p.getProperty().equals(old.getProperty()))
        {
          p.setValue(old.getValue());
        }
      });
    });
    return newProps;
  }
  
  private void updateDbConfig()
  {
    DatabaseConnectionConfiguration dbConfig = createConfiguration();
    systemDbConfig.setSystemDatabaseConnectionConfiguration(dbConfig);
    try
    {
      systemDbConfig.saveConfiguration(true);
      FacesContext.getCurrentInstance().addMessage("systemDbSave",
              new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "System Database config saved successfully"));
    }
    catch (IOException ex)
    {
      FacesContext.getCurrentInstance().addMessage("systemDbSave",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while save system database", ex.getMessage()));
    }
  }
  
  private DatabaseConnectionConfiguration createConfiguration()
  {
    ConnectionConfigurator configurator = driver.getConnectionConfigurator();
    Map<ConnectionProperty, String> props = connectionProperties.stream()
            .collect(Collectors.toMap(p -> p.getProperty(), p -> p.getValue()));
    DatabaseConnectionConfiguration config = configurator.getDatabaseConnectionConfiguration(props);
    config.setProperties(getAdditionalProperties());
    return config;
  }
  
}
