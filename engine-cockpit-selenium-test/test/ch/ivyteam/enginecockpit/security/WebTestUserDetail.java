package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
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
    await().untilAsserted(() -> assertThat(radioSettings.getSelected()).isEqualTo("Application"));
    await().untilAsserted(() -> assertThat(neverCheckbox.isChecked()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isFalse());
    
    radioSettings.selectItemByValue("Specific");
    taskCheckbox.setChecked();
    neverCheckbox.setChecked();
    saveScreenshot(driver, "new_settings");
    await().untilAsserted(() -> assertThat(radioSettings.getSelected()).isEqualTo("Specific"));
    await().untilAsserted(() -> assertThat(neverCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    driver.findElementById("userEmailForm:saveEmailNotificationSettings").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("userEmailForm:emailSaveSuccess_container").isDisplayed()).isTrue());
    //Todo: assert disabled, assert selectmanycheckboxes(days)
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
