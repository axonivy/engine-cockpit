package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestExternalDatabases extends WebTestBase
{
  @Test
  void testDatabasesInTable(FirefoxDriver driver)
  {
    navigateToDatabases(driver);
    Table table = new Table(driver, By.className("externalDatabasesTable"), true);
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).isNotEmpty());

    table.search(table.getFirstColumnEntries().get(0));
    saveScreenshot(driver, "search_externaldatabase");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(1));
  }
  
  private void navigateToDatabases(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toExternalDatabases(driver);
    saveScreenshot(driver, "externaldatabases");
  }
}
