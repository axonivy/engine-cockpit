package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.refresh;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestNotificationChannelDetail {

  private static SelenideElement enabledCheckbox;
  private static SelenideElement allEventsCheckbox;
  private static SelenideElement firstEventCheckbox;

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toNotificationChannelDetail("web");
    enabledCheckbox = checkbox("form:enabled");
    allEventsCheckbox = checkbox("form:allEvents");
    firstEventCheckbox = checkbox("form:events:0:eventEnabled");
  }

  @AfterEach
  void cleanup() {
    EngineCockpitUtil.resetNotificationConfig();
  }

  @Test
  void defaultConfig() {
    assertDefault();
  }

  @Test
  void uncheckEnabledCheckbox_disableOtherCheckboxes() {
    assertDefault();

    enabledCheckbox.click();

    assertCheckboxIsChecked(enabledCheckbox, false);

    assertCheckboxIsEnabled(allEventsCheckbox, false);
    assertCheckboxIsChecked(allEventsCheckbox, true);

    assertCheckboxIsEnabled(firstEventCheckbox, false);
    assertCheckboxIsChecked(firstEventCheckbox, false);
  }

  @Test
  void uncheckAllEventsCheckbox_enableEventCheckbox() {
    assertDefault();

    allEventsCheckbox.click();

    assertCheckboxIsChecked(enabledCheckbox, true);

    assertCheckboxIsEnabled(allEventsCheckbox, true);
    assertCheckboxIsChecked(allEventsCheckbox, false);

    assertCheckboxIsEnabled(firstEventCheckbox, true);
    assertCheckboxIsChecked(firstEventCheckbox, false);
  }

  @Test
  void resetChanges() {
    assertDefault();

    allEventsCheckbox.click();
    firstEventCheckbox.click();
    enabledCheckbox.click();

    assertCheckboxIsChecked(enabledCheckbox, false);

    assertCheckboxIsEnabled(allEventsCheckbox, false);
    assertCheckboxIsChecked(allEventsCheckbox, false);

    assertCheckboxIsEnabled(firstEventCheckbox, false);
    assertCheckboxIsChecked(firstEventCheckbox, true);

    $(By.id("reset")).click();

    assertDefault();
  }

  @Test
  void notSaveChanges() {
    assertDefault();

    allEventsCheckbox.click();
    firstEventCheckbox.click();
    enabledCheckbox.click();

    assertCheckboxIsChecked(enabledCheckbox, false);

    assertCheckboxIsEnabled(allEventsCheckbox, false);
    assertCheckboxIsChecked(allEventsCheckbox, false);

    assertCheckboxIsEnabled(firstEventCheckbox, false);
    assertCheckboxIsChecked(firstEventCheckbox, true);

    refresh();

    assertDefault();
  }

  @Test
  void saveChanges() {
    assertDefault();

    allEventsCheckbox.click();
    firstEventCheckbox.click();
    enabledCheckbox.click();

    assertCheckboxIsChecked(enabledCheckbox, false);

    assertCheckboxIsEnabled(allEventsCheckbox, false);
    assertCheckboxIsChecked(allEventsCheckbox, false);

    assertCheckboxIsEnabled(firstEventCheckbox, false);
    assertCheckboxIsChecked(firstEventCheckbox, true);

    $(By.id("save")).click();
    refresh();

    assertCheckboxIsChecked(enabledCheckbox, false);

    assertCheckboxIsEnabled(allEventsCheckbox, false);
    assertCheckboxIsChecked(allEventsCheckbox, false);

    assertCheckboxIsEnabled(firstEventCheckbox, false);
    assertCheckboxIsChecked(firstEventCheckbox, true);
  }

  @Test
  void eventsInTable() {
    Table table = new Table(By.id("form:events"));
    table.firstColumnShouldBe(size(1));
    table.firstColumnShouldBe(CollectionCondition.exactTexts("new-task"));
  }

  private SelenideElement checkbox(String id) {
    return $(By.id(id)).lastChild();
  }

  private void assertDefault() {
    assertCheckboxIsChecked(enabledCheckbox, true);

    assertCheckboxIsEnabled(allEventsCheckbox, true);
    assertCheckboxIsChecked(allEventsCheckbox, true);

    assertCheckboxIsEnabled(firstEventCheckbox, false);
    assertCheckboxIsChecked(firstEventCheckbox, false);
  }

  private void assertCheckboxIsEnabled(SelenideElement checkbox, boolean enabled) {
    if (enabled) {
      checkbox.shouldNotHave(cssClass("ui-state-disabled"));
    } else {
      checkbox.shouldHave(cssClass("ui-state-disabled"));
    }
  }

  private void assertCheckboxIsChecked(SelenideElement checkbox, boolean checked) {
    if (checked) {
      checkbox.lastChild().shouldHave(cssClass("ui-icon-check"));
    } else {
      checkbox.lastChild().shouldHave(cssClass("ui-icon-blank"));
    }
  }
}
