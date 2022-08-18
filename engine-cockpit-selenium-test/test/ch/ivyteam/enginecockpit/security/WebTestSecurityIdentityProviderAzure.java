package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest(headless =false)
class WebTestSecurityIdentityProviderAzure {

  private final static String NAME = "test-azure";

  @BeforeEach
  void createSecuritySystemAzure() {
    login();
    Navigation.toSecuritySystem();
    createSecuritySystem("Azure Active Directory", NAME);
    Navigation.toSecuritySystemProvider(NAME);
  }

  @AfterEach
  void deleteSecuritySystemAzure() {
    WebTestSecuritySystem.deleteSecuritySystem(NAME);
  }

  @Test
  void edit_stringProperty() {
    var property = $("#securityIdentityProviderForm\\:property\\:0\\:propertyString").shouldBe(visible);
    property.clear();
    property.sendKeys("tenantId");
    save();
    property.shouldHave(exactValue("tenantId"));
    success();
  }

  @Test
  void edit_passwordProperty() {
    var property = $("#securityIdentityProviderForm\\:property\\:1\\:propertyPassword").shouldBe(visible);
    property.clear();
    property.sendKeys("clientSecret");
    save();
    success();
    Selenide.refresh();
    property.shouldHave(attribute("placeholder", "************"));

    save();
    success();
    property.shouldHave(attribute("placeholder", "************"));

    Selenide.refresh();
    property.shouldHave(attribute("placeholder", "************"));
  }

  private void createSecuritySystem(String providerName, String securitySystemName) {
    $("#form\\:createSecuritySystemBtn").click();
    $("#newSecuritySystemForm\\:newSecuritySystemNameInput").sendKeys(securitySystemName);
    var provider = PrimeUi.selectOne(By.id("newSecuritySystemForm:newSecuritySystemProviderSelect"));
    provider.selectItemByLabel(providerName);
    $("#newSecuritySystemForm\\:saveNewSecuritySystem").click();
  }

  private void save() {
    $("#securityIdentityProviderForm\\:save").shouldBe(visible).click();
  }

  private void success() {
    $("#securityIdentityProviderForm\\:securityIdentityProviderSaveSuccess_container").shouldHave(text("Successfully saved 'Azure Active Directory'"));
  }
}
