package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestWebservices extends WebTestBase
{
  @Test
  void testWebserviesInTable()
  {
    navigateToWebservices();
    
    Table table = new Table(driver, By.className("webservicesTable"), true);
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).isNotEmpty());

    table.search(table.getFirstColumnEntries().get(0));
    saveScreenshot("search_webservices");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(1));
  }
  
  private void navigateToWebservices()
  {
    login();
    Navigation.toWebservices(driver);
    saveScreenshot("webservices");
  }
}
