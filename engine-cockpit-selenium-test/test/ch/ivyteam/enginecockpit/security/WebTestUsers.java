package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.noneMatch;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestUsers {

  private static final String SECURITY_SYSTEM_TAB_VIEW = "#securitySystems\\:securitySystemTabView\\:";

  private String user = "test";
  private String fullName = "test user";
  private String email = "test@test.ch";
  private String password = "password";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toUsers();
    Tab.SECURITY_SYSTEM.switchToDefault();
  }

  @Test
  void usersInTable() {
    $("h2").shouldHave(text("Users"));
    Table table = new Table(By.cssSelector(Tab.SECURITY_SYSTEM.activePanelCss + " .userTable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    table.search("d@ivyteam");
    table.firstColumnShouldBe(size(1));
  }

  @Test
  void manualUsersInManagedSecuritySystem() {
    triggerSync();
    $("#form\\:syncMoreBtn_menuButton").shouldBe(visible).click();
    $("#form\\:syncNewUserBtn").shouldBe(visible).click();

    $("#newUserModal").shouldBe(visible);
    $("#newUserForm\\:newUserNameInput").sendKeys("manual");
    $("#newUserForm\\:saveNewUser").click();

    Table table = new Table(By.cssSelector(Tab.SECURITY_SYSTEM.activePanelCss + " .userTable"), true);
    table.firstColumnShouldBe(sizeGreaterThanOrEqual(4));

    filterTableFor("Show manual users");
    table.firstColumnShouldBe(sizeLessThanOrEqual(2));
    table.search("user");
    table.firstColumnShouldBe(empty);
    table.search("ma");
    table.firstColumnShouldBe(sizeLessThanOrEqual(1));

    Navigation.toUserDetail("manual");
    $("#userInformationForm\\:deleteUser").shouldBe(visible).click();
    $("#userInformationForm\\:deleteUserConfirmYesBtn").shouldBe(visible).click();
  }

  @Test
  void disabledUsersInTable() {
    EngineCockpitUtil.createDisabledUser();

    login();
    Navigation.toUsers();
    Tab.SECURITY_SYSTEM.switchToDefault();

    Table table = new Table(By.cssSelector(Tab.SECURITY_SYSTEM.activePanelCss + " .userTable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    table.firstColumnShouldBe(noneMatch("disabled user not in table", ele -> ele.getText().contains("disableduser")));

    filterTableFor("Show disabled users");
    table.firstColumnShouldBe(size(1));
    table.firstColumnShouldBe(texts("disableduser"));

    table.search("fo");
    table.firstColumnShouldBe(empty);
    table.search("dis");
    table.firstColumnShouldBe(size(1));

    resetFilter();
    table.search("%");
    table.firstColumnShouldBe(sizeGreaterThan(0));
    table.firstColumnShouldBe(noneMatch("disabled user not in table", ele -> ele.getText().contains("disableduser")));
  }

  @Test
  void contentFilter() {
    $$(contentFilterSelector() + "filterPanel .ui-chkbox").shouldBe(size(1));
    assertContentFilterText("Filter: enabled users");
    filterTableFor("Show disabled users");
    assertContentFilterText("Filter: disabled users");
    resetFilter();
    assertContentFilterText("Filter: enabled users");
    $(filterBtn()).click();
  }

  @Test
  void contentFilterAD() {
    Tab.SECURITY_SYSTEM.switchToTab("test-ad");
    $$(contentFilterSelector() + "filterPanel .ui-chkbox").shouldBe(size(2));
    assertContentFilterText("Filter: enabled users");
    filterTableFor("Show disabled users");
    assertContentFilterText("Filter: disabled users");
    filterTableFor("Show manual users");
    assertContentFilterText("Filter: manual disabled users");
    resetFilter();
    assertContentFilterText("Filter: enabled users");
  }

  private void filterTableFor(String filter) {
    $(filterBtn()).shouldBe(visible).click();
    PrimeUi.selectManyCheckbox(By.cssSelector(contentFilterSelector() + "filterCheckboxes")).setCheckboxes(List.of(filter));
    $(contentFilterSelector() + "applyFilter").shouldBe(visible).click();
  }

  private void resetFilter() {
    $(filterBtn()).shouldBe(visible).click();
    $(contentFilterSelector() + "resetFilterBtn").shouldBe(visible).click();
  }

  private void assertContentFilterText(String expectedFilter) {
    $(filterBtn()).shouldHave(attribute("title", expectedFilter));
  }

  private String filterBtn() {
    return contentFilterSelector() + "filterBtn";
  }

  private String contentFilterSelector() {
    return SECURITY_SYSTEM_TAB_VIEW + Tab.SECURITY_SYSTEM.getSelectedTabIndex() + "\\:tableForm\\:userTable\\:";
  }

  @Test
  void newUserDialogNoUserName() {
    showNewUserDialog();
    $("#newUserForm\\:saveNewUser").click();
    $("#newUserForm\\:newUserNameMessage").shouldBe(visible);
  }

  @Test
  void newUserDialogNoPasswordMatch() {
    showNewUserDialog();
    $("#newUserForm\\:newUserNameInput").sendKeys("test");
    $("#newUserForm\\:password1").sendKeys("password");
    $("#newUserForm\\:saveNewUser").click();
    $("#msgs_container").shouldBe(visible);
  }

  @Test
  void newUserDialogValidInput() {
    showNewUserDialog();
    Table table = new Table(By.cssSelector(Tab.SECURITY_SYSTEM.activePanelCss + " .userTable"), true);
    int users = table.getFirstColumnEntries().size();
    $("#newUserForm\\:newUserNameInput").sendKeys(user);
    $("#newUserForm\\:fullName").sendKeys(fullName);
    $("#newUserForm\\:email").sendKeys(email);
    $("#newUserForm\\:password1").sendKeys(password);
    $("#newUserForm\\:password2").sendKeys(password);
    $("#newUserForm\\:saveNewUser").click();
    $("#newUserModal").shouldNotBe(visible);
    $("#msgs_container").shouldBe(visible, text("User '" + user + "' created successfully"));
    table.firstColumnShouldBe(sizeGreaterThan(users));
    table.firstColumnShouldBe(anyMatch("User should be in table", element -> element.getText().contains(user)));
    table.valueForEntryShould(user, 2, exactText(fullName));
    table.valueForEntryShould(user, 3, exactText(email));
  }

  @Test
  void createNewUserWithSameNameAsExisting() {
    showNewUserDialog();
    $("#newUserForm\\:newUserNameInput").sendKeys("foo");
    $("#newUserForm\\:saveNewUser").click();
    $("#newUserModal").shouldNotBe(visible);
    $("#msgs_container").should(visible, text("User 'foo' couldn't be created"));
  }

  @Test
  void synchronizeSingleUser() {
    showSynchUserDialog();
    $("#synchUserForm\\:userSynchName").shouldBe(exactValue("")).sendKeys("user1");
    $("#synchUserForm\\:synchUserVar").click();
    $("#synchUserForm\\:logViewer").shouldBe(text("INFO: User synchronization"), Duration.ofSeconds(10));
  }

  @Test
  void jumpToSyncLog() {
    Tab.SECURITY_SYSTEM.switchToTab("test-ad");
    $("#form\\:syncMoreBtn_menuButton").click();
    $("#form\\:userSyncLog").shouldBe(visible).click();
    $$(".ui-panel-titlebar").find(text("usersynch.log")).parent()
            .find(".ui-panel-content").shouldBe(visible);
  }

  private void showSynchUserDialog() {
    Tab.SECURITY_SYSTEM.switchToTab("test-ad");
    $("#form\\:syncMoreBtn_menuButton").click();
    $("#form\\:synchUserBtn").shouldBe(visible).click();
    $("#synchUserForm").shouldBe(visible);
  }

  private void showNewUserDialog() {
    $(By.id("form:newUserBtn")).click();
    $("#newUserModal").shouldBe(visible);
  }

  public static void triggerSync() {
    Tab.SECURITY_SYSTEM.switchToTab("test-ad");
    String syncBtnId = "#form\\:syncMoreBtn_button";
    $(syncBtnId).shouldBe(visible).click();
    $(syncBtnId).findAll("span").first().shouldHave(cssClass("si-is-spinning"));
    $(syncBtnId).findAll("span").first().shouldHave(not(cssClass("si-is-spinning")), Duration.ofSeconds(20));
  }
}
