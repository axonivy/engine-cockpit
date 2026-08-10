package ch.ivyteam.enginecockpit.system.database;

import java.sql.SQLException;

import ch.ivyteam.db.jdbc.DatabaseConnectionConfiguration;
import ch.ivyteam.db.jdbc.DatabaseUtil;

public class DatabaseCreator {

  public static void deleteTempDatabase() {
    deleteDb();
  }

  private static DatabaseConnectionConfiguration getDbConfig(String dbName) {
    var dbHost = System.getProperty("db.host", "db host not set via system property db.host");
    return new DatabaseConnectionConfiguration(
        "jdbc:mysql://" + dbHost + ":3306/" + dbName,
        "com.mysql.cj.jdbc.Driver", "root", "1234");
  }

  private static void deleteDb() {
    try (var connection = DatabaseUtil.openConnection(getDbConfig("temp"))) {
      try (var stmt = connection.createStatement()) {
        stmt.execute("DROP DATABASE temp");
      }
    } catch (SQLException _) {}
  }
}
