package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.engine.EngineUrl;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.InputNumber;
import com.axonivy.ivy.webtest.primeui.widget.SelectBooleanCheckbox;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestWebServer
{
  
  private static final String WEB_SERVER_FORM = "webServer:webserverForm:";

  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toWebServer();
  }
  
  @Test
  void webServerConnectors()
  {
    assertConnectorSettings();
    resetConnectorsToDefault();
  }
  
  @Test
  void frontEndSettings()
  {
    $("#frontendForm\\:infoMessage").shouldBe(visible);
    assertFrontEndConfig("", "0", "http");
    setFrontEndConfig("frontend", "9443", "https");
    Selenide.refresh();
    assertFrontEndConfig("frontend", "9'443", "https");
    setFrontEndConfig("", "0", "http");
  }

  @Test
  void checkData()
  {
    $("#coreRequestData").shouldHave(text(EngineUrl.base()), 
            text("/faces/view/engine-cockpit/webserver.xhtml"));
    $(By.id("form:requestHeaderTable")).shouldNot(exist);
    $("#form\\:showHeaders").shouldBe(visible).click();
    $("#form\\:showHeaders").shouldNotBe(visible);
    var headerTable = new Table(By.id("form:requestHeaderTable"));
    headerTable.firstColumnShouldBe(sizeGreaterThan(5));
    headerTable.valueForEntryShould("user-agent", 2, text("Firefox"));
  }
  
  @Test
  void liveStats()
  {
    EngineCockpitUtil.assertLiveStats(List.of("Requests", "Errors", "Bytes", "Processing Time"));
  }
  
  private void setFrontEndConfig(String host, String port, String portocol)
  {
    var portInput = PrimeUi.inputNumber(By.id("frontendForm:port"));
    var protocolInput = PrimeUi.selectOne(By.id("frontendForm:protocol"));
    $(By.id("frontendForm:host")).sendKeys(host);
    portInput.setValue(port);
    protocolInput.selectItemByLabel(portocol);
    $(By.id("frontendForm:save")).shouldBe(Condition.visible).click();
    assertGrowl("Frontend configuration changes saved");
  }

  private void assertFrontEndConfig(String host, String port, String portocol)
  {
    var portInput = PrimeUi.inputNumber(By.id("frontendForm:port"));
    var protocolInput = PrimeUi.selectOne(By.id("frontendForm:protocol"));
    $(By.id("frontendForm:host")).shouldBe(exactValue(host));
    portInput.should(exactValue(port));
    protocolInput.selectedItemShould(Condition.exactText(portocol));
  }

  public static void assertConnectorSettings()
  {
    $("#webServer\\:webserverWarnMessage").shouldBe(empty);

    var httpEnable = httpConnector();
    var httpsEnable = httpsConnector();
    var ajpEnable = PrimeUi.selectBooleanCheckbox(By.id(WEB_SERVER_FORM + "ajpEnabledCheckbox"));
    assertConnector(httpEnable, "httpPortInput", true, "8080");
    assertConnector(httpsEnable, "httpsPortInput", false, "8443");
    assertConnector(ajpEnable, "ajpPortInput", false, "8009");

    setConnector(httpEnable, "httpPortInput", false, "8081", "HTTP");
    setConnector(ajpEnable, "ajpPortInput", true, "8010", "AJP");
    $("#webServer\\:webserverWarnMessage").shouldBe(text("Enable at least the HTTP or HTTPS Connector"));
    
    setConnector(httpsEnable, "httpsPortInput", true, "8444", "HTTPS");
    $("#webServer\\:webserverWarnMessage").shouldBe(empty);

    Selenide.refresh();
    assertConnector(httpEnable, "httpPortInput", false, "8081");
    assertConnector(httpsEnable, "httpsPortInput", true, "8444");
    assertConnector(ajpEnable, "ajpPortInput", true, "8010");
  }

  public static void disableHttpConnectors()
  {
    httpConnector().removeChecked();
    httpsConnector().removeChecked();
  }

  private static SelectBooleanCheckbox httpsConnector()
  {
    return PrimeUi.selectBooleanCheckbox(By.id(WEB_SERVER_FORM + "httpsEnabledCheckbox"));
  }

  private static SelectBooleanCheckbox httpConnector()
  {
    return PrimeUi.selectBooleanCheckbox(By.id(WEB_SERVER_FORM + "httpEnabledCheckbox"));
  }

  public static void enableHttpsConnector()
  {
    var httpsEnable = httpsConnector();
    httpsEnable.setChecked();
  }

  public static void resetConnectorsToDefault()
  {
    var httpEnable = httpConnector();
    var httpsEnable = httpsConnector();
    var ajpEnable = PrimeUi.selectBooleanCheckbox(By.id(WEB_SERVER_FORM + "ajpEnabledCheckbox"));
    setConnector(httpEnable, "httpPortInput", true, "8080", "HTTP");
    setConnector(httpsEnable, "httpsPortInput", false, "8443", "HTTPS");
    setConnector(ajpEnable, "ajpPortInput", false, "8009", "AJP");
  }
  
  private static void setConnector(SelectBooleanCheckbox checkbox, String input, boolean enabled, String value, String growlMessage)
  {
    if (enabled)
    {
      checkbox.setChecked();
    }
    else
    {
      checkbox.removeChecked();
    }
    checkbox.shouldBeChecked(enabled);
    assertGrowl("Connector." + growlMessage + ".Enabled");

    InputNumber inputNumber = PrimeUi.inputNumber(By.id(WEB_SERVER_FORM + input));
    inputNumber.setValue(value);
    inputNumber.should(value(value));
    assertGrowl("Connector." + growlMessage + ".Port");
  }

  private static void assertGrowl(String message)
  {
    $(".ui-growl-title").shouldBe(text(message));
  }

  private static void assertConnector(SelectBooleanCheckbox checkbox, String input, boolean enabled, String value)
  {
    $(By.id(WEB_SERVER_FORM + input + "_input")).shouldBe(value(value));
    checkbox.shouldBeChecked(enabled);
  }
  
}
