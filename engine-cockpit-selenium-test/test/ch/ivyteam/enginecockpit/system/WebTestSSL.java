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
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestSSL {

  private interface Key {
    String STORE_PASSWORD = "sslClientformKey:keyStorePassword";
    String PASSWORD = "sslClientformKey:keyPassword";
    String FILE = "sslClientformKey:keyStoreFile";
    String ALGORITHM = "sslClientformKey:keyStoreAlgorithm";
    String TYPE = "sslClientformKey:keyStoreType";
    String PROVIDER = "sslClientformKey:keyStoreProvider";
    String USE_CUSTOM = "sslClientformKey:useCustomKeyStore";
  }

  private interface Trust {
    String ENABLE_INSECURE_SSL = "sslClientform:enableInsecureSSL";
    String ALGORITHM = "sslClientform:trustStoreAlgorithm";
    String TYPE = "sslClientform:trustStoreType";
    String PROVIDER = "sslClientform:trustStoreProvider";
    String PASSWORD = "sslClientform:trustStorePassword";
    String FILE = "sslClientform:trustStoreFile";
  }

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSSL();
  }

  @Test
  void useCustomKeyStore() {
    var useCustom = PrimeUi.selectBooleanCheckbox(By.id(Key.USE_CUSTOM));
    var propertyFile = $(By.id(Key.FILE));
    var propertyStorePassword = $(By.id(Key.STORE_PASSWORD));
    var propertyPassword = $(By.id(Key.PASSWORD));
    var propertyProvider = PrimeUi.selectOne(By.id(Key.PROVIDER));
    var propertyType = PrimeUi.selectOne(By.id(Key.TYPE));
    var propertyAlgorithm = PrimeUi.selectOne(By.id(Key.ALGORITHM));

    useCustom.shouldBeChecked(false);
    propertyFile.shouldHave(cssClass("ui-state-disabled"));

    useCustom.setChecked();
    propertyFile.shouldNotHave(cssClass("ui-state-disabled" ));
    propertyStorePassword.clear();
    propertyStorePassword.sendKeys("invalidPassword");
    saveKeyStore();
    $(By.id("sslKeyTable:storeTable:certificateLoadError")).shouldHave(text("Failed to load store configuration/keystore.p12"));

    propertyFile.clear();
    propertyFile.sendKeys("invalidFile");
    propertyStorePassword.clear();
    propertyStorePassword.sendKeys("invalidStorePassword");
    propertyProvider.selectItemByLabel("SUN");
    propertyType.selectItemByLabel("DKS");
    propertyAlgorithm.selectItemByLabel("SunX509");
    saveKeyStore();
    Selenide.refresh();
    $(By.id("sslKeyTable:storeTable:certificateLoadError")).shouldHave(text("Failed to load store invalidFile"));
    useCustom.shouldBeChecked(true);
    propertyFile.shouldHave(exactValue("invalidFile"));
    propertyStorePassword.shouldNotHave(exactValue("invalidStorePassword"));
    propertyPassword.shouldHave(exactValue(""));
    propertyProvider.selectedItemShould(text("SUN"));
    propertyType.selectedItemShould(text("DKS"));
    propertyAlgorithm.selectedItemShould(text("SunX509"));

    cleanUpCustomKeyStore();
  }

  @Test
  void uploadStoreToKeyStore() throws IOException {
    try {
      var table = new Table(By.id("sslKeyTable:storeTable:storeCertificates"));
      table.firstColumnShouldBe(texts("ivy"));
      var createTempFile = Files.createTempFile("client", ".p12");
      try (var is = WebTestSSL.class.getResourceAsStream("client.p12")) {
        Files.copy(is, createTempFile, StandardCopyOption.REPLACE_EXISTING);
      }
      $(By.id("sslKeyTable:storeTable:certUpload_input")).sendKeys(createTempFile.toString());
      $(By.id("sslKeyTable:storeTable:keyPassword")).sendKeys("password");
      $(By.id("sslKeyTable:storeTable:storePassword")).sendKeys("test");
      $(By.id("sslKeyTable:storeTable:savePassword")).click();
      table.firstColumnShouldBe(texts("ivy", "test-client"));
      table.clickButtonForEntry("test-client", "delete");
      $(By.id("sslKeyTable:storeTable:deleteCertDialog")).shouldBe(visible);
      $(By.id("sslKeyTable:storeTable:deleteYesBtn")).shouldBe(visible).click();
      table.firstColumnShouldBe(texts("ivy"));
    } finally {
      cleanUpCustomKeyStore();
    }
  }

  @Test
  void deleteKeyStoreCert() throws IOException {
    var table = new Table(By.id("sslKeyTable:storeTable:storeCertificates"));
    table.firstColumnShouldBe(texts("ivy"));
    var createTempFile = Files.createTempFile("jiraaxonivycom", ".crt");
    try (var is = WebTestSSL.class.getResourceAsStream("jiraaxonivycom.crt")) {
      Files.copy(is, createTempFile, StandardCopyOption.REPLACE_EXISTING);
    }
    $(By.id("sslKeyTable:storeTable:certUpload_input")).sendKeys(createTempFile.toString());
    table.firstColumnShouldBe(texts("ivy", "ivy1"));
    table.clickButtonForEntry("ivy1", "delete");
    $(By.id("sslKeyTable:storeTable:deleteCertDialog")).shouldBe(visible);
    $(By.id("sslKeyTable:storeTable:deleteYesBtn")).shouldBe(visible).click();
    table.firstColumnShouldBe(texts("ivy"));
  }

  void cleanUpCustomKeyStore() {
    var useCustom = PrimeUi.selectBooleanCheckbox(By.id(Key.USE_CUSTOM));

    var file = $(By.id(Key.FILE));
    var storePassword = $(By.id(Key.STORE_PASSWORD));
    var password = $(By.id(Key.PASSWORD));
    var provider = PrimeUi.selectOne(By.id(Key.PROVIDER));
    var type = PrimeUi.selectOne(By.id(Key.TYPE));
    var algorithm = PrimeUi.selectOne(By.id(Key.ALGORITHM));

    useCustom.setChecked();
    file.clear();
    file.sendKeys("configuration/keystore.p12");
    storePassword.clear();
    storePassword.sendKeys("changeit");
    password.clear();
    password.sendKeys("changeit");
    provider.selectItemByLabel("");
    type.selectItemByLabel("PKCS12");
    algorithm.selectItemByLabel("SunX509");
    saveKeyStore();
    useCustom.removeChecked();
    saveKeyStore();
  }

  private void saveKeyStore() {
    $(By.id("sslClientformKey:save")).shouldBe(visible).click();
    $(By.id("sslClientformKey:sslKeystoreSaveSuccess_container")).shouldHave(text("Key Store configurations saved"));
  }

  @Test
  void changeTrustStore() {
    var insecureSsl = PrimeUi.selectBooleanCheckbox(By.id(Trust.ENABLE_INSECURE_SSL));
    var propertyProvider = PrimeUi.selectOne(By.id(Trust.PROVIDER));
    var propertyType = PrimeUi.selectOne(By.id(Trust.TYPE));
    var propertyAlgorithm = PrimeUi.selectOne(By.id(Trust.ALGORITHM));
    var propertyFile = $(By.id(Trust.FILE));
    var propertyPassword = $(By.id(Trust.PASSWORD));

    propertyPassword.clear();
    propertyPassword.sendKeys("invalidPassword");
    saveTrustStore();
    $(By.id("sslTrustTable:storeTable:certificateLoadError")).shouldHave(text("Failed to load store configuration/truststore.p12"));

    insecureSsl.shouldBeChecked(false);
    insecureSsl.setChecked();
    propertyProvider.selectItemByLabel("SUN");
    propertyType.selectItemByLabel("DKS");
    propertyAlgorithm.selectItemByLabel("PKIX");
    propertyFile.clear();
    propertyFile.sendKeys("invalidFile");
    saveTrustStore();
    Selenide.refresh();
    $(By.id("sslTrustTable:storeTable:certificateLoadError")).shouldHave(text("Failed to load store invalidFile"));
    insecureSsl.shouldBeChecked(true);
    $(By.id(Trust.PROVIDER)).shouldHave(text("SUN"));
    $(By.id(Trust.TYPE)).shouldHave(text("DKS"));
    $(By.id(Trust.ALGORITHM)).shouldHave(text("PKIX"));
    propertyFile.shouldHave(exactValue("invalidFile"));
    propertyPassword.shouldHave(exactValue(""));

    cleanUpTrustStore();
  }

  @Test
  void deleteTrustStoreCert() throws IOException {
    var table = new Table(By.id("sslTrustTable:storeTable:storeCertificates"));
    table.firstColumnShouldBe(empty);
    var createTempFile = Files.createTempFile("jiraaxonivycom", ".crt");
    try (var is = WebTestSSL.class.getResourceAsStream("jiraaxonivycom.crt")) {
      Files.copy(is, createTempFile, StandardCopyOption.REPLACE_EXISTING);
    }
    $(By.id("sslTrustTable:storeTable:certUpload_input")).sendKeys(createTempFile.toString());
    table.firstColumnShouldBe(texts("ivy"));
    table.clickButtonForEntry("ivy", "delete");
    $(By.id("sslTrustTable:storeTable:deleteCertDialog")).shouldBe(visible);
    $(By.id("sslTrustTable:storeTable:deleteYesBtn")).shouldBe(visible).click();
    table.firstColumnShouldBe(empty);
  }

  void cleanUpTrustStore() {
    var file = $(By.id(Trust.FILE));
    var password = $(By.id(Trust.PASSWORD));
    var provider = PrimeUi.selectOne(By.id(Trust.PROVIDER));
    var type = PrimeUi.selectOne(By.id(Trust.TYPE));
    var algorithm = PrimeUi.selectOne(By.id(Trust.ALGORITHM));

    file.clear();
    file.sendKeys("configuration/truststore.p12");
    password.clear();
    password.sendKeys("changeit");
    provider.selectItemByLabel("");
    type.selectItemByLabel("PKCS12");
    algorithm.selectItemByLabel("PKIX");

    PrimeUi.selectBooleanCheckbox(By.id(Trust.ENABLE_INSECURE_SSL)).removeChecked();
    saveTrustStore();
  }

  private void saveTrustStore() {
    $(By.id("sslClientform:save")).shouldBe(visible).click();
    $(By.id("sslClientform:sslTruststoreSaveSuccess_container")).shouldHave(text("Trust Store configurations saved"));
  }
}
