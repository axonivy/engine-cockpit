package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUrl.viewUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.seljup.SeleniumExtension;

@ExtendWith(SeleniumExtension.class)
public class WebTestLogin extends WebTestBase
{

  @Test
  void testLogin(FirefoxDriver driver, TestInfo testInfo)
  {
    login(driver, testInfo);
    saveScreenshot(driver, testInfo);
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("dashboard.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).startsWith("Engine Cockpit").doesNotContain("Login"));
    await().untilAsserted(() -> assertThat(driver.findElementById("sessionUserName").getText()).isEqualTo(getAdminUser())); 
  }
  
  @Test
  void testLoginInvalid(FirefoxDriver driver, TestInfo testInfo)
  {
    driver.get(viewUrl("login.xhtml"));
    driver.findElementById("loginForm:login").click();
    saveScreenshot(driver, testInfo);
    await().untilAsserted(() -> assertThat(driver.findElementById("loginForm:userNameMessage").getText()).isEqualTo("Value is required."));
    await().untilAsserted(() -> assertThat(driver.findElementById("loginForm:passwordMessage").getText()).isEqualTo("Value is required."));
    
    driver.findElementById("loginForm:userName").sendKeys(getAdminUser());
    driver.findElementById("loginForm:login").click();
    saveScreenshot(driver, testInfo);
    await().untilAsserted(() -> assertThat(driver.findElementById("loginForm:passwordMessage").getText()).isEqualTo("Value is required."));
    
    driver.findElementById("loginForm:password").sendKeys("test");
    driver.findElementById("loginForm:login").click();
    saveScreenshot(driver, testInfo);
    await().untilAsserted(() -> assertThat(driver.findElementById("loginForm:passwordMessage").getText()).isEmpty());
    await().untilAsserted(() -> assertThat(driver.findElementById("loginForm:loginMessage").isDisplayed()).isTrue());
  }
  
  @Test
  void testLogout(FirefoxDriver driver, TestInfo testInfo)
  {
    login(driver, testInfo);
    logout(driver, testInfo);
    saveScreenshot(driver, testInfo);
    assertLoginPageVisible(driver);
    driver.get(viewUrl("dashboard.xhtml"));
    assertLoginPageVisible(driver);
  }

  private void assertLoginPageVisible(FirefoxDriver driver)
  {
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("login.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).contains("Login"));
  }
  
  private void logout(FirefoxDriver driver, TestInfo testInfo)
  {
    driver.get(viewUrl("dashboard.xhtml"));
    driver.findElementById("sessionUser").click();
    saveScreenshot(driver, testInfo);
    driver.findElementById("sessionLogoutBtn").click();
  }
}
