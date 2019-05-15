package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestRestClients extends WebTestBase
{
  @Test
  void testRestClientsInTable(FirefoxDriver driver)
  {
    navigateToRestClients(driver);
    WebElement table = driver.findElementByClassName("restClientsTable");
    webAssertThat(() -> assertThat(driver.findElementsByClassName("restclient-name")).isNotEmpty());

    String lastRest = table.findElement(By.xpath("(.//*[@class='restclient-name'])[last()]")).getText();
    table.findElement(By.xpath(".//input[contains(@class, 'table-search-input-withicon')]")).sendKeys(lastRest);
    saveScreenshot(driver, "search_restclients");
    webAssertThat(() -> assertThat(table.findElements(By.className("restclient-name"))).hasSize(1));
  }
  
  private void navigateToRestClients(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRestClients(driver);
    saveScreenshot(driver, "restclients");
  }
}
