package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestMonitor extends WebTestBase
{
  @Test
  void testMontorContent(FirefoxDriver driver)
  {
    toMonitor(driver);
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(4));
  }
  
  @Test
  void testLogsContent(FirefoxDriver driver)
  {
    toLogs(driver);
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(4));
  }
  
  private void toMonitor(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toMonitor(driver);
    saveScreenshot(driver, "monitor");
  }
  
  private void toLogs(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toLogs(driver);
    saveScreenshot(driver, "logs");
  }
}
