package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
  void testSyncTrigger(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toSecuritySystem(driver);
    saveScreenshot(driver);
    
    Optional<WebElement> findFirst = driver.findElementsByClassName("provider-name").stream().filter(e -> !e.getText().equals("ivy Security System")).findFirst();
    if (findFirst.isPresent())
    {
      findFirst.get().findElement(By.xpath("../..//button")).click();
      saveScreenshot(driver, "trigger_sync");
      await().untilAsserted(() -> assertThat(driver.findElementByClassName("log-viewer").isDisplayed()).isTrue());
      saveScreenshot(driver, "sync_log");
    }
  }
}
