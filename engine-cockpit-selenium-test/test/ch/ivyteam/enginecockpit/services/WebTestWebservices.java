package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestWebservices extends WebTestBase
{
  @Test
  void testWebserviesInTable(FirefoxDriver driver)
  {
    navigateToWebservices(driver);
    
    Table table = new Table(driver, By.className("webservicesTable"), true);
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).isNotEmpty());

    table.search(table.getFirstColumnEntries().get(0));
    saveScreenshot(driver, "search_webservices");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(1));
  }
  
  private void navigateToWebservices(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toWebservices(driver);
    saveScreenshot(driver, "webservices");
  }
}
