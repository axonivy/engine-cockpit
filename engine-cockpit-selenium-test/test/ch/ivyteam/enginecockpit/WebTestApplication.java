package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestApplication extends WebTestBase
{
  @Test
  void testApplications(FirefoxDriver driver)
  {
    toApplications(driver);

    assertThat(driver.findElementByTagName("h1").getText()).contains("Applications");
    List<WebElement> apps = driver.findElements(new By.ByClassName("activity-name"));
    if (!apps.isEmpty())
    {
      assertThat(driver.findElements(new By.ByClassName("activity-name"))).isNotEmpty();
      WebElement input = driver
              .findElement(new By.ByXPath(".//input[contains(@class, 'table-search-input-withicon')]"));
      input.sendKeys(EngineCockpitUrl.isDesignerApp() ? EngineCockpitUrl.applicationName() : "test-ad");
      saveScreenshot(driver, "search_app");
      await().untilAsserted(
              () -> assertThat(driver.findElements(new By.ByClassName("activity-name"))).hasSize(1));
    }
  }

  @Test
  void testAddRemoveApplication(FirefoxDriver driver)
  {
    toApplications(driver);

    int appCount = driver.findElements(new By.ByClassName("activity-name")).size();
    
    driver.findElementById("card:form:createApplicationBtn").click();
    saveScreenshot(driver, "new_app_dialog");
    await().untilAsserted(
            () -> assertThat(driver.findElementById("card:newApplicationModal").isDisplayed()).isTrue());

    driver.findElementById("card:newApplicationForm:saveNewApplication").click();
    saveScreenshot(driver, "new_app_value_required");
    await().untilAsserted(() -> assertThat(
            driver.findElementById("card:newApplicationForm:newApplicationNameMessage").isDisplayed())
                    .isTrue());
    
    driver.findElementById("card:newApplicationForm:newApplicationNameInput").sendKeys("newTestApp");
    driver.findElementById("card:newApplicationForm:newApplicationDescInput").sendKeys("test description");
    saveScreenshot(driver, "new_app_input");
    
    driver.findElementById("card:newApplicationForm:saveNewApplication").click();
    saveScreenshot(driver, "new_app_saved");
    await().untilAsserted(() -> assertThat(
            driver.findElements(new By.ByClassName("activity-name")).size()).isGreaterThan(appCount));
    await().untilAsserted(() -> assertThat(driver.findElementByXPath("//*[@class='activity-name'][text()='newTestApp']").isDisplayed()).isTrue());
    
    String newAppId = driver.findElementByXPath("//*[@class='activity-name'][text()='newTestApp']/../../..").getAttribute("id");
    await().untilAsserted(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("INACTIVE"));
    driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[1]")).click();
    saveScreenshot(driver, "new_app_run");
    await().untilAsserted(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("ACTIVE"));
    
    driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[2]")).click();
    saveScreenshot(driver, "new_app_stop");
    await().untilAsserted(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("INACTIVE"));
  
    String tasksButtonId = driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[3]")).getAttribute("id");
    String activityMenuId = tasksButtonId.substring(0, tasksButtonId.lastIndexOf(':')) + ":activityMenu";
    System.out.println(activityMenuId);
    driver.findElementById(newAppId).findElement(By.id(tasksButtonId)).click();
    saveScreenshot(driver, "new_app_menu");
    await().untilAsserted(() -> assertThat(driver.findElementById(activityMenuId).isDisplayed()).isTrue());
    
    driver.findElementByXPath("//*[@id='" + activityMenuId + "']//*[text()='Delete']/..").click();
    saveScreenshot(driver, "new_app_deletemodal");
    await().untilAsserted(() -> assertThat(driver.findElementById("card:form:deleteConfirmDialog").isDisplayed()).isTrue());
    
    driver.findElementById("card:form:deleteConfirmYesBtn").click();
    saveScreenshot(driver, "new_app_deleteapp");
    await().untilAsserted(() -> assertThat(driver.findElementById("card:form:applicationMessage").isDisplayed()).isTrue());
    await().untilAsserted(() -> assertThat(
            driver.findElements(new By.ByClassName("activity-name")).size()).isEqualTo(appCount));
  }

  private void toApplications(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toApplications(driver);
    saveScreenshot(driver, "applications");
  }
}
