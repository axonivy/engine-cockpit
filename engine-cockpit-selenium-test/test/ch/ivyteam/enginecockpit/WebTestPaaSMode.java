package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.open;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.viewUrl;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.ScrollIntoViewOptions;
import com.codeborne.selenide.ScrollIntoViewOptions.Block;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;

@IvyWebTest
class WebTestPaaSMode {

  private static final String LICENCE_DETAIL_BUTTON = "#licence\\:tasksButtonLicenceDetail";
  private static final String LICENCE_UPLOAD_BUTTON = "#licence\\:uploadLicenceBtn";
  private static final String SYSTEM_DATABASE_BUTTON = "#systemDatabase\\:configureSystemDbBtn";
  private static final String EMAIL_BUTTON = "#email\\:configureEmailBtn";
  private static final String INFO_SETUP_MENU = "#menuform\\:sr_setup";
  private static final String SECURITY_MENU = "#sidebarMenu\\:menuform\\:sr_security";
  private static final String SECURITY_SYSTEM_MENU = "#sidebarMenu\\:menuform\\:sr_security_system";
  private static final String SECURITY_USER_MENU = "#sidebarMenu\\:menuform\\:sr_users";
  private static final String SYSTEM_MENU = "#sidebarMenu\\:menuform\\:sr_system";
  private static final String MONITOR_MENU = "#sidebarMenu\\:menuform\\:sr_monitor";
  private static final String MONITOR_LOGS_MENU = "#sidebarMenu\\:menuform\\:sr_logs";

  @BeforeEach
  void beforeEach() {
    login();
  }

  @AfterEach
  void afterEach() {
    EngineCockpitUtil.setPaaSMode(false);
  }

  @Test
  void paasModeHidesRestrictedMenuEntries() {
    setPaaSMode(true);
    $(SYSTEM_MENU).shouldNot(exist);
    openMenu(SECURITY_MENU);
    $(SECURITY_SYSTEM_MENU).shouldNot(exist);
    $(SECURITY_USER_MENU).find("a").shouldBe(visible);
    openMenu(MONITOR_MENU);
    $(MONITOR_LOGS_MENU).find("a").shouldBe(visible);

    setPaaSMode(false);
    $(SYSTEM_MENU).find("a").shouldBe(visible);
    openMenu(SECURITY_MENU);
    $(SECURITY_SYSTEM_MENU).find("a").shouldBe(visible);
    openMenu(MONITOR_MENU);
    $(MONITOR_LOGS_MENU).find("a").shouldBe(visible);
  }

  @Test
  void paasModeHidesRestrictedPageEntries() {
    setPaaSMode(true);
    $(LICENCE_DETAIL_BUTTON).shouldNot(exist);
    $(LICENCE_UPLOAD_BUTTON).shouldNot(exist);
    $(SYSTEM_DATABASE_BUTTON).shouldNot(exist);
    $(EMAIL_BUTTON).shouldNot(exist);
    open(viewUrl("info.xhtml"));
    $(INFO_SETUP_MENU).shouldNot(exist);

    setPaaSMode(false);
    $(LICENCE_DETAIL_BUTTON).shouldBe(visible);
    $(SYSTEM_DATABASE_BUTTON).shouldBe(visible);
  }

  private void setPaaSMode(boolean enabled) {
    EngineCockpitUtil.setPaaSMode(enabled);
    open(viewUrl("dashboard.xhtml"));
  }

  private void openMenu(String menu) {
    EngineCockpitUtil.waitUntilMenuJsIsInitialized();
    $(menu).find("a").scrollIntoView(ScrollIntoViewOptions.instant().block(Block.end)).click();
  }
}
