package ch.ivyteam.enginecockpit.system.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.db.jdbc.DatabaseConnectionConfiguration;
import ch.ivyteam.db.jdbc.DatabaseServer;
import ch.ivyteam.db.jdbc.DatabaseUtil;

public class SystemDatabaseCreator
{

  private static final String OLD_DB_NAME = "old_version_60";
  private static final String TEST_DB_NAME = "temp";
  
  public static void createOldDatabase() throws Exception
  {
    deleteSystemDb(OLD_DB_NAME);
    createSystemDb(OLD_DB_NAME);
    fillSystemDb(OLD_DB_NAME);
  }

  public static void deleteOldDatabase()
  {
    deleteSystemDb(OLD_DB_NAME);
  }
  
  public static void deleteTempDatabase()
  {
    deleteSystemDb(TEST_DB_NAME);
  }
  
  private static DatabaseConnectionConfiguration getDbConfig(String dbName)
  {
    var dbHost = System.getProperty("db.host", "db host not set via system property db.host");
    return new DatabaseConnectionConfiguration(
            "jdbc:mysql://"+dbHost+":3306/" + dbName,
            "com.mysql.jdbc.Driver", "root", "1234");
  }
  
  private static void deleteSystemDb(String dbName)
  {
    try (Connection connection = DatabaseUtil.openConnection(getDbConfig(dbName)))
    {
      Statement stmt = connection.createStatement();
      stmt.execute("DROP DATABASE " + dbName);
    }
    catch (SQLException ex)
    {
    }
  }

  private static void createSystemDb(String dbName)
          throws SQLException
  {
    DatabaseServer.createInstance(getDbConfig(dbName)).createDatabase(dbName);
  }
  
  private static void fillSystemDb(String dbName)
          throws FileNotFoundException, IOException, SQLException
  {
    try (Connection connection = DatabaseUtil.openConnection(getDbConfig(dbName)))
    {
      try (Statement stmt = connection.createStatement())
      {
        executeScript("CreateBaseDatabaseVersion60.sql", ";", stmt);
        executeScript("CreateDatabaseVersion60.sql", ";", stmt);
        executeScript("CreateTriggerDatabaseVersion60.sql", "END;", stmt);
        executeScript("CreateSystemApplicationVersion60.sql", ";", stmt);
      }
    }
  }
  
  private static void executeScript(String scriptName, String splitter, Statement stmt)
          throws FileNotFoundException, IOException, SQLException
  {
    String sql = IOUtils.toString(SystemDatabaseCreator.class.getResource(scriptName), "utf-8");
    String[] sqlStatements = sql.split(splitter);
    for (String statement : sqlStatements)
    {
      if (StringUtils.isWhitespace(statement))
      {
        break;
      }
      statement = statement.concat(splitter);
      System.out.println(statement);
      stmt.execute(statement);
    }
  }
}
