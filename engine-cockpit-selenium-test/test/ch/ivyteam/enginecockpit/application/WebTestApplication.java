package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.href;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestApplication {

  private Table table;
  private static final String BUTTON_ID = "application:versionsForm:versionsTable:0";

  @BeforeEach
  void beforeEach() { 
    login();
    Navigation.toApplication("demo-portal");
    table = new Table(By.id("application:versionsForm:versionsTable"));
  }

  @Test
  void versionsAreListed() {
    table.rows().shouldHave(size(1));
  }

  @Test
  void addVersion() {
    var tableRows = table.rows().size();
    $(By.id("application:createVersion")).shouldBe(visible).click();
    table.rows().shouldHave(sizeGreaterThan(tableRows));
    deleteVersion();
    table.rows().shouldHave(size(1));
  }

  @Test
  void activateAndDeactivateButton() {
    $(By.id("application:createVersion")).shouldBe(visible).click();
    $(By.id(BUTTON_ID + ":activateBtn")).shouldBe(visible).click();
    $(By.id(BUTTON_ID + ":activateBtn")).shouldBe(disabled);
    $(By.id(BUTTON_ID + ":activityState")).shouldBe(text("Active"));
    
    $(By.id(BUTTON_ID + ":deactivateBtn")).shouldBe(visible).click();
    $(By.id(BUTTON_ID + ":deactivateBtn")).shouldBe(disabled);
    $(By.id(BUTTON_ID + ":activityState")).shouldBe(text("INACTIVE"));
    $(By.id(BUTTON_ID + ":activateBtn")).shouldBe(visible).click();
    deleteVersion();
  }

  @Test
  void releaseMenuItemReleasesVersion() {
    $(By.id("application:createVersion")).shouldBe(visible).click();
    $(By.id(BUTTON_ID + ":releaseState")).shouldBe(text("CREATED"));
    $(By.id(BUTTON_ID + ":tasksButton")).shouldBe(visible).click();
    $(By.id(BUTTON_ID + ":activityMenu")).shouldBe(visible);
    $(By.id(BUTTON_ID + ":releaseBtn")).shouldBe(visible).click();
    $(By.id(BUTTON_ID + ":releaseState")).shouldBe(text("RELEASED"));
    $(By.id(BUTTON_ID + ":tasksButton")).shouldBe(visible).click();
    $(By.id(BUTTON_ID + ":activityMenu")).shouldBe(visible);
    $(By.id(BUTTON_ID + ":releaseBtn")).shouldHave(cssClass("ui-state-disabled"));
    $(By.id("application:versionsForm:versionsTable:1:tasksButton")).shouldBe(visible).click();
    $(By.id("application:versionsForm:versionsTable:1:releaseBtn")).shouldBe(visible).click();
    deleteVersion();
  }

  @Test
  void deleteMenuItemRemovesVersion() {
    $(By.id("application:createVersion")).shouldBe(visible).click();
    table.rows().shouldHave(size(2));
    deleteVersion();
    table.rows().shouldHave(size(1));
  }

  @Test
  void moveRequiresInactiveApplication() {
    $(By.id(("security:appDetailSecurityForm:moveApplication"))).click();
    $(By.id("security:moveApplicationComposite:moveApplicationModal")).shouldBe(visible);
    $(By.id("security:moveApplicationComposite:moveApplicationForm:validateMoveApplication")).click();
    $(By.id("security:moveApplicationComposite:moveApplicationForm:validationMessage")).should(visible).should(Condition.text("Application must be deactivated."));
    $(By.id("security:moveApplicationComposite:moveApplicationForm:moveApplication")).shouldBe(disabled);
    $(By.id("security:moveApplicationComposite:moveApplicationForm:cancelMoveApplication")).click();
  }

  @Test
  void home() {
    var home = $(By.id("home"));
    home.$("a").shouldHave(Condition.href("demo-portal/1"));
    home.click();
    assertCurrentUrlContains("Login.xhtml");
  }

  @Test
  void workflow() {
    var workflow = $(By.id("workflow"));
    workflow.$("a").shouldHave(href("/dev-workflow-ui/faces/home.xhtml"));
    workflow.click();
    assertCurrentUrlContains("dev-workflow-ui");
  }

  private void deleteVersion() {
    $(By.id(BUTTON_ID + ":tasksButton")).shouldBe(visible).click();
    $(By.id(BUTTON_ID + ":activityMenu")).shouldBe(visible);
    $(By.id(BUTTON_ID + ":deleteBtn")).shouldBe(visible).click();
    $(By.id("application:versionsForm:deleteVersionDialog")).shouldBe(visible);
    $(By.id("application:versionsForm:deleteVersionDialogConfirmYesBtn")).shouldBe(visible).click();
  }
}
