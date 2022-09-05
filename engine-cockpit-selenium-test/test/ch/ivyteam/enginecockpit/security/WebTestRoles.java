package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
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
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
class WebTestRoles {

  private static final String SECURITY_SYSTEM_TAB_VIEW = "#tabs\\:securitySystemTabView\\:";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toRoles();
    Tab.SECURITY_SYSTEM.switchToDefault();
  }

  @Test
  void rolesInTable() {
    $("h2").shouldBe(Condition.text("Roles"));
    $$(Tab.SECURITY_SYSTEM.activePanelCss + " .ui-treetable-data tr").shouldBe(sizeGreaterThan(1));
    $(Tab.SECURITY_SYSTEM.activePanelCss + " .ui-inputfield").sendKeys("Everybody");
    $$(Tab.SECURITY_SYSTEM.activePanelCss + " .ui-treetable-data tr").shouldBe(size(1));
  }

  @Test
  void jumpToSyncLog() {
    Tab.SECURITY_SYSTEM.switchToTab("test-ad");
    $("#form\\:syncMoreBtn_menuButton").click();
    $("#form\\:userSyncLog").shouldBe(visible).click();
    $$(".ui-panel-titlebar").find(text("usersynch.log")).parent()
            .find(".ui-panel-content").shouldBe(visible);
  }

  @Test
  void expandCollapseTree() {
    getVisibleTreeNodes().as("Everybody, boss, worker, AXONIVY_PORTAL_ADMIN")
      .shouldBe(sizeGreaterThanOrEqual(3))
      .shouldBe(sizeLessThan(5));
    $(getTreeFormId() + "\\:tree\\:expandAll").shouldBe(visible).click();
    getVisibleTreeNodes().as("Everybody, boss, worker, AXONIVY_PORTAL_ADMIN, manager")
      .shouldBe(sizeGreaterThanOrEqual(4))
      .shouldBe(sizeLessThan(6));
    $(getTreeFormId() + "\\:tree\\:collapseAll").shouldBe(visible).click();
    getVisibleTreeNodes().shouldBe(size(1));
  }

  @Test
  void switchSecuritySystem() {
    $(By.id("roleCount")).shouldBe(Condition.text("4"));
    Tab.SECURITY_SYSTEM.switchToTab("test-ad");
    $(By.id("roleCount")).shouldBe(Condition.text("1"));
    Tab.SECURITY_SYSTEM.switchToDefault();
    $(By.id("roleCount")).shouldBe(Condition.text("4"));
  }

  private ElementsCollection getVisibleTreeNodes() {
    return $(getTreeFormId()).findAll(".ui-treetable-data tr").filter(visible);
  }

  private String getTreeFormId() {
    return SECURITY_SYSTEM_TAB_VIEW + Tab.SECURITY_SYSTEM.getSelectedTabIndex() + "\\:treeForm";
  }

  public static void triggerSync() {
    Tab.SECURITY_SYSTEM.switchToTab("test-ad");
    var syncBtnId = "#form\\:syncMoreBtn_button";
    $(syncBtnId).shouldBe(visible).click();
    $(syncBtnId).findAll("span").first().shouldHave(cssClass("si-is-spinning"));
    $(syncBtnId).findAll("span").first().shouldHave(not(cssClass("si-is-spinning")), Duration.ofSeconds(20));
  }
}
