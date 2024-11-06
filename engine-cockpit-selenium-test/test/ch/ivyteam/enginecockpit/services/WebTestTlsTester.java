package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

class WebTestTlsTester {

  private static final String RESTCLIENT_NAME = "test-rest";

  @BeforeEach
  void beforeEach() {
    login();
    Selenide.webdriver().driver().getWebDriver().manage().window().maximize();
    Navigation.toRestClients();
    Tab.APP.switchToDefault();
    Navigation.toRestClientDetail(RESTCLIENT_NAME);
  }

  @Test
  void TestRestConnection() {
    $(By.id("restClientConfigurationForm:testRestBtn")).click();
    $(By.id("connResult:connTestForm:testConnectionBtn")).click();
    $(By.id("connResult:connTestForm:resultConnect")).shouldHave(text("error"));
    $(By.id("connResult:connTestForm:closeConTesterDialog")).click();
  }

  @Test
  void TestTLSConnection() {
    $(By.id("restClientConfigurationForm:url")).clear();
    $(By.id("restClientConfigurationForm:url")).setValue("https://test-webservices.ivyteam.io:8090/api/v3");
    $(By.id("restClientConfigurationForm:saveRestConfig")).click();
    $(By.id("restClientConfigurationForm:testRestBtn")).click();
    $(By.id("connResult:connTestForm:testTlsConectionBtn")).click();
     try {
       $(By.id("connResult:connTestForm:resultTLS")).shouldHave(text("Connect, with Ivy SSLContext "));
    } finally {
      $(By.id("connResult:connTestForm:closeConTesterDialog")).click();
      $(By.id("restClientConfigurationForm:url")).setValue("http://test-webservices.ivyteam.io:8090/api/v3");
      $(By.id("restClientConfigurationForm:saveRestConfig")).click();
    }
  }

  @Test
  void TestAddToTruststore() {
    $(By.id("restClientConfigurationForm:url")).clear();
    $(By.id("restClientConfigurationForm:url")).setValue("https://test-webservices.ivyteam.io:8443");
    $(By.id("restClientConfigurationForm:saveRestConfig")).click();
    $(By.id("restClientConfigurationForm:testRestBtn")).click();
    $(By.id("connResult:connectionTestModel")).shouldNotHave(text("Missing Certs"));
    $(By.id("connResult:connTestForm:testTlsConectionBtn")).click();
    $(By.id("connResult:connTestForm:resultTLS")).shouldHave(text("Connect, with Ivy SSLContext "));
    try {
      $(By.id("connResult:connTestForm:missing:missingCert:0:subject")).shouldHave(text("CN=test-webservices.ivyteam.io, OU=ivyTeam, O=AXON Ivy AG, L=Zug, ST=Zug, C=CH"));
      $(By.id("connResult:connTestForm:missing:missingCert:0:add")).click();
      $(By.id("connResult:connTestForm:testTlsConectionBtn")).click();
      $(By.id("connResult:connectionTestModel")).shouldNotHave(text("CN=test-webservices.ivyteam.io, OU=ivyTeam, O=AXON Ivy AG, L=Zug, ST=Zug, C=CH"));
    } finally {
      $(By.id("connResult:connTestForm:closeConTesterDialog")).click();
      Navigation.toSSL();
      var table = new Table(By.id("sslTrustTable:storeTable:storeCertificates"));
      table.firstColumnShouldBe(texts("ivy1"));
      table.clickButtonForEntry("ivy1", "delete");
      Navigation.toRestClientDetail(RESTCLIENT_NAME);
      $(By.id("restClientConfigurationForm:url")).setValue("http://test-webservices.ivyteam.io:8090/api/v3");
      $(By.id("restClientConfigurationForm:saveRestConfig")).click();
    }
  }

}
