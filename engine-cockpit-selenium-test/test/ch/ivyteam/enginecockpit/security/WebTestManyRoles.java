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
    var treeNodes = Tab.SECURITY_SYSTEM.activePanelCss + " .ui-treetable-data tr";
    $$(treeNodes).shouldBe(size(102));
    $$(treeNodes).last().shouldHave(text("Show more (12 left)"));
    $(Tab.SECURITY_SYSTEM.activePanelCss + " .ui-inputfield").sendKeys("role-");
    $$(treeNodes).shouldBe(size(101));
    $$(treeNodes).last().shouldHave(text("The current search has more than 100 results."));
  }

  @Test
  void manyRolesLoadLimit_userDetail() {
    Navigation.toUserDetail("foo");
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-2").shouldBe(size(21));
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-2").last().shouldHave(text("Show more (92 left)"));
    $("#rolesOfUserForm\\:rolesTree\\:globalFilter").sendKeys("role-");
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-1").shouldBe(size(21));
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-1").last()
            .shouldHave(text("The current search has more than 20 results."));
  }
  
  @Test
  void showMoreRoles() {
    var treeNodes = Tab.SECURITY_SYSTEM.activePanelCss + " .ui-treetable-data tr";
    $$(treeNodes).shouldBe(size(102));
    $$(treeNodes).last().shouldHave(text("Show more")).$("button").click();
    $$(treeNodes).last().shouldNotHave(text("Show more"));
    $$(treeNodes).shouldBe(size(113));
  }

  @Test
  void showMoreRoles_userDetail() {
    Navigation.toUserDetail("foo");
    var treeNodes = "#rolesOfUserForm\\:rolesTree .ui-node-level-2";
    $$(treeNodes).shouldBe(size(21));
    $$(treeNodes).last().shouldHave(text("Show more")).$("button").click();
    $$(treeNodes).last().shouldNotHave(text("Show more"));
    $$(treeNodes).shouldBe(size(112));
  }
}
