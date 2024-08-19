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
  String threStateButton = "permissionsForm:permissionTable:0:ajaxTriState";
  String grantIcon = "#permissionsForm\\:permissionTable_node_0 .permission-icon #grant";
  String someGrantIcon = "#permissionsForm\\:permissionTable_node_0 .permission-icon #someGrant";
  String denyIcon = "#permissionsForm\\:permissionTable_node_0 .permission-icon #deny";
  String someDenyIcon = "#permissionsForm\\:permissionTable_node_0 .permission-icon #someDeny";
  String everybodyIcon = "#permissionsForm\\:permissionTable_node_0 .permission-icon #everybody";

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void permission() {
    Navigation.toUsers();
    Tab.SECURITY_SYSTEM.switchToDefault();
    Navigation.toUserDetail("foo");

    $(someGrantIcon).shouldHave(attribute("title", "Some Permission granted"));

    $(By.id(threStateButton)).click();
    $(grantIcon).shouldHave(attribute("title", "Permission granted"));

    $(By.id(threStateButton)).click();
    $(denyIcon).shouldHave(attribute("title", "Permission denied"));

    $(By.id("permissionsForm:permissionTable_node_0")).find(".ui-treetable-toggler").should(exist).click();

    $(By.id("permissionsForm:permissionTable:0_0:ajaxTriState")).click();
    $(someGrantIcon).shouldHave(attribute("title", "Some Permission granted"));
    $(someDenyIcon).shouldHave(attribute("title", "Some Permission denied"));
    
    $(By.id(threStateButton)).click();
  }
  
  @Test
  void permissionWithGroup() {
      Navigation.toUsers();
      Tab.SECURITY_SYSTEM.switchToDefault();
      Navigation.toUserDetail("foo");

      $(By.id("permissionsForm:globalFilter")).shouldBe(enabled).sendKeys("UserCreateOwnAbsence");
      
      $(grantIcon).shouldHave(attribute("title", "Permission granted"));
      $(everybodyIcon).shouldHave(attribute("title", "Everybody"));

      $(By.id(threStateButton)).click();
      $(grantIcon).shouldHave(attribute("title", "Permission granted"));

      $(By.id(threStateButton)).click();
      $(denyIcon).shouldHave(attribute("title", "Permission denied"));

      $(By.id(threStateButton)).click();
      $(grantIcon).shouldHave(attribute("title", "Permission granted"));
      $(everybodyIcon).shouldHave(attribute("title", "Everybody"));
  }
  
  @Test
  void permissionWithNothing() {
      Navigation.toUsers();
      Tab.SECURITY_SYSTEM.switchToDefault();
      Navigation.toUserDetail("foo");

      $(By.id("permissionsForm:globalFilter")).shouldBe(enabled).sendKeys("UserCreateOwnAbsence");
      
      $(grantIcon).shouldHave(attribute("title", "Permission granted"));
      $(everybodyIcon).shouldHave(attribute("title", "Everybody"));

      $(By.id(threStateButton)).click();
      $(grantIcon).shouldHave(attribute("title", "Permission granted"));

      $(By.id(threStateButton)).click();
      $(denyIcon).shouldHave(attribute("title", "Permission denied"));

      $(By.id(threStateButton)).click();
      $(grantIcon).shouldHave(attribute("title", "Permission granted"));
      $(everybodyIcon).shouldHave(attribute("title", "Everybody"));
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
