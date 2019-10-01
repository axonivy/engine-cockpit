package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectManyCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectOneRadio;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestUserDetail extends WebTestBase
{
  private static final String DETAIL_USER_NAME = "foo";
  private static final String DETAIL_USER_NAME_DELETE = "bar";
  
  @Test
  void testUsersDetailOpen(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("userdetail.xhtml?userName=" + DETAIL_USER_NAME));
  }

  @Test
  void testUserDetailInformation(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    assertThat(driver.findElementById("userInformationForm:name").getText()).isEqualTo(DETAIL_USER_NAME);
  }
  
  @Test
  void testSaveUserInformation(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    clearUserInfoInputs(driver);
    driver.findElementById("userInformationForm:fullName").sendKeys("Foo User");
    driver.findElementById("userInformationForm:email").sendKeys("foo@ivyteam.ch");
    driver.findElementById("userInformationForm:password1").sendKeys("foopassword");
    driver.findElementById("userInformationForm:password2").sendKeys("foopassword");
    driver.findElementById("userInformationForm:saveUserInformation").click();
    saveScreenshot(driver, "save_user_changes");
    
    webAssertThat(() -> assertThat(driver.findElementById("userInformationForm:informationSaveSuccess_container").isDisplayed()).isTrue());
    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    webAssertThat(() -> assertThat(driver.findElementById("userInformationForm:name").getText()).isEqualTo(DETAIL_USER_NAME));
    webAssertThat(() -> assertThat(driver.findElementById("userInformationForm:fullName").getAttribute("value")).isEqualTo("Foo User"));
    webAssertThat(() -> assertThat(driver.findElementById("userInformationForm:email").getAttribute("value")).isEqualTo("foo@ivyteam.ch"));
    webAssertThat(() -> assertThat(driver.findElementById("userInformationForm:password1").getAttribute("value")).isEqualTo(""));
    webAssertThat(() -> assertThat(driver.findElementById("userInformationForm:password2").getAttribute("value")).isEqualTo(""));
  }
  
  @Test
  void testSaveUserInformationNoPasswordMatch(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    driver.findElementById("userInformationForm:password1").sendKeys("foopassword");
    driver.findElementById("userInformationForm:saveUserInformation").click();
    saveScreenshot(driver, "no_password_match");
    webAssertThat(() -> assertThat(driver.findElementById("userInformationForm:informationMessages").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("userInformationForm:informationMessages").getText()).isEqualTo("Password didn't match"));
  }
  
  @Test
  void testDeleteUser(FirefoxDriver driver)
  {
    openUserDetail(driver, DETAIL_USER_NAME_DELETE);
    driver.findElementById("userInformationForm:deleteUser").click();
    saveScreenshot(driver, "delete_user");
    webAssertThat(() -> assertThat(driver.findElementById("userInformationForm:deleteUserConfirmDialog").isDisplayed()).isTrue());
    driver.findElementById("userInformationForm:deleteUserConfirmYesBtn").click();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("users.xhtml"));
  }
  
  @Test
  void testEmailLanguageSwitch(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    driver.findElementById("userEmailForm:emailSettings:languageDropDown_label").click();
    webAssertThat(() -> assertThat(driver.findElementById("userEmailForm:emailSettings:languageDropDown_items").isDisplayed()).isTrue());
    String chooseLanguage = driver.findElementById("userEmailForm:emailSettings:languageDropDown_1").getText();
    saveScreenshot(driver, "languages");
    driver.findElementById("userEmailForm:emailSettings:languageDropDown_1").click();
    webAssertThat(() -> assertThat(driver.findElementById("userEmailForm:emailSettings:languageDropDown_label").getText()).isEqualTo(chooseLanguage));
    saveScreenshot(driver, "choose_language");
    driver.findElementById("userEmailForm:saveEmailNotificationSettings").click();
    webAssertThat(() -> assertThat(driver.findElementById("userEmailForm:emailSaveSuccess_container").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("userEmailForm:emailSaveSuccess_container").getText()).isEqualTo("User email changes saved"));
    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    webAssertThat(() -> assertThat(driver.findElementById("userEmailForm:emailSettings:languageDropDown_label").getText()).isEqualTo(chooseLanguage));
  }
  
  @Test
  void testEmailSettings(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    PrimeUi primeUi = new PrimeUi(driver);
    SelectOneRadio radioSettings = primeUi.selectOneRadio(By.id("userEmailForm:emailSettings:radioSettings"));
    SelectBooleanCheckbox neverCheckbox = primeUi.selectBooleanCheckbox(By.id("userEmailForm:emailSettings:neverCheckbox"));
    SelectBooleanCheckbox taskCheckbox = primeUi.selectBooleanCheckbox(By.id("userEmailForm:emailSettings:taskCheckbox"));
    SelectManyCheckbox dailyCheckbox = primeUi.selectManyCheckbox(By.id("userEmailForm:emailSettings:radioDailyNotification"));
    webAssertThat(() -> assertThat(radioSettings.getSelected()).isEqualTo("Application"));
    webAssertThat(() -> assertThat(neverCheckbox.isChecked()).isFalse());
    webAssertThat(() -> assertThat(neverCheckbox.isDisabled()).isTrue());
    webAssertThat(() -> assertThat(taskCheckbox.isChecked()).isFalse());
    webAssertThat(() -> assertThat(taskCheckbox.isDisabled()).isTrue());
    webAssertThat(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue());
    
    radioSettings.selectItemByValue("Specific");
    saveScreenshot(driver, "specific");
    webAssertThat(() -> assertThat(neverCheckbox.isDisabled()).isFalse());
    webAssertThat(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    webAssertThat(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    
    List<String> days = new ArrayList<String>(Arrays.asList("Mon", "Fri", "Sun"));
    dailyCheckbox.setCheckboxes(days);
    saveScreenshot(driver, "days");
    webAssertThat(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Mon", "Fri", "Sun"));
    
    taskCheckbox.setChecked();
    neverCheckbox.setChecked();
    saveScreenshot(driver, "new_settings");
    webAssertThat(() -> assertThat(radioSettings.getSelected()).isEqualTo("Specific"));
    webAssertThat(() -> assertThat(neverCheckbox.isChecked()).isTrue());
    webAssertThat(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    webAssertThat(() -> assertThat(taskCheckbox.isDisabled()).isTrue());
    webAssertThat(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue());
    driver.findElementById("userEmailForm:saveEmailNotificationSettings").click();
    webAssertThat(() -> assertThat(driver.findElementById("userEmailForm:emailSaveSuccess_container").isDisplayed()).isTrue());
    saveScreenshot(driver, "save");

    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    webAssertThat(() -> assertThat(radioSettings.getSelected()).isEqualTo("Specific"));
    webAssertThat(() -> assertThat(neverCheckbox.isChecked()).isTrue());
    webAssertThat(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    webAssertThat(() -> assertThat(taskCheckbox.isDisabled()).isTrue());
    webAssertThat(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue());
    webAssertThat(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Mon", "Fri", "Sun"));
  }
  
  @Test
  void testRolesAddRemove(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    WebElement boss = driver.findElement(By.xpath("//*[contains(@id, 'rolesOfUserForm:rolesTree_node_0')]/td/a/span[@class='role-name'][text()='boss']"));
    String bossId = boss.findElement(By.xpath("../../..")).getAttribute("id");
    driver.findElementById(bossId).findElement(By.xpath("./td/span[2]")).click();
    saveScreenshot(driver, "expand_role");
    String managerId = bossId + "_0";
    webAssertThat(() -> assertThat(driver.findElement(By.id(managerId)).isDisplayed()).isTrue());
    String managerAddButtonId = driver.findElementById(managerId).findElement(By.xpath("./td/button[1]")).getAttribute("id");
    String managerRemoveButtonId = driver.findElementById(managerId).findElement(By.xpath("./td/button[2]")).getAttribute("id");
    String bossAddButtonId = driver.findElementById(bossId).findElement(By.xpath("./td/button[1]")).getAttribute("id");
    String bossRemoveButtonId = driver.findElementById(bossId).findElement(By.xpath("./td/button[2]")).getAttribute("id");
    driver.findElementById(managerAddButtonId).click();
    saveScreenshot(driver, "add_child_role");
    webAssertThat(() -> assertThat(driver.findElementById(managerAddButtonId).getAttribute("class")).contains("ui-state-disabled"));
    webAssertThat(() -> assertThat(driver.findElementById(managerRemoveButtonId).getAttribute("class")).doesNotContain("ui-state-disabled"));
    webAssertThat(() -> assertThat(driver.findElementById(managerId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("fa-check"));
    webAssertThat(() -> assertThat(driver.findElementById(bossId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("member-inherit-icon"));
    
    driver.findElementById(bossAddButtonId).click();
    saveScreenshot(driver, "add_parent_role");
    webAssertThat(() -> assertThat(driver.findElementById(bossAddButtonId).getAttribute("class")).contains("ui-state-disabled"));
    webAssertThat(() -> assertThat(driver.findElementById(bossRemoveButtonId).getAttribute("class")).doesNotContain("ui-state-disabled"));
    webAssertThat(() -> assertThat(driver.findElementById(managerId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("fa-check"));
    webAssertThat(() -> assertThat(driver.findElementById(bossId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("fa-check").doesNotContain("member-inherit-icon"));
    
    Navigation.toUserDetail(driver, DETAIL_USER_NAME);
    saveScreenshot(driver, "refresh");
    waitUntilElementClickable(driver, By.xpath("//*[@id='" + bossId + "']/td/span[2]")).click();
    saveScreenshot(driver, "expand_boss");
    webAssertThat(() -> assertThat(driver.findElementById(managerId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("fa-check"));
    webAssertThat(() -> assertThat(driver.findElementById(bossId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("fa-check").doesNotContain("member-inherit-icon"));

    driver.findElementById(managerRemoveButtonId).click();
    saveScreenshot(driver, "remove_child_role");
    webAssertThat(() -> assertThat(driver.findElementById(managerAddButtonId).getAttribute("class")).doesNotContain("ui-state-disabled"));
    webAssertThat(() -> assertThat(driver.findElementById(managerRemoveButtonId).getAttribute("class")).contains("ui-state-disabled"));
    
    driver.findElementById(bossRemoveButtonId).click();
  }
  
  @Test
  void testPermission(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='permission-icon'][1]/i")
            .getAttribute("title")).isEqualTo("Some Permission granted"));
    
    driver.findElementById("permissionsForm:permissionTable:0:grantPermissionBtn").click();
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='permission-icon'][1]/i")
                    .getAttribute("title")).isEqualTo("Permission granted"));
    saveScreenshot(driver, "grant");

    driver.findElementById("permissionsForm:permissionTable:0:unGrantPermissionBtn").click();
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='permission-icon'][1]/i")
                    .getAttribute("title")).isEqualTo("Some Permission granted"));
    saveScreenshot(driver, "ungrant");

    driver.findElementById("permissionsForm:permissionTable:0:denyPermissionBtn").click();
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='permission-icon'][1]/i")
                    .getAttribute("title")).isEqualTo("Permission denied"));
    saveScreenshot(driver, "deny");

    driver.findElementById("permissionsForm:permissionTable:0:unDenyPermissionBtn").click();
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='permission-icon'][1]/i")
                    .getAttribute("title")).isEqualTo("Some Permission granted"));
    saveScreenshot(driver, "undeny");
  }
  
  private void clearUserInfoInputs(FirefoxDriver driver)
  {
    driver.findElementById("userInformationForm:fullName").clear();
    driver.findElementById("userInformationForm:email").clear();
    driver.findElementById("userInformationForm:password1").clear();
    driver.findElementById("userInformationForm:password2").clear();
  }
  
  private void openUserDetail(FirefoxDriver driver, String userName)
  {
    login(driver);
    Navigation.toUserDetail(driver, userName);
    saveScreenshot(driver, "userdetail");
  }
  
  private void openUserFooDetail(FirefoxDriver driver)
  {
    openUserDetail(driver, DETAIL_USER_NAME);
  }

}
