package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.application.IExternalDatabaseConfiguration;
import ch.ivyteam.util.Property;

public class ExternalDatabase
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
  
  public boolean getPasswordChanged()
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
  
}
