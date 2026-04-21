package ch.ivyteam.enginecockpit.system.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ivyteam.db.jdbc.DatabaseConnectionConfiguration;
import ch.ivyteam.db.jdbc.DatabaseUtil;

public class DatabaseCreator {

  private static final String TEST_DB_NAME = "temp";

  public static void deleteTempDatabase() {
    deleteDb(TEST_DB_NAME);
  }

  private static DatabaseConnectionConfiguration getDbConfig(String dbName) {
    var dbHost = System.getProperty("db.host", "db host not set via system property db.host");
    return new DatabaseConnectionConfiguration(
        "jdbc:mysql://" + dbHost + ":3306/" + dbName,
        "com.mysql.cj.jdbc.Driver", "root", "1234");
  }

  private static void deleteDb(String dbName) {
    try (Connection connection = DatabaseUtil.openConnection(getDbConfig(dbName))) {
      Statement stmt = connection.createStatement();
      stmt.execute("DROP DATABASE " + dbName);
    } catch (SQLException ex) {}
  }
}
