package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.refresh;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectBooleanCheckbox;
import com.codeborne.selenide.CollectionCondition;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestNotificationChannelDetail {

  private static SelectBooleanCheckbox enabledCheckbox;
  private static SelectBooleanCheckbox allEventsCheckbox;
  private static SelectBooleanCheckbox firstEventCheckbox;

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toNotificationChannelDetail("web");
    enabledCheckbox = PrimeUi.selectBooleanCheckbox(By.id("form:enabled"));
    allEventsCheckbox = PrimeUi.selectBooleanCheckbox(By.id("form:allEvents"));
    firstEventCheckbox = PrimeUi.selectBooleanCheckbox(By.id("form:events:0:eventEnabled"));
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

    enabledCheckbox.removeChecked();

    allEventsCheckbox.shouldBeDisabled(true);
    allEventsCheckbox.shouldBeChecked(true);

    firstEventCheckbox.shouldBeDisabled(true);
    firstEventCheckbox.shouldBeChecked(false);
  }

  @Test
  void uncheckAllEventsCheckbox_enableEventCheckbox() {
    assertDefault();

    allEventsCheckbox.removeChecked();

    enabledCheckbox.shouldBeChecked(true);

    allEventsCheckbox.shouldBeDisabled(false);

    firstEventCheckbox.shouldBeDisabled(false);
    firstEventCheckbox.shouldBeChecked(false);
  }

  @Test
  void resetChanges() {
    assertDefault();

    allEventsCheckbox.removeChecked();
    firstEventCheckbox.setChecked();
    enabledCheckbox.removeChecked();

    allEventsCheckbox.shouldBeDisabled(true);

    firstEventCheckbox.shouldBeDisabled(true);

    $(By.id("reset")).click();

    assertDefault();
  }

  @Test
  void notSaveChanges() {
    assertDefault();

    allEventsCheckbox.removeChecked();
    firstEventCheckbox.setChecked();
    enabledCheckbox.removeChecked();

    allEventsCheckbox.shouldBeDisabled(true);

    firstEventCheckbox.shouldBeDisabled(true);

    refresh();

    assertDefault();
  }

  @Test
  void saveChanges() {
    assertDefault();

    allEventsCheckbox.removeChecked();
    firstEventCheckbox.setChecked();
    enabledCheckbox.removeChecked();

    allEventsCheckbox.shouldBeDisabled(true);

    firstEventCheckbox.shouldBeDisabled(true);

    $(By.id("save")).click();
    refresh();

    enabledCheckbox.shouldBeChecked(false);

    allEventsCheckbox.shouldBeDisabled(true);
    allEventsCheckbox.shouldBeChecked(false);

    firstEventCheckbox.shouldBeDisabled(true);
    firstEventCheckbox.shouldBeChecked(true);
  }

  @Test
  void eventsInTable() {
    Table table = new Table(By.id("form:events"));
    table.firstColumnShouldBe(size(1));
    table.firstColumnShouldBe(CollectionCondition.exactTexts("new-task"));
  }

  private void assertDefault() {
    enabledCheckbox.shouldBeChecked(true);

    allEventsCheckbox.shouldBeDisabled(false);
    allEventsCheckbox.shouldBeChecked(true);

    firstEventCheckbox.shouldBeDisabled(true);
    firstEventCheckbox.shouldBeChecked(false);
  }
}
