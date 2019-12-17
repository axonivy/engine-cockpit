package ch.ivyteam.enginecockpit.setupwizard;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestWizardWebServer extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetConfig();
    driver.quit();
  }
  
  @Test
  void testWebServerStep()
  {
    navigateToWebServerWizardStep();
    
    SelectBooleanCheckbox httpEnable = primeUi.selectBooleanCheckbox(By.id("webserverForm:httpEnabledCheckbox"));
    SelectBooleanCheckbox httpsEnable = primeUi.selectBooleanCheckbox(By.id("webserverForm:httpsEnabledCheckbox"));
    SelectBooleanCheckbox ajpEnable = primeUi.selectBooleanCheckbox(By.id("webserverForm:ajpEnabledCheckbox"));
    assertConnector(httpEnable, "httpPortInput", true, "8080");
    assertConnector(httpsEnable, "httpsPortInput", true, "8443");
    assertConnector(ajpEnable, "ajpPortInput", false, "8009");
    
    setConnector(httpEnable, "httpPortInput", false, "8081", "HTTP");
    setConnector(httpsEnable, "httpsPortInput", false, "8444", "HTTPS");
    setConnector(ajpEnable, "ajpPortInput", true, "8010", "AJP");
    
    Selenide.refresh();
    assertConnector(httpEnable, "httpPortInput", false, "8081");
    assertConnector(httpsEnable, "httpsPortInput", false, "8444");
    assertConnector(ajpEnable, "ajpPortInput", true, "8010");
    
    httpEnable.removeChecked();
    assertThat(httpEnable.isChecked()).isFalse();
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

  private void navigateToWebServerWizardStep()
  {
    login("setup.xhtml");
    WebTestWizardLicence.skipLicStep();
    WebTestWizardAdmins.skipAdminStep();
    $("#wizardSteps li.ui-state-highlight").shouldBe(text("Web Server"));
  }

  public static void skipWebserverStep()
  {
    $("#wsNextStep").click();
  }
}
