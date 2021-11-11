package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestApplication {
  private static final String NEW_TEST_APP = "newTestApp";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toApplications();
  }

  @Test
  void testApplications() {
    $("h1").shouldHave(text("Applications"));
    Table table = new Table(By.className("ui-treetable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    $(".table-search-input-withicon").sendKeys("test-ad");
    table.firstColumnShouldBe(size(1));
  }

  @Test
  void testInvalidInputNewApplication() {
    openNewApplicationModal();

    $("#card\\:newApplicationForm\\:saveNewApplication").click();
    $("#card\\:newApplicationForm\\:newApplicationNameMessage").shouldBe(visible);
  }

  @Test
  void testAddStartStopRemoveApplication() {
    int appCount = $$(".activity-name").size();
    addNewApplication();
    $$(".activity-name").shouldBe(size(appCount + 1));
    addNewApplication();
    $("#card\\:form\\:applicationMessage_container .ui-growl-message")
            .shouldHave(text("Application with name '" + NEW_TEST_APP + "' already exists"));

    By newApp = getNewAppId();
    startNewApplication(newApp);
    stopNewApplication(newApp);
    deleteNewApplication(newApp);
    $$(".activity-name").shouldBe(size(appCount));
  }

  @Test
  void testStartStopDeleteNewAppInsideDetailView() {
    addNewAppAndNavigateToIt();

    $$(".activity-state-inactive").shouldBe(size(2));
    startAppInsideDetailView();
    stopAppInsideDetailView();
    Navigation.toApplications();
    deleteNewApplication(getNewAppId());
  }

  @Test
  void testExpandCollapseTree() {
    Table table = new Table(By.className("ui-treetable"), true);
    table.firstColumnShouldBe(sizeLessThanOrEqual(3));
    expandAppTree();
    table.firstColumnShouldBe(sizeGreaterThan(3));
    $("#card\\:form\\:collapseAll").shouldBe(visible).click();
    table.firstColumnShouldBe(sizeLessThanOrEqual(3));
  }

  @Test
  void testChildProblemOnParent() {
    expandAppTree();

    var appName = isDesigner() ? DESIGNER : "test";
    var app = $$(".activity-name").find(text(appName)).parent().parent().parent();
    var pm = $$(".activity-name").find(text("engine-cockpit-test-data")).parent().parent();
    app.find(".module-state").shouldBe(attribute("title", "ACTIVE"))
            .findAll("i").shouldHave(size(1));
    pm.find("button", 1).click();
    pm.find(".module-state").shouldBe(attribute("title", "INACTIVE"))
            .findAll("i").shouldHave(size(1));
    app.find(".module-state").shouldBe(attribute("title", "ACTIVE\n" +
            "engine-cockpit-test-data: INACTIVE\n" +
            "engine-cockpit-test-data$1: INACTIVE"))
            .findAll("i").shouldHave(size(2));

    pm.find("button", 0).click();
    pm.find(".module-state").shouldBe(attribute("title", "ACTIVE"))
            .findAll("i").shouldHave(size(1));
    app.find(".module-state").shouldBe(attribute("title", "ACTIVE"))
            .findAll("i").shouldHave(size(1));
  }

  @Test
  void showOverrideProjectIconInTree() {
    expandAppTree();
    //project of app test has override configured
    $$(".activity-name").filter(text("engine-cockpit-test-data")).first().parent().find(".table-icon")
            .shouldHave(cssClass("si-move-to-bottom"))
            .shouldHave(attribute("title", "This PM is configured as strict override project"));
    //project of app test-ad has no override configured
    $$(".activity-name").filter(text("engine-cockpit-test-data")).last().parent().find(".table-icon")
            .shouldHave(cssClass("si-module-three-2"))
            .shouldHave(attribute("title", "PM"));

  }

  private void expandAppTree() {
    $("#card\\:form\\:expandAll").shouldBe(visible).click();
  }

  private void stopAppInsideDetailView() {
    $("#appDetailStateForm\\:deActivateApplication").click();
    $$(".activity-state-inactive").shouldBe(size(2));
    $("#appDetailStateForm\\:activateApplication").shouldBe(visible);
  }

  private void startAppInsideDetailView() {
    $("#appDetailStateForm\\:activateApplication").click();
    $$(".activity-state-active").shouldBe(size(2));
    $("#appDetailStateForm\\:deActivateApplication").shouldBe(visible);
  }

  private void addNewAppAndNavigateToIt() {
    addNewApplication();
    Navigation.toApplicationDetail(NEW_TEST_APP);
  }

  private void stopNewApplication(By newAppId) {
    $(newAppId).find(By.xpath("./td[4]/button[3]")).click();
    $(newAppId).find(By.xpath("./td[3]/span")).shouldBe(attribute("title", "INACTIVE"));
  }

  private void startNewApplication(By newAppId) {
    $(newAppId).find(By.xpath("./td[3]/span")).shouldBe(attribute("title", "INACTIVE"));
    $(newAppId).find(By.xpath("./td[4]/button[2]")).click();
    $(newAppId).find(By.xpath("./td[3]/span")).shouldBe(attribute("title", "ACTIVE"));
  }

  private void deleteNewApplication(By newAppId) {
    String tasksButtonId = $(newAppId).find(By.xpath("./td[4]/button[4]")).getAttribute("id");
    By activityMenu = By.id(tasksButtonId.substring(0, tasksButtonId.lastIndexOf(':')) + ":activityMenu");
    $(By.id(tasksButtonId)).click();
    $(activityMenu).shouldBe(visible);

    $(activityMenu).findAll("li").find(text("Delete")).click();
    $("#card\\:form\\:deleteConfirmDialog").shouldBe(visible);

    $("#card\\:form\\:deleteConfirmYesBtn").click();
    $("#card\\:form\\:applicationMessage_container").shouldBe(visible);
  }

  private void addNewApplication() {
    openNewApplicationModal();

    $("#card\\:newApplicationForm\\:newApplicationNameInput").clear();
    $("#card\\:newApplicationForm\\:newApplicationNameInput").sendKeys(NEW_TEST_APP);
    $("#card\\:newApplicationForm\\:newApplicationDescInput").sendKeys("test description");
    PrimeUi.selectBooleanCheckbox(By.id("card:newApplicationForm:newApplicationActivate")).removeChecked();

    $("#card\\:newApplicationForm\\:saveNewApplication").click();

    $$(".activity-name").find(text(NEW_TEST_APP)).shouldBe(visible);
  }

  private void openNewApplicationModal() {
    $("#card\\:form\\:createApplicationBtn").click();
    $("#card\\:newApplicationModal").shouldBe(visible);
  }

  private By getNewAppId() {
    return By.id($$(".activity-name").find(text(NEW_TEST_APP)).parent().parent().parent().getAttribute("id"));
  }

}
