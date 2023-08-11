package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.containExactTextsCaseSensitive;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestSecuritySystem {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSecuritySystem();
  }

  @Test
  void securitySystem() {
    $("h2").shouldBe(text("Security Systems"));
    var table = new Table(By.id("form:securitySystemTable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    table.valueForEntryShould("test-nd", 3, empty);
    table.valueForEntryShould("test-ad", 3, exactText("test-ad"));
  }

  @Test
  void addNewSecuritySystemInvalid() {
    $("#form\\:createSecuritySystemBtn").click();
    $("#newSecuritySystemModal").shouldBe(visible);
    $("#newSecuritySystemForm\\:saveNewSecuritySystem").click();
    $("#newSecuritySystemForm\\:newSecuritySystemNameMessage").shouldBe(text("Value is required"));

    $("#newSecuritySystemForm\\:newSecuritySystemNameInput").sendKeys("invalid?!");
    $("#newSecuritySystemForm\\:saveNewSecuritySystem").click();
    $("#newSecuritySystemForm\\:newSecuritySystemNameMessage").shouldBe(empty);
    $("#newSecuritySystemModal").shouldNotBe(visible);
    $("#form\\:msgs_container .ui-growl-message")
            .shouldHave(text("Security Context Name 'invalid?!' must match pattern"));
  }

  @Test
  void addAndDeleteSecuritySystem() {
    $("#form\\:createSecuritySystemBtn").click();
    $("#newSecuritySystemModal").shouldBe(visible);
    $("#newSecuritySystemForm\\:newSecuritySystemNameInput").sendKeys("NewFromTest");
    $("#newSecuritySystemForm\\:saveNewSecuritySystem").click();
    $("#newSecuritySystemModal").shouldNotBe(visible);
    $$(".security-name").shouldBe(containExactTextsCaseSensitive("NewFromTest", "default"));
    $$(".provider-name").shouldBe(textsInAnyOrder("Microsoft Active Directory", "Microsoft Active Directory", "Novell eDirectory", "ivy Security System"));

    int sizeBeforeDelete = $$(".security-name").size();
    deleteSecuritySystem("NewFromTest");
    $$(".security-name").shouldBe(sizeLessThan(sizeBeforeDelete));
  }

  static void deleteSecuritySystem(String securitySystemName) {
    Navigation.toSecuritySystemDetail(securitySystemName);
    $("#securitySystemConfigForm\\:deleteSecuritySystem").shouldBe(visible);

    $("#securitySystemConfigForm\\:deleteSecuritySystem").click();
    $("#securitySystemConfigForm\\:deleteSecuritySystemConfirmDialog").shouldBe(visible);

    $("#securitySystemConfigForm\\:deleteSecuritySystemConfirmYesBtn").click();
    assertCurrentUrlContains("securitysystem.xhtml");
  }
}
