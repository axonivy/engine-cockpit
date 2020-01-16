package ch.ivyteam.enginecockpit.security;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.widget.SelectBooleanCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.widget.SelectManyCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.widget.SelectOneRadio;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

public class WebTestUserDetail extends WebTestBase
{
  private static final String CSS_MEMBER_INHERIT = "member-inherit-icon";
  private static final String CSS_MEMBER = "fa-check";
  private static final String CSS_DISABLED = "ui-state-disabled";
  private static final String ROLE_REMOVE_BUTTON = "button:nth-child(2)";
  private static final String ROLE_ADD_BUTTON = "button:nth-child(1)";
  private static final String ROLE_EXPANDER = "span:nth-child(2)";
  private static final String MEMBER_ICON = "td:nth-child(2) > i";
  private static final String DETAIL_USER_NAME = "foo";
  private static final String DETAIL_AD_USER_NAME = "user1";
  private static final String DETAIL_USER_NAME_DELETE = "bar";
  
  @Test
  void testUsersDetailOpen()
  {
    openUserFooDetail();
    assertCurrentUrlEndsWith("userdetail.xhtml?userName=" + DETAIL_USER_NAME);
  }

  @Test
  void testUserDetailInformation()
  {
    openUserFooDetail();
    $("#userInformationForm\\:name").shouldBe(exactText(DETAIL_USER_NAME));
  }
  
  @Test
  void testSaveUserInformation()
  {
    openUserFooDetail();
    clearUserInfoInputs();
    $("#userInformationForm\\:fullName").sendKeys("Foo User");
    $("#userInformationForm\\:email").sendKeys("foo@ivyteam.ch");
    $("#userInformationForm\\:password1").sendKeys("foopassword");
    $("#userInformationForm\\:password2").sendKeys("foopassword");
    $("#userInformationForm\\:saveUserInformation").click();
    
    $("#userInformationForm\\:informationSaveSuccess_container").shouldBe(visible);
    Selenide.refresh();
    $("#userInformationForm\\:name").shouldBe(exactText(DETAIL_USER_NAME));
    $("#userInformationForm\\:fullName").shouldBe(exactValue("Foo User"));
    $("#userInformationForm\\:email").shouldBe(exactValue("foo@ivyteam.ch"));
    $("#userInformationForm\\:password1").shouldBe(exactValue(""));
    $("#userInformationForm\\:password2").shouldBe(exactValue(""));
  }
  
  @Test
  void testSaveUserInformationNoPasswordMatch()
  {
    openUserFooDetail();
    $("#userInformationForm\\:password1").sendKeys("foopassword");
    $("#userInformationForm\\:saveUserInformation").click();
    $("#userInformationForm\\:informationMessages").shouldBe(visible);
    $("#userInformationForm\\:informationMessages").shouldBe(exactText("Password didn't match"));
  }
  
  @Test
  void testDeleteUser()
  {
    openUserDetail(DETAIL_USER_NAME_DELETE, "test");
    $("#userInformationForm\\:deleteUser").click();
    $("#userInformationForm\\:deleteUserConfirmDialog").shouldBe(visible);
    $("#userInformationForm\\:deleteUserConfirmYesBtn").click();
    assertCurrentUrlEndsWith("users.xhtml");
  }
  
  @Test
  void testEmailLanguageSwitch()
  {
    openUserFooDetail();
    $("#userEmailForm\\:emailSettings\\:languageDropDown_label").shouldBe(visible).click();
    $("#userEmailForm\\:emailSettings\\:languageDropDown_items").shouldBe(visible);
    String chooseLanguage = $("#userEmailForm\\:emailSettings\\:languageDropDown_1").getText();
    $("#userEmailForm\\:emailSettings\\:languageDropDown_1").click();
    $("#userEmailForm\\:emailSettings\\:languageDropDown_label").shouldBe(exactText(chooseLanguage));
    $("#userEmailForm\\:saveEmailNotificationSettings").click();
    $("#userEmailForm\\:emailSaveSuccess_container").shouldBe(visible, exactText("User email changes saved"));
    Selenide.refresh();
    $("#userEmailForm\\:emailSettings\\:languageDropDown_label").shouldBe(exactText(chooseLanguage));
  }
  
  @Test
  void testEmailSettings()
  {
    openUserFooDetail();
    SelectOneRadio radioSettings = PrimeUi.selectOneRadio(By.id("userEmailForm:emailSettings:radioSettings"));
    SelectBooleanCheckbox neverCheckbox = PrimeUi.selectBooleanCheckbox(By.id("userEmailForm:emailSettings:neverCheckbox"));
    SelectBooleanCheckbox taskCheckbox = PrimeUi.selectBooleanCheckbox(By.id("userEmailForm:emailSettings:taskCheckbox"));
    SelectManyCheckbox dailyCheckbox = PrimeUi.selectManyCheckbox(By.id("userEmailForm:emailSettings:radioDailyNotification"));
    assertThat(radioSettings.getSelectedValue()).isEqualTo("Application");
    assertThat(neverCheckbox.isChecked()).isFalse();
    assertThat(neverCheckbox.isDisabled()).isTrue();
    assertThat(taskCheckbox.isChecked()).isFalse();
    assertThat(taskCheckbox.isDisabled()).isTrue();
    assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue();
    
    radioSettings.selectItemByValue("Specific");
    assertThat(neverCheckbox.isDisabled()).isFalse();
    assertThat(taskCheckbox.isDisabled()).isFalse();
    assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse();
    
    List<String> days = new ArrayList<String>(Arrays.asList("Mon", "Fri", "Sun"));
    dailyCheckbox.setCheckboxes(days);
    assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Mon", "Fri", "Sun");
    
    taskCheckbox.setChecked();
    neverCheckbox.setChecked();
    assertThat(radioSettings.getSelectedValue()).isEqualTo("Specific");
    assertThat(neverCheckbox.isChecked()).isTrue();
    assertThat(taskCheckbox.isChecked()).isTrue();
    assertThat(taskCheckbox.isDisabled()).isTrue();
    assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue();
    $("#userEmailForm\\:saveEmailNotificationSettings").click();
    $("#userEmailForm\\:emailSaveSuccess_container").shouldBe(visible);

    Selenide.refresh();
    assertThat(radioSettings.getSelectedValue()).isEqualTo("Specific");
    assertThat(neverCheckbox.isChecked()).isTrue();
    assertThat(taskCheckbox.isChecked()).isTrue();
    assertThat(taskCheckbox.isDisabled()).isTrue();
    assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue();
    assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Mon", "Fri", "Sun");
  }
  
