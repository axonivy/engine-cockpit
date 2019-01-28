package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
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
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("userdetail.xhtml?userName=" + DETAIL_USER_NAME));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("User Detail"));
  }

  @Test
  void testUserDetailInformation(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    assertThat(driver.findElementById("userInformationForm:name").getAttribute("value")).isEqualTo(DETAIL_USER_NAME);
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
    
    await().untilAsserted(() -> assertThat(driver.findElementById("userInformationForm:informationSaveSuccess_container").isDisplayed()).isTrue());
    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    await().untilAsserted(() -> assertThat(driver.findElementById("userInformationForm:name").getAttribute("value")).isEqualTo(DETAIL_USER_NAME));
    await().untilAsserted(() -> assertThat(driver.findElementById("userInformationForm:fullName").getAttribute("value")).isEqualTo("Foo User"));
    await().untilAsserted(() -> assertThat(driver.findElementById("userInformationForm:email").getAttribute("value")).isEqualTo("foo@ivyteam.ch"));
    await().untilAsserted(() -> assertThat(driver.findElementById("userInformationForm:password1").getAttribute("value")).isEqualTo(""));
    await().untilAsserted(() -> assertThat(driver.findElementById("userInformationForm:password2").getAttribute("value")).isEqualTo(""));
  }
  
  @Test
  void testSaveUserInformationNoPasswordMatch(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    driver.findElementById("userInformationForm:password1").sendKeys("foopassword");
    driver.findElementById("userInformationForm:saveUserInformation").click();
    saveScreenshot(driver, "no_password_match");
    await().untilAsserted(() -> assertThat(driver.findElementById("userInformationForm:informationMessages").isDisplayed()).isTrue());
    await().untilAsserted(() -> assertThat(driver.findElementById("userInformationForm:informationMessages").getText()).isEqualTo("Password didn't match"));
  }
  
  @Test
  void testDeleteUser(FirefoxDriver driver)
  {
    openUserDetail(driver, DETAIL_USER_NAME_DELETE);
    driver.findElementById("userInformationForm:deleteUser").click();
    saveScreenshot(driver, "delete_user");
    await().untilAsserted(() -> assertThat(driver.findElementById("userInformationForm:deleteUserConfirmDialog").isDisplayed()).isTrue());
    driver.findElementById("userInformationForm:deleteUserConfirmYesBtn").click();
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("users.xhtml"));
  }
  
  @Test
  void testEmailLanguageSwitch(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    driver.findElementById("userEmailForm:languageDropDown_label").click();
    await().until(() -> driver.findElementById("userEmailForm:languageDropDown_items").isDisplayed());
    String chooseLanguage = driver.findElementById("userEmailForm:languageDropDown_0").getText();
    saveScreenshot(driver, "languages");
    driver.findElementById("userEmailForm:languageDropDown_0").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("userEmailForm:languageDropDown_label").getText()).isEqualTo(chooseLanguage));
    saveScreenshot(driver, "choose_language");
    driver.findElementById("userEmailForm:saveEmailNotificationSettings").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("userEmailForm:emailSaveSuccess_container").isDisplayed()).isTrue());
    await().untilAsserted(() -> assertThat(driver.findElementById("userEmailForm:emailSaveSuccess_container").getText()).isEqualTo("User email changes saved"));
    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    await().untilAsserted(() -> assertThat(driver.findElementById("userEmailForm:languageDropDown_label").getText()).isEqualTo(chooseLanguage));
  }
  
  @Test
  void testEmailSettings(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    PrimeUi primeUi = new PrimeUi(driver);
    SelectOneRadio radioSettings = primeUi.selectOneRadio(new By.ById("userEmailForm:radioSettings"));
    SelectBooleanCheckbox neverCheckbox = primeUi.selectBooleanCheckbox(new By.ById("userEmailForm:neverCheckbox"));
    SelectBooleanCheckbox taskCheckbox = primeUi.selectBooleanCheckbox(new By.ById("userEmailForm:taskCheckbox"));
    SelectManyCheckbox dailyCheckbox = primeUi.selectManyCheckbox(By.id("userEmailForm:radioDailyNotification"));
    await().untilAsserted(() -> assertThat(radioSettings.getSelected()).isEqualTo("Application"));
    await().untilAsserted(() -> assertThat(neverCheckbox.isChecked()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isTrue());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isTrue());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue());
    
    radioSettings.selectItemByValue("Specific");
    saveScreenshot(driver, "specific");
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    
    List<String> days = new ArrayList<String>(Arrays.asList("Mon", "Fri", "Sun"));
    dailyCheckbox.setCheckboxes(days);
    saveScreenshot(driver, "days");
    await().untilAsserted(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Mon", "Fri", "Sun"));
    
    taskCheckbox.setChecked();
    neverCheckbox.setChecked();
    saveScreenshot(driver, "new_settings");
    await().untilAsserted(() -> assertThat(radioSettings.getSelected()).isEqualTo("Specific"));
    await().untilAsserted(() -> assertThat(neverCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isTrue());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue());
    driver.findElementById("userEmailForm:saveEmailNotificationSettings").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("userEmailForm:emailSaveSuccess_container").isDisplayed()).isTrue());
    saveScreenshot(driver, "save");

    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    await().untilAsserted(() -> assertThat(radioSettings.getSelected()).isEqualTo("Specific"));
    await().untilAsserted(() -> assertThat(neverCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isTrue());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue());
    await().untilAsserted(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Mon", "Fri", "Sun"));
  }
  
  @Test
  void testRolesAddRemove(FirefoxDriver driver)
  {
    openUserFooDetail(driver);
    WebElement boss = driver.findElement(By.xpath("//*[contains(@id, 'rolesOfUserForm:rolesTree_node_0')]/td/span[@class='role-name'][text()='boss']"));
    String bossId = boss.findElement(By.xpath("../..")).getAttribute("id");
    driver.findElementById(bossId).findElement(By.xpath("./td/span[2]")).click();
    saveScreenshot(driver, "expand_role");
    String managerId = bossId + "_0";
    await().untilAsserted(() -> assertThat(driver.findElement(By.id(managerId)).isDisplayed()).isTrue());
    String managerAddButtonId = driver.findElementById(managerId).findElement(By.xpath("./td/button[1]")).getAttribute("id");
    String managerRemoveButtonId = driver.findElementById(managerId).findElement(By.xpath("./td/button[2]")).getAttribute("id");
    String bossAddButtonId = driver.findElementById(bossId).findElement(By.xpath("./td/button[1]")).getAttribute("id");
    String bossRemoveButtonId = driver.findElementById(bossId).findElement(By.xpath("./td/button[2]")).getAttribute("id");
    driver.findElementById(managerAddButtonId).click();
    saveScreenshot(driver, "add_child_role");
    await().untilAsserted(() -> assertThat(driver.findElementById(managerAddButtonId).getAttribute("class")).contains("ui-state-disabled"));
    await().untilAsserted(() -> assertThat(driver.findElementById(managerRemoveButtonId).getAttribute("class")).doesNotContain("ui-state-disabled"));
    await().untilAsserted(() -> assertThat(driver.findElementById(managerId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("fa-check"));
    await().untilAsserted(() -> assertThat(driver.findElementById(bossId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("member-inherit-icon"));
    
    driver.findElementById(bossAddButtonId).click();
    saveScreenshot(driver, "add_parent_role");
    await().untilAsserted(() -> assertThat(driver.findElementById(bossAddButtonId).getAttribute("class")).contains("ui-state-disabled"));
    await().untilAsserted(() -> assertThat(driver.findElementById(bossRemoveButtonId).getAttribute("class")).doesNotContain("ui-state-disabled"));
    await().untilAsserted(() -> assertThat(driver.findElementById(managerId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("fa-check"));
    await().untilAsserted(() -> assertThat(driver.findElementById(bossId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("fa-check").doesNotContain("member-inherit-icon"));
    
    driver.navigate().refresh();
    driver.findElementById(bossId).findElement(By.xpath("./td/span[2]")).click();
    saveScreenshot(driver, "refresh");
    await().untilAsserted(() -> assertThat(driver.findElementById(managerId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("fa-check"));
    await().untilAsserted(() -> assertThat(driver.findElementById(bossId)
            .findElement(By.xpath("./td[2]/i")).getAttribute("class")).contains("fa-check").doesNotContain("member-inherit-icon"));

    driver.findElementById(managerRemoveButtonId).click();
    saveScreenshot(driver, "remove_child_role");
    await().untilAsserted(() -> assertThat(driver.findElementById(managerAddButtonId).getAttribute("class")).doesNotContain("ui-state-disabled"));
    await().untilAsserted(() -> assertThat(driver.findElementById(managerRemoveButtonId).getAttribute("class")).contains("ui-state-disabled"));
  }
  
  @Test
  void testPermission(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toUserDetail(driver, DETAIL_USER_NAME);
    saveScreenshot(driver, "userdetail");
    await().untilAsserted(() -> assertThat(driver.findElementByXPath("//*[@class='permission-icon'][1]/i")
            .getAttribute("title")).isEqualTo("Some Permission granted"));
    
    driver.findElementById("permissionsForm:permissionTable:0:grantPermissionBtn").click();
    await().ignoreExceptionsInstanceOf(StaleElementReferenceException.class)
            .untilAsserted(() -> assertThat(driver.findElementByXPath("//*[@class='permission-icon'][1]/i")
                    .getAttribute("title")).isEqualTo("Permission granted"));
    saveScreenshot(driver, "grant");

    driver.findElementById("permissionsForm:permissionTable:0:unGrantPermissionBtn").click();
    await().ignoreExceptionsInstanceOf(StaleElementReferenceException.class)
            .untilAsserted(() -> assertThat(driver.findElementByXPath("//*[@class='permission-icon'][1]/i")
                    .getAttribute("title")).isEqualTo("Some Permission granted"));
    saveScreenshot(driver, "ungrant");

    driver.findElementById("permissionsForm:permissionTable:0:denyPermissionBtn").click();
    await().ignoreExceptionsInstanceOf(StaleElementReferenceException.class)
            .untilAsserted(() -> assertThat(driver.findElementByXPath("//*[@class='permission-icon'][1]/i")
                    .getAttribute("title")).isEqualTo("Permission denied"));
    saveScreenshot(driver, "deny");

    driver.findElementById("permissionsForm:permissionTable:0:unDenyPermissionBtn").click();
    await().ignoreExceptionsInstanceOf(StaleElementReferenceException.class)
            .untilAsserted(() -> assertThat(driver.findElementByXPath("//*[@class='permission-icon'][1]/i")
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
    Navigation.toUsers(driver);
    saveScreenshot(driver, "users");
    WebElement user = getUser(driver, userName);
    user.click();
    saveScreenshot(driver, "userdetail");
    await().until(() -> driver.getCurrentUrl().endsWith("userdetail.xhtml?userName=" + userName)); 
  }
  
  private void openUserFooDetail(FirefoxDriver driver)
  {
    openUserDetail(driver, DETAIL_USER_NAME);
  }

  private WebElement getUser(FirefoxDriver driver, String user)
  {
    Optional<WebElement> foundUser = driver.findElements(new By.ByXPath(("//div[contains(@class, 'ui-tabs-panel')]//*[@class='user-name']")))
            .stream()
            .filter(e -> e.getText().equals(user)).findAny();
    assertThat(foundUser).isPresent();
    return foundUser.get();
  }
}
