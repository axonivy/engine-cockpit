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
  private static final String NEW_TEST_APP = "newTestApp";


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
  void testInvalidInputNewApplication(FirefoxDriver driver)
  {
    toApplications(driver);
    
    openNewApplicationModal(driver);

    driver.findElementById("card:newApplicationForm:saveNewApplication").click();
    saveScreenshot(driver, "new_app_value_required");
    await().untilAsserted(() -> assertThat(
            driver.findElementById("card:newApplicationForm:newApplicationNameMessage").isDisplayed())
                    .isTrue());
  }

  @Test
  void testAddStartStopRemoveApplication(FirefoxDriver driver)
  {
    toApplications(driver);

    int appCount = driver.findElements(new By.ByClassName("activity-name")).size();
    addNewApplication(driver);
    await().untilAsserted(() -> assertThat(
            driver.findElements(new By.ByClassName("activity-name")).size()).isGreaterThan(appCount));
    
    String newAppId = driver.findElementByXPath("//*[@class='activity-name'][text()='" + NEW_TEST_APP + "']/../../..").getAttribute("id");
    startNewApplication(driver, newAppId);
    
    stopNewApplication(driver, newAppId);
  
    deleteNewApplication(driver, newAppId);
    await().untilAsserted(() -> assertThat(
            driver.findElements(new By.ByClassName("activity-name")).size()).isEqualTo(appCount));
  }
  
  @Test
  void testStartStopDeleteNewAppInsideDetailView(FirefoxDriver driver)
  {
    addNewAppAndNavigateToIt(driver);
    
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailStateForm:operationStateLabel").getText()).isEqualTo("INACTIVE"));
    startAppInsideDetailView(driver);
    stopAppInsideDetailView(driver);
    deleteAppInsideDetailView(driver);
  }
  
  private void deleteAppInsideDetailView(FirefoxDriver driver)
  {
    driver.findElementById("appDetailInfoForm:deleteApplication").click();
    saveScreenshot(driver, "delete_modal");
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailInfoForm:deleteAppConfirmDialog").isDisplayed()).isTrue());
    
    
    driver.findElementById("appDetailInfoForm:deleteAppConfirmYesBtn").click();
    saveScreenshot(driver, "all_apps");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("applications.xhtml"));
  }
  
  private void stopAppInsideDetailView(FirefoxDriver driver)
  {
    driver.findElementById("appDetailStateForm:deActivateApplication").click();
    saveScreenshot(driver, "stop_app");
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailStateForm:operationStateLabel").getText()).isEqualTo("INACTIVE"));
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailStateForm:activateApplication").isDisplayed()).isTrue());
  }

  private void startAppInsideDetailView(FirefoxDriver driver)
  {
    driver.findElementById("appDetailStateForm:activateApplication").click();
    saveScreenshot(driver, "start_app");
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailStateForm:operationStateLabel").getText()).isEqualTo("ACTIVE"));
    await().untilAsserted(() -> assertThat(driver.findElementById("appDetailStateForm:deActivateApplication").isDisplayed()).isTrue());
  }
  
  private void addNewAppAndNavigateToIt(FirefoxDriver driver)
  {
    toApplications(driver);
    addNewApplication(driver);
    Navigation.toApplicationDetail(driver, NEW_TEST_APP);
    saveScreenshot(driver, "application_detail");
  }

  private void stopNewApplication(FirefoxDriver driver, String newAppId)
  {
    driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[2]")).click();
    saveScreenshot(driver, "new_app_stop");
    await().untilAsserted(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("INACTIVE"));
  }

  private void startNewApplication(FirefoxDriver driver, String newAppId)
  {
    await().untilAsserted(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("INACTIVE"));
    driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[1]")).click();
    saveScreenshot(driver, "new_app_run");
    await().untilAsserted(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("ACTIVE"));
  }

  private void deleteNewApplication(FirefoxDriver driver, String newAppId)
  {
    String tasksButtonId = driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[3]")).getAttribute("id");
    String activityMenuId = tasksButtonId.substring(0, tasksButtonId.lastIndexOf(':')) + ":activityMenu";
    driver.findElementById(newAppId).findElement(By.id(tasksButtonId)).click();
    saveScreenshot(driver, "new_app_menu");
    await().untilAsserted(() -> assertThat(driver.findElementById(activityMenuId).isDisplayed()).isTrue());
    
    driver.findElementByXPath("//*[@id='" + activityMenuId + "']//*[text()='Delete']/..").click();
    saveScreenshot(driver, "new_app_deletemodal");
    await().untilAsserted(() -> assertThat(driver.findElementById("card:form:deleteConfirmDialog").isDisplayed()).isTrue());
    
    driver.findElementById("card:form:deleteConfirmYesBtn").click();
    saveScreenshot(driver, "new_app_deleteapp");
    await().untilAsserted(() -> assertThat(driver.findElementById("card:form:applicationMessage_container").isDisplayed()).isTrue());
  }

  private void addNewApplication(FirefoxDriver driver)
  {
    openNewApplicationModal(driver);
    
    driver.findElementById("card:newApplicationForm:newApplicationNameInput").sendKeys(NEW_TEST_APP);
    driver.findElementById("card:newApplicationForm:newApplicationDescInput").sendKeys("test description");
    saveScreenshot(driver, "new_app_input");
    
    driver.findElementById("card:newApplicationForm:saveNewApplication").click();
    saveScreenshot(driver, "new_app_saved");

    await().untilAsserted(() -> assertThat(driver.findElementByXPath("//*[@class='activity-name'][text()='" + NEW_TEST_APP + "']").isDisplayed()).isTrue());
  }

  private void openNewApplicationModal(FirefoxDriver driver)
  {
    driver.findElementById("card:form:addButton").click();
    webAssertThat(() -> assertThat(driver.findElementById("card:form:addMenu").isDisplayed()).isTrue());
    driver.findElementById("card:form:createApplicationBtn").click();
    saveScreenshot(driver, "new_app_dialog");
    await().untilAsserted(
            () -> assertThat(driver.findElementById("card:newApplicationModal").isDisplayed()).isTrue());
  }
  
  private void toApplications(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toApplications(driver);
    saveScreenshot(driver, "applications");
  }
}
