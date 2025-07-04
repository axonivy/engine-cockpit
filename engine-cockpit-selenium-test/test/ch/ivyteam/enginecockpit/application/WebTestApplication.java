package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestApplication {

  private static final String NEW_TEST_APP = "newTestApp";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toApplications();
  }

  @Test
  void applications() {
    $("h2").shouldHave(text("Applications"));
    Table table = new Table(By.className("ui-treetable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    filter("eSt-A");
    table.firstColumnShouldBe(size(1));
  }

  @Test
  void invalidInputNewApplication() {
    openNewApplicationModal();
    $("#newApplicationForm\\:saveNewApplication").click();
    $("#newApplicationForm\\:newApplicationNameMessage").shouldBe(visible);
  }

  @Test
  void addStartStopRemoveApplication() {
    int appCount = $$(".activity-name").size();
    addNewApplication();
    $$(".activity-name").shouldBe(size(appCount + 1));
    addNewApplication();
    $("#form\\:applicationMessage_container .ui-growl-message")
        .shouldHave(text("Application with name '" + NEW_TEST_APP + "' already exists"));

    By newApp = getNewAppId();
    startApplication(newApp);
    stopApplication(newApp);
    deleteApplication(newApp);
    $$(".activity-name").shouldBe(size(appCount));
  }

  @Test
  void startStopDeleteNewAppInsideDetailView() {
    addNewAppAndNavigateToIt();

    $$(".activity-state-inactive").shouldBe(size(2));
    startAppInsideDetailView();
    stopAppInsideDetailView();
    Navigation.toApplications();
    deleteApplication(getNewAppId());
  }

  @Test
  void expandCollapseTree() {
    Table table = new Table(By.className("ui-treetable"), true);
    table.firstColumnShouldBe(sizeLessThanOrEqual(4));
    expandAppTree();
    table.firstColumnShouldBe(sizeGreaterThan(4));
    collapseAppTree();
    table.firstColumnShouldBe(sizeLessThanOrEqual(4));
  }

  @Test
  void childProblemOnParent() {
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
    // project of app test has override configured
    $$(".activity-name").find(exactText("portal-components")).parent().find(".table-icon")
        .shouldHave(cssClass("si-move-to-bottom"))
        .shouldHave(attribute("title", "This PM is configured as strict override project"));
    // project of app test-ad has no override configured
    $$(".activity-name").find(exactText("portal-user-examples")).parent().find(".table-icon")
        .shouldHave(cssClass("si-module-three-2"))
        .shouldHave(attribute("title", "PM"));
  }

  @Test
  void keepExpandedState() {
    $("#form\\:tree_node_0 > td > span").shouldBe(visible).click();
    $$("#form\\:tree_node_0_0 > td > span").get(1).shouldBe(visible).click();
    addNewApplication();
    assertFirstPmvExpanded();
    deleteApplication(getNewAppId());
    assertFirstPmvExpanded();
    closeGrowlMessage();

    $("#form\\:tree_node_0 > td > span").shouldBe(visible).click();
    addNewApplication();
    assertFirstApplicationCollapsed();
    deleteApplication(getNewAppId());
    assertFirstApplicationCollapsed();
  }

  @Test
  void keepExpandedState_ExpandCollapseAll() {
    expandAppTree();
    addNewApplication();
    assertFirstPmvExpanded();
    deleteApplication(getNewAppId());
    assertFirstPmvExpanded();
    closeGrowlMessage();

    collapseAppTree();
    addNewApplication();
    assertFirstApplicationCollapsed();
    deleteApplication(getNewAppId());
    assertFirstApplicationCollapsed();
  }

  private void assertFirstPmvExpanded() {
    $("#form\\:tree_node_0").shouldHave(attribute("aria-expanded", "true"));
    $("#form\\:tree_node_0_0").shouldHave(attribute("aria-expanded", "true"));
  }

  private void assertFirstApplicationCollapsed() {
    $("#form\\:tree_node_0").shouldHave(attribute("aria-expanded", "false"));
  }

  void notDeletableMessage() {
    expandAppTree();
    $$("#form:tree:0:deleteBtn").get(1)
        .shouldHave(attribute("title", "App 'designer' can not be deleted"));
    $$("#form:tree:0_0:deleteBtn").get(1)
        .shouldHave(attribute("title", "'dev-workflow-ui' is required by 'dev-workflow-ui-web-test', 'dev-workflow-ui-test'"));
    $$("#form:tree:0_0_0:deleteBtn").get(2)
        .shouldHave(attribute("title", "Is required by 'dev-workflow-ui-web-test', 'dev-workflow-ui-test'\nIs in state RELEASED but must be one of [CREATED, PREPARED, ARCHIVED]"));
  }

  private void filter(String search) {
    var filter = $(By.id("form:globalFilter"));
    filter.shouldBe(visible).click();
    filter.sendKeys(search);
  }

  private void expandAppTree() {
    Selenide.executeJavaScript("scroll(0,0);");
    $("#form\\:expandAll").shouldBe(visible).click();
  }

  private void collapseAppTree() {
    Selenide.executeJavaScript("scroll(0,0);");
    $("#form\\:collapseAll").shouldBe(visible).click();
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

  private void stopApplication(By appId) {
    $(appId).find(By.cssSelector("td button"), 3).click();
    $(appId).find(By.xpath("./td[3]/span")).shouldBe(attribute("title", "INACTIVE"));
  }

  private void startApplication(By appId) {
    $(appId).find(By.xpath("./td[3]/span")).shouldBe(attribute("title", "INACTIVE"));
    $(appId).find(By.cssSelector("td button"), 2).click();
    $(appId).find(By.xpath("./td[3]/span")).shouldBe(attribute("title", "ACTIVE"));
  }

  private void deleteApplication(By appId) {
    String tasksButtonId = $(appId).find(By.cssSelector("td button"), 4).getAttribute("id");
    By activityMenu = By.id(tasksButtonId.substring(0, tasksButtonId.lastIndexOf(':')) + ":activityMenu");
    $(By.id(tasksButtonId)).click();
    $(activityMenu).shouldBe(visible);

    $(activityMenu).findAll("li").find(text("Delete")).click();
    $("#form\\:deleteConfirmDialog").shouldBe(visible);

    $("#form\\:deleteConfirmYesBtn").click();
    $("#form\\:applicationMessage_container").shouldBe(visible);
  }

  private void addNewApplication() {
    openNewApplicationModal();

    $("#newApplicationForm\\:newApplicationNameInput").clear();
    $("#newApplicationForm\\:newApplicationNameInput").sendKeys(NEW_TEST_APP);
    PrimeUi.selectBooleanCheckbox(By.id("newApplicationForm:newApplicationActivate")).removeChecked();

    $("#newApplicationForm\\:saveNewApplication").click();

    $$(".activity-name").find(text(NEW_TEST_APP)).shouldBe(visible);
  }

  private void openNewApplicationModal() {
    $("#form\\:createApplicationBtn").click();
    $("#newApplicationModal").shouldBe(visible);
  }

  private By getNewAppId() {
    return By.id($$(".activity-name").find(text(NEW_TEST_APP)).parent().parent().parent().getAttribute("id"));
  }

  private void closeGrowlMessage() {
    var js = (JavascriptExecutor) WebDriverRunner.getWebDriver();
    js.executeScript("arguments[0].click();", $(".ui-growl-icon-close"));
  }
}
