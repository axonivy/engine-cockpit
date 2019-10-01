package ch.ivyteam.enginecockpit.dashboad;

import java.util.IdentityHashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import ch.ivyteam.db.jdbc.DatabaseConnectionConfiguration;
import ch.ivyteam.db.jdbc.DatabaseProduct;
import ch.ivyteam.db.jdbc.JdbcDriver;
import ch.ivyteam.enginecockpit.util.SystemDbUtil;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SystemDbBean
{
  private String url;
  private String driver;
  private static final Map<DatabaseProduct, String> ANCHORS = new IdentityHashMap<>(10);
  static 
  {
      ANCHORS.put(DatabaseProduct.MYSQL, "mysql");
      ANCHORS.put(DatabaseProduct.MARIADB, "mariadb");
      ANCHORS.put(DatabaseProduct.POSTGRE_SQL, "postgres");
      ANCHORS.put(DatabaseProduct.MICROSOFT_SQL_SERVER, "mssql");
      ANCHORS.put(DatabaseProduct.ORACLE, "oracle");
  }
  
  public SystemDbBean()
  {
    initSystemDbConfigs();
  }
  
  private void initSystemDbConfigs()
  {
    url = IConfiguration.get().getOrDefault(SystemDbUtil.URL);
    driver = IConfiguration.get().getOrDefault(SystemDbUtil.DRIVER);
  }

  public String getUrl()
  {
    return url;
  }

  public String getDriver()
  {
    return driver;
  }
  
  public String getHelpPath()
  {
    String anchor = getDatabaseSpecificAnchor();
    return "installation/systemdatabase.html" + anchor;
  }

  private String getDatabaseSpecificAnchor() 
  {
    DatabaseConnectionConfiguration config = new DatabaseConnectionConfiguration(getUrl(), getDriver());
    JdbcDriver jdbcDriver = JdbcDriver.getJdbcDriverForConnectionConfiguration(config);
    if (jdbcDriver == null)
    {
      return "";
    }
    DatabaseProduct product = jdbcDriver.getDatabaseProduct();
    if (!ANCHORS.containsKey(product))
    {
      return "";
    }
    return "#systemdb-"+ANCHORS.get(product);
  }	
}
