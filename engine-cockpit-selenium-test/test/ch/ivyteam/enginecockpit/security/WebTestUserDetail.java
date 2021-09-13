package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlEndsWith;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectBooleanCheckbox;
import com.axonivy.ivy.webtest.primeui.widget.SelectManyCheckbox;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneMenu;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneRadio;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestUserDetail {
  private static final String CSS_MEMBER_INHERIT = "member-inherit-icon";
  private static final String CSS_MEMBER = "si-check-circle";
  private static final String CSS_DISABLED = "ui-state-disabled";
  private static final String ROLE_REMOVE_BUTTON = "button:nth-child(2)";
  private static final String ROLE_ADD_BUTTON = "button:nth-child(1)";
  private static final String ROLE_EXPANDER = "span:nth-child(2)";
  private static final String MEMBER_ICON = "td:nth-child(2) > i";
  private static final String USER_FOO = "foo";
  private static final String USER_BAR = "bar";
  private static final String USER_AD = "user1";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toUsers();
    Tab.switchToDefault();
  }

  @Test
  void testUsersDetailOpen() {
    Navigation.toUserDetail(USER_FOO);
    assertCurrentUrlEndsWith("userdetail.xhtml?userName=" + USER_FOO);
  }

  @Test
  void testUserDetailInformation() {
    Navigation.toUserDetail(USER_FOO);
    $("#userInformationForm\\:name").shouldBe(exactText(USER_FOO));
  }

  @Test
  void testSaveUserInformation() {
    Navigation.toUserDetail(USER_FOO);
    clearUserInfoInputs();
    $("#userInformationForm\\:fullName").sendKeys("Foo User");
    $("#userInformationForm\\:email").sendKeys("foo@ivyteam.ch");
    $("#userInformationForm\\:password1").sendKeys("foo");
    $("#userInformationForm\\:password2").sendKeys("foo");
    $("#userInformationForm\\:saveUserInformation").click();

    $("#userInformationForm\\:informationSaveSuccess_container").shouldBe(visible);
    Selenide.refresh();
    $("#userInformationForm\\:name").shouldBe(exactText(USER_FOO));
    $("#userInformationForm\\:fullName").shouldBe(exactValue("Foo User"));
    $("#userInformationForm\\:email").shouldBe(exactValue("foo@ivyteam.ch"));
    $("#userInformationForm\\:password1").shouldBe(exactValue(""));
    $("#userInformationForm\\:password2").shouldBe(exactValue(""));

    clearUserInfoInputs();
    $("#userInformationForm\\:fullName").sendKeys("Marcelo Footer");
    $("#userInformationForm\\:email").sendKeys("m.footer@ivyteam.ch");
    $("#userInformationForm\\:password1").sendKeys("foo");
    $("#userInformationForm\\:password2").sendKeys("foo");
    $("#userInformationForm\\:saveUserInformation").click();

    $("#userInformationForm\\:informationSaveSuccess_container").shouldBe(visible);
    Selenide.refresh();
    $("#userInformationForm\\:name").shouldBe(exactText(USER_FOO));
    $("#userInformationForm\\:fullName").shouldBe(exactValue("Marcelo Footer"));
    $("#userInformationForm\\:email").shouldBe(exactValue("m.footer@ivyteam.ch"));
  }

  @Test
  void testSaveUserInformationNoPasswordMatch() {
    Navigation.toUserDetail(USER_FOO);
    $("#userInformationForm\\:password1").sendKeys("foopassword");
    $("#userInformationForm\\:saveUserInformation").click();
    $("#userInformationForm\\:informationMessages").shouldBe(visible);
    $("#userInformationForm\\:informationMessages").shouldBe(exactText("Password didn't match"));
  }

  @Test
  void testDeleteUser() {
    Navigation.toUserDetail(USER_BAR);
    $("#userInformationForm\\:deleteUser").click();
    $("#userInformationForm\\:deleteUserConfirmDialog").shouldBe(visible);
    $("#userInformationForm\\:deleteUserConfirmYesBtn").click();
    assertCurrentUrlEndsWith("users.xhtml");
  }

  @Test
  void testEnableDisableUser() {
    Navigation.toUserDetail(USER_FOO);
    $("#userInformationForm .card-top-static-message").shouldBe(empty);
    $("#userInformationForm\\:disableUser").shouldBe(visible).click();
    $("#userInformationForm\\:disableUser").shouldNotBe(visible);
    $("#userInformationForm .card-top-static-message").shouldHave(text("This user is disabled"));
    $("#userInformationForm\\:enableUser").shouldBe(visible).click();
    $("#userInformationForm\\:enableUser").shouldNotBe(visible);
    $("#userInformationForm .card-top-static-message").shouldBe(empty);
    $("#userInformationForm\\:disableUser").shouldBe(visible);
  }

  @Test
  void testEmailLanguageSwitch() {
    Navigation.toUserDetail(USER_FOO);
    changeEmailLanguage("Application default (English)", "German");
    Selenide.refresh();
    changeEmailLanguage("German", "Application default (English)");
  }

  private void changeEmailLanguage(String oldLang, String lang) {
    SelectOneMenu language = PrimeUi.selectOne(By.id("userEmailForm:emailSettings:languageDropDown"));
    language.selectedItemShould(exactText(oldLang));
    language.selectItemByLabel(lang);
    language.selectedItemShould(exactText(lang));
    $("#userEmailForm\\:saveEmailNotificationSettings").click();
    $("#userEmailForm\\:emailSaveSuccess_container").shouldBe(visible, exactText("User email changes saved"));
  }

  @Test
  void testEmailSettings() {
    Navigation.toUserDetail(USER_FOO);
    SelectOneRadio radioSettings = PrimeUi.selectOneRadio(By.id("userEmailForm:emailSettings:radioSettings"));
    SelectBooleanCheckbox neverCheckbox = PrimeUi
            .selectBooleanCheckbox(By.id("userEmailForm:emailSettings:neverCheckbox"));
    SelectBooleanCheckbox taskCheckbox = PrimeUi
            .selectBooleanCheckbox(By.id("userEmailForm:emailSettings:taskCheckbox"));
    SelectManyCheckbox dailyCheckbox = PrimeUi
            .selectManyCheckbox(By.id("userEmailForm:emailSettings:radioDailyNotification"));
    radioSettings.selectedValueShouldBe(exactValue("Application"));
    neverCheckbox.shouldBeChecked(false);
    neverCheckbox.shouldBeDisabled(true);
    taskCheckbox.shouldBeChecked(false);
    taskCheckbox.shouldBeDisabled(true);
    dailyCheckbox.shouldBeDisabled(true);

    radioSettings.selectItemByValue("Specific");
    neverCheckbox.shouldBeDisabled(false);
    taskCheckbox.shouldBeDisabled(false);
    dailyCheckbox.shouldBeDisabled(false);

    dailyCheckbox.clear();
    dailyCheckbox.setCheckboxes(List.of("Mon", "Fri", "Sun"));
    taskCheckbox.setChecked();
    neverCheckbox.setChecked();
    $("#userEmailForm\\:saveEmailNotificationSettings").click();
    $("#userEmailForm\\:emailSaveSuccess_container").shouldBe(visible);

    Selenide.refresh();
    radioSettings.selectedValueShouldBe(exactValue("Specific"));
    neverCheckbox.shouldBeChecked(true);
    taskCheckbox.shouldBeChecked(true);
    taskCheckbox.shouldBeDisabled(true);
    dailyCheckbox.shouldBeDisabled(true);
    dailyCheckbox.shouldBe(exactTexts("Mon", "Fri", "Sun"));
  }

  @Test
  void testRolesAddRemove() {
    Navigation.toUserDetail(USER_FOO);
    String boss = Selenide.$$(".role-name").find(Condition.text("boss")).parent().parent().parent()
            .getAttribute("id");
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

    Navigation.toUserDetail(USER_FOO);
    $(bossId).find(ROLE_EXPANDER).shouldBe(visible, enabled).click();
    $(managerId).find(MEMBER_ICON).shouldHave(cssClass(CSS_MEMBER));
    $(bossId).find(MEMBER_ICON).shouldHave(cssClass(CSS_MEMBER)).shouldNotHave(cssClass(CSS_MEMBER_INHERIT));

    $(managerRemoveButtonId).click();
    $(managerAddButtonId).shouldNotHave(cssClass(CSS_DISABLED));
    $(managerRemoveButtonId).shouldHave(cssClass(CSS_DISABLED));

    $(bossRemoveButtonId).click();
  }

  @Test
  void testSynchronizeUser() {
    WebTestUsers.triggerSync();

    Navigation.toUserDetail(USER_AD);
    checkUserIsExternal();
    $("#userInformationForm\\:userSynchBtn").click();
    $("#synchUserForm").shouldBe(visible);
    $("#synchUserForm\\:userSynchName").shouldBe(disabled, value(USER_AD));
    $("#synchUserForm\\:synchUserVar").click();
    $("#synchUserForm\\:logViewer").shouldHave(text("INFO: User synchronization"));
  }

  @Test
  void testExpandCollapseRoleTree() {
    Navigation.toUserDetail(USER_FOO);
    Table table = new Table(By.id("rolesOfUserForm:rolesTree"), true);
    table.firstColumnShouldBe(size(3));
    $("#rolesOfUserForm\\:expandAll").shouldBe(visible).click();
    table.firstColumnShouldBe(size(4));
    $("#rolesOfUserForm\\:collapseAll").shouldBe(visible).click();
    table.firstColumnShouldBe(size(1));
  }

  private void checkUserIsExternal() {
    $("#userInformationForm .card-top-static-message").shouldHave(text("This user is managed"));
    $("#userInformationForm\\:disableUser").shouldNotBe(visible);
    $("#userInformationForm\\:deleteUser").shouldNotBe(visible);
    $("#userInformationForm\\:saveUserInformation").shouldNotBe(visible);
    $("#userInformationForm\\:userSynchBtn").shouldBe(visible);
  }

  private void clearUserInfoInputs() {
    $("#userInformationForm\\:fullName").clear();
    $("#userInformationForm\\:email").clear();
    $("#userInformationForm\\:password1").clear();
    $("#userInformationForm\\:password2").clear();
  }

}
