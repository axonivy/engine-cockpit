package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestSecuritySystem extends WebTestBase
{
  @Test
  void testSecuritySystem(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toSecuritySystem(driver);
    saveScreenshot(driver);
    assertThat(driver.findElementByTagName("h1").getText()).contains("Security Systems");
    assertThat(driver.findElementsByXPath("//tbody/tr")).isNotEmpty();
  }
  
  @Test
  void testAddNewSecuritySystem(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toSecuritySystem(driver);
    saveScreenshot(driver);
    driver.findElementById("card:form:createSecuritySystemBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("card:newSecuritySystemModal").isDisplayed()).isTrue());
    driver.findElementById("card:newSecuritySystemForm:saveNewSecuritySystem").click();
    webAssertThat(() -> assertThat(driver.findElementById("card:newSecuritySystemForm:newSecuritySystemNameMessage").getText())
            .contains("Value is required"));
    saveScreenshot(driver, "invalid");
    
    driver.findElementById("card:newSecuritySystemForm:newSecuritySystemNameInput").sendKeys("NewFromTest");
    driver.findElementById("card:newSecuritySystemForm:saveNewSecuritySystem").click();
    saveScreenshot(driver, "new_sec_system");
    webAssertThat(() -> assertThat(driver.findElementById("card:newSecuritySystemModal").isDisplayed()).isFalse());
    webAssertThat(() -> assertThat(driver.findElementsByXPath("//tbody//*[@class='security-name'][text()='NewFromTest']")).hasSize(1));
  }
}
