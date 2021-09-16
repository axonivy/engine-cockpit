package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;

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
public class WebTestUsers {
  private static final String APPLICATION_TAB_VIEW = "#apps\\:applicationTabView\\:";
  private String user = "test";
  private String fullName = "test user";
  private String email = "test@test.ch";
  private String password = "password";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toUsers();
    Tab.switchToDefault();
  }

  @Test
  void testUsersInTable() {
    $(Tab.ACITVE_PANEL_CSS + " h1").shouldHave(text("Users"));
    Table table = new Table(By.cssSelector(Tab.ACITVE_PANEL_CSS + " .userTable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    String firstUser = table.getFirstColumnEntries().get(0);
    table.search(firstUser);
    table.firstColumnShouldBe(size(1));
  }

  @Test
  void testManualUsersInManagedApp() {
    triggerSync();
    $(getAppTabId() + "syncMoreBtn_menuButton").shouldBe(visible).click();
    $(getAppTabId() + "syncNewUserBtn").shouldBe(visible).click();

    $("#newUserModal").shouldBe(visible);
    $("#newUserForm\\:newUserNameInput").sendKeys("manual");
    $("#newUserForm\\:saveNewUser").click();

    Table table = new Table(By.cssSelector(Tab.ACITVE_PANEL_CSS + " .userTable"), true);
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
  void testDisabledUsersInTable() {
    EngineCockpitUtil.createDisabledUser();

    login();
    Navigation.toUsers();
    Tab.switchToTab("test");
    $(Tab.ACITVE_PANEL_CSS + " h1").shouldHave(text("Users"));

    Table table = new Table(By.cssSelector(Tab.ACITVE_PANEL_CSS + " .userTable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    assertThat(table.getFirstColumnEntries()).doesNotContain("disableduser");

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
    assertThat(table.getFirstColumnEntries()).doesNotContain("disableduser");
  }

  @Test
  void contentFilter() {
    var filterBtn = APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:contentFilter\\:form\\:filterBtn";
    var resetFilter = APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex()
            + "\\:contentFilter\\:form\\:resetFilter";
    var filterPanel = APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex()
            + "\\:contentFilter\\:form\\:filterPanel";
    $(filterBtn).shouldHave(text("Filter: enabled users"));
    filterTableFor("Show disabled users");
    $(filterBtn).shouldHave(text("Filter: disabled users"));
    $(resetFilter).shouldBe(visible).click();
    $(filterBtn).shouldHave(text("Filter: enabled users"));
    $(filterBtn).click();
    $$(filterPanel + " .ui-chkbox").shouldBe(size(1));
  }

  @Test
  void contentFilterAD() {
    Tab.switchToTab("test-ad");
    var filterBtn = APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:contentFilter\\:form\\:filterBtn";
    var resetFilter = APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex()
            + "\\:contentFilter\\:form\\:resetFilter";
    var filterPanel = APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex()
            + "\\:contentFilter\\:form\\:filterPanel";
    $(filterBtn).shouldHave(text("Filter: enabled users"));
    filterTableFor("Show disabled users");
    $(filterBtn).shouldHave(text("Filter: disabled users"));
    filterTableFor("Show manual users");
    $(filterBtn).shouldHave(text("Filter: manual disabled users"));
    $(resetFilter).shouldBe(visible).click();
    $(filterBtn).shouldHave(text("Filter: enabled users"));
    $$(filterPanel + " .ui-chkbox").shouldBe(size(2));
  }

  private void filterTableFor(String filter) {
    var appId = APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:";
    $(appId + "contentFilter\\:form\\:filterBtn").shouldBe(visible).click();
    PrimeUi.selectManyCheckbox(By.cssSelector(appId + "contentFilter\\:form\\:filterCheckboxes"))
            .setCheckboxes(List.of(filter));
    $(appId + "contentFilter\\:form\\:applyFilter").shouldBe(visible).click();
  }

  private void resetFilter() {
    var appId = APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:";
    $(appId + "contentFilter\\:form\\:resetFilter").shouldBe(visible).click();
  }

  @Test
  void testNewUserDialogNoUserName() {
    showNewUserDialog();
    $("#newUserForm\\:saveNewUser").click();
    $("#newUserForm\\:newUserNameMessage").shouldBe(visible);
  }

  @Test
  void testNewUserDialogNoPasswordMatch() {
    showNewUserDialog();
    $("#newUserForm\\:newUserNameInput").sendKeys("test");
    $("#newUserForm\\:password1").sendKeys("password");
    $("#newUserForm\\:saveNewUser").click();
    $("#msgs_container").shouldBe(visible);
  }

  @Test
  void testNewUserDialogValidInput() {
    showNewUserDialog();
    Table table = new Table(By.cssSelector(Tab.ACITVE_PANEL_CSS + " .userTable"), true);
    int users = table.getFirstColumnEntries().size();
    $("#newUserForm\\:newUserNameInput").sendKeys(user);
    $("#newUserForm\\:fullName").sendKeys(fullName);
    $("#newUserForm\\:email").sendKeys(email);
    $("#newUserForm\\:password1").sendKeys(password);
    $("#newUserForm\\:password2").sendKeys(password);
    $("#newUserForm\\:saveNewUser").click();
    $("#newUserModal").shouldNotBe(visible);
    $("#msgs_container").shouldBe(visible, text("User '" + user + "' created successfully"));
    assertThat(table.getFirstColumnEntries().size()).isGreaterThan(users);
    assertThat(table.getFirstColumnEntries().get(users)).isEqualTo(user);
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
  void testSynchronizeSingleUser() {
    showSynchUserDialog();
    $("#synchUserForm\\:userSynchName").shouldBe(exactValue("")).sendKeys("user1");
    $("#synchUserForm\\:synchUserVar").click();
    $("#synchUserForm\\:logViewer").shouldBe(text("INFO: User synchronization"), Duration.ofSeconds(10));
  }

  @Test
  void jumpToSyncLog() {
    Tab.switchToTab("test-ad");
    $(getAppTabId() + "syncMoreBtn_menuButton").click();
    $(getAppTabId() + "userSyncLog").shouldBe(visible).click();
    $$(".ui-panel-titlebar").find(text("usersynch.log")).parent()
            .find(".ui-panel-content").shouldBe(visible);
  }

  private void showSynchUserDialog() {
    Tab.switchToTab("test-ad");
    $(getAppTabId() + "syncMoreBtn_menuButton").click();
    $(getAppTabId() + "synchUserBtn").shouldBe(visible).click();
    $("#synchUserForm").shouldBe(visible);
  }

  private void showNewUserDialog() {
    $(getAppTabId() + "newUserBtn").click();
    $("#newUserModal").shouldBe(visible);
  }

  private static String getAppTabId() {
    return APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:form\\:";
  }

  public static void triggerSync() {
    Tab.switchToTab("test-ad");
    String syncBtnId = getAppTabId() + "syncMoreBtn_button";
    $(syncBtnId).shouldBe(visible).click();
    $(syncBtnId).findAll("span").first().shouldHave(cssClass("si-is-spinning"));
    $(syncBtnId).findAll("span").first().shouldHave(not(cssClass("si-is-spinning")), Duration.ofSeconds(20));
  }
}
