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

  private static final String OLD_DB_NAME = "engine_cockpit_old_db_version44";
  private static final String TEST_DB_NAME = "engine_cockpit_test_temp";
  
  public static void createOldDatabase() throws Exception
  {
    DatabaseConnectionConfiguration dbConnectionConfig = getOldDbConfig();
    deleteSystemDb(dbConnectionConfig, OLD_DB_NAME);
    createSystemDb(dbConnectionConfig);
    fillSystemDb(dbConnectionConfig);
  }

  public static void deleteOldDatabase()
  {
    deleteSystemDb(getOldDbConfig(), OLD_DB_NAME);
  }
  
  public static void deleteTempDatabase()
  {
    deleteSystemDb(getTempDbConfig(), TEST_DB_NAME);
  }
  
  private static DatabaseConnectionConfiguration getTempDbConfig()
  {
    return new DatabaseConnectionConfiguration(
            "jdbc:mysql://zugtstdbsmys:3306/" + TEST_DB_NAME,
            "com.mysql.jdbc.Driver", "admin", "nimda");
  }
  
  private static DatabaseConnectionConfiguration getOldDbConfig()
  {
    return new DatabaseConnectionConfiguration(
            "jdbc:mysql://zugtstdbsmys:3306/" + OLD_DB_NAME,
            "com.mysql.jdbc.Driver", "admin", "nimda");
  }
  
  private static void deleteSystemDb(DatabaseConnectionConfiguration dbConnectionConfig, String dbName)
  {
    try (Connection connection = DatabaseUtil.openConnection(dbConnectionConfig))
    {
      Statement stmt = connection.createStatement();
      stmt.execute("DROP DATABASE " + dbName);
    }
    catch (SQLException ex)
    {
    }
  }

  private static void createSystemDb(DatabaseConnectionConfiguration dbConnectionConfig)
          throws SQLException
  {
    DatabaseServer.createInstance(dbConnectionConfig).createDatabase(OLD_DB_NAME);
  }
  
  private static void fillSystemDb(DatabaseConnectionConfiguration dbConnectionConfig)
          throws FileNotFoundException, IOException, SQLException
  {
    try (Connection connection = DatabaseUtil.openConnection(dbConnectionConfig))
    {
      try (Statement stmt = connection.createStatement())
      {
        executeScript("CreateBaseDatabaseVersion39.sql", ";", stmt);
        executeScript("CreateDatabaseVersion39.sql", ";", stmt);
        executeScript("CreateTriggerDatabaseVersion39.sql", "END;", stmt);
        executeScript("CreateSystemApplication.sql", ";", stmt);
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