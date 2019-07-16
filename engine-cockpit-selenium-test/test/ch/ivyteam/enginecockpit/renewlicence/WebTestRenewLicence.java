package ch.ivyteam.enginecockpit.renewlicence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestRenewLicence extends WebTestBase
{
  
  private void waitForElasticsearch() throws InterruptedException
  {
    Thread.sleep(1000);
  }
  
  @Test
  public void testRenewRequest(FirefoxDriver driver) throws InterruptedException {
    toDashboard(driver);
    String response = sendRenew(driver);
    assertThat(response).contains("Your request has been sent");
    removeGrowl(driver);
    waitForElasticsearch();
    response = sendRenew(driver);
    assertThat(response).contains("Your request already exists");
  }


  private String sendRenew(FirefoxDriver driver)
  {
    driver.findElementByCssSelector(".ui-icon-refresh").click();
    driver.findElementById("renewLicence:emailInput").sendKeys("test@mail.com");
    driver.findElementById("renewLicence:renewBtn").click();
    return driver.findElementByCssSelector(".ui-growl-message").getText();
  }

  private void removeGrowl(FirefoxDriver driver)
  {
    Actions action = new Actions(driver);
    WebElement we = driver.findElementById("renewLicence:responseMessage_container");
    action.moveToElement(we).moveToElement(driver.findElementByClassName("ui-growl-icon-close")).click().build().perform();
  }
    
  private void toDashboard(FirefoxDriver driver)
  {
    login(driver);
    saveScreenshot(driver);
  }
}
