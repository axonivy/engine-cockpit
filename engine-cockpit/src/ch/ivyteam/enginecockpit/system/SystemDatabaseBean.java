package ch.ivyteam.enginecockpit.system;

import java.util.Collection;
import java.util.Collections;
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
import ch.ivyteam.db.jdbc.SystemDatabaseConfig;
import ch.ivyteam.enginecockpit.setupwizard.WizardBean.StepStatus;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.persistence.db.connection.ConnectionTestResult;
import ch.ivyteam.ivy.persistence.db.connection.ConnectionTester;
import ch.ivyteam.ivy.server.configuration.system.db.SystemDatabaseConverter;
import ch.ivyteam.ivy.server.configuration.system.db.SystemDatabaseCreator;
import ch.ivyteam.ivy.server.configuration.system.db.SystemDatabaseSetup;
import ch.ivyteam.ivy.server.restricted.EngineMode;
import ch.ivyteam.ivy.server.restricted.MaintenanceReason;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SystemDatabaseBean extends StepStatus
{
  private DatabaseProduct product;
  private JdbcDriver driver;
  private List<SystemDbConnectionProperty> connectionProperties;
  private List<SystemDbCreationParameter> creationParameters;
  private ConnectionInfo connectionInfo;
  private Properties additionalProps;
  private String propKey;
  private String propValue;
  private SystemDatabaseConverter converter;
  private SystemDatabaseCreator creator;
  
  private final ConnectionTestWrapper connectionTest;
  
  public SystemDatabaseBean()
  {
    DatabaseConnectionConfiguration config = SystemDatabaseSetup.getConfiguredSystemDatabaseConfig().getDbConnectionConfig();
    this.driver = JdbcDriver.forConnectionConfiguration(config).orElseThrow();
    this.product = driver.getDatabaseProduct();
    this.connectionProperties = getConnectionPropertiesList(config);
    this.creationParameters = Collections.emptyList();
    this.additionalProps = config.getProperties();
    this.connectionInfo = new ConnectionInfo();
    this.connectionTest = new ConnectionTestWrapper(new ConnectionInfo());
  }

  private Set<DatabaseProduct> getSupportedDatabases()
  {
    return SystemDatabaseSetup.getSupportedDatabases();
  }
  
  private List<JdbcDriver> getSupportedDrivers()
  {
    return SystemDatabaseSetup.getSupportedDrivers(product);
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
  
  public String getUrl()
  {
    return IConfiguration.get().getOrDefault("SystemDb.Url");
  }

  public void setDriver(String driver)
  {
    this.driver = getSupportedDrivers().stream().filter(d -> StringUtils.equals(d.getName(), driver)).findFirst().orElseThrow();
    this.connectionProperties = mergeConnectionProperties(connectionProperties, getConnectionPropertiesList());
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
    configChanged();
  }

  public ConnectionInfo getConnectionInfo()
  {
    return connectionInfo;
  }
  
  public void configChanged()
  {
    connectionTest.stop();
    this.connectionInfo = new ConnectionInfo();
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
    connectionInfo = (ConnectionInfo) connectionTest.test(() -> testSystemDbConnection());
  }
  
  private ConnectionInfo testSystemDbConnection()
  {
    ConnectionTestResult testConnection = ConnectionTester.testConnection(createConfiguration());
    ConnectionInfo connection = new ConnectionInfo(testConnection);
    if (connection.hasError())
    {
      Ivy.log().error("System Database connection test has an error:", connectionInfo.getError());
    }
    return connection;
  }
  
  public void saveConfiguration()
  {
    DatabaseConnectionConfiguration dbConnectionConfig = createConfiguration();

    SystemDatabaseConfig newSystemDbConfig = SystemDatabaseConfig.create()
            .dbConnectionConfig(dbConnectionConfig)
            .toSystemDatabaseConfig();  
    
    SystemDatabaseSetup.saveSystemDatabaseConfig(newSystemDbConfig);
    FacesContext.getCurrentInstance().addMessage("systemDbSave",
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "System Database config saved successfully"));
  }
  
  public void initCreator()
  {
    creationParameters = new SystemDatabaseSetup(createConfiguration())
            .getDatabaseCreationParameters().entrySet().stream().map(e -> new SystemDbCreationParameter(e.getKey(), e.getValue()))
            .collect(Collectors.toList());
    creator = null;
  }
  
  public void createDatabase()
  {
    saveConfiguration();
    creator = new SystemDatabaseSetup(createConfiguration())
            .createCreator(creationParameters.stream().collect(Collectors.toMap(p -> p.getParam(), p -> p.getValue())));
    creator.executeAsync();
  }
  
  public boolean isDbCreatorRunning()
  {
    if (creator == null)
    {
      return false;
    }
    return creator.isRunning();
  }
  
  public boolean isDbCreatorFinished()
  {
    return creator != null && 
            !creator.isRunning() && 
            StringUtils.equals(creator.getProgressText(), "Finished") &&
            getDbCreatorError().isBlank();
  }
  
  public String getDbCreatorError()
  {
    if (creator != null && creator.getError() != null)
    {
      return creator.getError().getMessage();
    }
    return "";
  }
  
  public void createConverter()
  {
    converter = new SystemDatabaseSetup(createConfiguration())
            .createConverter();
  }
  
  public void convertDatabase()
  {
    saveConfiguration();
    converter.executeAsync();
  }
  
  public boolean isDbConversionRunning()
  {
    return converter != null && converter.isRunning();
  }
  
  public boolean isDbConversionFinished()
  {
    return converter != null && 
            !converter.isRunning() && 
            StringUtils.equals(converter.getProgressText(), "Finished") &&
            getDbConversionError().isBlank();
  }
  
  public String getDbConversionError()
  {
    if (converter != null && converter.getError() != null)
    {
      return converter.getError().getMessage();
    }
    return "";
  }
  
  public Collection<SystemDbConnectionProperty> getConnectionProperties()
  {
    return connectionProperties;
  }
  
  public List<SystemDbCreationParameter> getCreationParams()
  {
    return creationParameters;
  }

  private List<SystemDbConnectionProperty> getConnectionPropertiesList(DatabaseConnectionConfiguration config)
  {
    var connectionPropertiesWithCorrectOrder = getConnectionPropertiesList();
    var connectionPropertiesWithValues = driver.getConnectionConfigurator().getDatabaseConnectionProperties(config);
    for (var connectionProperty : connectionPropertiesWithCorrectOrder)
    {
      var value = connectionPropertiesWithValues.get(connectionProperty.getProperty());
      connectionProperty.setValue(value);
    }
    return connectionPropertiesWithCorrectOrder;
  }

  private List<SystemDbConnectionProperty> getConnectionPropertiesList()
  {
    return driver.getConnectionConfigurator().getDatabaseConnectionProperties().stream()
            .map(p -> new SystemDbConnectionProperty(p, driver.getConnectionConfigurator().getDefaultValue(p)))
            .collect(Collectors.toList());
  }
  
  private static List<SystemDbConnectionProperty> mergeConnectionProperties(
          List<SystemDbConnectionProperty> oldProps,
          List<SystemDbConnectionProperty> newProps)
  {
    oldProps.forEach(old -> {
      newProps.forEach(p -> {
        if (p.getProperty().equals(old.getProperty()) && !old.getValue().isBlank() && !wasDefaultPort(old))
        {
          p.setValue(old.getValue());
        }
      });
    });
    return newProps;
  }
  
  private static boolean wasDefaultPort(SystemDbConnectionProperty oldPort)
  {
    return StringUtils.equals(oldPort.getProperty().getLabel(), "Port") && oldPort.isDefaultValue();
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
  
  @Override
  public boolean isStepOk()
  {
    return getConnectionInfo().isSuccessful();
  }
  
  @Override
  public String getStepWarningMessage()
  {
    return isStepOk() ? getProblemMessage() : getConnectionInfo().getAdvise();
  }
  
}
