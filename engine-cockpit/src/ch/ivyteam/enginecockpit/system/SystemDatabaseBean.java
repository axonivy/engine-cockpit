package ch.ivyteam.enginecockpit.system;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.Strings;

import ch.ivyteam.db.jdbc.DatabaseConnectionConfiguration;
import ch.ivyteam.db.jdbc.DatabaseProduct;
import ch.ivyteam.db.jdbc.JdbcDriver;
import ch.ivyteam.db.jdbc.SystemDatabaseConfig;
import ch.ivyteam.enginecockpit.services.model.ConnectionTestWrapper;
import ch.ivyteam.enginecockpit.setup.WizardBean.StepStatus;
import ch.ivyteam.enginecockpit.system.model.ConnectionInfo;
import ch.ivyteam.enginecockpit.system.model.SystemDbConnectionProperty;
import ch.ivyteam.enginecockpit.system.model.SystemDbCreationParameter;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.persistence.db.connection.ConnectionTester;
import ch.ivyteam.ivy.persistence.db.init.SystemDatabaseCreator;
import ch.ivyteam.ivy.persistence.db.init.SystemDatabaseSetup;
import ch.ivyteam.ivy.server.restricted.EngineMode;
import ch.ivyteam.ivy.server.restricted.MaintenanceReason;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SystemDatabaseBean extends StepStatus {
  public static String HSQL_DB = "org.hsqldb";

  private DatabaseProduct product;
  private JdbcDriver driver;
  private List<SystemDbConnectionProperty> connectionProperties;
  private List<SystemDbCreationParameter> creationParameters;
  private ConnectionInfo connectionInfo;
  private final Properties additionalProps;
  private String propKey;
  private String propValue;
  private SystemDatabaseCreator creator;

  private final ConnectionTestWrapper connectionTest;

  public SystemDatabaseBean() {
    var config = SystemDatabaseSetup.getConfiguredSystemDatabaseConfig().getDbConnectionConfig();
    this.driver = JdbcDriver.forConnectionConfiguration(config).orElseThrow();
    this.product = driver.getDatabaseProduct();
    this.connectionProperties = getConnectionPropertiesList(config);
    this.creationParameters = Collections.emptyList();
    this.additionalProps = config.getProperties();
    this.connectionInfo = new ConnectionInfo();
    this.connectionTest = new ConnectionTestWrapper(new ConnectionInfo());
  }

  private Set<DatabaseProduct> getSupportedDatabases() {
    return SystemDatabaseSetup.getSupportedDatabases();
  }

  private List<JdbcDriver> getSupportedDrivers() {
    return SystemDatabaseSetup.getSupportedDrivers(product);
  }

  public List<String> getSupporedDatabaseNames() {
    return getSupportedDatabases().stream().map(DatabaseProduct::getName).collect(Collectors.toList());
  }

  public List<String> getSupportedDriverNames() {
    return getSupportedDrivers().stream().map(JdbcDriver::getName).collect(Collectors.toList());
  }

  public DatabaseProduct getDbProduct() {
    return product;
  }

  public String getProduct() {
    return product.getName();
  }

  public void setProduct(String product) {
    this.product = getSupportedDatabases().stream().filter(p -> Objects.equals(p.getName(), product))
        .findFirst().orElseThrow();
    setDriver(getSupportedDriverNames().get(0));
  }

  public String getDriver() {
    return driver.getName();
  }

  public String getUrl() {
    return IConfiguration.instance().getOrDefault("SystemDb.Url");
  }

  public void setDriver(String driver) {
    var newDriver = getSupportedDrivers().stream()
        .filter(d -> Objects.equals(d.getName(), driver))
        .findFirst()
        .orElseThrow();

    if (!newDriver.equals(this.driver)) {
      this.driver = newDriver;
      this.connectionProperties = mergeConnectionProperties(connectionProperties, getConnectionPropertiesList());
    }
  }

  public Properties getAdditionalProperties() {
    return additionalProps;
  }

  public String getPropKey() {
    return propKey;
  }

  public void setPropKey(String propKey) {
    this.propKey = propKey;
  }

  public String getPropValue() {
    return propValue;
  }

  public void setPropValue(String propValue) {
    this.propValue = propValue;
  }

  public void addProp() {
    propKey = "";
    propValue = "";
  }

  public void removeProp(String key) {
    additionalProps.remove(key);
    configChanged();
  }

  public void saveProp() {
    additionalProps.put(propKey, propValue);
    configChanged();
  }

  public ConnectionInfo getConnectionInfo() {
    return connectionInfo;
  }

  public void configChanged() {
    connectionTest.stop();
    this.connectionInfo = new ConnectionInfo();
  }

  public boolean isHasProblem() {
    return EngineMode.is(EngineMode.MAINTENANCE) &&
        MaintenanceReason.isSystemDatabaseReason();
  }

  public String getProblemMessage() {
    return MaintenanceReason.getMessage();
  }

  public void testConnection() {
    connectionInfo = (ConnectionInfo) connectionTest.test(this::testSystemDbConnection);
  }

  private ConnectionInfo testSystemDbConnection() {
    var testConnection = ConnectionTester.testConnection(createConfiguration());
    return new ConnectionInfo(testConnection);
  }

  public void saveConfiguration() {
    var dbConnectionConfig = createConfiguration();
    var newSystemDbConfig = SystemDatabaseConfig.create()
        .dbConnectionConfig(dbConnectionConfig)
        .toSystemDatabaseConfig();
    SystemDatabaseSetup.saveSystemDatabaseConfig(newSystemDbConfig);
    FacesContext.getCurrentInstance().addMessage("systemDbSave",
        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
            "System Database config saved successfully"));
  }

  public void saveConfigurationAndRestart() throws IOException {
    saveConfiguration();
    new RestartBean().restart();
  }

  public void initCreator() {
    creationParameters = new SystemDatabaseSetup(createConfiguration())
        .getDatabaseCreationParameters().entrySet().stream()
        .map(e -> new SystemDbCreationParameter(e.getKey(), e.getValue()))
        .collect(Collectors.toList());
    creator = null;
  }

  public void createDatabase() {
    saveConfiguration();
    creator = new SystemDatabaseSetup(createConfiguration())
        .createCreator(creationParameters.stream()
            .collect(Collectors.toMap(SystemDbCreationParameter::getParam, SystemDbCreationParameter::getValue)));
    creator.executeAsync();
  }

  public boolean isDbCreatorRunning() {
    if (creator == null) {
      return false;
    }
    return creator.isRunning();
  }

  public boolean isDbCreatorFinished() {
    return creator != null &&
        !creator.isRunning() &&
        Objects.equals(creator.getProgressText(), "Finished") &&
        getDbCreatorError().isBlank();
  }

  public String getDbCreatorError() {
    if (creator != null && creator.getError() != null) {
      return creator.getError().getMessage();
    }
    return "";
  }

  public Collection<SystemDbConnectionProperty> getConnectionProperties() {
    return connectionProperties;
  }

  public List<SystemDbCreationParameter> getCreationParams() {
    return creationParameters;
  }

  private List<SystemDbConnectionProperty> getConnectionPropertiesList(DatabaseConnectionConfiguration config) {
    var connectionPropertiesWithCorrectOrder = getConnectionPropertiesList();
    var connectionPropertiesWithValues = driver.getConnectionConfigurator()
        .getDatabaseConnectionProperties(config);
    for (var connectionProperty : connectionPropertiesWithCorrectOrder) {
      var value = connectionPropertiesWithValues.get(connectionProperty.getProperty());
      connectionProperty.setValue(value);
    }
    return connectionPropertiesWithCorrectOrder;
  }

  private List<SystemDbConnectionProperty> getConnectionPropertiesList() {
    return driver.getConnectionConfigurator().getDatabaseConnectionProperties().stream()
        .map(p -> new SystemDbConnectionProperty(p,
            driver.getConnectionConfigurator().getDefaultValue(p)))
        .collect(Collectors.toList());
  }

  private static List<SystemDbConnectionProperty> mergeConnectionProperties(List<SystemDbConnectionProperty> oldProps, List<SystemDbConnectionProperty> newProps) {
    oldProps.forEach(old -> {
      newProps.forEach(p -> {
        if (p.getProperty().equals(old.getProperty()) && !old.getValue().isBlank() && !wasDefaultPort(old)) {
          p.setValue(old.getValue());
        }
      });
    });
    return newProps;
  }

  private static boolean wasDefaultPort(SystemDbConnectionProperty oldPort) {
    return Objects.equals(oldPort.getProperty().getLabel(), "Port") && oldPort.isDefaultValue();
  }

  private DatabaseConnectionConfiguration createConfiguration() {
    var configurator = driver.getConnectionConfigurator();
    var props = connectionProperties.stream()
        .collect(Collectors.toMap(SystemDbConnectionProperty::getProperty, SystemDbConnectionProperty::getValue));
    var config = configurator.getDatabaseConnectionConfiguration(props);
    config.setProperties(getAdditionalProperties());
    return config;
  }

  @Override
  public boolean isStepOk() {
    return getConnectionInfo().isSuccessful() && isPersistentDb();
  }

  public boolean isPersistentDb() {
    return !Strings.CS.contains(driver.getDriverName(), HSQL_DB);
  }

  @Override
  public String getStepWarningMessage() {
    return isStepOk() ? getProblemMessage() : getConnectionInfo().getAdvise();
  }

}
