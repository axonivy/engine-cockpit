package ch.ivyteam.enginecockpit.model;

import java.util.List;
import java.util.stream.Collectors;

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
  }

  public String getName()
  {
    return name;
  }

  public String getUrl()
  {
    return url;
  }

  public String getDriver()
  {
    return driver;
  }

  public String getUserName()
  {
    return userName;
  }

  public String getPassword()
  {
    return password;
  }

  public int getMaxConnections()
  {
    return maxConnections;
  }

  public List<Property> getProperties()
  {
    return properties;
  }
  
}
