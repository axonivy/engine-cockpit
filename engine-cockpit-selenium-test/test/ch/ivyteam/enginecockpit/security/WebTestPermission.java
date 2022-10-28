package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestPermission {

  @BeforeEach
  void beforeEach()
  {
    login();
  }

  @Test
  void testPermission()
  {
    Navigation.toUsers();
    Tab.switchToDefault();
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
  void duplicatedPortalPermissions_onlyShowOnce() {
    Navigation.toUsers();
    Tab.switchToTab("demo-portal");
    Navigation.toUserDetail("demo");

    $(By.id("permissionsForm:globalFilter")).sendKeys("CaseWriteName");
    $(By.id("permissionsForm:permissionTable"))
            .shouldHave(text("CaseWriteName"))
            .findAll("tbody tr").shouldHave(size(1));
    $(By.id("permissionsForm:permissionTable:0:grantPermissionBtn")).click();
    $(By.id("permissionsForm:permissionTable_node_0")).find(".permission-icon > i")
            .shouldHave(attribute("title", "Permission granted"));

    $(By.id("permissionsForm:permissionTable:0:unGrantPermissionBtn")).click();
    $(By.id("permissionsForm:permissionTable_node_0")).find(".permission-icon > i")
            .shouldNot(exist);
  }
}
