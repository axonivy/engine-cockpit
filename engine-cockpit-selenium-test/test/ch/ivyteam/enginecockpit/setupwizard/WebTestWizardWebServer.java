package ch.ivyteam.enginecockpit.setupwizard;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;

import ch.ivyteam.enginecockpit.WebTestBase;

public class WebTestWizardWebServer extends WebTestBase
{
  
  @AfterEach
  void cleanup()
  {
    resetConfig(driver);
  }
  
  @Test
  void testWebServerStep()
  {
    navigateToWebServerWizardStep();
    
    PrimeUi primeUi = new PrimeUi(driver);
    SelectBooleanCheckbox httpEnable = primeUi.selectBooleanCheckbox(By.id("webserverForm:httpEnabledCheckbox"));
    SelectBooleanCheckbox httpsEnable = primeUi.selectBooleanCheckbox(By.id("webserverForm:httpsEnabledCheckbox"));
    SelectBooleanCheckbox ajpEnable = primeUi.selectBooleanCheckbox(By.id("webserverForm:ajpEnabledCheckbox"));
    assertConnector(httpEnable, "httpPortInput", true, "8080");
    assertConnector(httpsEnable, "httpsPortInput", true, "8443");
    assertConnector(ajpEnable, "ajpPortInput", false, "8009");
    
    setConnector(httpEnable, "httpPortInput", false, "8081", "HTTP");
    setConnector(httpsEnable, "httpsPortInput", false, "8444", "HTTPS");
    setConnector(ajpEnable, "ajpPortInput", true, "8010", "AJP");
    
    driver.navigate().refresh();
    assertConnector(httpEnable, "httpPortInput", false, "8081");
    assertConnector(httpsEnable, "httpsPortInput", false, "8444");
    assertConnector(ajpEnable, "ajpPortInput", true, "8010");
    
    httpEnable.removeChecked();
    webAssertThat(() -> assertThat(httpEnable.isChecked()).isFalse());
    
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
    webAssertThat(() -> assertThat(checkbox.isChecked()).isEqualTo(enabled));
    assertGrowl("Connector." + growlMessage + ".Enabled");

    driver.findElementById("webserverForm:" + input + "_input").clear();
    driver.findElementById("webserverForm:" + input + "_input").sendKeys(value);
    webAssertThat(() -> assertThat(driver.findElementById("webserverForm:" + input + "_input").getAttribute("value"))
            .isEqualTo(value));
    assertGrowl("Connector." + growlMessage + ".Port");
  }

  private void assertGrowl(String message)
  {
    webAssertThat(() -> assertThat(driver.findElementByClassName("ui-growl-title").getText()).contains(message));
  }

  private void assertConnector(SelectBooleanCheckbox checkbox, String input, boolean enabled, String value)
  {
    webAssertThat(() -> assertThat(checkbox.isChecked()).isEqualTo(enabled));
    webAssertThat(() -> assertThat(driver.findElementById("webserverForm:" + input + "_input").getAttribute("value"))
            .isEqualTo(value));
  }

  private void navigateToWebServerWizardStep()
  {
    login("setup.xhtml");
    WebTestWizardLicence.skipLicStep(driver);
    WebTestWizardAdmins.skipAdminStep(driver);
    saveScreenshot("webserver");
    webAssertThat(() -> assertThat(driver.findElementByCssSelector("#wizardSteps li.ui-state-highlight").getText())
            .contains("Web Server"));
  }

  public static void skipWebserverStep(RemoteWebDriver driver)
  {
    driver.findElementById("wsNextStep").click();
  }
}
