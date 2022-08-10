package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.createLicenceEvents;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestDashboard {
  @BeforeAll
  static void setup() {
    createLicenceEvents();
  }

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void dashboardContent() {
    EngineCockpitUtil.createRunningCase();
    login();
    var sessions = $(".overview-box h1", 0).shouldBe(visible).text();
    assertThat(Integer.parseInt(sessions)).isGreaterThan(0);
    var users = $(".overview-box h1", 1).shouldBe(visible).text();
    assertThat(Integer.parseInt(users)).isBetween(5, 15);
    var cases = $(".overview-box h1", 2).shouldBe(visible).text();
    assertThat(Integer.parseInt(cases)).isGreaterThan(0);
    var apps = $(".overview-box h1", 3).shouldBe(visible).text();
    assertThat(Integer.parseInt(apps)).isEqualTo(3);
    $$(".card").shouldHave(size(9));
    EngineCockpitUtil.destroyRunningCase();
  }

  @Test
  void checkLicenceInfo() {
    $("#tasksButtonLicenceDetail").shouldBe(visible).click();
    $("h2").shouldHave(text("Licence"));
    $("#licence\\:licWarnMessage").shouldHave(text("Please upload a valid licence"));
  }

  @Test
  void checkLicenceEvents() {
    $("#tasksButtonLicenceEvents").shouldBe(visible);
    $(".licence-notification").shouldBe(visible);

    $("#tasksButtonLicenceEvents").click();
    $("#licenceEventsDialog").shouldBe(visible);
    $$(".licence-messages li").shouldHave(size(2));
    $(".licence-messages li", 0).find(".ui-button").click();
    $$(".licence-messages li").shouldHave(size(1));
    $("#licenceEventForm\\:closeLicenceEventsDialog").click();
    $("#licenceEventsDialog").shouldNotBe(visible);

    $(".licence-notification > a").click();
    $("#licenceEventsDialog").shouldBe(visible);
    $$(".licence-messages li").shouldHave(size(1));
    $("#licenceEventForm\\:confirmAllLicenceEvents").click();

    $("#tasksButtonLicenceDetail").shouldBe(visible);
    $("#licenceEventsDialog").shouldNotBe(visible);
    $("#tasksButtonLicenceEvents").shouldNotBe(exist);
    $(".licence-notification").shouldNotBe(exist);
  }

  @Test
  void checkJavaInfo() {
    $("#tasksButtonJavaDetail").shouldBe(visible).click();
    $("#javaDetailDialog").shouldBe(visible);
    new Table(By.id("javaInfoForm:javaJVMInfoTable")).firstColumnShouldBe(sizeGreaterThan(0));
    new Table(By.id("javaInfoForm:javaPropertiesInfoTable")).firstColumnShouldBe(sizeGreaterThan(0));
  }

  @Test
  void sendTestMailInvalidInputs() {
    openSendMailModal();
    $("#sendTestMailForm\\:sendToInput").clear();
    $("#sendTestMailForm\\:subjectInput").clear();
    $("#sendTestMailForm\\:sendTestMailBtn").click();
    $("#sendTestMailForm\\:sendToInput").shouldHave(cssClass("ui-state-error"));
    $("#sendTestMailForm\\:subjectInput").shouldHave(cssClass("ui-state-error"));
  }

  @Test
  void sendTestMailError() {
    openSendMailModal();
    $("#sendTestMailForm\\:sendToInput").sendKeys("test@example.com");
    $("#sendTestMailForm\\:sendTestMailBtn").click();
    $("#msgs_container").shouldBe(visible);
    $("#msgs_container").shouldHave(Condition.text("Error while sending test mail"));
  }

  private void openSendMailModal() {
    $("#openTestMailBtn").shouldBe(visible).click();
    $("#sendTestMailModal").shouldBe(visible);
  }

}
