package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestRestClients extends WebTestBase
{
  @Test
  void testRestClientsInTable(FirefoxDriver driver)
  {
    navigateToRestClients(driver);
    Table table = new Table(driver, By.className("restClientsTable"), true);
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).isNotEmpty());

    table.search(table.getFirstColumnEntries().get(0));
    saveScreenshot(driver, "search_restclients");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(1));
  }
  
  private void navigateToRestClients(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRestClients(driver);
    saveScreenshot(driver, "restclients");
  }
}
