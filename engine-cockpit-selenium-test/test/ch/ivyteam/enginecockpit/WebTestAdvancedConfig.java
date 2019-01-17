package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestAdvancedConfig extends WebTestBase
{
  @Test
  void testSecuritySystem(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toAdvancedConfig(driver);
    saveScreenshot(driver);
    assertThat(driver.findElementByTagName("h1").getText()).contains("Advanced Config");
    List<WebElement> configs = driver.findElementsByClassName("config-name");
    if (!configs.isEmpty())
    {
      assertThat(driver.findElementsByClassName("config-name")).isNotEmpty();
      WebElement lastConfig = driver.findElementByXPath("(//*[@class='config-name'])[last()]");
      WebElement input = driver.findElementByXPath("//input[contains(@class, 'table-search-input-withicon')]");
      input.sendKeys(lastConfig.getText());
      await().untilAsserted(() -> assertThat(driver.findElementsByClassName("config-name")).hasSize(1));
    }
  }
}
