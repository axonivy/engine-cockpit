package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlContains;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeLessThan;
import static com.codeborne.selenide.Condition.attribute;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestUserDetail {

  private static final String CSS_MEMBER_INHERIT = "light";
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
    Tab.SECURITY_SYSTEM.switchToDefault();
  }

  @Test
  void usersDetailOpen() {
    Navigation.toUserDetail(USER_FOO);
    assertCurrentUrlContains("userdetail.xhtml?system=default&name=" + USER_FOO);
  }

  @Test
  void userDetailInformation() {
    Navigation.toUserDetail(USER_FOO);
    $("#userInformationForm\\:name").shouldBe(exactText(USER_FOO));
  }

  @Test
  void saveUserInformation() {
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
  void saveUserInformationNoPasswordMatch() {
    Navigation.toUserDetail(USER_FOO);
    $("#userInformationForm\\:password1").sendKeys("foopassword");
    $("#userInformationForm\\:saveUserInformation").click();
    $("#userInformationForm\\:informationMessages").shouldBe(visible);
    $("#userInformationForm\\:informationMessages").shouldBe(exactText("Password didn't match"));
  }

  @Test
  void deleteUser() {
    Navigation.toUserDetail(USER_BAR);
    $("#userInformationForm\\:deleteUser").click();
    $("#userInformationForm\\:deleteUserConfirmDialog").shouldBe(visible);
    $("#userInformationForm\\:deleteUserConfirmYesBtn").click();
    assertCurrentUrlContains("users.xhtml");
  }

  @Test
  void enableDisableUser() {
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
  void language() {
    Navigation.toUserDetail(USER_FOO);
    var language = PrimeUi.selectOne(By.id("userInformationForm:language"));
    language.selectItemByLabel("German (de)");
    $("#userInformationForm\\:saveUserInformation").click();
    language.selectedItemShould(value("de"));

    Selenide.refresh();
    language.selectedItemShould(value("de"));
    language.selectItemByLabel("English (en)");
    $("#userInformationForm\\:saveUserInformation").click();

    Selenide.refresh();
    language.selectedItemShould(value("en"));
  }

  @Test
  void formattingLanguage() {
    Navigation.toUserDetail(USER_FOO);
    var language = PrimeUi.selectOne(By.id("userInformationForm:formattingLanguage"));
    language.selectItemByLabel("Aghem (agq)");
    $("#userInformationForm\\:saveUserInformation").click();
    language.selectedItemShould(value("agq"));

    Selenide.refresh();
    language.selectedItemShould(value("agq"));
    language.selectItemByLabel("English (en)");
    $("#userInformationForm\\:saveUserInformation").click();

    Selenide.refresh();
    language.selectedItemShould(value("en"));
  }

  @Test
  void notificationChannels() {
    Navigation.toUserDetail(USER_FOO);

    Table table = new Table(By.id("notificationsForm:notificationChannelsTable"));
    var webNewTaskIcon = $(By.className("subscription-icon"));
    var webNewTaskCheckbox = $(By.id("notificationsForm:notificationChannelsTable:0:channels:0:subscriptionCheckbox")).lastChild().lastChild();

    table.firstColumnShouldBe(size(1));
    table.firstColumnShouldBe(CollectionCondition.exactTexts("new-task"));
    table.headerShouldBe(size(2));
    table.headerShouldBe(CollectionCondition.exactTexts("Event", "Web"));

    shouldHaveSubscribedByDefaultState(webNewTaskIcon, webNewTaskCheckbox);

    webNewTaskCheckbox.parent().click();
    shouldHaveSubscribedState(webNewTaskIcon, webNewTaskCheckbox);

    webNewTaskCheckbox.parent().click();
    shouldHaveNotSubscribedState(webNewTaskIcon, webNewTaskCheckbox);

    // Refresh without saving
    Selenide.refresh();
    shouldHaveSubscribedByDefaultState(webNewTaskIcon, webNewTaskCheckbox);

    // Refresh after saving
    webNewTaskCheckbox.parent().click();
    $(By.id("notificationsForm:save")).click();
    Selenide.refresh();
    shouldHaveSubscribedState(webNewTaskIcon, webNewTaskCheckbox);

    // Reset
    $(By.id("notificationsForm:reset")).click();
    shouldHaveSubscribedByDefaultState(webNewTaskIcon, webNewTaskCheckbox);
    Selenide.refresh();
    shouldHaveSubscribedByDefaultState(webNewTaskIcon, webNewTaskCheckbox);
  }

  @Test
  void notificationChannels_notSubscribedByDefault() {
    Navigation.toNotificationChannelDetail("web");
    var allEventsCheckbox = PrimeUi.selectBooleanCheckbox(By.id("form:allEvents"));
    allEventsCheckbox.removeChecked();
    $(By.id("save")).click();

    Navigation.toUserDetail(USER_FOO);

    var webNewTaskIcon = $(By.className("subscription-icon"));
    var webNewTaskCheckbox = $(By.id("notificationsForm:notificationChannelsTable:0:channels:0:subscriptionCheckbox")).lastChild().lastChild();

    shouldHaveNotSubscribedByDefaultState(webNewTaskIcon, webNewTaskCheckbox);

    EngineCockpitUtil.resetNotificationConfig();
  }

  private void shouldHaveSubscribedByDefaultState(SelenideElement webNewTaskIcon,
          SelenideElement webNewTaskCheckbox) {
    iconShouldHaveState(webNewTaskIcon, true, true, "Subscribed by default");
    checkboxShouldHaveState(webNewTaskCheckbox, 0);
  }

  private void shouldHaveNotSubscribedByDefaultState(SelenideElement webNewTaskIcon,
          SelenideElement webNewTaskCheckbox) {
    iconShouldHaveState(webNewTaskIcon, false, true, "Not subscribed by default");
    checkboxShouldHaveState(webNewTaskCheckbox, 0);
  }

  private void shouldHaveSubscribedState(SelenideElement webNewTaskIcon, SelenideElement webNewTaskCheckbox) {
    iconShouldHaveState(webNewTaskIcon, true, false, "Subscribed");
    checkboxShouldHaveState(webNewTaskCheckbox, 1);
  }

  private void shouldHaveNotSubscribedState(SelenideElement webNewTaskIcon,
          SelenideElement webNewTaskCheckbox) {
    iconShouldHaveState(webNewTaskIcon, false, false, "Not subscribed");
    checkboxShouldHaveState(webNewTaskCheckbox, 2);
  }

  private void iconShouldHaveState(SelenideElement webNewTaskIcon, boolean iconSubscribedState,
          boolean iconByDefaultState, String iconTitle) {
    iconShouldHaveSubscribedState(webNewTaskIcon, iconSubscribedState);
    iconShouldHaveByDefaultState(webNewTaskIcon, iconByDefaultState);
    iconShouldHaveTitle(webNewTaskIcon, iconTitle);
  }

  private void checkboxShouldHaveState(SelenideElement checkbox, int state) {
    switch (state) {
      case 0 -> checkbox.shouldNotHave(cssClass("ui-icon"));
      case 1 -> checkbox.shouldHave(cssClass("ui-icon-check"));
      case 2 -> checkbox.shouldHave(cssClass("ui-icon-closethick"));
      default -> throw new IllegalArgumentException("Unexpected value: " + state);
    }
  }

  private void iconShouldHaveSubscribedState(SelenideElement icon, boolean subscribed) {
    if (subscribed) {
      icon.shouldHave(cssClass("si-check-circle-1"));
    } else {
      icon.shouldHave(cssClass("si-remove-circle"));
    }
  }

  private void iconShouldHaveByDefaultState(SelenideElement icon, boolean byDefault) {
    if (byDefault) {
      icon.shouldHave(cssClass("light"));
    } else {
      icon.shouldNotHave(cssClass("light"));
    }
  }

  private void iconShouldHaveTitle(SelenideElement icon, String title) {
    icon.shouldHave(attribute("title", title));
  }

  @Test
  void substitutes() {
    EngineCockpitUtil.addSubstitutes();
    beforeEach();
    Navigation.toUserDetail(USER_FOO);

    String tableId = "substitutesForm:substitutesTable";
    Table tableWithLink = new Table(By.id(tableId), true);
    Table tableWithoutLink = new Table(By.id(tableId));

    tableWithLink.firstColumnShouldBe(size(3));
    tableWithLink.firstColumnShouldBe(CollectionCondition.exactTexts("substitute1", "substitute2", "substitute3"));
    tableWithoutLink.columnShouldBe(2, CollectionCondition.exactTexts("On absence", "Permanent", "On absence"));
    tableWithoutLink.columnShouldBe(3, CollectionCondition.exactTexts("Personal", "Personal", "Role"));
    tableWithLink.columnShouldBe(4, CollectionCondition.exactTexts("", "", "role"));

    EngineCockpitUtil.cleanupSubstitutes();
  }

  @Test
  void absences_isWorking() {
    Navigation.toUserDetail(USER_FOO);
    $(By.id("working")).shouldHave(cssClass("si-check-circle-1"));
  }

  @Test
  void absences() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

    EngineCockpitUtil.addAbsences();
    beforeEach();
    Navigation.toUserDetail(USER_FOO);

    $(By.id("working")).shouldHave(cssClass("si-remove-circle"));

    LocalDate currentDate = LocalDate.now();

    Table table = new Table(By.id("absencesForm:absencesTable"));
    table.firstColumnShouldBe(size(3));
    table.firstColumnShouldBe(CollectionCondition.exactTexts(
            dateTimeFormatter.format(currentDate.minusDays(7)),
            dateTimeFormatter.format(currentDate.minusDays(1)),
            dateTimeFormatter.format(currentDate.plusDays(1))));
    table.columnShouldBe(2, CollectionCondition.exactTexts(
            dateTimeFormatter.format(currentDate.minusDays(1)),
            dateTimeFormatter.format(currentDate.plusDays(1)),
            dateTimeFormatter.format(currentDate.plusDays(7))));

    EngineCockpitUtil.cleanupAbsences();
  }

  @Test
  void rolesAddRemove() {
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
  void synchronizeUser() {
    WebTestUsers.triggerSync();
    Navigation.toUserDetail("test-ad", USER_AD);
    checkUserIsExternal();
    $("#userInformationForm\\:userSynchBtn").click();
    $("#userSynch\\:synchUserForm").shouldBe(visible);
    $("#userSynch\\:synchUserForm\\:userSynchName").shouldBe(disabled, value(USER_AD));
    $("#userSynch\\:synchUserForm\\:synchUserVar").click();
    $("#userSynch\\:synchUserForm\\:logViewer").shouldHave(text("INFO: Synchronization of user"));
  }

  @Test
  void expandCollapseRoleTree() {
    Navigation.toUserDetail(USER_FOO);
    Table table = new Table(By.id("rolesOfUserForm:rolesTree"), true);
    table.firstColumnShouldBe(sizeGreaterThan(3));
    table.firstColumnShouldBe(sizeLessThan(7));
    $("#rolesOfUserForm\\:rolesTree\\:expandAll").shouldBe(visible).click();
    table.firstColumnShouldBe(sizeGreaterThan(3));
    table.firstColumnShouldBe(sizeLessThan(7));
    $("#rolesOfUserForm\\:rolesTree\\:collapseAll").shouldBe(visible).click();
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
