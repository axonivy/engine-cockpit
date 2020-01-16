package ch.ivyteam.enginecockpit.setupwizard;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.resetConfig;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.widget.SelectBooleanCheckbox;
import com.codeborne.selenide.Selenide;

public class WebTestWizardWebServer
{
  
  @BeforeEach
  void beforeEach()
  {
    WebTestWizard.navigateToStep("Web Server");
  }
  
  @AfterEach
  void afterEach()
  {
    resetConfig();
  }
  
  @Test
  void testWebServerStep()
  {
    $("#webserverWarnMessage").shouldBe(empty);
    WebTestWizard.activeStepShouldBeOk();

    SelectBooleanCheckbox httpEnable = PrimeUi.selectBooleanCheckbox(By.id("webserverForm:httpEnabledCheckbox"));
    SelectBooleanCheckbox httpsEnable = PrimeUi.selectBooleanCheckbox(By.id("webserverForm:httpsEnabledCheckbox"));
    SelectBooleanCheckbox ajpEnable = PrimeUi.selectBooleanCheckbox(By.id("webserverForm:ajpEnabledCheckbox"));
    assertConnector(httpEnable, "httpPortInput", true, "8080");
    assertConnector(httpsEnable, "httpsPortInput", true, "8443");
    assertConnector(ajpEnable, "ajpPortInput", false, "8009");
    
    setConnector(httpEnable, "httpPortInput", false, "8081", "HTTP");
    setConnector(httpsEnable, "httpsPortInput", false, "8444", "HTTPS");
    setConnector(ajpEnable, "ajpPortInput", true, "8010", "AJP");
    
    $("#webserverWarnMessage").shouldBe(text("Enable at least the HTTP or HTTPS Connector"));
    WebTestWizard.activeStepShouldHaveWarnings();
    
    Selenide.refresh();
    assertConnector(httpEnable, "httpPortInput", false, "8081");
    assertConnector(httpsEnable, "httpsPortInput", false, "8444");
    assertConnector(ajpEnable, "ajpPortInput", true, "8010");
  }
  
  private void setConnector(SelectBooleanCheckbox checkbox, String input, boolean enabled, String value, String growlMessage)
  {
    if (enabled)
    {
      checkbox.setChecked();
    }
    else
    {
      checkbox.removeChecked();
    }
    assertThat(checkbox.isChecked()).isEqualTo(enabled);
    assertGrowl("Connector." + growlMessage + ".Enabled");

    $(By.id("webserverForm:" + input + "_input")).clear();
    $(By.id("webserverForm:" + input + "_input")).sendKeys(value);
    $(By.id("webserverForm:" + input + "_input")).shouldBe(value(value));
    assertGrowl("Connector." + growlMessage + ".Port");
  }

  private void assertGrowl(String message)
  {
    $(".ui-growl-title").shouldBe(text(message));
  }

  private void assertConnector(SelectBooleanCheckbox checkbox, String input, boolean enabled, String value)
  {
    $(By.id("webserverForm:" + input + "_input")).shouldBe(value(value));
    assertThat(checkbox.isChecked()).isEqualTo(enabled);
  }

}
