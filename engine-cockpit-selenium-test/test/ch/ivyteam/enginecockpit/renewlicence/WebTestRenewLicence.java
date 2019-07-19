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
    saveScreenshot(driver, "in_dashboard");
    sendRenew(driver);
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".ui-growl-message").getText()).contains("Your request has been sent"));
    saveScreenshot(driver, "renew_positive");
    removeGrowl(driver);
    waitForElasticsearch();
    sendRenew(driver);
    webAssertThat(() -> assertThat(driver.findElementByCssSelector(".ui-growl-message").getText()).contains("Your request already exists"));
  }


  private void sendRenew(FirefoxDriver driver)
  {
    driver.findElementByCssSelector(".ui-icon-refresh").click();
    if (driver.findElementById("renewLicence:form:emailInput").getAttribute("value").isEmpty())
    {
    driver.findElementById("renewLicence:form:emailInput").sendKeys("test@mail.com");
    }
    saveScreenshot(driver, "filled_renew");
    driver.findElementById("renewLicence:form:renewBtn").click();
  }

  private void removeGrowl(FirefoxDriver driver)
  {
    Actions action = new Actions(driver);
    WebElement we = driver.findElementById("renewLicence:form:responseMessage_container");
    action.moveToElement(we).moveToElement(driver.findElementByClassName("ui-growl-icon-close")).click().build().perform();
  }
    
  private void toDashboard(FirefoxDriver driver)
  {
    login(driver);
    saveScreenshot(driver);
  }
}
