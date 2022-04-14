package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
class WebTestPermission {

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void permission() {
    Navigation.toUsers();
    Tab.SECURITY_SYSTEM.switchToDefault();
    Navigation.toUserDetail("foo");

    String permissionStateCss = "#permissionsForm\\:permissionTable_node_0 > .permission-icon > i";
    String firstPermissionCss = "#permissionsForm\\:permissionTable\\:0\\:";
    $(permissionStateCss).shouldHave(attribute("title", "Some Permission granted"));

    $(firstPermissionCss + "grantPermissionBtn").click();
    $(permissionStateCss).shouldHave(attribute("title", "Permission granted"));

    $(firstPermissionCss + "unGrantPermissionBtn").click();
    $(permissionStateCss).shouldHave(attribute("title", "Some Permission granted"));

    $(firstPermissionCss + "denyPermissionBtn").click();
    $(permissionStateCss).shouldHave(attribute("title", "Permission denied"));

    $(firstPermissionCss + "unDenyPermissionBtn").click();
    $(permissionStateCss).shouldHave(attribute("title", "Some Permission granted"));
  }

  @Test
  void duplicatedPortalPermissions() {
    Navigation.toUsers();
    Tab.SECURITY_SYSTEM.switchToDefault();
    Navigation.toUserDetail("demo");

    Selenide.executeJavaScript("window.scrollTo(0,document.body.scrollHeight);");

    $(By.id("permissionsForm:globalFilter")).sendKeys("CaseWriteName");
    var table = $(By.id("permissionsForm:permissionTable"));
    table.findAll("tbody tr").shouldHave(size(2));
    $(By.id("permissionsForm:permissionTable:0:grantPermissionBtn")).click();
    $(By.id("permissionsForm:permissionTable_node_0")).find(".permission-icon > i")
            .shouldHave(attribute("title", "Permission granted"));
    $(By.id("permissionsForm:permissionTable_node_1")).find(".permission-icon > i")
            .shouldHave(attribute("title", "Permission granted"));

    $(By.id("permissionsForm:permissionTable:0:unGrantPermissionBtn")).click();
    $(By.id("permissionsForm:permissionTable_node_0")).find(".permission-icon > i")
            .shouldNot(exist);
    $(By.id("permissionsForm:permissionTable_node_1")).find(".permission-icon > i")
            .shouldNot(exist);
  }

  @Test
  void expandCollapsePermissionTree() {
    Navigation.toRoles();
    Tab.SECURITY_SYSTEM.switchToDefault();
    Navigation.toRoleDetail("boss");

    getVisibleTreeNodes().shouldBe(size(4));
    $("#permissionsForm\\:collapseAll").shouldBe(visible).click();
    getVisibleTreeNodes().shouldBe(size(1));
    $("#permissionsForm\\:expandAll").shouldBe(visible).click();
    getVisibleTreeNodes().shouldBe(sizeGreaterThan(50));
  }

  private ElementsCollection getVisibleTreeNodes() {
    return $$("#permissionsForm\\:permissionTable .ui-treetable-data > tr").filter(visible);
  }
}
