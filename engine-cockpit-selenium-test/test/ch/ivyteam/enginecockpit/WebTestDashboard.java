package ch.ivyteam.enginecockpit;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.Table;

public class WebTestDashboard extends WebTestBase
{
  @Test
  void testDashboardContent()
  {
    toDashboard();
    $$(".overview-box-content").shouldHave(size(4));
    $$(".ui-panel").shouldHave(size(5));
  }
  
  @Test
  void checkLicenceInfo()
  {
    toDashboard();
    $("#tasksButtonLicenceDetail").click();
    $("#licenceDetailDialog").shouldBe(visible);
    new Table(By.id("licenceInfoForm:licenceInfoTable")).firstColumnShouldBe(sizeGreaterThan(0));
  }
  
  @Test
  void checkLicenceEvents()
  {
    createLicenceEvents();
    toDashboard();
    $("#tasksButtonLicenceEvents").shouldBe(visible);
    $$(".topbar-notifications > a > span").first().shouldBe(exactText("2"));
    
    $("#tasksButtonLicenceEvents").click();
    $("#licenceEventsDialog").shouldBe(visible);
    $$("#licenceEventForm\\:licenceEventList li").shouldHave(size(2));
    $("#licenceEventForm\\:licenceEventList\\:0\\:confirmEventBtn").click();
    $$("#licenceEventForm\\:licenceEventList li").shouldHave(size(1));
    $("#licenceEventForm\\:closeLicenceEventsDialog").click();
    $("#licenceEventsDialog").shouldNotBe(visible);
   
    $(".topbar-notifications > a").click();
    $("#licenceEventsDialog").shouldBe(visible);
    $$("#licenceEventForm\\:licenceEventList li").shouldHave(size(1));
    $("#licenceEventForm\\:confirmAllLicenceEvents").click();
    
    $("#tasksButtonLicenceDetail").shouldBe(visible);
    $("#licenceEventsDialog").shouldNotBe(visible);
    $("#tasksButtonLicenceEvents").shouldNotBe(exist);
    $(".topbar-notifications").shouldNotBe(exist);
  }
  
  @Test
  void checkJavaInfo()
  {
    toDashboard();
    $("#tasksButtonJavaDetail").click();
    $("#javaDetailDialog").shouldBe(visible);
    new Table(By.id("javaInfoForm:javaJVMInfoTable")).firstColumnShouldBe(sizeGreaterThan(0));
    new Table(By.id("javaInfoForm:javaPropertiesInfoTable")).firstColumnShouldBe(sizeGreaterThan(0));
  }
  
  @Test
  public void testSendTestMailInvalidInputs()
  {
    toDashboardAndOpenSendMailModal();
    
    $("#sendTestMailForm\\:sendToInput").clear();
    $("#sendTestMailForm\\:subjectInput").clear();
    $("#sendTestMailForm\\:sendTestMailBtn").click();
    $("#sendTestMailForm\\:sendToInputMessage").shouldBe(exactText("Value is required"));
    $("#sendTestMailForm\\:subjectInputMessage").shouldBe(exactText("Value is required"));
  }
  
  @Test
  public void testSendTestMailError()
  {
    toDashboardAndOpenSendMailModal();
    
    $("#sendTestMailForm\\:sendToInput").sendKeys("test@example.com");
    $("#sendTestMailForm\\:sendTestMailBtn").click();
    $("#mailConfigForm\\:msgs_container").shouldBe(visible);
    $("#mailConfigForm\\:msgs_container").shouldHave(Condition.text("Error while sending test mail"));
  }

  private void toDashboardAndOpenSendMailModal()
  {
    toDashboard();
    $("#mailConfigForm\\:openTestMailBtn").click();
    $("#sendTestMailModal").shouldBe(visible);
  }
  
  private void toDashboard()
  {
    login();
  }
}
