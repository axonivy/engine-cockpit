package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.security.WebTestSecuritySystemDetail.LDAP_BROWSER_DIALOG;
import static ch.ivyteam.enginecockpit.security.WebTestSecuritySystemDetail.LDAP_BROWSER_FORM;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestRoleDetail {
  private static final String DETAIL_ROLE_NAME = "boss";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toRoles();
    Tab.switchToDefault();
    Navigation.toRoleDetail(DETAIL_ROLE_NAME);
  }

  @Test
  void testRoleDetailOpen() {
    assertCurrentUrlContains("roledetail.xhtml?roleName=" + DETAIL_ROLE_NAME);
  }

  @Test
  void testSaveRoleInformation() {
    clearRoleInfoInputs();

    $("#roleInformationForm\\:displayName").sendKeys("display");
    $("#roleInformationForm\\:description").sendKeys("desc");
    $("#roleInformationForm\\:externalSecurityName").sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    $("#roleInformationForm\\:saveRoleInformation").click();

    $("#roleInformationForm\\:informationSaveSuccess_container").shouldBe(visible);
    Selenide.refresh();

    $("#roleInformationForm\\:name").shouldBe(exactText(DETAIL_ROLE_NAME));
    $("#roleInformationForm\\:displayName").shouldBe(exactValue("display"));
    $("#roleInformationForm\\:description").shouldBe(exactValue("desc"));
    $("#roleInformationForm\\:externalSecurityName")
            .shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    clearRoleInfoInputs();
    $("#roleInformationForm\\:saveRoleInformation").click();
  }

  @Test
  void testNewChildRole() {
    $("#roleInformationForm\\:createNewChildRole").shouldBe(visible).click();
    $("#newChildRoleDialog").shouldBe(visible);

    $("#newChildRoleForm\\:saveNewRole").click();
    $("#newChildRoleForm\\:newRoleNameMessage").shouldBe(visible);
    $("#newChildRoleForm\\:newRoleNameMessage").shouldBe(text("Value is required"));

    String newRoleName = "testäöü";
    $("#newChildRoleForm\\:newChildRoleNameInput").clear();
    $("#newChildRoleForm\\:newChildRoleNameInput").sendKeys(newRoleName);
    $("#newChildRoleForm\\:saveNewRole").click();
    $("#msgs_container").should(visible, text("Role '" + newRoleName + "' created successfully"));
    assertCurrentUrlContains("roledetail.xhtml?roleName=test");
    $("#roleInformationForm\\:name").shouldBe(exactText(newRoleName));

    $("#roleInformationForm\\:deleteRole").shouldBe(visible);
    $("#roleInformationForm\\:deleteRole").click();
    $("#roleInformationForm\\:deleteRoleConfirmDialog").shouldBe(visible);

    $("#roleInformationForm\\:deleteRoleConfirmDialogYesBtn").click();
    assertCurrentUrlContains("roles.xhtml");
  }

  @Test
  void createNewChildRoleWithSameNameAsExisting() {
    $("#roleInformationForm\\:createNewChildRole").shouldBe(visible).click();
    $("#newChildRoleDialog").shouldBe(visible);
    $("#newChildRoleForm\\:newChildRoleNameInput").clear();
    $("#newChildRoleForm\\:newChildRoleNameInput").sendKeys(DETAIL_ROLE_NAME);
    $("#newChildRoleForm\\:saveNewRole").click();
    $("#msgs_container").should(visible, text("Role '" + DETAIL_ROLE_NAME + "' couldn't be created"));
    assertCurrentUrlContains("roledetail.xhtml?roleName=" + DETAIL_ROLE_NAME);
  }

  @Test
  void testAddAndRemoveUser() {
    var roleUsers = new Table(By.id("usersOfRoleForm:roleUserTable"), true);
    removeUserIfExists();
    roleUsers.firstColumnShouldBe(empty);
    addUserFoo();
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));

    Selenide.refresh();
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));

    roleUsers.search("ba");
    roleUsers.firstColumnShouldBe(empty);
    roleUsers.search("fo");
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));

    roleUsers.clickButtonForEntry("foo", "removeUserFromRoleBtn");
    roleUsers.firstColumnShouldBe(empty);
  }

  @Test
  void testAddUserByFullname() {
    var roleUsers = new Table(By.id("usersOfRoleForm:roleUserTable"), true);
    removeUserIfExists();
    roleUsers.firstColumnShouldBe(empty);
    addUser("Johnny Depp", "jon (Johnny Depp)");
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));
  }

  @Test
  void testAddUserByEmail() {
    var roleUsers = new Table(By.id("usersOfRoleForm:roleUserTable"), true);
    removeUserIfExists();
    roleUsers.firstColumnShouldBe(empty);
    addUser("jd@ivyteam.ch", "jon (Johnny Depp)");
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));
  }

  @Test
  void testAddAndRemoveMember() {
    var roleMembers = new Table(By.id("membersOfRoleForm:roleMemberTable"), true);
    roleMembers.firstColumnShouldBe(empty);
    addRoleMember();
    roleMembers.firstColumnShouldBe(sizeGreaterThan(0));

    Selenide.refresh();
    roleMembers.firstColumnShouldBe(sizeGreaterThan(0));

    roleMembers.clickButtonForEntry("worker", "removeMemberFromRoleBtn");
    roleMembers.firstColumnShouldBe(empty);
  }

  @Test
  void testInheritedUserFromSubRole() {
    var roleUsers = new Table(By.id("usersOfRoleForm:roleUserTable"), true);
    removeUserIfExists();
    roleUsers.firstColumnShouldBe(empty);

    Navigation.toRoleDetail("manager");
    removeUserIfExists();
    addUserFoo();
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));

    Navigation.toRoleDetail(DETAIL_ROLE_NAME);
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));
    roleUsers.buttonForEntryShouldBeDisabled("foo", "removeUserFromRoleBtn");

    Navigation.toRoleDetail("manager");
    roleUsers.clickButtonForEntry("foo", "removeUserFromRoleBtn");
  }

  @Test
  void testInheritedUserFromRoleMember() {
    var roleMembers = new Table(By.id("membersOfRoleForm:roleMemberTable"), true);
    var roleUsers = new Table(By.id("usersOfRoleForm:roleUserTable"), true);
    removeUserIfExists();
    roleMembers.firstColumnShouldBe(empty);
    addRoleMember();
    roleMembers.firstColumnShouldBe(sizeGreaterThan(0));

    Navigation.toRoleDetail("worker");
    removeUserIfExists();
    addUserFoo();
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));

    Navigation.toRoleDetail(DETAIL_ROLE_NAME);
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));
    roleUsers.buttonForEntryShouldBeDisabled("foo", "removeUserFromRoleBtn");

    roleMembers.clickButtonForEntry("worker", "removeMemberFromRoleBtn");
    roleMembers.firstColumnShouldBe(empty);

    Navigation.toRoleDetail("worker");
    roleUsers.clickButtonForEntry("foo", "removeUserFromRoleBtn");
  }

  private void addRoleMember() {
    $("#membersOfRoleForm\\:addMemberDropDown_input").shouldBe(visible).sendKeys("wor");
    $("#membersOfRoleForm\\:addMemberDropDown_panel").shouldBe(visible);
    $$(".ui-autocomplete-list-item").shouldBe(sizeGreaterThan(0));
    $(".ui-autocomplete-list-item").click();
    $("#membersOfRoleForm\\:addMemberDropDown_input").shouldBe(exactValue("worker"));
    $("#membersOfRoleForm\\:addMemberToRoleBtn").click();
  }

  private void addUserFoo() {
    addUser("fo", "foo (Marcelo Footer)");
  }

  private void addUser(String filter, String display) {
    $("#usersOfRoleForm\\:addUserDropDown_input").shouldBe(visible).sendKeys(filter);
    $$(".ui-autocomplete-list-item").shouldBe(sizeGreaterThan(0));
    $(".ui-autocomplete-list-item").click();
    $("#usersOfRoleForm\\:addUserDropDown_input").shouldBe(exactValue(display));
    $("#usersOfRoleForm\\:addUserToRoleBtn").click();
  }

  private void removeUserIfExists() {
    var firstUser = "usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn";
    if ($(By.id(firstUser)).is(visible)) {
      $(By.id(firstUser)).click();
    }
  }

  @Test
  void testExternalSecurityName() {
    Navigation.toRoles();
    Tab.switchToTab("test-ad");
    Navigation.toRoleDetail(DETAIL_ROLE_NAME);

    new Table(By.id("usersOfRoleForm:roleUserTable"), true).firstColumnShouldBe(size(0));
    $("#roleInformationForm\\:externalSecurityName").sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    $("#roleInformationForm\\:saveRoleInformation").click();

    Navigation.toRoles();
    WebTestRoles.triggerSync();

    Navigation.toRoleDetail(DETAIL_ROLE_NAME);

    checkIfRoleIsExternal();

    $("#roleInformationForm\\:externalSecurityName").clear();
    $("#roleInformationForm\\:externalSecurityName").shouldBe(Condition.empty);
    $("#roleInformationForm\\:saveRoleInformation").click();
    Selenide.refresh();
    assertCurrentUrlContains("roledetail.xhtml?roleName=" + DETAIL_ROLE_NAME);
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn")
            .shouldNotHave(cssClass("ui-state-disabled")).click();
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn")
            .shouldNotHave(cssClass("ui-state-disabled")).click();
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn")
            .shouldNotHave(cssClass("ui-state-disabled")).click();
  }

  private void checkIfRoleIsExternal() {
    Table usersOfRole = new Table(By.id("usersOfRoleForm:roleUserTable"), true);
    usersOfRole.firstColumnShouldBe(size(3));
    usersOfRole.buttonForEntryShouldBeDisabled("user1", "removeUserFromRoleBtn");
    usersOfRole.buttonForEntryShouldBeDisabled("user2", "removeUserFromRoleBtn");
    usersOfRole.buttonForEntryShouldBeDisabled("user3", "removeUserFromRoleBtn");
    $("#usersOfRoleForm\\:addUserDropDown_input").shouldBe(disabled);
    $("#usersOfRoleForm\\:addUserToRoleBtn").shouldBe(disabled);
  }

  @Test
  void testExternalSecurityName_ldapBrowser() {
    $("#roleInformationForm\\:browseExternalName").shouldBe(disabled);
    Navigation.toRoles();
    Tab.switchToTab("test-ad");
    Navigation.toRoleDetail("Everybody");
    $("#roleInformationForm\\:browseExternalName").shouldBe(disabled);
    Navigation.toRoles();
    Navigation.toRoleDetail(DETAIL_ROLE_NAME);
    $("#roleInformationForm\\:browseExternalName").shouldNotBe(disabled).click();

    $(LDAP_BROWSER_DIALOG).shouldBe(visible);
    $(LDAP_BROWSER_FORM + "tree\\:0").shouldHave(text("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    $(LDAP_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
    $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(11));
    $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("CN=role1")).click();
    $(LDAP_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("CN=role1")).shouldHave(cssClass("ui-state-highlight"));
    $("#ldapBrowser\\:chooseLdapName").click();
    $(LDAP_BROWSER_DIALOG).shouldNotBe(visible);
    $("#roleInformationForm\\:externalSecurityName")
            .shouldHave(value("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  void testExternalSecurityName_ldapBrowser_initValue() {
    Navigation.toRoles();
    Tab.switchToTab("test-ad");
    Navigation.toRoleDetail(DETAIL_ROLE_NAME);
    $("#roleInformationForm\\:externalSecurityName")
            .sendKeys("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    $("#roleInformationForm\\:browseExternalName").shouldNotBe(disabled).click();

    $(LDAP_BROWSER_DIALOG).shouldBe(visible);
    $$(LDAP_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("CN=role1"))
            .shouldBe(visible, cssClass("ui-state-highlight"));
    Table table = new Table(By.id("ldapBrowser:ldapBrowserForm:nodeAttrTable"));
    table.tableEntry("distinguishedName", 2)
            .shouldBe(exactText("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  private void clearRoleInfoInputs() {
    $("#roleInformationForm\\:displayName").clear();
    $("#roleInformationForm\\:description").clear();
    $("#roleInformationForm\\:externalSecurityName").clear();
  }
}
