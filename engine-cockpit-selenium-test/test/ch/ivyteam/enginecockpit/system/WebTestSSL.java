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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

  @Nested
  public class KeyStoreTests {

    private interface Key {
      String STORE_PASSWORD = "sslClientformKey:keyStorePassword";
      String PASSWORD = "sslClientformKey:keyPassword";
      String FILE = "sslClientformKey:keyStoreFile";
      String ALGORITHM = "sslClientformKey:keyStoreAlgorithm";
      String TYPE = "sslClientformKey:keyStoreType";
      String PROVIDER = "sslClientformKey:keyStoreProvider";
      String USE_CUSTOM = "sslClientformKey:useCustomKeyStore";
    }

    @Test
    void useCustomKeyStore() {
      PrimeUi.selectBooleanCheckbox(By.id(Key.USE_CUSTOM))
      .shouldBeChecked(false);
      $(By.id(Key.FILE)).shouldHave(cssClass("ui-state-disabled"));
      PrimeUi.selectBooleanCheckbox(By.id(Key.USE_CUSTOM)).setChecked();
      PrimeUi.selectBooleanCheckbox(By.id(Key.USE_CUSTOM))
      .shouldBeChecked(true);
      $(By.id(Key.FILE)).shouldNotHave(cssClass("ui-state-disabled"));
    }

    @Test
    void inputFields() {
      PrimeUi.selectBooleanCheckbox(By.id(Key.USE_CUSTOM)).setChecked();

      var propertyFile = $(By.id(Key.FILE));
      propertyFile.clear();
      propertyFile.sendKeys("invalidFile");

      var propertyStorePassword = $(By.id(Key.STORE_PASSWORD));
      propertyStorePassword.clear();
      propertyStorePassword.sendKeys("invalidStorePassword");

      var propertyPassword = $(By.id(Key.PASSWORD));
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
    void dropdowns() {
      PrimeUi.selectBooleanCheckbox(By.id(Key.USE_CUSTOM)).setChecked();

      var propertyProvider = PrimeUi.selectOne(By.id(Key.PROVIDER));
      propertyProvider.selectItemByLabel("SUN");

      var propertyType = PrimeUi.selectOne(By.id(Key.TYPE));
      propertyType.selectItemByLabel("DKS");

      var propertyAlgorithm = PrimeUi.selectOne(By.id(Key.ALGORITHM));
      propertyAlgorithm.selectItemByLabel("SunX509");

      saveKeyStore();
      successKeyStore();
      Navigation.toSSL();

      $(By.id(Key.PROVIDER)).shouldHave(text("SUN"));
      $(By.id(Key.TYPE)).shouldHave(text("DKS"));
      $(By.id(Key.ALGORITHM)).shouldHave(text("SunX509"));
    }

    @Test
    void deleteCert() throws IOException {
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

    @AfterEach
    void cleanUpKeyStore() {

      PrimeUi.selectBooleanCheckbox(By.id(Key.USE_CUSTOM)).setChecked();

      var file = $(By.id(Key.FILE));
      var storePassword = $(By.id(Key.STORE_PASSWORD));
      var password = $(By.id(Key.PASSWORD));
      var provider = PrimeUi.selectOne(By.id(Key.PROVIDER));
      var type = PrimeUi.selectOne(By.id(Key.TYPE));
      var algorithm = PrimeUi.selectOne(By.id(Key.ALGORITHM));

      file.clear();
      file.sendKeys("configuration/keystore.p12");
      storePassword.clear();
      storePassword.sendKeys("changeit");
      password.clear();
      password.sendKeys("changeit");
      provider.selectItemByLabel("");
      type.selectItemByLabel("PKCS12");
      algorithm.selectItemByLabel("SunX509");

      PrimeUi.selectBooleanCheckbox(By.id(Key.USE_CUSTOM)).setChecked();
      saveKeyStore();
    }

    private void saveKeyStore() {
      $(By.id("sslClientformKey:save")).shouldBe(visible).click();
    }

    private void successKeyStore() {
      $(By.id("sslClientformKey:sslKeystoreSaveSuccess_container")).shouldHave(text("Key Store configurations saved"));
    }
  }

  @Nested
  public class TrustStoreTests {

    private static final String ENABLE_INSECURE_SSL = "sslClientform:enableInsecureSSL";

    private interface Trust {
      String ALGORITHM = "sslClientform:trustStoreAlgorithm";
      String TYPE = "sslClientform:trustStoreType";
      String PROVIDER = "sslClientform:trustStoreProvider";
      String PASSWORD = "sslClientform:trustStorePassword";
      String FILE = "sslClientform:trustStoreFile";
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
    void dropdowns() {
      var propertyProvider = PrimeUi.selectOne(By.id(Trust.PROVIDER));
      propertyProvider.selectItemByLabel("SUN");

      var propertyType = PrimeUi.selectOne(By.id(Trust.TYPE));
      propertyType.selectItemByLabel("DKS");

      var propertyAlgorithm = PrimeUi.selectOne(By.id(Trust.ALGORITHM));
      propertyAlgorithm.selectItemByLabel("PKIX");

      saveTrustStore();
      successTrustStore();
      Navigation.toSSL();

      $(By.id(Trust.PROVIDER)).shouldHave(text("SUN"));
      $(By.id(Trust.TYPE)).shouldHave(text("DKS"));
      $(By.id(Trust.ALGORITHM)).shouldHave(text("PKIX"));
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
    void inputField() {
      var propertyFile = $(By.id(Trust.FILE));
      propertyFile.clear();
      propertyFile.sendKeys("invalidFile");

      var propertyPassword = $(By.id(Trust.PASSWORD));
      propertyPassword.clear();
      propertyPassword.sendKeys("invalidPassword");

      saveTrustStore();
      successTrustStore();
      Navigation.toSSL();

      propertyFile.shouldHave(exactValue("invalidFile"));
      propertyPassword.shouldHave(exactValue(""));
    }

    @AfterEach
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

      PrimeUi.selectBooleanCheckbox(By.id(ENABLE_INSECURE_SSL)).removeChecked();
      saveTrustStore();
    }

    private void successTrustStore() {
      $(By.id("sslClientform:sslTruststoreSaveSuccess_container")).shouldHave(text("Trust Store configurations saved"));
    }

    private void saveTrustStore() {
      $(By.id("sslClientform:save")).shouldBe(visible).click();
    }
  }
}
