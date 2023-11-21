package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectBooleanCheckbox;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestPushNotificationChannel {

  private static SelectBooleanCheckbox enabledCheckbox;
  private static SelectBooleanCheckbox allEventsCheckbox;
  private static SelectBooleanCheckbox firstEventCheckbox;

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toNotificationChannelDetail("mail");
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
  void locked_open_inDetails() {
    enable();
    EngineCockpitUtil.createNotification();
    login();
    Navigation.toNotificationChannelDetail("mail");

    $(By.id("form:state")).$("i.si-remove-circle").shouldBe(Condition.visible);
    $(By.id("form:state")).$("i.si-button-refresh-arrows").shouldBe(Condition.visible);
    $(By.id("form:state")).click();
    var lockDetails = $(By.id("form:lockDetails"));
    assertLockDetails(lockDetails);

    $(By.id("open")).click();

    $(By.id("form:state")).$("i.si-check-circle-1").shouldBe(Condition.visible);
  }

  @Test
  void locked_open_inOverview() {
    enable();
    EngineCockpitUtil.createNotification();
    login();
    Navigation.toNotificationChannels();
    Table table = new Table(By.id("securitySystems:securitySystemTabView:" + Tab.SECURITY_SYSTEM.getSelectedTabIndex() + ":tableForm:" + WebTestNotificationChannels.TABLE_ID), true);

    table.tableEntry("Email", 4).$("i.si-remove-circle").shouldBe(Condition.visible);
    table.tableEntry("Email", 4).$("i.si-synchronize-arrow-clock").shouldBe(Condition.visible);
    table.tableEntry("Email", 4).click();
    var lockDetails = $(By.id("securitySystems:securitySystemTabView:" + Tab.SECURITY_SYSTEM.getSelectedTabIndex() + ":tableForm:channelsTable:2:state:lockDetails"));
    assertLockDetails(lockDetails);

    Navigation.toNotificationChannelDetail("mail");
    $(By.id("open")).click();
    Navigation.toNotificationChannels();

    table.tableEntry("Email", 4).$("i.si-check-circle-1").shouldBe(Condition.visible);
  }

  @Test
  void lifeStats() {
    EngineCockpitUtil.assertLiveStats(List.of("Channel Deliveries", "Channel Pushes Time", "Channel Locks"), "Email", false);
  }

  private void assertLockDetails(SelenideElement lockDetails) {
    lockDetails.shouldHave(text("Error Information"));
    lockDetails.shouldHave(text("Failed deliver attemps so far:"));
    lockDetails.shouldHave(text("Next retry to deliver:"));
    lockDetails.shouldHave(text("Last Error:"));
    lockDetails.shouldHave(text("Connection refused"));
  }

  private void enable() {
    enabledCheckbox.setChecked();
    $(By.id("save")).click();
  }

  private void assertDefault() {
    enabledCheckbox.shouldBeChecked(false);

    allEventsCheckbox.shouldBeDisabled(true);
    allEventsCheckbox.shouldBeChecked(true);

    firstEventCheckbox.shouldBeDisabled(true);
    firstEventCheckbox.shouldBeChecked(false);
  }

}
