package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestMonitor extends WebTestBase
{
  @Test
  void testMontorContent()
  {
    toMonitor();
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(4));
  }
  
  @Test
  void testLogsContent()
  {
    toLogs();
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(4));
  }
  
  private void toMonitor()
  {
    login();
    Navigation.toMonitor(driver);
    saveScreenshot("monitor");
  }
  
  private void toLogs()
  {
    login();
    Navigation.toLogs(driver);
    saveScreenshot("logs");
  }
}
