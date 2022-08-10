package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestSecurityIdentityProviderAzure {

  @Test
  void edit() {
    login();
    Navigation.toSecuritySystem();
    var securitySystemName = "test-azure";
    createSecuritySystem("Azure Active Directory", securitySystemName);
    Navigation.toSecuritySystemProvider(securitySystemName);
    editProperty();

    Navigation.toSecuritySystemDetail(securitySystemName);
    $("#securitySystemConfigForm\\:deleteSecuritySystem").shouldBe(visible).click();
    $("#securitySystemConfigForm\\:deleteSecuritySystemConfirmDialog").shouldBe(visible);
  }

  private void createSecuritySystem(String providerName, String securitySystemName) {
    $("#form\\:createSecuritySystemBtn").click();
    $("#newSecuritySystemForm\\:newSecuritySystemNameInput").sendKeys(securitySystemName);
    var provider = PrimeUi.selectOne(By.id("newSecuritySystemForm:newSecuritySystemProviderSelect"));
    provider.selectItemByLabel(providerName);
    $("#newSecuritySystemForm\\:saveNewSecuritySystem").click();
  }

  private void editProperty() {
    $("#securityIdentityProviderForm\\:property\\:0\\:property").shouldBe(visible).sendKeys("tenantId");
    $("#securityIdentityProviderForm\\:save").click();
    $("#securityIdentityProviderForm\\:property\\:0\\:property").shouldBe(visible).shouldHave(exactValue("tenantId"));
    $("#securityIdentityProviderForm\\:securityIdentityProviderSaveSuccess_container").shouldHave(text("Successfully saved 'Azure Active Directory'"));
  }
}
