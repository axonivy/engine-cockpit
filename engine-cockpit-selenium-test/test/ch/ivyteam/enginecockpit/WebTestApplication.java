package ch.ivyteam.enginecockpit;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

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

    $("h1").shouldHave(text("Applications"));
    Table table = new Table(By.className("ui-treetable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    $(".table-search-input-withicon").sendKeys("test-ad");
    table.firstColumnShouldBe(size(1));
  }
  
  @Test
  void testInvalidInputNewApplication()
  {
    toApplications();
    
    openNewApplicationModal();

    $("#card\\:newApplicationForm\\:saveNewApplication").click();
    $("#card\\:newApplicationForm\\:newApplicationNameMessage").shouldBe(visible);
  }

  @Test
  void testAddStartStopRemoveApplication()
  {
    toApplications();

    int appCount = $$(".activity-name").size();
    addNewApplication();
    $$(".activity-name").shouldBe(size(appCount + 1));
    
    By newApp = getNewAppId();
    startNewApplication(newApp);
    stopNewApplication(newApp);
    deleteNewApplication(newApp);
    $$(".activity-name").shouldBe(size(appCount));
  }

  @Test
  void testStartStopDeleteNewAppInsideDetailView()
  {
    addNewAppAndNavigateToIt();
    
    $("#appDetailStateForm\\:operationStateLabel").shouldBe(exactText("INACTIVE"));
    startAppInsideDetailView();
    stopAppInsideDetailView();
    Navigation.toApplications();
    deleteNewApplication(getNewAppId());
  }

  private void stopAppInsideDetailView()
  {
    $("#appDetailStateForm\\:deActivateApplication").click();
    $("#appDetailStateForm\\:operationStateLabel").shouldBe(exactText("INACTIVE"));
    $("#appDetailStateForm\\:activateApplication").shouldBe(visible);
  }

  private void startAppInsideDetailView()
  {
    $("#appDetailStateForm\\:activateApplication").click();
    $("#appDetailStateForm\\:operationStateLabel").shouldBe(exactText("ACTIVE"));
    $("#appDetailStateForm\\:deActivateApplication").shouldBe(visible);
  }
  
  private void addNewAppAndNavigateToIt()
  {
    toApplications();
    addNewApplication();
    Navigation.toApplicationDetail(NEW_TEST_APP);
  }

  private void stopNewApplication(By newAppId)
  {
    $(newAppId).find(By.xpath("./td[4]/button[3]")).click();
    $(newAppId).find(By.xpath("./td[3]")).shouldBe(exactText("INACTIVE"));
  }

  private void startNewApplication(By newAppId)
  {
    $(newAppId).find(By.xpath("./td[3]")).shouldBe(exactText("INACTIVE"));
    $(newAppId).find(By.xpath("./td[4]/button[2]")).click();
    $(newAppId).find(By.xpath("./td[3]")).shouldBe(exactText("ACTIVE"));
  }

  private void deleteNewApplication(By newAppId)
  {
    String tasksButtonId = $(newAppId).find(By.xpath("./td[4]/button[4]")).getAttribute("id");
    By activityMenu = By.id(tasksButtonId.substring(0, tasksButtonId.lastIndexOf(':')) + ":activityMenu");
    $(By.id(tasksButtonId)).click();
    $(activityMenu).shouldBe(visible);
    
    $(activityMenu).findAll("li").find(text("Delete")).click();
    $("#card\\:form\\:deleteConfirmDialog").shouldBe(visible);
    
    $("#card\\:form\\:deleteConfirmYesBtn").click();
    $("#card\\:form\\:applicationMessage_container").shouldBe(visible);
  }

  private void addNewApplication()
  {
    openNewApplicationModal();
    
    $("#card\\:newApplicationForm\\:newApplicationNameInput").sendKeys(NEW_TEST_APP);
    $("#card\\:newApplicationForm\\:newApplicationDescInput").sendKeys("test description");
    PrimeUi.selectBooleanCheckbox(By.id("card:newApplicationForm:newApplicationActivate")).removeChecked();
    
    $("#card\\:newApplicationForm\\:saveNewApplication").click();

    $$(".activity-name").find(text(NEW_TEST_APP)).shouldBe(visible);
  }

  private void openNewApplicationModal()
  {
    $("#card\\:form\\:createApplicationBtn").click();
    $("#card\\:newApplicationModal").shouldBe(visible);
  }
  
  private By getNewAppId()
  {
    return By.id($$(".activity-name").find(text(NEW_TEST_APP)).parent().parent().parent().getAttribute("id"));
  }
  
  private void toApplications()
  {
    login();
    Navigation.toApplications();
  }
}
