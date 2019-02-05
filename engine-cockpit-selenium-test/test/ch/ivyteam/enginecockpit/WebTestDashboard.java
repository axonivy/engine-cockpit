package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebTestDashboard extends WebTestBase
{
  @Test
  void testDashboardContent(FirefoxDriver driver)
  {
    login(driver);
    saveScreenshot(driver);
    checkOverviewBoxes(driver);

    checkInfoPanels(driver);
    
    checkLicenceInfo(driver);
  }

  private void checkOverviewBoxes(FirefoxDriver driver)
  {
    List<WebElement> overviewBoxes = driver.findElementsByClassName("overview-box-content");
    assertThat(overviewBoxes).hasSize(4);
    List<String> boxesExpect = new ArrayList<>(
            Arrays.asList("Sessions", "Users", "Running Cases", "Applications"));
    overviewBoxes.stream().map(b -> b.findElement(new By.ByClassName("overview-box-title")).getText())
            .forEach(t -> assertThat(t).isNotEmpty().isIn(boxesExpect));
    overviewBoxes.stream().map(b -> b.findElement(new By.ByClassName("overview-box-count")).getText())
            .forEach(c -> assertThat(c).isNotEmpty());
  }

  private void checkInfoPanels(FirefoxDriver driver)
  {
    List<WebElement> infoPanels = driver.findElementsByClassName("ui-panel");
    assertThat(infoPanels).hasSize(5);
    List<String> panelsExpect = new ArrayList<>(
            Arrays.asList("Axon.ivy", "Licence", "Email", "System Database", "Java"));
    infoPanels.stream().map(p -> p.findElement(new By.ByClassName("ui-panel-title")).getText())
            .forEach(t -> assertThat(t).isNotEmpty().isIn(panelsExpect));
    for (WebElement ele : infoPanels)
    {
      assertThat(ele.findElement(new By.ByClassName("ui-panel-title")).getText()).isNotEmpty().isIn(panelsExpect);
      for (WebElement col : ele.findElements(new By.ByClassName("ui-grid-col-7")))
      {
        assertThat(col.getText()).isNotEmpty();
      }
    }
  }
  
  private void checkLicenceInfo(FirefoxDriver driver)
  {
    driver.findElementById("tasksButtonLicenceDetail").click();
    assertThat(driver.findElementById("licenceDetailDialog_title").isDisplayed()).isTrue();
    WebElement licenceList = driver.findElementById("licenceInfoForm:detailsList");
    assertThat(licenceList.isDisplayed()).isTrue();
  }
}
