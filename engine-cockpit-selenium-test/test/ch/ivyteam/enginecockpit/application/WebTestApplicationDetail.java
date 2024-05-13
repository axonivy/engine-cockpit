package ch.ivyteam.enginecockpit.application;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.waitUntilAjaxIsFinished;
import static com.axonivy.ivy.webtest.engine.EngineUrl.DESIGNER;
import static com.axonivy.ivy.webtest.engine.EngineUrl.isDesigner;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.href;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneMenu;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestApplicationDetail {

  private static final String APP = isDesigner() ? DESIGNER : "test-ad";

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void applicationDetailDashboardContent() {
    EngineCockpitUtil.createRunningCase();
    login();
    Navigation.toApplicationDetail("test");
    var sessions = $(".overview-box h1", 0).shouldBe(visible).text();
    assertThat(Integer.parseInt(sessions)).isGreaterThan(0);
    var users = $(".overview-box h1", 1).shouldBe(visible).text();
    assertThat(Integer.parseInt(users)).isBetween(2, 10);
    var cases = $(".overview-box h1", 2).shouldBe(visible).text();
    assertThat(Integer.parseInt(cases)).isGreaterThan(0);
    var pms = $(".overview-box h1", 3).shouldBe(visible).text();
    assertThat(Integer.parseInt(pms)).isEqualTo(1);
    $$(".card").shouldHave(size(8));
    EngineCockpitUtil.destroyRunningCase();
  }

  @Test
  void changeEnvironment() {
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
  void securitySystemInfo() {
    Navigation.toApplicationDetail("test-ad");
    $(By.id("appDetailSecurityForm")).find(".ui-panelgrid-content a", 0).shouldHave(exactText("test-ad"), href("security-detail.xhtml?securitySystemName=test-ad"));
    var userCount = $(By.id("appDetailSecurityForm")).find(".ui-panelgrid-content a", 1).shouldHave(href("users.xhtml")).text();
    assertThat(Integer.parseInt(userCount)).isBetween(2, 8);
    var roleCount = $(By.id("appDetailSecurityForm")).find(".ui-panelgrid-content a", 2).shouldHave(href("roles.xhtml")).text();
    assertThat(Integer.parseInt(roleCount)).isBetween(3, 5);
  }

  @Test
  void move() {
    Navigation.toApplicationDetail("test-ad");
    $(By.id(("appDetailSecurityForm:moveApplication"))).click();
    $(By.id("moveApplicationModal")).shouldBe(visible);
    $(By.id("moveApplicationForm:validateMoveApplication")).click();
    $(By.id("moveApplicationForm:validationMessage")).should(visible).should(Condition.text("Application must be deactivated."));
    $(By.id("moveApplicationForm:cancelMoveApplication")).click();
  }

  @Test
  void adSync() {
    Navigation.toApplicationDetail("test-ad");
    waitUntilAjaxIsFinished();
    $("#appDetailSecurityForm\\:showAdSyncLogBtn").should(exist);
    $("#appDetailSecurityForm\\:synchronizeSecurity").shouldBe(visible, enabled).click();
    $$("#appDetailSecurityForm\\:synchronizeSecurity span").first().shouldHave(cssClass("si-is-spinning"));
    $$("#appDetailSecurityForm\\:synchronizeSecurity span").first()
            .shouldHave(not(cssClass("si-is-spinning")), Duration.ofSeconds(20));

    $("#appDetailSecurityForm\\:showAdSyncLogBtn").click();
    $$(".ui-panel-titlebar").find(text("usersynch.log")).parent()
            .find(".ui-panel-content").shouldBe(visible);
  }
}
