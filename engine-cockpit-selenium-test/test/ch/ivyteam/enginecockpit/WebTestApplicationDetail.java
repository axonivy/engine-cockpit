package ch.ivyteam.enginecockpit;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectOneMenu;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUrl;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestApplicationDetail extends WebTestBase
{
  private static final String APP = EngineCockpitUrl.isDesignerApp() ? "designer" : "test-ad";

  @Test
  void testApplicationDetailDashboardContent()
  {
    toApplicationDetail(APP);
    
    $$(".overview-box-content").shouldHave(size(4));
    $$(".ui-panel").shouldHave(size(4));
  }

  @Test
  void testChangeEnvironment()
  {
    toApplicationDetail(APP);
    
    $("#appDetailInfoForm\\:activeEnvironmentSelect").shouldBe(visible);
    SelectOneMenu env = primeUi.selectOne(By.id("appDetailInfoForm:activeEnvironmentSelect"));
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
    toApplicationDetail("test-ad");
    
    waitUntilAjaxIsFinished();
    $("#appDetailSecurityForm\\:showAdSyncLogBtn").shouldNotBe(exist);
    $("#appDetailSecurityForm\\:synchronizeSecurity").shouldBe(visible, enabled).click();
    $$("#appDetailSecurityForm\\:synchronizeSecurity span").first().shouldHave(cssClass("fa-spin"));
    $$("#appDetailSecurityForm\\:synchronizeSecurity span").first().waitUntil(not(cssClass("fa-spin")), 10000);
    
    $("#appDetailSecurityForm\\:showAdSyncLogBtn").shouldBe(visible);
  }

  private void toApplicationDetail(String app)
  {
    login();
    Navigation.toApplicationDetail(app);
  }
  
}
