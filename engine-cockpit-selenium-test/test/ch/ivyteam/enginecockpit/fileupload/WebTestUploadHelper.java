package ch.ivyteam.enginecockpit.fileupload;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.disableRestServlet;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.enableRestServlet;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestUploadHelper {
  private static final String APP = isDesigner() ? DESIGNER : "test";

  @Test
  void licenceUploadAndDeploymentDisabled() {
    assertEnabled();
    disableRestServlet();
    assertDisabled();
    enableRestServlet();
    assertEnabled();
  }

  private void assertEnabled() {
    login();
    Navigation.toApplications();
    $(By.id("form:tree:0:deployBtn")).shouldBe(visible, enabled);

    Navigation.toApplicationDetail(APP);
    $(By.id("appDetailInfoForm:showDeployment")).shouldBe(visible, enabled);
  }

  private void assertDisabled() {
    login();
    Navigation.toApplications();
    $(By.id("form:tree:0:deployBtn")).shouldBe(visible, not(enabled));

    Navigation.toApplicationDetail(APP);
    $(By.id("appDetailInfoForm:showDeployment")).shouldBe(visible, not(enabled));
  }
}
