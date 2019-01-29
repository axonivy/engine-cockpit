package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.ApplicationTab;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestRoleDetail extends WebTestBase
{
  private static final String DETAIL_ROLE_NAME = "boss";
  
  @Test
  void testRoleDetailOpen(FirefoxDriver driver)
  {
    toRoleDetail(driver);
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("roledetail.xhtml?roleName=" + DETAIL_ROLE_NAME));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Role Detail"));
  }
  
  @Test
  void testSaveRoleInformation(FirefoxDriver driver)
  {
    toRoleDetail(driver);
    clearRoleInfoInputs(driver);
    
    driver.findElementById("roleInformationForm:displayName").sendKeys("display");
    driver.findElementById("roleInformationForm:description").sendKeys("desc");
    driver.findElementById("roleInformationForm:externalSecurityName").sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    driver.findElementById("roleInformationForm:saveRoleInformation").click();
    saveScreenshot(driver, "save_user_changes");
    
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:informationSaveSuccess_container").isDisplayed()).isTrue());
    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:name").getAttribute("value")).isEqualTo(DETAIL_ROLE_NAME));
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:displayName").getAttribute("value")).isEqualTo("display"));
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:description").getAttribute("value")).isEqualTo("desc"));
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:externalSecurityName").getAttribute("value")).isEqualTo("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  
    clearRoleInfoInputs(driver);
    driver.findElementById("roleInformationForm:saveRoleInformation").click();
  }
  
  @Test
  void testNewChildRole(FirefoxDriver driver)
  {
    toRoleDetail(driver);
    
    driver.findElementById("roleInformationForm:createNewChildRole").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("newChildRoleDialog").isDisplayed()).isTrue());
    saveScreenshot(driver, "newroledialog");
    
    driver.findElementById("newChildRoleForm:saveNewRole").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("newChildRoleForm:newRoleNameMessage").isDisplayed()).isTrue());
    await().untilAsserted(() -> assertThat(driver.findElementById("newChildRoleForm:newRoleNameMessage").getText()).contains("Value is required"));
    saveScreenshot(driver, "newrole_namerequried");
    
    String newRoleName = "test";
    driver.findElementById("newChildRoleForm:newChildRoleNameInput").sendKeys(newRoleName);
    driver.findElementById("newChildRoleForm:saveNewRole").click();
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("roledetail.xhtml?roleName=" + newRoleName));
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:name").getAttribute("value")).isEqualTo(newRoleName));
    saveScreenshot(driver, "newroledetail");
    
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:deleteRole").isDisplayed()).isTrue());
    driver.findElementById("roleInformationForm:deleteRole").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:deleteRoleConfirmDialog").isDisplayed()).isTrue());
    saveScreenshot(driver, "delete_role");
    
    driver.findElementById("roleInformationForm:deleteRoleConfirmDialogYesBtn").click();
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("roles.xhtml"));
    saveScreenshot(driver, "roles");
  }
  
  @Test
  void testAddAndRemoveUser(FirefoxDriver driver)
  {
    toRoleDetail(driver);
    
    String roleUsers = "//*[@id='usersOfRoleForm:roleUserTable']//*[@class='user-row']";
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleUsers)).isEmpty());
    
    driver.findElementById("usersOfRoleForm:addUserDropDown_input").sendKeys("fo");
    await().untilAsserted(() -> assertThat(driver.findElementsByClassName("ui-autocomplete-list-item")).isNotEmpty());
    saveScreenshot(driver, "search_autocomplete");
    driver.findElementByClassName("ui-autocomplete-list-item").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("usersOfRoleForm:addUserDropDown_input").getAttribute("value")).isEqualTo("foo"));
    saveScreenshot(driver, "search_user");
    
    driver.findElementById("usersOfRoleForm:addUserToRoleBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleUsers)).isNotEmpty());
    saveScreenshot(driver, "add_user");
    
    driver.navigate().refresh();
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleUsers)).isNotEmpty());
    saveScreenshot(driver, "refresh");
    
    driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleUsers)).isEmpty());
    saveScreenshot(driver, "remove_user");
  }
  
  @Test
  void testAddAndRemoveMember(FirefoxDriver driver)
  {
    toRoleDetail(driver);
    
    String roleMembers = "//*[@id='membersOfRoleForm:roleMemberTable']//*[@class='member-row']";
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleMembers)).isEmpty());
    
    driver.findElementById("membersOfRoleForm:addMemberDropDown_input").sendKeys("wor");
    await().untilAsserted(() -> assertThat(driver.findElementsByClassName("ui-autocomplete-list-item")).isNotEmpty());
    saveScreenshot(driver, "search_autocomplete");
    driver.findElementByClassName("ui-autocomplete-list-item").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("membersOfRoleForm:addMemberDropDown_input").getAttribute("value")).isEqualTo("worker"));
    saveScreenshot(driver, "search_member");
    
    driver.findElementById("membersOfRoleForm:addMemberToRoleBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleMembers)).isNotEmpty());
    saveScreenshot(driver, "add_member");
    
    driver.navigate().refresh();
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleMembers)).isNotEmpty());
    saveScreenshot(driver, "refresh");
    
    driver.findElementById("membersOfRoleForm:roleMemberTable:0:removeMemberFromRoleBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleMembers)).isEmpty());
    saveScreenshot(driver, "remove_member");
  }
  
  @Test
  void testExternalSecurityName(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRoles(driver);
    saveScreenshot(driver, "roles");
    
    if (ApplicationTab.getApplicationCount(driver) == 1)
    {
      return; //Don't run in designer
    }
    ApplicationTab.switchToApplication(driver, "test-ad");
    saveScreenshot(driver, "switch_to_ad_app");
    
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    saveScreenshot(driver, "roledetail");
    
    driver.findElementById("roleInformationForm:externalSecurityName").sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    driver.findElementById("roleInformationForm:saveRoleInformation").click();
    saveScreenshot(driver, "save_user_changes");
    
    Navigation.toRoles(driver);
    await().untilAsserted(() -> assertThat(driver.findElementByXPath("//*[contains(@id, 'applicationTabView:1:panelSyncBtn')]").isDisplayed()).isTrue());
    String syncBtnId = driver.findElementByXPath("//*[contains(@id, 'applicationTabView:1:panelSyncBtn')]").getAttribute("id");
    driver.findElementById(syncBtnId).click();
    await().untilAsserted(() -> assertThat(driver.findElementByXPath("//*[@id='" + syncBtnId + "']/span[1]").getAttribute("class")).contains("fa-spin"));
    saveScreenshot(driver, "trigger_roles_sync");
    await().untilAsserted(() -> assertThat(driver.findElementByXPath("//*[@id='" + syncBtnId + "']/span[1]").getAttribute("class")).doesNotContain("fa-spin"));
    saveScreenshot(driver, "finish_roles_sync");
    
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    saveScreenshot(driver, "roledetail");
    
    String roleUsers = "//*[@id='usersOfRoleForm:roleUserTable']//*[@class='user-row']";
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleUsers)).hasSize(3));
    await().untilAsserted(() -> assertThat(driver.findElementById("usersOfRoleForm:addUserDropDown_input").getAttribute("class")).contains("ui-state-disabled"));
    await().untilAsserted(() -> assertThat(driver.findElementById("usersOfRoleForm:addUserToRoleBtn").getAttribute("class")).contains("ui-state-disabled"));
    await().untilAsserted(() -> assertThat(driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").getAttribute("class")).contains("ui-state-disabled"));
  
    driver.findElementById("roleInformationForm:externalSecurityName").clear();
    driver.findElementById("roleInformationForm:saveRoleInformation").click();
    driver.navigate().refresh();
    await().untilAsserted(() -> assertThat(driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").getAttribute("class")).doesNotContain("ui-state-disabled"));
    saveScreenshot(driver, "refresh_before_cleanup");
    driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").click();
    driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").click();
    driver.findElementById("usersOfRoleForm:roleUserTable:0:removeUserFromRoleBtn").click();
    saveScreenshot(driver, "cleanup");
  }

  private void toRoleDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    saveScreenshot(driver, "roledetail");
  }
  
  private void clearRoleInfoInputs(FirefoxDriver driver)
  {
    driver.findElementById("roleInformationForm:displayName").clear();
    driver.findElementById("roleInformationForm:description").clear();
    driver.findElementById("roleInformationForm:externalSecurityName").clear();
  }
}
