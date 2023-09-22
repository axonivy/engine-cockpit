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
import com.axonivy.ivy.webtest.primeui.PrimeUi;
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
    saveTrustStore();
    successTrustStore();
    Navigation.toSSL();
    property.shouldHave(exactValue("truststore"));
  }

  @Test
  void StringPassword() {
    var property = $(By.id("sslClientform:trustStorePassword")).shouldBe(visible);
    property.clear();
    property.sendKeys("truststore");
    saveTrustStore();
    successTrustStore();
    Navigation.toSSL();
    property.shouldNotHave(exactValue("truststore"));
  }

  @Test
  void InputField() {
    var propertyFile = $(By.id("sslClientform:trustStoreFile"));
    propertyFile.clear();
    propertyFile.sendKeys("File");

    var propertyPassword = $(By.id("sslClientform:trustStorePassword"));
    propertyPassword.clear();
    propertyPassword.sendKeys("Password");

    var propertyProvider = $(By.id("sslClientform:trustStoreProvider"));
    propertyProvider.clear();
    propertyProvider.sendKeys("Provider");

    var propertyType = $(By.id("sslClientform:trustStoreType"));
    propertyType.clear();
    propertyType.sendKeys("Type");

    var propertyAlgorithm = $(By.id("sslClientform:trustStoreAlgorithm"));
    propertyAlgorithm.clear();
    propertyAlgorithm.sendKeys("Algorithm");

    var propertyManagerClass = $(By.id("sslClientform:trustManagerClass"));
    propertyManagerClass.clear();
    propertyManagerClass.sendKeys("ManagerClass");

    saveTrustStore();
    successTrustStore();
    Navigation.toSSL();

    propertyFile.shouldHave(exactValue("File"));
    propertyPassword.shouldNotHave(exactValue("Password"));
    propertyProvider.shouldHave(exactValue("Provider"));
    propertyType.shouldHave(exactValue("Type"));
    propertyAlgorithm.shouldHave(exactValue("Algorithm"));
    propertyManagerClass.shouldHave(exactValue("ManagerClass"));
  }

  @Test
  void KeyStoreInputFields() {
    var propertyFile = $(By.id("sslClientformKey:keyStoreFile"));
    propertyFile.clear();
    propertyFile.sendKeys("File");

    var propertyStorePassword = $(By.id("sslClientformKey:keyStorePassword"));
    propertyStorePassword.clear();
    propertyStorePassword.sendKeys("StorePassword");

    var propertyPassword = $(By.id("sslClientformKey:keyPassword"));
    propertyPassword.clear();
    propertyPassword.sendKeys("Password");

    saveKeyStore();
    successKeyStore();
    Navigation.toSSL();

    propertyFile.shouldHave(exactValue("File"));
    propertyStorePassword.shouldNotHave(exactValue("StorePassword"));
    propertyPassword.shouldNotHave(exactValue("Password"));

  }

  @Test
  public void keyStoreDropdowns() {
    var propertyProvider = PrimeUi.selectOne(By.id("sslClientformKey:keyStoreProvider"));
    propertyProvider.selectItemByLabel("BC");

    var propertyType = PrimeUi.selectOne(By.id("sslClientformKey:keyStoreType"));
    propertyType.selectItemByLabel("BKS");

    var propertyAlgorithm = PrimeUi.selectOne(By.id("sslClientformKey:keyStoreAlgorithm"));
    propertyAlgorithm.selectItemByLabel("SunX509");

    $(By.id("sslClientformKey:keyStoreProvider")).shouldHave(text("BC"));
    $(By.id("sslClientformKey:keyStoreType")).shouldHave(text("BKS"));
    $(By.id("sslClientformKey:keyStoreAlgorithm")).shouldHave(text("SunX509"));
  }

  private void saveTrustStore() {
    $(By.id("sslClientform:save")).shouldBe(visible).click();
  }

  private void saveKeyStore() {
    $(By.id("sslClientformKey:save")).shouldBe(visible).click();
  }

  private void successTrustStore() {
    $(By.id("sslClientform:sslTruststoreSaveSuccess_container")).shouldHave(text("Trust Store configurations saved"));
  }

  private void successKeyStore() {
    $(By.id("sslClientformKey:sslKeystoreSaveSuccess_container")).shouldHave(text("Key Store configurations saved"));
  }
}
