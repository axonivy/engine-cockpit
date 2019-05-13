package ch.ivyteam.enginecockpit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import ch.ivyteam.ivy.application.IExternalDatabaseConfiguration;

public class ExternalDatabase
{
  private String name;
  private String url;
  private String driver;
  private String userName;
  private String password;
  private int maxConnections;
  private List<Entry<Object, Object>> properties;
  
  public ExternalDatabase(IExternalDatabaseConfiguration externalDatabase)
  {
    name = externalDatabase.getUserFriendlyName();
    url = externalDatabase.getDatabaseConnectionConfiguration().getConnectionUrl();
    driver = externalDatabase.getDatabaseConnectionConfiguration().getDriverName();
    userName = externalDatabase.getDatabaseConnectionConfiguration().getUserName();
    password = externalDatabase.getDatabaseConnectionConfiguration().getPassword();
    maxConnections = externalDatabase.getMaxConnections();
    properties = new ArrayList<>(externalDatabase.getDatabaseConnectionConfiguration().getProperties().entrySet());
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

  public List<Entry<Object, Object>> getProperties()
  {
    return properties;
  }
  
  
}
