package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import com.axonivy.ivy.webtest.IvyWebTest;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestSSL {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSSL();
  }

  @Test
  void StringProperty() {
    var property = $(By.id("sslClientform:trustStoreFile")).shouldBe(visible);
    property.clear();
    property.sendKeys("truststore");
    save();
    success();
    property.shouldHave(exactValue("truststore"));
  }

  @Test
  void StringPassword() {
    var property = $(By.id("sslClientform:trustStorePassword")).shouldBe(visible);
    property.clear();
    property.sendKeys("truststore");
    save();
    success();
    property.shouldHave(exactValue("truststore"));
  }

  private void save() {
    $(By.id("sslClientform:save")).shouldBe(visible).click();
  }

  private void success() {
    $(By.id("sslClientform:sslTruststoreSaveSuccess_container")).shouldHave(text("Trust Store configurations saved"));
  }
}
