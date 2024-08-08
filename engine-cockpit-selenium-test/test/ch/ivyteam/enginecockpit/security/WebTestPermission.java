package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
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
    String button = "permissionsForm:permissionTable:0:ajaxTriState";
    $(permissionStateCss).shouldHave(attribute("title", "Some Permission granted"));

    $(By.id(button)).click();
    $(permissionStateCss).shouldHave(attribute("title", "Permission granted"));

    $(By.id(button)).click();
    $(permissionStateCss).shouldHave(attribute("title", "Permission denied"));

    $(By.id("permissionsForm:permissionTable_node_0")).find(".ui-treetable-toggler").should(exist).click();

    $(By.id("permissionsForm:permissionTable:0_0:ajaxTriState")).click();
    $(permissionStateCss).shouldHave(attribute("title", "Some Permission granted"));
  }

  @Test
  void duplicatedPortalPermissions_onlyShownOnce() {
    Navigation.toUsers();
    Tab.SECURITY_SYSTEM.switchToDefault();
    Navigation.toUserDetail("demo");

    Selenide.executeJavaScript("window.scrollTo(0,document.body.scrollHeight);");

    $(By.id("permissionsForm:globalFilter")).shouldBe(enabled).sendKeys("CaseWriteName");
    $(By.id("permissionsForm:permissionTable"))
            .shouldHave(Condition.text("CaseWriteName"))
            .findAll("tbody tr").shouldHave(size(1));
    $(By.id("permissionsForm:permissionTable:0:ajaxTriState")).click();
    $(By.id("permissionsForm:permissionTable_node_0")).find(".permission-icon > i")
            .shouldHave(attribute("title", "Permission granted"));

    $(By.id("permissionsForm:permissionTable:0:ajaxTriState")).click();
    $(By.id("permissionsForm:permissionTable:0:ajaxTriState")).click();
    $(By.id("permissionsForm:permissionTable_node_0")).find(".permission-icon > i")
            .shouldNot(exist);
  }

  @Test
  void collapsePermissionTree() {
    Navigation.toRoles();
    Tab.SECURITY_SYSTEM.switchToDefault();
    Navigation.toRoleDetail("boss");

    getVisibleTreeNodes().shouldBe(size(1));
    $(By.id("permissionsForm:permissionTable_node_0")).find(".ui-treetable-toggler").should(exist).click();
    getVisibleTreeNodes().shouldBe(size(4));
    $(By.id("permissionsForm:collapseAll")).shouldBe(visible).click();
    getVisibleTreeNodes().shouldBe(size(1));
  }

  private ElementsCollection getVisibleTreeNodes() {
    return $$("#permissionsForm\\:permissionTable .ui-treetable-data > tr").filter(visible);
  }
}
