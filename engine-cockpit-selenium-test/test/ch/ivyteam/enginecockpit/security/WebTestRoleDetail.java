package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectManyCheckbox;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestRoleDetail {

  private static final String DIRECTORY_BROWSER_FORM = "#directoryBrowser\\:directoryBrowserForm\\:";
  private static final String DIRECTORY_BROWSER_DIALOG = "directoryBrowser:directoryBrowserDialog";
  private static final String ROLE_USERS_TABLE = "usersOfRoleForm:roleUserTable";
  private static final String DETAIL_ROLE_NAME = "boss";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toRoles();
    Tab.SECURITY_SYSTEM.switchToDefault();
    Navigation.toRoleDetail(DETAIL_ROLE_NAME);
  }

  @Test
  void roleDetailOpen() {
    assertCurrentUrlContains("roledetail.xhtml?system=default&name=" + DETAIL_ROLE_NAME);
  }

  @Test
  void saveRoleInformation() {
    clearRoleInfoInputs();

    $(By.id("roleInformationForm:displayName")).sendKeys("display");
    $(By.id("roleInformationForm:description")).sendKeys("desc");
    $(By.id("roleInformationForm:externalSecurityName")).sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    $(By.id("roleInformationForm:saveRoleInformation")).click();

    $(By.id("roleInformationForm:informationSaveSuccess_container")).shouldBe(visible);
    Selenide.refresh();

    $(By.id("roleInformationForm:name")).shouldBe(exactValue(DETAIL_ROLE_NAME));
    $(By.id("roleInformationForm:displayName")).shouldBe(exactValue("display"));
    $(By.id("roleInformationForm:description")).shouldBe(exactValue("desc"));
    $(By.id("roleInformationForm:parentRole")).shouldBe(exactValue("Everybody"));

    $(By.id("roleInformationForm:externalSecurityName")).shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    clearRoleInfoInputs();
    $(By.id("roleInformationForm:saveRoleInformation")).click();
  }

  @Test
  void newChildRole() {
    $("#roleInformationForm\\:createNewChildRole").shouldBe(visible).click();
    $("#newChildRoleDialog").shouldBe(visible);

    $("#newChildRoleForm\\:saveNewRole").click();
    $("#newChildRoleForm\\:newRoleNameMessage").shouldBe(visible);
    $("#newChildRoleForm\\:newRoleNameMessage").shouldBe(text("Value is required"));

    String newRoleName = "testäöü";
    $("#newChildRoleForm\\:newChildRoleNameInput").clear();
    $("#newChildRoleForm\\:newChildRoleNameInput").sendKeys(newRoleName);
    $("#newChildRoleForm\\:saveNewRole").click();
    assertCurrentUrlContains("roledetail.xhtml?system=default&name=test");
    $("#roleInformationForm\\:name").shouldBe(exactValue(newRoleName));

    $("#roleInformationForm\\:deleteRole").shouldBe(visible).click();
    $("#roleInformationForm\\:deleteRoleConfirmDialog").shouldBe(visible);

    $("#roleInformationForm\\:deleteRoleConfirmDialogYesBtn").click();
    assertCurrentUrlContains("roles.xhtml");
  }

  @Test
  void renameRole() {
    $("#roleInformationForm\\:createNewChildRole").shouldBe(visible).click();
    $("#newChildRoleDialog").shouldBe(visible);

    $("#newChildRoleForm\\:newChildRoleNameInput").sendKeys("avengers");
    $("#newChildRoleForm\\:saveNewRole").click();
    assertCurrentUrlContains("roledetail.xhtml?system=default&name=avengers");
    $("#roleInformationForm\\:name").shouldBe(exactValue("avengers"));

    $("#roleInformationForm\\:renameRole").shouldBe(visible).click();

    $("#renameRoleForm\\:renameRoleNameInput").shouldBe(visible).clear();
    $("#renameRoleForm\\:renameRoleNameInput").sendKeys("xmen");

    $("#renameRoleForm\\:renameRole").click();
    assertCurrentUrlContains("roledetail.xhtml?system=default&name=xmen");

    $("#roleInformationForm\\:deleteRole").shouldBe(visible).click();
    $("#roleInformationForm\\:deleteRoleConfirmDialog").shouldBe(visible);

    $("#roleInformationForm\\:deleteRoleConfirmDialogYesBtn").shouldBe(visible).click();
    assertCurrentUrlContains("roles.xhtml");
  }

  @Test
  void updateParent() {
    $("#roleInformationForm\\:createNewChildRole").shouldBe(visible).click();
    $("#newChildRoleDialog").shouldBe(visible);
    $("#newChildRoleForm\\:newChildRoleNameInput").sendKeys("goodcops");
    $("#newChildRoleForm\\:saveNewRole").click();
    assertCurrentUrlContains("roledetail.xhtml?system=default&name=goodcops");
    $("#roleInformationForm\\:name").shouldBe(exactValue("goodcops"));
    $("#roleInformationForm\\:parentRole").shouldBe(exactValue("boss"));
    $("#roleInformationForm\\:updateRoleParent").shouldBe(visible).click();

    var provider = PrimeUi.selectOne(By.id("updateRoleParentForm:updateRoleParentNameInput"));
    provider.selectItemByLabel("Everybody");
    $("#updateRoleParentForm\\:updateRoleParent").click();
    $("#roleInformationForm\\:parentRole").shouldBe(exactValue("Everybody"));

    $("#roleInformationForm\\:deleteRole").shouldBe(visible).click();
    $("#roleInformationForm\\:deleteRoleConfirmDialog").shouldBe(visible);
    $("#roleInformationForm\\:deleteRoleConfirmDialogYesBtn").click();
    assertCurrentUrlContains("roles.xhtml");
  }

  @Test
  void createNewChildRoleWithSameNameAsExisting() {
    $(By.id("roleInformationForm:createNewChildRole")).shouldBe(visible).click();
    $(By.id("newChildRoleDialog")).shouldBe(visible);
    $(By.id("newChildRoleForm:newChildRoleNameInput")).clear();
    $(By.id("newChildRoleForm:newChildRoleNameInput")).sendKeys(DETAIL_ROLE_NAME);
    $(By.id("newChildRoleForm:saveNewRole")).click();
    $(By.id("msgs_container")).should(visible, text("Role '" + DETAIL_ROLE_NAME + "' couldn't be created"));
  }

  @Test
  void userContentFilter() {
    Navigation.toRoles();
    Tab.SECURITY_SYSTEM.switchToDefault();
    Navigation.toRoleDetail("Everybody");

    var roleUsers = new Table(By.id(ROLE_USERS_TABLE), true);
    roleUsers.firstColumnShouldBe(sizeGreaterThan(2));
    filterBtn().shouldHave(text("Filter: enabled users"));

    filterTableFor("Show disabled users");
    assertContentFilterText("Filter: disabled users");
    roleUsers.firstColumnShouldBe(sizeLessThan(2));

    resetFilter();
    assertContentFilterText("Filter: enabled users");
    roleUsers.firstColumnShouldBe(sizeGreaterThan(2));
  }

  private void filterTableFor(String filter) {
    $(filterBtn()).shouldBe(visible).click();
    filterCheckboxes().setCheckboxes(List.of(filter));
    $(By.id(ROLE_USERS_TABLE + ":applyFilter")).shouldBe(visible).click();
  }

  private void resetFilter() {
    $(filterBtn()).shouldBe(visible).click();
    $(By.id(ROLE_USERS_TABLE + ":resetFilterBtn")).shouldBe(visible).click();
  }

  private void assertContentFilterText(String expectedFilter) {
    $(filterBtn()).shouldHave(attribute("title", expectedFilter));
  }

  private SelenideElement filterBtn() {
    return $(By.id(ROLE_USERS_TABLE + ":filterBtn"));
  }

  private SelectManyCheckbox filterCheckboxes() {
    return PrimeUi.selectManyCheckbox(By.id(ROLE_USERS_TABLE + ":filterCheckboxes"));
  }

  @Test
  void addAndRemoveUser() {
    var roleUsers = new Table(By.id(ROLE_USERS_TABLE), true);
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
  void addUserByFullname() {
    var roleUsers = new Table(By.id(ROLE_USERS_TABLE), true);
    removeUserIfExists();
    roleUsers.firstColumnShouldBe(empty);
    addUser("Johnny Depp", "jon (Johnny Depp)");
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));
  }

  @Test
  void addUserByEmail() {
    var roleUsers = new Table(By.id(ROLE_USERS_TABLE), true);
    removeUserIfExists();
    roleUsers.firstColumnShouldBe(empty);
    addUser("jd@ivyteam.ch", "jon (Johnny Depp)");
    roleUsers.firstColumnShouldBe(sizeGreaterThan(0));
  }

  @Test
  void addAndRemoveMember() {
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
  void inheritedUserFromSubRole() {
    var roleUsers = new Table(By.id(ROLE_USERS_TABLE), true);
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
  void inheritedUserFromRoleMember() {
    var roleMembers = new Table(By.id("membersOfRoleForm:roleMemberTable"), true);
    var roleUsers = new Table(By.id(ROLE_USERS_TABLE), true);
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
  void externalSecurityName() {
    Navigation.toRoles();
    Tab.SECURITY_SYSTEM.switchToTab("test-ad");
    Navigation.toRoleDetail("test-ad", DETAIL_ROLE_NAME);

    new Table(By.id(ROLE_USERS_TABLE), true).firstColumnShouldBe(size(0));
    $("#roleInformationForm\\:externalSecurityName").sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    $("#roleInformationForm\\:saveRoleInformation").click();

    Navigation.toRoles();
    WebTestRoles.triggerSync();

    Navigation.toRoleDetail("test-ad", DETAIL_ROLE_NAME);

    checkIfRoleIsExternal();

    $("#roleInformationForm\\:externalSecurityName").clear();
    $("#roleInformationForm\\:externalSecurityName").shouldBe(Condition.empty);
    $("#roleInformationForm\\:saveRoleInformation").click();
    Selenide.refresh();
    assertCurrentUrlContains("roledetail.xhtml?system=test-ad&name=" + DETAIL_ROLE_NAME);
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn")
            .shouldNotHave(cssClass("ui-state-disabled")).click();
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn")
            .shouldNotHave(cssClass("ui-state-disabled")).click();
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn")
            .shouldNotHave(cssClass("ui-state-disabled")).click();
  }

  private void checkIfRoleIsExternal() {
    Table usersOfRole = new Table(By.id(ROLE_USERS_TABLE), true);
    usersOfRole.firstColumnShouldBe(size(3));
    usersOfRole.buttonForEntryShouldBeDisabled("user1", "removeUserFromRoleBtn");
    usersOfRole.buttonForEntryShouldBeDisabled("user2", "removeUserFromRoleBtn");
    usersOfRole.buttonForEntryShouldBeDisabled("user3", "removeUserFromRoleBtn");
    $("#usersOfRoleForm\\:addUserDropDown_input").shouldBe(disabled);
    $("#usersOfRoleForm\\:addUserToRoleBtn").shouldBe(disabled);
  }

  @Test
  void externalSecurityName_ldapBrowser() {
    $(By.id("roleInformationForm:browseExternalName")).shouldBe(disabled);
    Navigation.toRoles();
    Tab.SECURITY_SYSTEM.switchToTab("test-ad");
    Navigation.toRoleDetail("test-ad", "Everybody");
    $(By.id("roleInformationForm:browseExternalName")).shouldBe(disabled);
    Navigation.toRoles();
    Navigation.toRoleDetail("test-ad", DETAIL_ROLE_NAME);
    $(By.id("roleInformationForm:browseExternalName")).shouldNotBe(disabled).click();

    $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldBe(visible);
    $(DIRECTORY_BROWSER_FORM + "tree\\:0").shouldHave(text("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-tree-toggler").click();
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode").shouldHave(size(11));
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("CN=role1")).click();
    $(DIRECTORY_BROWSER_FORM + "tree\\:0 .ui-treenode-children").findAll(".ui-treenode-label")
            .find(text("CN=role1")).parent().shouldHave(cssClass("ui-state-highlight"));
    $(By.id("directoryBrowser:chooseDirectoryName")).click();
    $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldNotBe(visible);
    $(By.id("roleInformationForm:externalSecurityName"))
            .shouldHave(value("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  @Test
  void externalSecurityName_ldapBrowser_initValue() {
    Navigation.toRoles();
    Tab.SECURITY_SYSTEM.switchToTab("test-ad");
    Navigation.toRoleDetail("test-ad", DETAIL_ROLE_NAME);
    $(By.id("roleInformationForm:externalSecurityName"))
            .sendKeys("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    $(By.id("roleInformationForm:browseExternalName")).shouldNotBe(disabled).click();

    $(By.id(DIRECTORY_BROWSER_DIALOG)).shouldBe(visible);
    $$(DIRECTORY_BROWSER_FORM + "tree .ui-treenode-label").find(exactText("CN=role1")).parent()
            .shouldBe(visible, cssClass("ui-state-highlight"));
    Table table = new Table(By.id("directoryBrowser:directoryBrowserForm:nodeAttrTable"));
    table.tableEntry("distinguishedName", 2)
            .shouldBe(exactText("CN=role1,OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  }

  private void clearRoleInfoInputs() {
    $(By.id("roleInformationForm:displayName")).clear();
    $(By.id("roleInformationForm:description")).clear();
    $(By.id("roleInformationForm:externalSecurityName")).clear();
  }
}
