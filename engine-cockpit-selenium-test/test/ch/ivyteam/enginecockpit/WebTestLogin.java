package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUrl.viewUrl;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class WebTestLogin extends WebTestBase
{

  @Test
  void testLogin()
  {
    login();
    saveScreenshot("after_login");
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("dashboard.xhtml"));
    webAssertThat(() -> assertThat(driver.getTitle()).startsWith("Engine Cockpit").doesNotContain("Login"));
    webAssertThat(() -> assertThat(driver.findElementById("sessionUserName").getText()).isEqualTo(getAdminUser())); 
  }
  
  @Test
  void testLoginInvalid()
  {
    driver.get(viewUrl("login.xhtml"));
    driver.findElementById("loginForm:login").click();
    saveScreenshot("empty_login");
    webAssertThat(() -> assertThat(driver.findElementById("loginForm:userNameMessage").getText()).isEqualTo("Value is required."));
    webAssertThat(() -> assertThat(driver.findElementById("loginForm:passwordMessage").getText()).isEqualTo("Value is required."));
    
    driver.findElementById("loginForm:userName").sendKeys(getAdminUser());
    driver.findElementById("loginForm:login").click();
    saveScreenshot("empty_password");
    webAssertThat(() -> assertThat(driver.findElementById("loginForm:passwordMessage").getText()).isEqualTo("Value is required."));
    
    driver.findElementById("loginForm:password").sendKeys("test");
    driver.findElementById("loginForm:login").click();
    saveScreenshot("wrong_password");
    webAssertThat(() -> assertThat(driver.findElementById("loginForm:passwordMessage").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("loginForm:loginMessage").isDisplayed()).isTrue());
  }
  
  @Test
  void testLogout()
  {
    login();
    logout();
    saveScreenshot("after_logout");
    assertLoginPageVisible();
    driver.get(viewUrl("dashboard.xhtml"));
    assertLoginPageVisible();
  }

  private void assertLoginPageVisible()
  {
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("login.xhtml"));
    webAssertThat(() -> assertThat(driver.getTitle()).contains("Login"));
  }
  
  private void logout()
  {
    driver.findElementByXPath("//*[@id='sessionUser']/a").click();
    saveScreenshot("logout");
    webAssertThat(() -> assertThat(driver.findElementById("sessionLogoutBtn").isDisplayed()).isTrue());
    driver.findElementById("sessionLogoutBtn").click();
  }
}
