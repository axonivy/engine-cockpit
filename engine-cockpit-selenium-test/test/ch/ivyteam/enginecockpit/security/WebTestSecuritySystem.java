package ch.ivyteam.enginecockpit.security;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestSecuritySystem extends WebTestBase
{
  @Test
  void testSecuritySystem()
  {
    toSecuritySystem();
    $("h1").shouldBe(text("Security Systems"));
    $$("tbody tr").shouldBe(sizeGreaterThan(0));
  }
  
  @Test
  void testAddNewSecuritySystemInvalid()
  {
    toSecuritySystem();
    $("#card\\:form\\:createSecuritySystemBtn").click();
    $("#card\\:newSecuritySystemModal").shouldBe(visible);
    $("#card\\:newSecuritySystemForm\\:saveNewSecuritySystem").click();
    $("#card\\:newSecuritySystemForm\\:newSecuritySystemNameMessage").shouldBe(text("Value is required"));
  }
  
  @Test
  void testAddAndDeleteSecuritySystem()
  {
    toSecuritySystem();
    $("#card\\:form\\:createSecuritySystemBtn").click();
    $("#card\\:newSecuritySystemModal").shouldBe(visible);
    $("#card\\:newSecuritySystemForm\\:newSecuritySystemNameInput").sendKeys("NewFromTest");
    $("#card\\:newSecuritySystemForm\\:saveNewSecuritySystem").click();
    $("#card\\:newSecuritySystemModal").shouldNotBe(visible);
    $$(".security-name").shouldBe(textsInAnyOrder("NewFromTest", "test-ad", "ivy Security System"));
  
    Navigation.toSecuritySystemDetail("NewFromTest");
    $("#securitySystemConfigForm\\:deleteSecuritySystem").shouldBe(visible);
    
    $("#securitySystemConfigForm\\:deleteSecuritySystem").click();
    $("#securitySystemConfigForm\\:deleteSecuritySystemConfirmDialog").shouldBe(visible);
    
    $("#securitySystemConfigForm\\:deleteSecuritySystemConfirmYesBtn").click();
    assertCurrentUrlEndsWith("securitysystem.xhtml");
    $$(".security-name").shouldBe(textsInAnyOrder("test-ad", "ivy Security System"));
  }
  
  private void toSecuritySystem()
  {
    login();
    Navigation.toSecuritySystem();
  }
}
