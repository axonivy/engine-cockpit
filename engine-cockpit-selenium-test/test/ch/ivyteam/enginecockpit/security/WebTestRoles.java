package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestRoles
{
  
  private static final String APPLICATION_TAB_VIEW = "#tabs\\:applicationTabView\\:";
  
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
    $$(".ui-panel-titlebar").find(text("usersynch.log")).parent()
            .find(".ui-panel-content").shouldBe(visible);  
  }
  
  @Test
  void testExpandCollapseTree()
  {
    getVisibleTreeNodes().shouldBe(size(3));
    $(getTreeFormId() + "\\:expandAll").shouldBe(visible).click();
    getVisibleTreeNodes().shouldBe(size(4));
    $(getTreeFormId() + "\\:collapseAll").shouldBe(visible).click();
    getVisibleTreeNodes().shouldBe(size(1));
  }
  
  private ElementsCollection getVisibleTreeNodes()
  {
    return $(getTreeFormId()).findAll(".ui-treenode-content").filter(visible);
  }
  
  private String getTreeFormId()
  {
    return APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:treeForm";
  }
  
  public static void triggerSync()
  {
    Tab.switchToTab("test-ad");
    String syncBtnId = getAppTabId() + "syncMoreBtn_button";
    $(syncBtnId).shouldBe(visible).click();
    $(syncBtnId).findAll("span").first().shouldHave(cssClass("si-is-spinning"));
    $(syncBtnId).findAll("span").first().shouldNotHave(cssClass("si-is-spinning"));
  }
  
  private static String getAppTabId()
  {
    return APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:form\\:";
  }

}
