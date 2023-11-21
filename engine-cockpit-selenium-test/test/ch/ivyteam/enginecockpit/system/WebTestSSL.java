package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestSSL {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSSL();
  }

  @Test
  void inputField() {
    var propertyFile = $(By.id("sslClientform:trustStoreFile"));
    propertyFile.clear();
    propertyFile.sendKeys("invalidFile");

    var propertyPassword = $(By.id("sslClientform:trustStorePassword"));
    propertyPassword.clear();
    propertyPassword.sendKeys("invalidPassword");

    try {
      saveTrustStore();
      successTrustStore();
      Navigation.toSSL();

      propertyFile.shouldHave(exactValue("invalidFile"));
      propertyPassword.shouldHave(exactValue(""));
    } finally {
      propertyFile.clear();
      propertyFile.sendKeys("configuration/truststore.p12");
      propertyPassword.clear();
      propertyPassword.sendKeys("changeit");

      saveTrustStore();
      Navigation.toSSL();
    }
  }

  @Test
  void trustStoreDropdowns() {
    var propertyProvider = PrimeUi.selectOne(By.id("sslClientform:trustStoreProvider"));
    propertyProvider.selectItemByLabel("SUN");

    var propertyType = PrimeUi.selectOne(By.id("sslClientform:trustStoreType"));
    propertyType.selectItemByLabel("DKS");

    var propertyAlgorithm = PrimeUi.selectOne(By.id("sslClientform:trustStoreAlgorithm"));
    propertyAlgorithm.selectItemByLabel("PKIX");

    try {
      saveTrustStore();
      successTrustStore();
      Navigation.toSSL();

      $(By.id("sslClientform:trustStoreProvider")).shouldHave(text("SUN"));
      $(By.id("sslClientform:trustStoreType")).shouldHave(text("DKS"));
      $(By.id("sslClientform:trustStoreAlgorithm")).shouldHave(text("PKIX"));
    } finally {
      propertyProvider.selectItemByLabel("");
      propertyType.selectItemByLabel("PKCS12");
      propertyAlgorithm.selectItemByLabel("PKIX");

      saveTrustStore();
      Navigation.toSSL();
    }
  }

  @Test
  void keyStoreInputFields() {
    PrimeUi.selectBooleanCheckbox(By.id("sslClientformKey:useCustomKeyStore")).setChecked();

    var propertyFile = $(By.id("sslClientformKey:keyStoreFile"));
    propertyFile.clear();
    propertyFile.sendKeys("invalidFile");

    var propertyStorePassword = $(By.id("sslClientformKey:keyStorePassword"));
    propertyStorePassword.clear();
    propertyStorePassword.sendKeys("invalidStorePassword");

    var propertyPassword = $(By.id("sslClientformKey:keyPassword"));
    propertyPassword.clear();
    propertyPassword.sendKeys("invalidPassword");

    try {
      saveKeyStore();
      successKeyStore();
      Navigation.toSSL();

      propertyFile.shouldHave(exactValue("invalidFile"));
      propertyStorePassword.shouldNotHave(exactValue("invalidStorePassword"));
      propertyPassword.shouldHave(exactValue(""));
    } finally {
      PrimeUi.selectBooleanCheckbox(By.id("sslClientformKey:useCustomKeyStore")).setChecked();

      propertyFile.clear();
      propertyFile.sendKeys("configuration/keystore.p12");
      propertyStorePassword.clear();
      propertyStorePassword.sendKeys("changeit");
      propertyPassword.clear();
      propertyPassword.sendKeys("changeit");

      saveKeyStore();
    }
  }

  @Test
  void enableInsecureSSL() {
    PrimeUi.selectBooleanCheckbox(By.id("sslClientform:enableInsecureSSL"))
    .shouldBeChecked(false);
    PrimeUi.selectBooleanCheckbox(By.id("sslClientform:enableInsecureSSL")).setChecked();

    saveTrustStore();
    successTrustStore();
    Navigation.toSSL();

    PrimeUi.selectBooleanCheckbox(By.id("sslClientform:enableInsecureSSL"))
    .shouldBeChecked(true);
    PrimeUi.selectBooleanCheckbox(By.id("sslClientform:enableInsecureSSL")).removeChecked();
    saveTrustStore();
  }

  @Test
  void keyStoreDropdowns() {
    PrimeUi.selectBooleanCheckbox(By.id("sslClientformKey:useCustomKeyStore")).setChecked();

    var propertyProvider = PrimeUi.selectOne(By.id("sslClientformKey:keyStoreProvider"));
    propertyProvider.selectItemByLabel("SUN");

    var propertyType = PrimeUi.selectOne(By.id("sslClientformKey:keyStoreType"));
    propertyType.selectItemByLabel("DKS");

    var propertyAlgorithm = PrimeUi.selectOne(By.id("sslClientformKey:keyStoreAlgorithm"));
    propertyAlgorithm.selectItemByLabel("SunX509");

    try {
      saveKeyStore();
      successKeyStore();
      Navigation.toSSL();

      $(By.id("sslClientformKey:keyStoreProvider")).shouldHave(text("SUN"));
      $(By.id("sslClientformKey:keyStoreType")).shouldHave(text("DKS"));
      $(By.id("sslClientformKey:keyStoreAlgorithm")).shouldHave(text("SunX509"));
    } finally {
      PrimeUi.selectBooleanCheckbox(By.id("sslClientformKey:useCustomKeyStore")).setChecked();
      propertyProvider.selectItemByLabel("");
      propertyType.selectItemByLabel("PKCS12");

      saveKeyStore();
    }
  }

  @Test
  void useCustomKeyStore() {
    PrimeUi.selectBooleanCheckbox(By.id("sslClientformKey:useCustomKeyStore"))
    .shouldBeChecked(false);
    $(By.id("sslClientformKey:keyStoreFile")).shouldHave(cssClass("ui-state-disabled"));
    PrimeUi.selectBooleanCheckbox(By.id("sslClientformKey:useCustomKeyStore")).setChecked();
    PrimeUi.selectBooleanCheckbox(By.id("sslClientformKey:useCustomKeyStore"))
    .shouldBeChecked(true);
    $(By.id("sslClientformKey:keyStoreFile")).shouldNotHave(cssClass("ui-state-disabled"));
  }

  @Test
  void deleteCert() throws IOException {
    var table = new Table(By.id("sslTrustTable:storeTable:storeCertificates"));
    table.firstColumnShouldBe(empty);
    var createTempFile = Files.createTempFile("jiraaxonivycom", ".crt");
    try (var is = WebTestSSL.class.getResourceAsStream("jiraaxonivycom.crt")) {
      Files.copy(is, createTempFile, StandardCopyOption.REPLACE_EXISTING);
    }
    $(By.id("sslTrustTable:storeTable:certUpload_input")).sendKeys(createTempFile.toString());
    table.firstColumnShouldBe(texts("ivy1"));
    table.clickButtonForEntry("ivy1", "delete");
    table.firstColumnShouldBe(empty);
  }

  @Test
  void deleteKeyCert() throws IOException {
    var table = new Table(By.id("sslKeyTable:storeTable:storeCertificates"));
    table.firstColumnShouldBe(texts("ivy"));
    var createTempFile = Files.createTempFile("jiraaxonivycom", ".crt");
    try (var is = WebTestSSL.class.getResourceAsStream("jiraaxonivycom.crt")) {
      Files.copy(is, createTempFile, StandardCopyOption.REPLACE_EXISTING);
    }
    $(By.id("sslKeyTable:storeTable:certUpload_input")).sendKeys(createTempFile.toString());
    table.firstColumnShouldBe(texts("ivy", "ivy1"));
    table.clickButtonForEntry("ivy1", "delete");
    table.firstColumnShouldBe(texts("ivy"));
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
