package ch.ivyteam.enginecockpit.system.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.db.jdbc.DatabaseConnectionConfiguration;
import ch.ivyteam.db.jdbc.DatabaseServer;
import ch.ivyteam.db.jdbc.DatabaseUtil;

public class SystemDatabaseCreator {

  private static final String OLD_DB_NAME = "old_version_150";
  private static final String TEST_DB_NAME = "temp";

  public static void createOldDatabase() throws Exception {
    deleteSystemDb(OLD_DB_NAME);
    createSystemDb(OLD_DB_NAME);
    fillSystemDb(OLD_DB_NAME);
  }

  public static void deleteOldDatabase() {
    deleteSystemDb(OLD_DB_NAME);
  }

  public static void deleteTempDatabase() {
    deleteSystemDb(TEST_DB_NAME);
  }

  private static DatabaseConnectionConfiguration getDbConfig(String dbName) {
    var dbHost = System.getProperty("db.host", "db host not set via system property db.host");
    return new DatabaseConnectionConfiguration(
        "jdbc:mysql://" + dbHost + ":3306/" + dbName,
        "com.mysql.cj.jdbc.Driver", "root", "1234");
  }

  private static void deleteSystemDb(String dbName) {
    try (Connection connection = DatabaseUtil.openConnection(getDbConfig(dbName))) {
      Statement stmt = connection.createStatement();
      stmt.execute("DROP DATABASE " + dbName);
    } catch (SQLException ex) {}
  }

  private static void createSystemDb(String dbName) throws SQLException {
    DatabaseServer.createInstance(getDbConfig(dbName)).createDatabase(dbName);
  }

  private static void fillSystemDb(String dbName) throws Exception {
    try (Connection connection = DatabaseUtil.openConnection(getDbConfig(dbName))) {
      try (Statement stmt = connection.createStatement()) {
        executeScript("CreateDatabaseVersion150.sql", ";", stmt);
        executeScript("CreateTriggers150.sql", "END;", stmt);
      }
    }
  }

  private static void executeScript(String scriptName, String splitter, Statement stmt) throws Exception {
    var sql = IOUtils.toString(SystemDatabaseCreator.class.getResource(scriptName), "utf-8");
    for (var statement : sql.split(splitter)) {
      if (StringUtils.isWhitespace(statement)) {
        break;
      }
      statement = statement.concat(splitter);
      try {
        stmt.execute(statement);
      } catch (Exception ex) {
        System.out.println("Could not execute: " + statement);
        throw ex;
      }
    }
  }
}
