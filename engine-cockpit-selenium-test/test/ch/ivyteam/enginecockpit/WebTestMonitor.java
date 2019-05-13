package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestMonitor extends WebTestBase
{
  @Test
  void testMontorContent(FirefoxDriver driver)
  {
    toMonitor(driver);

    checkMonitorPanels(driver);
  }
  
  @Test
  void testLogsContent(FirefoxDriver driver)
  {
    toLogs(driver);
    
    checkLogsPanels(driver);
  }
  
  private void checkMonitorPanels(FirefoxDriver driver)
  {
    List<WebElement> infoPanels = driver.findElementsByClassName("ui-panel");
    assertThat(infoPanels).hasSize(4);
  }
  
  private void checkLogsPanels(FirefoxDriver driver)
  {
    List<WebElement> infoPanels = driver.findElementsByClassName("ui-panel");
    assertThat(infoPanels).hasSize(3);
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
