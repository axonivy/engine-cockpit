package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.waitUntilAjaxIsFinished;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneMenu;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestApplicationDetail
{
  private static final String APP = isDesigner() ? DESIGNER : "test-ad";

  @BeforeEach
  void beforeEach()
  {
    login();
  }
  
  @Test
  void testApplicationDetailDashboardContent()
  {
    Navigation.toApplicationDetail(APP);
    
    $$(".overview-box-content").shouldHave(size(4));
    $$(".ui-panel").shouldHave(size(4));
  }

  @Test
  void testChangeEnvironment()
  {
    Navigation.toApplicationDetail(APP);
    
    $("#appDetailInfoForm\\:activeEnvironmentSelect").shouldBe(visible);
    SelectOneMenu env = PrimeUi.selectOne(By.id("appDetailInfoForm:activeEnvironmentSelect"));
    env.selectItemByLabel("test");
    $("#appDetailInfoForm\\:saveApplicationInformation").click();
    $("#appDetailInfoForm\\:informationSaveSuccess_container").shouldBe(visible);
    
    Selenide.refresh();
    $("#appDetailInfoForm\\:activeEnvironmentSelect").shouldBe(visible);
    assertThat(env.getSelectedItem()).isEqualTo("test");
    
    env.selectItemByLabel("Default");
    $("#appDetailInfoForm\\:saveApplicationInformation").click();
    $("#appDetailInfoForm\\:informationSaveSuccess_container").shouldBe(visible);
  
    assertThat(env.getSelectedItem()).isEqualTo("Default");
  }
  
  @Test
  void testAdSync()
  {
    Navigation.toApplicationDetail("test-ad");
    
    waitUntilAjaxIsFinished();
    $("#appDetailSecurityForm\\:showAdSyncLogBtn").should(exist);
    $("#appDetailSecurityForm\\:synchronizeSecurity").shouldBe(visible, enabled).click();
    $$("#appDetailSecurityForm\\:synchronizeSecurity span").first().shouldHave(cssClass("icon-is-spinning"));
    $$("#appDetailSecurityForm\\:synchronizeSecurity span").first().waitUntil(not(cssClass("icon-is-spinning")), 10000);
    
    $("#appDetailSecurityForm\\:showAdSyncLogBtn").click();
    $$(".ui-panel-titlebar").find(text("usersynch.log")).parent()
            .find(".ui-panel-content").shouldBe(visible);
  }

}
