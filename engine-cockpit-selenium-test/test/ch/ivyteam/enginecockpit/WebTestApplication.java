package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestApplication extends WebTestBase
{
  private static final String NEW_TEST_APP = "newTestApp";

  @Test
  void testApplications()
  {
    toApplications();

    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("Applications"));
    Table table = new Table(driver, By.className("ui-treetable"), true);
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).isNotEmpty());
    driver.findElement(By.xpath(".//input[contains(@class, 'table-search-input-withicon')]")).sendKeys("test-ad");
    saveScreenshot("search_app");
    webAssertThat(() -> assertThat(driver.findElements(By.className("activity-name"))).hasSize(1));
  }
  
  @Test
  void testInvalidInputNewApplication()
  {
    toApplications();
    
    openNewApplicationModal();

    driver.findElementById("card:newApplicationForm:saveNewApplication").click();
    saveScreenshot("new_app_value_required");
    webAssertThat(() -> assertThat(driver.findElementById("card:newApplicationForm:newApplicationNameMessage").isDisplayed())
                    .isTrue());
  }

  @Test
  void testAddStartStopRemoveApplication()
  {
    toApplications();

    int appCount = driver.findElements(By.className("activity-name")).size();
    addNewApplication();
    webAssertThat(() -> assertThat(driver.findElements(By.className("activity-name")).size()).isGreaterThan(appCount));
    
    String newAppId = driver.findElementByXPath("//*[@class='activity-name'][text()='" + NEW_TEST_APP + "']/../../..").getAttribute("id");
    startNewApplication(newAppId);
    
    stopNewApplication(newAppId);
  
    deleteNewApplication(newAppId);
    webAssertThat(() -> assertThat(driver.findElements(By.className("activity-name")).size()).isEqualTo(appCount));
  }
  
  @Test
  void testStartStopDeleteNewAppInsideDetailView()
  {
    addNewAppAndNavigateToIt();
    
    webAssertThat(() -> assertThat(driver.findElementById("appDetailStateForm:operationStateLabel").getText()).isEqualTo("INACTIVE"));
    startAppInsideDetailView();
    stopAppInsideDetailView();
    Navigation.toApplications(driver);
    deleteNewApplication(driver.findElementByXPath("//*[@class='activity-name'][text()='" + NEW_TEST_APP + "']/../../..").getAttribute("id"));
  }

  private void stopAppInsideDetailView()
  {
    driver.findElementById("appDetailStateForm:deActivateApplication").click();
    saveScreenshot("stop_app");
    webAssertThat(() -> assertThat(driver.findElementById("appDetailStateForm:operationStateLabel").getText()).isEqualTo("INACTIVE"));
    webAssertThat(() -> assertThat(driver.findElementById("appDetailStateForm:activateApplication").isDisplayed()).isTrue());
  }

  private void startAppInsideDetailView()
  {
    driver.findElementById("appDetailStateForm:activateApplication").click();
    saveScreenshot("start_app");
    webAssertThat(() -> assertThat(driver.findElementById("appDetailStateForm:operationStateLabel").getText()).isEqualTo("ACTIVE"));
    webAssertThat(() -> assertThat(driver.findElementById("appDetailStateForm:deActivateApplication").isDisplayed()).isTrue());
  }
  
  private void addNewAppAndNavigateToIt()
  {
    toApplications();
    addNewApplication();
    Navigation.toApplicationDetail(driver, NEW_TEST_APP);
    saveScreenshot("application_detail");
  }

  private void stopNewApplication(String newAppId)
  {
    driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[3]")).click();
    saveScreenshot("new_app_stop");
    webAssertThat(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("INACTIVE"));
  }

  private void startNewApplication(String newAppId)
  {
    webAssertThat(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("INACTIVE"));
    driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[2]")).click();
    saveScreenshot("new_app_run");
    webAssertThat(() -> assertThat(driver.findElementById(newAppId).findElement(By.xpath("./td[3]")).getText()).isEqualTo("ACTIVE"));
  }

  private void deleteNewApplication(String newAppId)
  {
    String tasksButtonId = driver.findElementById(newAppId).findElement(By.xpath("./td[4]/button[4]")).getAttribute("id");
    String activityMenuId = tasksButtonId.substring(0, tasksButtonId.lastIndexOf(':')) + ":activityMenu";
    driver.findElementById(newAppId).findElement(By.id(tasksButtonId)).click();
    saveScreenshot("new_app_menu");
    webAssertThat(() -> assertThat(driver.findElementById(activityMenuId).isDisplayed()).isTrue());
    
    driver.findElementByXPath("//*[@id='" + activityMenuId + "']//*[text()='Delete']/..").click();
    saveScreenshot("new_app_deletemodal");
    webAssertThat(() -> assertThat(driver.findElementById("card:form:deleteConfirmDialog").isDisplayed()).isTrue());
    
    driver.findElementById("card:form:deleteConfirmYesBtn").click();
    saveScreenshot("new_app_deleteapp");
    webAssertThat(() -> assertThat(driver.findElementById("card:form:applicationMessage_container").isDisplayed()).isTrue());
  }

  private void addNewApplication()
  {
    openNewApplicationModal();
    
    driver.findElementById("card:newApplicationForm:newApplicationNameInput").sendKeys(NEW_TEST_APP);
    driver.findElementById("card:newApplicationForm:newApplicationDescInput").sendKeys("test description");
    new PrimeUi(driver).selectBooleanCheckbox(By.id("card:newApplicationForm:newApplicationActivate")).removeChecked();
    saveScreenshot("new_app_input");
    
    driver.findElementById("card:newApplicationForm:saveNewApplication").click();
    saveScreenshot("new_app_saved");

    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='activity-name'][text()='" + NEW_TEST_APP + "']").isDisplayed()).isTrue());
  }

  private void openNewApplicationModal()
  {
    driver.findElementById("card:form:createApplicationBtn").click();
    saveScreenshot("new_app_dialog");
    webAssertThat(() -> assertThat(driver.findElementById("card:newApplicationModal").isDisplayed()).isTrue());
  }
  
  private void toApplications()
  {
    login();
    Navigation.toApplications(driver);
    saveScreenshot("applications");
  }
}
