package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.ApplicationTab;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestRoleDetail extends WebTestBase
{
  private static final String DETAIL_ROLE_NAME = "boss";
  
  @Test
  void testRoleDetailOpen()
  {
    toRoleDetail();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("roledetail.xhtml?roleName=" + DETAIL_ROLE_NAME));
  }
  
  @Test
  void testSaveRoleInformation()
  {
    toRoleDetail();
    clearRoleInfoInputs();
    
    driver.findElementById("roleInformationForm:displayName").sendKeys("display");
    driver.findElementById("roleInformationForm:description").sendKeys("desc");
    driver.findElementById("roleInformationForm:externalSecurityName").sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    driver.findElementById("roleInformationForm:saveRoleInformation").click();
    saveScreenshot("save_user_changes");
    
    webAssertThat(() -> assertThat(driver.findElementById("roleInformationForm:informationSaveSuccess_container").isDisplayed()).isTrue());
    driver.navigate().refresh();
    saveScreenshot("refresh");
    
    webAssertThat(() -> assertThat(driver.findElementById("roleInformationForm:name").getText()).isEqualTo(DETAIL_ROLE_NAME));
    webAssertThat(() -> assertThat(driver.findElementById("roleInformationForm:displayName").getAttribute("value")).isEqualTo("display"));
    webAssertThat(() -> assertThat(driver.findElementById("roleInformationForm:description").getAttribute("value")).isEqualTo("desc"));
    webAssertThat(() -> assertThat(driver.findElementById("roleInformationForm:externalSecurityName").getAttribute("value")).isEqualTo("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  
    clearRoleInfoInputs();
    driver.findElementById("roleInformationForm:saveRoleInformation").click();
  }
  
  @Test
  void testNewChildRole()
  {
    toRoleDetail();
    
    driver.findElementById("roleInformationForm:createNewChildRole").click();
    webAssertThat(() -> assertThat(driver.findElementById("newChildRoleDialog").isDisplayed()).isTrue());
    saveScreenshot("newroledialog");
    
    driver.findElementById("newChildRoleForm:saveNewRole").click();
    webAssertThat(() -> assertThat(driver.findElementById("newChildRoleForm:newRoleNameMessage").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("newChildRoleForm:newRoleNameMessage").getText()).contains("Value is required"));
    saveScreenshot("newrole_namerequried");
    
    String newRoleName = "test";
    driver.findElementById("newChildRoleForm:newChildRoleNameInput").sendKeys(newRoleName);
    driver.findElementById("newChildRoleForm:saveNewRole").click();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("roledetail.xhtml?roleName=" + newRoleName));
    webAssertThat(() -> assertThat(driver.findElementById("roleInformationForm:name").getText()).isEqualTo(newRoleName));
    saveScreenshot("newroledetail");
    
    webAssertThat(() -> assertThat(driver.findElementById("roleInformationForm:deleteRole").isDisplayed()).isTrue());
    driver.findElementById("roleInformationForm:deleteRole").click();
    webAssertThat(() -> assertThat(driver.findElementById("roleInformationForm:deleteRoleConfirmDialog").isDisplayed()).isTrue());
    saveScreenshot("delete_role");
    
    driver.findElementById("roleInformationForm:deleteRoleConfirmDialogYesBtn").click();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("roles.xhtml"));
    saveScreenshot("roles");
  }
  
  @Test
  void testAddAndRemoveUser()
  {
    toRoleDetail();
    
    String roleUsers = "//*[@id='usersOfRoleForm:roleUserTable']//*[@class='user-row']";
    webAssertThat(() -> assertThat(driver.findElementsByXPath(roleUsers)).isEmpty());
    
    driver.findElementById("usersOfRoleForm:addUserDropDown_input").sendKeys("fo");
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-autocomplete-list-item")).isNotEmpty());
    saveScreenshot("search_autocomplete");
    driver.findElementByClassName("ui-autocomplete-list-item").click();
    webAssertThat(() -> assertThat(driver.findElementById("usersOfRoleForm:addUserDropDown_input").getAttribute("value")).isEqualTo("foo"));
    saveScreenshot("search_user");
    
    driver.findElementById("usersOfRoleForm:addUserToRoleBtn").click();
    webAssertThat(() -> assertThat(driver.findElementsByXPath(roleUsers)).isNotEmpty());
    saveScreenshot("add_user");
    
    driver.navigate().refresh();
    webAssertThat(() -> assertThat(driver.findElementsByXPath(roleUsers)).isNotEmpty());
    saveScreenshot("refresh");
    
    driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").click();
    webAssertThat(() -> assertThat(driver.findElementsByXPath(roleUsers)).isEmpty());
    saveScreenshot("remove_user");
  }
  
  @Test
  void testAddAndRemoveMember()
  {
    toRoleDetail();
    
    String roleMembers = "//*[@id='membersOfRoleForm:roleMemberTable']//*[@class='member-row']";
    webAssertThat(() -> assertThat(driver.findElementsByXPath(roleMembers)).isEmpty());
    
    driver.findElementById("membersOfRoleForm:addMemberDropDown_input").sendKeys("wor");
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-autocomplete-list-item")).isNotEmpty());
    saveScreenshot("search_autocomplete");
    driver.findElementByClassName("ui-autocomplete-list-item").click();
    webAssertThat(() -> assertThat(driver.findElementById("membersOfRoleForm:addMemberDropDown_input").getAttribute("value")).isEqualTo("worker"));
    saveScreenshot("search_member");
    
    driver.findElementById("membersOfRoleForm:addMemberToRoleBtn").click();
    webAssertThat(() -> assertThat(driver.findElementsByXPath(roleMembers)).isNotEmpty());
    saveScreenshot("add_member");
    
    driver.navigate().refresh();
    webAssertThat(() -> assertThat(driver.findElementsByXPath(roleMembers)).isNotEmpty());
    saveScreenshot("refresh");
    
    driver.findElementById("membersOfRoleForm:roleMemberTable:0:removeMemberFromRoleBtn").click();
    webAssertThat(() -> assertThat(driver.findElementsByXPath(roleMembers)).isEmpty());
    saveScreenshot("remove_member");
  }
  
  @Test
  void testExternalSecurityName()
  {
    login();
    Navigation.toRoles(driver);
    saveScreenshot("roles");
    
    ApplicationTab.switchToApplication(driver, "test-ad");
    saveScreenshot("switch_to_ad_app");
    
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    saveScreenshot("roledetail");
    
    driver.findElementById("roleInformationForm:externalSecurityName").sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    driver.findElementById("roleInformationForm:saveRoleInformation").click();
    saveScreenshot("save_user_changes");
    
    Navigation.toRoles(driver);
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[contains(@id, 'applicationTabView:1:panelSyncBtn')]").isDisplayed()).isTrue());
    String syncBtnId = driver.findElementByXPath("//*[contains(@id, 'applicationTabView:1:panelSyncBtn')]").getAttribute("id");
    driver.findElementById(syncBtnId).click();
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@id='" + syncBtnId + "']/span[1]").getAttribute("class")).contains("fa-spin"));
    saveScreenshot("trigger_roles_sync");
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@id='" + syncBtnId + "']/span[1]").getAttribute("class")).doesNotContain("fa-spin"));
    saveScreenshot("finish_roles_sync");
    
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    saveScreenshot("roledetail");
    
    String roleUsers = "//*[@id='usersOfRoleForm:roleUserTable']//*[@class='user-row']";
    webAssertThat(() -> assertThat(driver.findElementsByXPath(roleUsers)).hasSize(3));
    webAssertThat(() -> assertThat(driver.findElementById("usersOfRoleForm:addUserDropDown_input").getAttribute("class")).contains("ui-state-disabled"));
    webAssertThat(() -> assertThat(driver.findElementById("usersOfRoleForm:addUserToRoleBtn").getAttribute("class")).contains("ui-state-disabled"));
    webAssertThat(() -> assertThat(driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").getAttribute("class")).contains("ui-state-disabled"));
  
    driver.findElementById("roleInformationForm:externalSecurityName").clear();
    webAssertThat(() -> assertThat(driver.findElementById("roleInformationForm:externalSecurityName").getAttribute("value")).isEmpty());
    driver.findElementById("roleInformationForm:saveRoleInformation").click();
    saveScreenshot("remove_external");
    driver.navigate().refresh();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("roledetail.xhtml?roleName=" + DETAIL_ROLE_NAME));
    saveScreenshot("refresh_before_cleanup");
    webAssertThat(() -> assertThat(driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").getAttribute("class")).doesNotContain("ui-state-disabled"));
    driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").click();
    driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").click();
    driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").click();
    saveScreenshot("cleanup");
  }

  private void toRoleDetail()
  {
    login();
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    saveScreenshot("roledetail");
  }
  
  private void clearRoleInfoInputs()
  {
    driver.findElementById("roleInformationForm:displayName").clear();
    driver.findElementById("roleInformationForm:description").clear();
    driver.findElementById("roleInformationForm:externalSecurityName").clear();
  }
}
