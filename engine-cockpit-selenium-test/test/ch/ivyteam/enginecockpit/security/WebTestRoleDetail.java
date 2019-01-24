package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestRoleDetail extends WebTestBase
{
  private static final String DETAIL_ROLE_NAME = "boss";
  
  @Test
  void testRoleDetailOpen(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    saveScreenshot(driver, "roledetail");
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("roledetail.xhtml?roleName=" + DETAIL_ROLE_NAME));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Role Detail"));
  }
  
  @Test
  void testSaveRoleInformation(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
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
    login(driver);
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    saveScreenshot(driver, "roledetail");
    
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
  @Disabled
  void testAddRemoveUser(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    saveScreenshot(driver, "roledetail");
    
    String roleUsers = "//*[@id='usersOfRoleForm:roleUserTable']//*[@class='user-row']";
    String allUsers = "//*[@id='usersOfRoleForm:allUserTable']//*[@class='user-row']";
    
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleUsers)).isEmpty());
    List<WebElement> allUsersInTable = driver.findElementsByXPath(allUsers);
    await().untilAsserted(() -> assertThat(allUsersInTable).isNotEmpty());
    WebElement addUser = driver.findElementsByXPath(allUsers).stream()
            .filter(e -> e.findElement(By.className("user-name")).getText().equals("foo"))
            .findAny().get();
    addUser.findElement(By.tagName("button")).click();
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleUsers)).hasSize(1));
    await().untilAsserted(() -> assertThat(allUsersInTable.size())
            .isGreaterThan(driver.findElementsByXPath(allUsers).size()));
    saveScreenshot(driver, "adduser");
    
    driver.navigate().refresh();
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleUsers)).hasSize(1));
    await().untilAsserted(() -> assertThat(allUsersInTable.size())
            .isGreaterThan(driver.findElementsByXPath(allUsers).size()));
    
    WebElement removeUser = driver.findElementsByXPath(roleUsers).stream()
            .filter(e -> e.findElement(By.className("user-name")).getText().equals("foo"))
            .findAny().get();
    removeUser.findElement(By.tagName("button")).click();
    await().untilAsserted(() -> assertThat(driver.findElementsByXPath(roleUsers)).isEmpty());
    await().untilAsserted(() -> assertThat(allUsersInTable.size())
            .isEqualTo(driver.findElementsByXPath(allUsers).size()));
  }
  
  private void clearRoleInfoInputs(FirefoxDriver driver)
  {
    driver.findElementById("roleInformationForm:displayName").clear();
    driver.findElementById("roleInformationForm:description").clear();
    driver.findElementById("roleInformationForm:externalSecurityName").clear();
  }
}
