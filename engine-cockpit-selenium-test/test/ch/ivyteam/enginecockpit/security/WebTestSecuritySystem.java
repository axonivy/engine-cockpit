package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
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
    $("h1").shouldBe(text("Security Systems"));
    var table = new Table(By.id("card:form:securitySystemTable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    table.valueForEntryShould("test-nd", 3, empty);
    table.valueForEntryShould("test-ad", 3, exactText("test-ad"));
  }

  @Test
  void addNewSecuritySystemInvalid() {
    $("#card\\:form\\:createSecuritySystemBtn").click();
    $("#card\\:newSecuritySystemModal").shouldBe(visible);
    $("#card\\:newSecuritySystemForm\\:saveNewSecuritySystem").click();
    $("#card\\:newSecuritySystemForm\\:newSecuritySystemNameMessage").shouldBe(text("Value is required"));
  }

  @Test
  void addAndDeleteSecuritySystem() {
    $("#card\\:form\\:createSecuritySystemBtn").click();
    $("#card\\:newSecuritySystemModal").shouldBe(visible);
    $("#card\\:newSecuritySystemForm\\:newSecuritySystemNameInput").sendKeys("NewFromTest");
    $("#card\\:newSecuritySystemForm\\:saveNewSecuritySystem").click();
    $("#card\\:newSecuritySystemModal").shouldNotBe(visible);
    $$(".security-name")
            .shouldBe(textsInAnyOrder("NewFromTest", "test-ad", "test-nd", "default"));

    Navigation.toSecuritySystemDetail("NewFromTest");
    $("#securitySystemConfigForm\\:deleteSecuritySystem").shouldBe(visible);

    $("#securitySystemConfigForm\\:deleteSecuritySystem").click();
    $("#securitySystemConfigForm\\:deleteSecuritySystemConfirmDialog").shouldBe(visible);

    $("#securitySystemConfigForm\\:deleteSecuritySystemConfirmYesBtn").click();
    assertCurrentUrlContains("securitysystem.xhtml");
    $$(".security-name").shouldBe(textsInAnyOrder("test-ad", "test-nd", "default"));
  }
}
