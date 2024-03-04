package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestRoles
{
  
  @BeforeAll
  static void setup()
  {
    EngineCockpitUtil.createManyDynamicRoles();
  }
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toRoles();
    Tab.switchToTab("test");
  }

  @Test
  void testRolesInTable()
  {
    $(Tab.ACITVE_PANEL_CSS + " h1").shouldBe(Condition.text("Roles"));
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").shouldBe(sizeGreaterThan(1));
    $(Tab.ACITVE_PANEL_CSS + " .ui-inputfield").sendKeys("Everybody");
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").shouldBe(size(1));
  }
  
  @Test
  void testManyRolesLoadLimit()
  {
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").shouldBe(size(103));
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").last().shouldHave(text("Show more ("), text("left)"));
    $(Tab.ACITVE_PANEL_CSS + " .ui-inputfield").sendKeys("role-");
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").shouldBe(size(101));
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").last().shouldHave(text("The current search has more than 100 results."));
  }
  
  @Test
  void testManyRolesLoadLimit_userDetail()
  {
    Navigation.toUserDetail("foo");
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-2").shouldBe(size(21));
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-2").last().shouldHave(text("Show more ("), text("left)"));
    $("#rolesOfUserForm .table-search-input-withicon").sendKeys("role-");
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-1").shouldBe(size(21));
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-1").last().shouldHave(text("The current search has more than 20 results."));
  }

  @Test
  void showMoreRoles()
  {
    var treeNodes = $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content");
    treeNodes.shouldBe(size(103));
    treeNodes.last().shouldHave(text("Show more")).$("button").click();
    treeNodes.last().shouldNotHave(text("Show more"));
    treeNodes.shouldBe(sizeGreaterThan(103));
  }

  @Test
  void showMoreRoles_userDetail()
  {
    Navigation.toUserDetail("foo");
    var treeNodes = $$("#rolesOfUserForm\\:rolesTree .ui-node-level-2");
    treeNodes.shouldBe(size(21));
    treeNodes.last().shouldHave(text("Show more")).$("button").click();
    treeNodes.last().shouldNotHave(text("Show more"));
    treeNodes.shouldBe(sizeGreaterThan(102));
  }
}
