package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlEndsWith;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestSecuritySystem
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toSecuritySystem();
  }
  
  @Test
  void testSecuritySystem()
  {
    $("h1").shouldBe(text("Security Systems"));
    $$("tbody tr").shouldBe(sizeGreaterThan(0));
  }
  
  @Test
  void testAddNewSecuritySystemInvalid()
  {
    $("#card\\:form\\:createSecuritySystemBtn").click();
    $("#card\\:newSecuritySystemModal").shouldBe(visible);
    $("#card\\:newSecuritySystemForm\\:saveNewSecuritySystem").click();
    $("#card\\:newSecuritySystemForm\\:newSecuritySystemNameMessage").shouldBe(text("Value is required"));
  }
  
  @Test
  void testAddAndDeleteSecuritySystem()
  {
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
  
}
