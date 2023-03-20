package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.resetLicence;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestRenewLicence {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toLicence();
    File file = new File(System.getProperty("user.dir") + "/resources/test.lic");
    String path = file.getAbsolutePath();
    $(By.id("licence:fileUploadForm:licenceUpload_input")).sendKeys(path);
    $(".ui-growl-message").shouldHave(text("Successfully uploaded licence"));
    $(".ui-growl-item").hover();
    $(".ui-growl-message").hover();
    $(".ui-growl-title").hover();
    $(".ui-growl-icon-close").shouldBe(visible).click();
    var table = new Table(By.id("licence:fileUploadForm:licenceInfoTable"));
    table.firstColumnShouldBe(size(13));
    table.valueForEntryShould("licence.type", 2, text("Standard Edition"));
  }

  @AfterEach
  void afterEach() {
    resetLicence();
  }

  @Test
  public void testRenewRequest() {
    sendRenew("webTest@renewLicence.axonivy.test");
    $(".ui-growl-message").shouldHave(text("This is for testing"));
    $("#renewLicence\\:renewLicence").shouldNotBe(visible);
  }

  @Test
  public void testRenewRequestNoOrInvalidMail() {
    sendRenew("");
    $("#renewLicence\\:form\\:emailInputMessage").shouldBe(exactText("Please put your mail"));
    $("#renewLicence\\:form\\:cancelRenewBtn").click();
    $("#renewLicence\\:renewLicence").shouldNotBe(visible);

    sendRenew("invalid");
    $(".ui-growl-message").shouldHave(text("Your email address is not valid"));
  }

  private void sendRenew(String mailTo) {
    $("#renewForm\\:tasksButtonLicenceRenew").click();
    $("#renewLicence\\:renewLicence").shouldBe(visible);
    $("#renewLicence\\:form\\:emailInput").sendKeys(mailTo);
    $("#renewLicence\\:form\\:renewBtn").click();
  }

}
