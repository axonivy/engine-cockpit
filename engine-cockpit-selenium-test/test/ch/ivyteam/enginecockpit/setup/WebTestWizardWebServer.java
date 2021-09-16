package ch.ivyteam.enginecockpit.setup;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.resetConfig;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.system.WebTestWebServer;

@IvyWebTest
public class WebTestWizardWebServer {

  @BeforeEach
  void beforeEach() {
    WebTestWizard.navigateToStep("Web Server");
  }

  @AfterEach
  void afterEach() {
    resetConfig();
  }

  @Test
  void testWebServerStep() {
    WebTestWizard.activeStepShouldBeOk();
    WebTestWebServer.assertConnectorSettings();
    WebTestWebServer.disableHttpConnectors();
    WebTestWizard.activeStepShouldHaveWarnings();
    WebTestWebServer.enableHttpsConnector();
    WebTestWebServer.resetConnectorsToDefault();
  }

}
