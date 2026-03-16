package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.open;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.getAdminUser;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Condition.value;
import org.openqa.selenium.By;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

@IvyWebTest
class WebTestProfile {
  
  private final String PROFILE = "profile.xhtml";

  private void navigateToProfilePage() {
    open(viewUrl(PROFILE));
  }

  @BeforeEach
  void beforeEach() {
    login();
  }
  
  @Test
  void linkToProfile() {
    $("ul.layout-topbar-actions").shouldBe(visible);
    $("li.topbar-item.user-profile").shouldBe(visible);
    $("li.topbar-item.user-profile > a").click();
    $("#profileLink").shouldBe(visible).click();
    assertCurrentUrlContains(PROFILE);
  }

  @Test
  void profileUsernameMatch() {
    navigateToProfilePage();
    $(By.id("profileForm:userName")).shouldBe(visible).shouldHave(value(getAdminUser()));
  }

}
