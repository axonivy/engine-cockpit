package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestExternalDatabases extends WebTestBase
{
  @Test
  void testDatabasesInTable(FirefoxDriver driver)
  {
    navigateToDatabases(driver);
    WebElement table = driver.findElementByClassName("externalDatabasesTable");
    webAssertThat(() -> assertThat(driver.findElementsByClassName("database-name")).isNotEmpty());

    String lastDb = table.findElement(By.xpath("(.//*[@class='database-name'])[last()]")).getText();
    table.findElement(By.xpath(".//input[contains(@class, 'table-search-input-withicon')]")).sendKeys(lastDb);
    saveScreenshot(driver, "search_externaldatabase");
    webAssertThat(() -> assertThat(table.findElements(By.className("database-name"))).hasSize(1));
  }
  
  private void navigateToDatabases(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toExternalDatabases(driver);
    saveScreenshot(driver, "externaldatabases");
  }
}