  @Test
  void testRolesAddRemove()
  {
    openUserFooDetail();
    String boss = Selenide.$$(".role-name").find(Condition.text("boss")).parent().parent().parent().getAttribute("id");
    By bossId = By.id(boss);
    $(bossId).find(ROLE_EXPANDER).click();
    By managerId = By.id(boss + "_0");
    $(managerId).shouldBe(visible);
    By managerAddButtonId = By.id($(managerId).find(ROLE_ADD_BUTTON).getAttribute("id"));
    By managerRemoveButtonId = By.id($(managerId).find(ROLE_REMOVE_BUTTON).getAttribute("id"));
    By bossAddButtonId = By.id($(bossId).find(ROLE_ADD_BUTTON).getAttribute("id"));
    By bossRemoveButtonId = By.id($(bossId).find(ROLE_REMOVE_BUTTON).getAttribute("id"));
    $(managerAddButtonId).click();
    $(managerAddButtonId).shouldHave(cssClass(CSS_DISABLED));
    $(managerRemoveButtonId).shouldNotHave(cssClass(CSS_DISABLED));
    $(managerId).find(MEMBER_ICON).shouldHave(cssClass(CSS_MEMBER));
    $(bossId).find(MEMBER_ICON).shouldHave(cssClass(CSS_MEMBER_INHERIT));
    
    $(bossAddButtonId).click();
    $(bossAddButtonId).shouldHave(cssClass(CSS_DISABLED));
    $(bossRemoveButtonId).shouldNotHave(cssClass(CSS_DISABLED));
    $(managerId).find(MEMBER_ICON).shouldHave(cssClass(CSS_MEMBER));
    $(bossId).find(MEMBER_ICON).shouldNotHave(cssClass(CSS_MEMBER_INHERIT));
    
    Navigation.toUserDetail(DETAIL_USER_NAME);
    $(bossId).find(ROLE_EXPANDER).shouldBe(visible, enabled).click();
    $(managerId).find(MEMBER_ICON).shouldHave(cssClass(CSS_MEMBER));
    $(bossId).find(MEMBER_ICON).shouldHave(cssClass(CSS_MEMBER)).shouldNotHave(cssClass(CSS_MEMBER_INHERIT));

    $(managerRemoveButtonId).click();
    $(managerAddButtonId).shouldNotHave(cssClass(CSS_DISABLED));
    $(managerRemoveButtonId).shouldHave(cssClass(CSS_DISABLED));
    
    $(bossRemoveButtonId).click();
  }
  
  @Test
  void testPermission()
  {
    openUserFooDetail();
    String permissionStateCss = "#permissionsForm\\:permissionTable_node_0 > .permission-icon > i";
    String firstPermissionCss = "#permissionsForm\\:permissionTable\\:0\\:";
    $(permissionStateCss).shouldHave(attribute("title", "Some Permission granted"));
    
    $(firstPermissionCss + "grantPermissionBtn").click();
    $(permissionStateCss).shouldHave(attribute("title", "Permission granted"));

    $(firstPermissionCss + "unGrantPermissionBtn").click();
    $(permissionStateCss).shouldHave(attribute("title", "Some Permission granted"));

    $(firstPermissionCss + "denyPermissionBtn").click();
    $(permissionStateCss).shouldHave(attribute("title", "Permission denied"));

    $(firstPermissionCss + "unDenyPermissionBtn").click();
    $(permissionStateCss).shouldHave(attribute("title", "Some Permission granted"));
  }

  @Test
  void testSynchronizeUser()
  {
    openADUserDetail();
    $("#userInformationForm\\:userSynchBtn").click();
    $("#synchUserForm").shouldBe(visible);
    $("#synchUserForm\\:userSynchName").shouldBe(disabled, value(DETAIL_AD_USER_NAME));
    $("#synchUserForm\\:synchUserVar").click();
    $("#synchUserForm\\:logViewer").shouldHave(text("INFO: User synchronization"));
  }

  private void clearUserInfoInputs()
  {
    $("#userInformationForm\\:fullName").clear();
    $("#userInformationForm\\:email").clear();
    $("#userInformationForm\\:password1").clear();
    $("#userInformationForm\\:password2").clear();
  }
  
  private void openUserDetail(String userName, String appName)
  {
    login();
    Navigation.toUsers();
    Tab.switchToTab(appName);
    Navigation.toUserDetail(userName);
  }
  
  private void openUserFooDetail()
  {
    openUserDetail(DETAIL_USER_NAME, "test");
  }

  private void openADUserDetail()
  {
    openUserDetail(DETAIL_AD_USER_NAME, "test-ad");
  }
}
