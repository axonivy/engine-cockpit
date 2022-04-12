package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.SecuritySystemTab;

@IvyWebTest
class WebTestRoles {

  private static final String SECURITY_SYSTEM_TAB_VIEW = "#tabs\\:securitySystemTabView\\:";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toRoles();
    SecuritySystemTab.switchToDefault();
  }

  @Test
  void rolesInTable() {
    $(SecuritySystemTab.ACITVE_PANEL_CSS + " h1").shouldBe(Condition.text("Roles"));
    $$(SecuritySystemTab.ACITVE_PANEL_CSS + " .ui-treenode-content").shouldBe(sizeGreaterThan(1));
    $(SecuritySystemTab.ACITVE_PANEL_CSS + " .ui-inputfield").sendKeys("Everybody");
    $$(SecuritySystemTab.ACITVE_PANEL_CSS + " .ui-treenode-content").shouldBe(size(1));
  }

  @Test
  void jumpToSyncLog() {
    SecuritySystemTab.switchToTab("test-ad");
    $(securitySystemTabId() + "syncMoreBtn_menuButton").click();
    $(securitySystemTabId() + "userSyncLog").shouldBe(visible).click();
    $$(".ui-panel-titlebar").find(text("usersynch.log")).parent()
            .find(".ui-panel-content").shouldBe(visible);
  }

  @Test
  void expandCollapseTree() {
    getVisibleTreeNodes().as("Everybody, AXONIVY_PORTAL_ADMIN, boss, worker")
      .shouldBe(sizeGreaterThan(3))
      .shouldBe(sizeLessThan(7));
    $(getTreeFormId() + "\\:expandAll").shouldBe(visible).click();
    getVisibleTreeNodes().shouldBe(size(4));
    $(getTreeFormId() + "\\:collapseAll").shouldBe(visible).click();
    getVisibleTreeNodes().shouldBe(size(1));
  }

  private ElementsCollection getVisibleTreeNodes() {
    return $(getTreeFormId()).findAll(".ui-treenode-content").filter(visible);
  }

  private String getTreeFormId() {
    return SECURITY_SYSTEM_TAB_VIEW + SecuritySystemTab.getSelectedTabIndex() + "\\:treeForm";
  }

  public static void triggerSync() {
    SecuritySystemTab.switchToTab("test-ad");
    var syncBtnId = securitySystemTabId() + "syncMoreBtn_button";
    $(syncBtnId).shouldBe(visible).click();
    $(syncBtnId).findAll("span").first().shouldHave(cssClass("si-is-spinning"));
    $(syncBtnId).findAll("span").first().shouldHave(not(cssClass("si-is-spinning")), Duration.ofSeconds(20));
  }

  private static String securitySystemTabId() {
    return SECURITY_SYSTEM_TAB_VIEW + SecuritySystemTab.getSelectedTabIndex() + "\\:form\\:";
  }
}
