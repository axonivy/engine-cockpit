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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WebTestSSL {

  private static final String ENABLE_INSECURE_SSL = "sslClientform:enableInsecureSSL";

  private static final String TRUST_STORE_ALGORITHM = "sslClientform:trustStoreAlgorithm";
  private static final String TRUST_STORE_TYPE = "sslClientform:trustStoreType";
  private static final String TRUST_STORE_PROVIDER = "sslClientform:trustStoreProvider";
  private static final String TRUST_STORE_PASSWORD = "sslClientform:trustStorePassword";
  private static final String TRUST_STORE_FILE = "sslClientform:trustStoreFile";

  private static final String USE_CUSTOM_KEY_STORE = "sslClientformKey:useCustomKeyStore";

  private static final String KEY_STORE_PASSWORD = "sslClientformKey:keyStorePassword";
  private static final String KEY_PASSWORD = "sslClientformKey:keyPassword";
  private static final String KEY_STORE_FILE = "sslClientformKey:keyStoreFile";
  private static final String KEY_STORE_ALGORITHM = "sslClientformKey:keyStoreAlgorithm";
  private static final String KEY_STORE_TYPE = "sslClientformKey:keyStoreType";
  private static final String KEY_STORE_PROVIDER = "sslClientformKey:keyStoreProvider";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSSL();
  }

  @Test
  void inputField() {
    var propertyFile = $(By.id(TRUST_STORE_FILE));
    propertyFile.clear();
    propertyFile.sendKeys("invalidFile");

    var propertyPassword = $(By.id(TRUST_STORE_PASSWORD));
    propertyPassword.clear();
    propertyPassword.sendKeys("invalidPassword");

    saveTrustStore();
    successTrustStore();
    Navigation.toSSL();

    propertyFile.shouldHave(exactValue("invalidFile"));
    propertyPassword.shouldHave(exactValue(""));
  }

  @Test
  void trustStoreDropdowns() {
    var propertyProvider = PrimeUi.selectOne(By.id(TRUST_STORE_PROVIDER));
    propertyProvider.selectItemByLabel("SUN");

    var propertyType = PrimeUi.selectOne(By.id(TRUST_STORE_TYPE));
    propertyType.selectItemByLabel("DKS");

    var propertyAlgorithm = PrimeUi.selectOne(By.id(TRUST_STORE_ALGORITHM));
    propertyAlgorithm.selectItemByLabel("PKIX");

    saveTrustStore();
    successTrustStore();
    Navigation.toSSL();

    $(By.id(TRUST_STORE_PROVIDER)).shouldHave(text("SUN"));
    $(By.id(TRUST_STORE_TYPE)).shouldHave(text("DKS"));
    $(By.id(TRUST_STORE_ALGORITHM)).shouldHave(text("PKIX"));
  }

  @Test
  void keyStoreInputFields() {
    PrimeUi.selectBooleanCheckbox(By.id(USE_CUSTOM_KEY_STORE)).setChecked();

    var propertyFile = $(By.id(KEY_STORE_FILE));
    propertyFile.clear();
    propertyFile.sendKeys("invalidFile");

    var propertyStorePassword = $(By.id(KEY_STORE_PASSWORD));
    propertyStorePassword.clear();
    propertyStorePassword.sendKeys("invalidStorePassword");

    var propertyPassword = $(By.id(KEY_PASSWORD));
    propertyPassword.clear();
    propertyPassword.sendKeys("invalidPassword");

    saveKeyStore();
    successKeyStore();
    Navigation.toSSL();

    propertyFile.shouldHave(exactValue("invalidFile"));
    propertyStorePassword.shouldNotHave(exactValue("invalidStorePassword"));
    propertyPassword.shouldHave(exactValue(""));
  }

  @Test
  void enableInsecureSSL() {
    PrimeUi.selectBooleanCheckbox(By.id(ENABLE_INSECURE_SSL))
    .shouldBeChecked(false);
    PrimeUi.selectBooleanCheckbox(By.id(ENABLE_INSECURE_SSL)).setChecked();

    saveTrustStore();
    successTrustStore();
    Navigation.toSSL();

    PrimeUi.selectBooleanCheckbox(By.id(ENABLE_INSECURE_SSL))
    .shouldBeChecked(true);
  }

  @Test
  void keyStoreDropdowns() {
    PrimeUi.selectBooleanCheckbox(By.id(USE_CUSTOM_KEY_STORE)).setChecked();

    var propertyProvider = PrimeUi.selectOne(By.id(KEY_STORE_PROVIDER));
    propertyProvider.selectItemByLabel("SUN");

    var propertyType = PrimeUi.selectOne(By.id(KEY_STORE_TYPE));
    propertyType.selectItemByLabel("DKS");

    var propertyAlgorithm = PrimeUi.selectOne(By.id(KEY_STORE_ALGORITHM));
    propertyAlgorithm.selectItemByLabel("SunX509");

    saveKeyStore();
    successKeyStore();
    Navigation.toSSL();

    $(By.id(KEY_STORE_PROVIDER)).shouldHave(text("SUN"));
    $(By.id(KEY_STORE_TYPE)).shouldHave(text("DKS"));
    $(By.id(KEY_STORE_ALGORITHM)).shouldHave(text("SunX509"));
  }

  @Test
  void useCustomKeyStore() {
    PrimeUi.selectBooleanCheckbox(By.id(USE_CUSTOM_KEY_STORE))
    .shouldBeChecked(false);
    $(By.id(KEY_STORE_FILE)).shouldHave(cssClass("ui-state-disabled"));
    PrimeUi.selectBooleanCheckbox(By.id(USE_CUSTOM_KEY_STORE)).setChecked();
    PrimeUi.selectBooleanCheckbox(By.id(USE_CUSTOM_KEY_STORE))
    .shouldBeChecked(true);
    $(By.id(KEY_STORE_FILE)).shouldNotHave(cssClass("ui-state-disabled"));
  }

  @Test
  @Order(1)
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
  @Order(2)
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

  @AfterAll
  static void cleanUpTrustStore() {
    var file = $(By.id(TRUST_STORE_FILE));
    var password = $(By.id(TRUST_STORE_PASSWORD));
    var provider = PrimeUi.selectOne(By.id(TRUST_STORE_PROVIDER));
    var type = PrimeUi.selectOne(By.id(TRUST_STORE_TYPE));
    var algorithm = PrimeUi.selectOne(By.id(TRUST_STORE_ALGORITHM));

    file.clear();
    file.sendKeys("configuration/truststore.p12");
    password.clear();
    password.sendKeys("changeit");
    provider.selectItemByLabel("");
    type.selectItemByLabel("PKCS12");
    algorithm.selectItemByLabel("PKIX");

    PrimeUi.selectBooleanCheckbox(By.id(ENABLE_INSECURE_SSL)).removeChecked();
    saveTrustStore();

    saveTrustStore();
  }

  @AfterAll
  static void cleanUpKeyStore() {

    PrimeUi.selectBooleanCheckbox(By.id(USE_CUSTOM_KEY_STORE)).setChecked();

    var file = $(By.id(KEY_STORE_FILE));
    var storePassword = $(By.id(KEY_STORE_PASSWORD));
    var password = $(By.id(KEY_PASSWORD));
    var provider = PrimeUi.selectOne(By.id(KEY_STORE_PROVIDER));
    var type = PrimeUi.selectOne(By.id(KEY_STORE_TYPE));
    var algorithm = PrimeUi.selectOne(By.id(KEY_STORE_ALGORITHM));

    file.clear();
    file.sendKeys("configuration/keystore.p12");
    storePassword.clear();
    storePassword.sendKeys("changeit");
    password.clear();
    password.sendKeys("changeit");
    provider.selectItemByLabel("");
    type.selectItemByLabel("PKCS12");
    algorithm.selectItemByLabel("SunX509");

    PrimeUi.selectBooleanCheckbox(By.id(USE_CUSTOM_KEY_STORE)).setChecked();

    saveKeyStore();
  }

  private static void saveTrustStore() {
    $(By.id("sslClientform:save")).shouldBe(visible).click();
  }

  private static void saveKeyStore() {
    $(By.id("sslClientformKey:save")).shouldBe(visible).click();
  }

  private void successTrustStore() {
    $(By.id("sslClientform:sslTruststoreSaveSuccess_container")).shouldHave(text("Trust Store configurations saved"));
  }

  private void successKeyStore() {
    $(By.id("sslClientformKey:sslKeystoreSaveSuccess_container")).shouldHave(text("Key Store configurations saved"));
  }
}
