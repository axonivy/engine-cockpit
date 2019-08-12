package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUrl.viewUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.firefox.FirefoxDriver;

import io.github.bonigarcia.seljup.SeleniumExtension;

@ExtendWith(SeleniumExtension.class)
public class WebTestLogin extends WebTestBase
{

  @Test
  void testLogin(FirefoxDriver driver)
  {
    login(driver);
    saveScreenshot(driver, "after_login");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("dashboard.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).startsWith("Engine Cockpit").doesNotContain("Login"));
    await().untilAsserted(() -> assertThat(driver.findElementById("sessionUserName").getText()).isEqualTo(getAdminUser())); 
  }
  
  @Test
  void testLoginInvalid(FirefoxDriver driver)
  {
    driver.get(viewUrl("login.xhtml"));
    driver.findElementById("loginForm:login").click();
    saveScreenshot(driver, "empty_login");
    await().untilAsserted(() -> assertThat(driver.findElementById("loginForm:userNameMessage").getText()).isEqualTo("Value is required."));
    await().untilAsserted(() -> assertThat(driver.findElementById("loginForm:passwordMessage").getText()).isEqualTo("Value is required."));
    
    driver.findElementById("loginForm:userName").sendKeys(getAdminUser());
    driver.findElementById("loginForm:login").click();
    saveScreenshot(driver, "empty_password");
    await().untilAsserted(() -> assertThat(driver.findElementById("loginForm:passwordMessage").getText()).isEqualTo("Value is required."));
    
    driver.findElementById("loginForm:password").sendKeys("test");
    driver.findElementById("loginForm:login").click();
    saveScreenshot(driver, "wrong_password");
    await().untilAsserted(() -> assertThat(driver.findElementById("loginForm:passwordMessage").getText()).isEmpty());
    await().untilAsserted(() -> assertThat(driver.findElementById("loginForm:loginMessage").isDisplayed()).isTrue());
  }
  
  @Test
  void testLogout(FirefoxDriver driver)
  {
    login(driver);
    logout(driver);
    saveScreenshot(driver, "after_logout");
    assertLoginPageVisible(driver);
    driver.get(viewUrl("dashboard.xhtml"));
    assertLoginPageVisible(driver);
  }

  private void assertLoginPageVisible(FirefoxDriver driver)
  {
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("login.xhtml"));
    await().untilAsserted(() -> assertThat(driver.getTitle()).contains("Login"));
  }
  
  private void logout(FirefoxDriver driver)
  {
    driver.findElementByXPath("//*[@id='sessionUser']/a").click();
    saveScreenshot(driver, "logout");
    webAssertThat(() -> assertThat(driver.findElementById("sessionLogoutBtn").isDisplayed()).isTrue());
    driver.findElementById("sessionLogoutBtn").click();
  }
}
