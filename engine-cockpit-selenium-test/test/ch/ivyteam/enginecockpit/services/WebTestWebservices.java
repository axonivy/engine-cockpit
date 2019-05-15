package ch.ivyteam.enginecockpit.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestWebservices extends WebTestBase
{
  @Test
  void testWebserviesInTable(FirefoxDriver driver)
  {
    navigateToWebservices(driver);
    WebElement table = driver.findElementByClassName("webservicesTable");
    webAssertThat(() -> assertThat(driver.findElementsByClassName("webservice-name")).isNotEmpty());

    String last = table.findElement(By.xpath("(.//*[@class='webservice-name'])[last()]")).getText();
    table.findElement(By.xpath(".//input[contains(@class, 'table-search-input-withicon')]")).sendKeys(last);
    saveScreenshot(driver, "search_webservices");
    webAssertThat(() -> assertThat(table.findElements(By.className("webservice-name"))).hasSize(1));
  }
  
  private void navigateToWebservices(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toWebservices(driver);
    saveScreenshot(driver, "webservices");
  }
}
