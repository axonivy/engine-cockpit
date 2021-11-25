package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.createLicenceEvents;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
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
  void testDashboardContent() {
    EngineCockpitUtil.createRunningCase();
    login();
    var sessions = $(".overview-box-count", 0).shouldBe(visible).text();
    assertThat(Integer.parseInt(sessions)).isGreaterThan(0);
    var users = $(".overview-box-count", 1).shouldBe(visible).text();
    assertThat(Integer.parseInt(users)).isBetween(5, 10);
    var cases = $(".overview-box-count", 2).shouldBe(visible).text();
    assertThat(Integer.parseInt(cases)).isGreaterThan(0);
    var apps = $(".overview-box-count", 3).shouldBe(visible).text();
    assertThat(Integer.parseInt(apps)).isEqualTo(3);
    $$(".ui-panel").shouldHave(size(5));
    EngineCockpitUtil.destroyRunningCase();
  }

  @Test
  void checkLicenceInfo() {
    $("#tasksButtonLicenceDetail").shouldBe(visible).click();
    $("h1").shouldHave(text("Licence"));
    $("#licence\\:licWarnMessage").shouldHave(text("Please upload a valid licence"));
  }

  @Test
  void checkLicenceEvents() {
    $("#tasksButtonLicenceEvents").shouldBe(visible);
    $$(".licence-notification > a > span").first().shouldBe(exactText("2"));

    $("#tasksButtonLicenceEvents").click();
    $("#licenceEventsDialog").shouldBe(visible);
    $$("#licenceEventForm\\:licenceEventList li").shouldHave(size(2));
    $("#licenceEventForm\\:licenceEventList\\:0\\:confirmEventBtn").click();
    $$("#licenceEventForm\\:licenceEventList li").shouldHave(size(1));
    $("#licenceEventForm\\:closeLicenceEventsDialog").click();
    $("#licenceEventsDialog").shouldNotBe(visible);

    $(".licence-notification > a").click();
    $("#licenceEventsDialog").shouldBe(visible);
    $$("#licenceEventForm\\:licenceEventList li").shouldHave(size(1));
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
  public void testSendTestMailInvalidInputs() {
    openSendMailModal();
    $("#sendTestMailForm\\:sendToInput").clear();
    $("#sendTestMailForm\\:subjectInput").clear();
    $("#sendTestMailForm\\:sendTestMailBtn").click();
    $("#sendTestMailForm\\:sendToInputMessage").shouldBe(exactText("Value is required"));
    $("#sendTestMailForm\\:subjectInputMessage").shouldBe(exactText("Value is required"));
  }

  @Test
  public void testSendTestMailError() {
    openSendMailModal();
    $("#sendTestMailForm\\:sendToInput").sendKeys("test@example.com");
    $("#sendTestMailForm\\:sendTestMailBtn").click();
    $("#mailConfigForm\\:msgs_container").shouldBe(visible);
    $("#mailConfigForm\\:msgs_container").shouldHave(Condition.text("Error while sending test mail"));
  }

  private void openSendMailModal() {
    $("#mailConfigForm\\:openTestMailBtn").shouldBe(visible).click();
    $("#sendTestMailModal").shouldBe(visible);
  }

}
