package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestCluster extends WebTestBase
{
  @Test
  void testCluster()
  {
    toCluster();

    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("Cluster"));
    Table table = new Table(driver, By.className("ui-datatable"), true);
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).isNotEmpty());
    
    webAssertThat(() -> assertThat(driver.findElementById("clusterNodeDialog").isDisplayed()).isFalse());
    driver.findElementById("card:form:clusterTable:0:clusterNode").click();
    webAssertThat(() -> assertThat(driver.findElementById("clusterNodeDialog").isDisplayed()).isTrue());
  }
  
  private void toCluster()
  {
    login();
    Navigation.toCluster(driver);
    saveScreenshot("cluster");
  }
}
