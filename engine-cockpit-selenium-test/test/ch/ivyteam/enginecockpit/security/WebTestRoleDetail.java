package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlEndsWith;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.supplements.IvySelenide;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvySelenide
public class WebTestRoleDetail
{
  private static final String DETAIL_ROLE_NAME = "boss";
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toRoles();
    Tab.switchToTab("test");
    Navigation.toRoleDetail(DETAIL_ROLE_NAME);
  }
  
  @Test
  void testRoleDetailOpen()
  {
    assertCurrentUrlEndsWith("roledetail.xhtml?roleName=" + DETAIL_ROLE_NAME);
  }
  
  @Test
  void testSaveRoleInformation()
  {
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
    $("#roleInformationForm\\:externalSecurityName").shouldBe(exactValue("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  
    clearRoleInfoInputs();
    $("#roleInformationForm\\:saveRoleInformation").click();
  }
  
  @Test
  void testNewChildRole()
  {
    $("#roleInformationForm\\:createNewChildRole").click();
    $("#newChildRoleDialog").shouldBe(visible);
    
    $("#newChildRoleForm\\:saveNewRole").click();
    $("#newChildRoleForm\\:newRoleNameMessage").shouldBe(visible);
    $("#newChildRoleForm\\:newRoleNameMessage").shouldBe(text("Value is required"));
    
    String newRoleName = "test";
    $("#newChildRoleForm\\:newChildRoleNameInput").sendKeys(newRoleName);
    $("#newChildRoleForm\\:saveNewRole").click();
    assertCurrentUrlEndsWith("roledetail.xhtml?roleName=" + newRoleName);
    $("#roleInformationForm\\:name").shouldBe(exactText(newRoleName));
    
    $("#roleInformationForm\\:deleteRole").shouldBe(visible);
    $("#roleInformationForm\\:deleteRole").click();
    $("#roleInformationForm\\:deleteRoleConfirmDialog").shouldBe(visible);
    
    $("#roleInformationForm\\:deleteRoleConfirmDialogYesBtn").click();
    assertCurrentUrlEndsWith("roles.xhtml");
  }
  
  @Test
  void testAddAndRemoveUser()
  {
    String roleUsers = "#usersOfRoleForm\\:roleUserTable td.user-row";
    $$(roleUsers).shouldBe(empty);
    $("#usersOfRoleForm\\:addUserDropDown_input").sendKeys("fo");
    $$(".ui-autocomplete-list-item").shouldBe(sizeGreaterThan(0));
    $(".ui-autocomplete-list-item").click();
    $("#usersOfRoleForm\\:addUserDropDown_input").shouldBe(exactValue("foo"));
    
    $("#usersOfRoleForm\\:addUserToRoleBtn").click();
    $$(roleUsers).shouldBe(sizeGreaterThan(0));
    
    Selenide.refresh();
    $$(roleUsers).shouldBe(sizeGreaterThan(0));
    
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn").click();
    $$(roleUsers).shouldBe(empty);
  }
  
  @Test
  void testAddAndRemoveMember()
  {
    String roleMembers = "#membersOfRoleForm\\:roleMemberTable td.member-row";
    $$(roleMembers).shouldBe(empty);
    
    $("#membersOfRoleForm\\:addMemberDropDown_input").sendKeys("wor");
    $("#membersOfRoleForm\\:addMemberDropDown_panel").shouldBe(visible);
    $$(".ui-autocomplete-list-item").shouldBe(sizeGreaterThan(0));
    $(".ui-autocomplete-list-item").click();
    $("#membersOfRoleForm\\:addMemberDropDown_input").shouldBe(exactValue("worker"));
    
    $("#membersOfRoleForm\\:addMemberToRoleBtn").click();
    $$(roleMembers).shouldBe(sizeGreaterThan(0));
    
    Selenide.refresh();
    $$(roleMembers).shouldBe(sizeGreaterThan(0));
    
    $("#membersOfRoleForm\\:roleMemberTable\\:0\\:removeMemberFromRoleBtn").click();
    $$(roleMembers).shouldBe(empty);
  }
  
  @Test
  void testExternalSecurityName()
  {
    Navigation.toRoles();
    Tab.switchToTab("test-ad");
    Navigation.toRoleDetail(DETAIL_ROLE_NAME);
    
    $("#roleInformationForm\\:externalSecurityName").sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    $("#roleInformationForm\\:saveRoleInformation").click();
    
    Navigation.toRoles();
    String syncBtnId = "#form\\:card\\:tabs\\:applicationTabView\\:1\\:panelSyncBtn";
    $(syncBtnId).shouldBe(visible).click();
    $(syncBtnId).findAll("span").first().shouldHave(cssClass("fa-spin"));
    $(syncBtnId).findAll("span").first().shouldNotHave(cssClass("fa-spin"));
    
    Navigation.toRoleDetail(DETAIL_ROLE_NAME);
    
    $$("#usersOfRoleForm\\:roleUserTable td.user-row").shouldHave(size(3));
    $("#usersOfRoleForm\\:addUserDropDown_input").shouldHave(cssClass("ui-state-disabled"));
    $("#usersOfRoleForm\\:addUserToRoleBtn").shouldHave(cssClass("ui-state-disabled"));
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn").shouldHave(cssClass("ui-state-disabled"));
  
    $("#roleInformationForm\\:externalSecurityName").clear();
    $("#roleInformationForm\\:externalSecurityName").shouldBe(Condition.empty);
    $("#roleInformationForm\\:saveRoleInformation").click();
    Selenide.refresh();
    assertCurrentUrlEndsWith("roledetail.xhtml?roleName=" + DETAIL_ROLE_NAME);
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn").shouldNotHave(cssClass("ui-state-disabled"));
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn").click();
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn").click();
    $("#usersOfRoleForm\\:roleUserTable\\:0\\:removeUserFromRoleBtn").click();
  }

  private void clearRoleInfoInputs()
  {
    $("#roleInformationForm\\:displayName").clear();
    $("#roleInformationForm\\:description").clear();
    $("#roleInformationForm\\:externalSecurityName").clear();
  }
}
