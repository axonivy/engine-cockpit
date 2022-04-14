package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
class WebTestManyRoles {

  @BeforeAll
  static void setup() {
    EngineCockpitUtil.createManyDynamicRoles();
  }

  @AfterAll
  static void cleanup() {
    EngineCockpitUtil.cleanupDynamicRoles();
  }

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toRoles();
    Tab.SECURITY_SYSTEM.switchToDefault();
  }

  @Test
  void manyRolesLoadLimit() {
    $$(Tab.SECURITY_SYSTEM.activePanelCss + " .ui-treenode-content").shouldBe(size(102));
    $$(Tab.SECURITY_SYSTEM.activePanelCss + " .ui-treenode-content").last()
            .shouldHave(text("Please use the search to find a specific role ("), text("more roles)"));
    $(Tab.SECURITY_SYSTEM.activePanelCss + " .ui-inputfield").sendKeys("role-");
    $$(Tab.SECURITY_SYSTEM.activePanelCss + " .ui-treenode-content").shouldBe(size(101));
    $$(Tab.SECURITY_SYSTEM.activePanelCss + " .ui-treenode-content").last()
            .shouldHave(text("The current search has more than 100 results."));
  }

  @Test
  void manyRolesLoadLimit_userDetail() {
    Navigation.toUserDetail("foo");
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-2").shouldBe(size(101));
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-2").last()
            .shouldHave(text("Please use the search to find a specific role ("), text("more roles)"));
    $("#rolesOfUserForm .table-search-input-withicon").sendKeys("role-");
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-1").shouldBe(size(101));
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-1").last()
            .shouldHave(text("The current search has more than 100 results."));
  }
}
