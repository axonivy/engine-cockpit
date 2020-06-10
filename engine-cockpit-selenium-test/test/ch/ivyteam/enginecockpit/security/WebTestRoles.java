package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
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
  
  private static final String APPLICATION_TAB_VIEW = "#tabs\\:applicationTabView\\:";
  
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
  void jumpToSyncLog()
  {
    Tab.switchToTab("test-ad");
    $(getAppTabId() + "syncMoreBtn_menuButton").click();
    $(getAppTabId() + "userSyncLog").shouldBe(visible).click();
    $("#userSynchLogView\\:logPanel_content").shouldBe(visible);
  }
  
  @Test
  void testManyRolesLoadLimit()
  {
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").shouldBe(size(102));
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").last().shouldHave(text("Please use the search to find a specific role ("), text("more roles)"));
    $(Tab.ACITVE_PANEL_CSS + " .ui-inputfield").sendKeys("role-");
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").shouldBe(size(101));
    $$(Tab.ACITVE_PANEL_CSS + " .ui-treenode-content").last().shouldHave(text("The current search has more than 100 results."));
  }
  
  @Test
  void testManyRolesLoadLimit_userDetail()
  {
    Navigation.toUserDetail("foo");
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-2").shouldBe(size(101));
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-2").last().shouldHave(text("Please use the search to find a specific role ("), text("more roles)"));
    $("#rolesOfUserForm .table-search-input-withicon").sendKeys("role-");
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-1").shouldBe(size(101));
    $$("#rolesOfUserForm\\:rolesTree .ui-node-level-1").last().shouldHave(text("The current search has more than 100 results."));
  }
  
  public static void triggerSync()
  {
    Tab.switchToTab("test-ad");
    String syncBtnId = getAppTabId() + "syncMoreBtn_button";
    $(syncBtnId).shouldBe(visible).click();
    $(syncBtnId).findAll("span").first().shouldHave(cssClass("fa-spin"));
    $(syncBtnId).findAll("span").first().shouldNotHave(cssClass("fa-spin"));
  }
  
  private static String getAppTabId()
  {
    return APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:form\\:";
  }

}
