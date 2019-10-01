package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestApplication extends WebTestBase
{
  private static final String NEW_TEST_APP = "newTestApp";

  @Test
  void testApplications(FirefoxDriver driver)
  {
    toApplications(driver);

    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("Applications"));
    List<WebElement> apps = driver.findElements(By.className("activity-name"));
    if (!apps.isEmpty())
    {
      webAssertThat(() -> assertThat(driver.findElements(By.className("activity-name"))).isNotEmpty());
      WebElement input = driver
              .findElement(By.xpath(".//input[contains(@class, 'table-search-input-withicon')]"));
      input.sendKeys(EngineCockpitUrl.isDesignerApp() ? EngineCockpitUrl.applicationName() : "test-ad");
      saveScreenshot(driver, "search_app");
      webAssertThat(() -> assertThat(driver.findElements(By.className("activity-name"))).hasSize(1));
    }
  }
  
  @Test
  void testInvalidInputNewApplication(FirefoxDriver driver)
  {
    toApplications(driver);
    
    openNewApplicationModal(driver);

    driver.findElementById("card:newApplicationForm:saveNewApplication").click();
    saveScreenshot(driver, "new_app_value_required");
    webAssertThat(() -> assertThat(driver.findElementById("card:newApplicationForm:newApplicationNameMessage").isDisplayed())
                    .isTrue());
  }

  @Test
  void testAddStartStopRemoveApplication(FirefoxDriver driver)
  {
    toApplications(driver);

    int appCount = driver.findElements(By.className("activity-name")).size();
    addNewApplication(driver);
    webAssertThat(() -> assertThat(driver.findElements(By.className("activity-name")).size()).isGreaterThan(appCount));
    
    String newAppId = driver.findElementByXPath("//*[@class='activity-name'][text()='" + NEW_TEST_APP + "']/../../..").getAttribute("id");
    startNewApplication(driver, newAppId);
    
    stopNewApplication(driver, newAppId);
  
    deleteNewApplication(driver, newAppId);
    webAssertThat(() -> assertThat(driver.findElements(By.className("activity-name")).size()).isEqualTo(appCount));
  }
  
  @Test
  void testStartStopDeleteNewAppInsideDetailView(FirefoxDriver driver)
  {
    addNewAppAndNavigateToIt(driver);
    
    webAssertThat(() -> assertThat(driver.findElementById("appDetailStateForm:operationStateLabel").getText()).isEqualTo("INACTIVE"));
    startAppInsideDetailView(driver);
    stopAppInsideDetailView(driver);
    Navigation.toApplications(driver);
    deleteNewApplication(driver, driver.findElementByXPath("//*[@class='activity-name'][text()='" + NEW_TEST_APP + "']/../../..").getAttribute("id"));
  }

  private void stopAppInsideDetailView(FirefoxDriver driver)
  {
    driver.findElementById("appDetailStateForm:deActivateApplication").click();
    saveScreenshot(driver, "stop_app");
    webAssertThat(() -> assertThat(driver.findElementById("appDetailStateForm:operationStateLabel").getText()).isEqualTo("INACTIVE"));
    webAssertThat(() -> assertThat(driver.findElementById("appDetailStateForm:activateApplication").isDisplayed()).isTrue());
  }

  private void startAppInsideDetailView(FirefoxDriver driver)
  {
    driver.findElementById("appDetailStateForm:activateApplication").click();
    saveScreenshot(driver, "start_app");
    webAssertThat(() -> assertThat(driver.findElementById("appDetailStateForm:operationStateLabel").getText()).isEqualTo("ACTIVE"));
    webAssertThat(() -> assertThat(driver.findElementById("appDetailStateForm:deActivateApplication").isDisplayed()).isTrue());
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
    driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[3]")).click();
    saveScreenshot(driver, "new_app_stop");
    webAssertThat(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("INACTIVE"));
  }

  private void startNewApplication(FirefoxDriver driver, String newAppId)
  {
    webAssertThat(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("INACTIVE"));
    driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[2]")).click();
    saveScreenshot(driver, "new_app_run");
    webAssertThat(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("ACTIVE"));
  }

  private void deleteNewApplication(FirefoxDriver driver, String newAppId)
  {
    String tasksButtonId = driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[4]")).getAttribute("id");
    String activityMenuId = tasksButtonId.substring(0, tasksButtonId.lastIndexOf(':')) + ":activityMenu";
    driver.findElementById(newAppId).findElement(By.id(tasksButtonId)).click();
    saveScreenshot(driver, "new_app_menu");
    webAssertThat(() -> assertThat(driver.findElementById(activityMenuId).isDisplayed()).isTrue());
    
    driver.findElementByXPath("//*[@id='" + activityMenuId + "']//*[text()='Delete']/..").click();
    saveScreenshot(driver, "new_app_deletemodal");
    webAssertThat(() -> assertThat(driver.findElementById("card:form:deleteConfirmDialog").isDisplayed()).isTrue());
    
    driver.findElementById("card:form:deleteConfirmYesBtn").click();
    saveScreenshot(driver, "new_app_deleteapp");
    webAssertThat(() -> assertThat(driver.findElementById("card:form:applicationMessage_container").isDisplayed()).isTrue());
  }

  private void addNewApplication(FirefoxDriver driver)
  {
    openNewApplicationModal(driver);
    
    driver.findElementById("card:newApplicationForm:newApplicationNameInput").sendKeys(NEW_TEST_APP);
    driver.findElementById("card:newApplicationForm:newApplicationDescInput").sendKeys("test description");
    new PrimeUi(driver).selectBooleanCheckbox(By.id("card:newApplicationForm:newApplicationActivate")).removeChecked();
    saveScreenshot(driver, "new_app_input");
    
    driver.findElementById("card:newApplicationForm:saveNewApplication").click();
    saveScreenshot(driver, "new_app_saved");

    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='activity-name'][text()='" + NEW_TEST_APP + "']").isDisplayed()).isTrue());
  }

  private void openNewApplicationModal(FirefoxDriver driver)
  {
    driver.findElementById("card:form:createApplicationBtn").click();
    saveScreenshot(driver, "new_app_dialog");
    webAssertThat(() -> assertThat(driver.findElementById("card:newApplicationModal").isDisplayed()).isTrue());
  }
  
  private void toApplications(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toApplications(driver);
    saveScreenshot(driver, "applications");
  }
}
