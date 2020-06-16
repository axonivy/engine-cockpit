package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestUsers
{
  private static final String APPLICATION_TAB_VIEW = "#apps\\:applicationTabView\\:";
  private String user = "test";
  private String fullName = "test user";
  private String email = "test@test.ch";
  private String password = "password";
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toUsers();
    Tab.switchToTab("test");
  }
  
  @Test
  void testUsersInTable()
  {
    $(Tab.ACITVE_PANEL_CSS + " h1").shouldHave(text("Users"));
    Table table = new Table(By.cssSelector(Tab.ACITVE_PANEL_CSS + " .userTable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    String firstUser = table.getFirstColumnEntries().get(0);
    table.search(firstUser);
    table.firstColumnShouldBe(size(1));
  }
  
  @Test
  void testManualUsersInManagedApp()
  {
    triggerSync();
    $(getAppTabId() + "syncMoreBtn_menuButton").shouldBe(visible).click();
    $(getAppTabId() + "syncNewUserBtn").shouldBe(visible).click();
    
    $("#newUserModal").shouldBe(visible);
    $("#newUserForm\\:newUserNameInput").sendKeys("manual");
    $("#newUserForm\\:saveNewUser").click();
    
    Table table = new Table(By.cssSelector(Tab.ACITVE_PANEL_CSS + " .userTable"), true);
    table.firstColumnShouldBe(sizeGreaterThanOrEqual(4));
    
    $(getAppTabId() + "moreBtn").click();
    $(getAppTabId() + "showManualUserBtn").click();
    
    table.firstColumnShouldBe(sizeLessThanOrEqual(2));
    Navigation.toUserDetail("manual");
    
    $("#userInformationForm\\:deleteUser").shouldBe(visible).click();
    $("#userInformationForm\\:deleteUserConfirmYesBtn").shouldBe(visible).click();
  }

  @Test
  void testDisabledUsersInTable()
  {
    EngineCockpitUtil.createDisabledUser();
    
    login();
    Navigation.toUsers();
    Tab.switchToTab("test");
    $(Tab.ACITVE_PANEL_CSS + " h1").shouldHave(text("Users"));
    
    Table table = new Table(By.cssSelector(Tab.ACITVE_PANEL_CSS + " .userTable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    assertThat(table.getFirstColumnEntries()).doesNotContain("disableduser");

    clickShowHideDisabledUserButton();
    table.firstColumnShouldBe(size(1));
    assertThat(table.getFirstColumnEntries()).contains("disableduser");

    clickShowHideDisabledUserButton();
    table.firstColumnShouldBe(sizeGreaterThan(0));
    assertThat(table.getFirstColumnEntries()).doesNotContain("disableduser");
  }

  private void clickShowHideDisabledUserButton()
  {
    $(getAppTabId() + "moreBtn").click();
    $(getAppTabId() + "showDisabledUserBtn").shouldBe(visible).click();
  }

  @Test
  void testNewUserDialogNoUserName()
  {
    showNewUserDialog();
    $("#newUserForm\\:saveNewUser").click();
    $("#newUserForm\\:newUserNameMessage").shouldBe(visible);
  }

  @Test
  void testNewUserDialogNoPasswordMatch()
  {
    showNewUserDialog();
    $("#newUserForm\\:newUserNameInput").sendKeys("test");
    $("#newUserForm\\:password1").sendKeys("password");
    $("#newUserForm\\:saveNewUser").click();
    $("#msgs_container").shouldBe(visible);
  }
  
  @Test
  void testNewUserDialogValidInput()
  {
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
  void createNewUserWithSameNameAsExisting()
  {
    showNewUserDialog();
    $("#newUserForm\\:newUserNameInput").sendKeys("foo");
    $("#newUserForm\\:saveNewUser").click();
    $("#newUserModal").shouldNotBe(visible);
    $("#msgs_container").should(visible, text("User 'foo' couldn't be created"));
  }

  @Test
  void testSynchronizeSingleUser()
  {
    showSynchUserDialog();
    $("#synchUserForm\\:userSynchName").shouldHave(text("")).sendKeys("user1");
    $("#synchUserForm\\:synchUserVar").click();
    $("#synchUserForm\\:logViewer").shouldHave(text("INFO: User synchronization"));
  }
  
  @Test
  void jumpToSyncLog()
  {
    Tab.switchToTab("test-ad");
    $(getAppTabId() + "syncMoreBtn_menuButton").click();
    $(getAppTabId() + "userSyncLog").shouldBe(visible).click();
    $("#userSynchLogView\\:logPanel_content").shouldBe(visible);
  }
  
  private void showSynchUserDialog()
  {
    Tab.switchToTab("test-ad");
    $(getAppTabId() + "syncMoreBtn_menuButton").click();
    $(getAppTabId() + "synchUserBtn").shouldBe(visible).click();
    $("#synchUserForm").shouldBe(visible);
  }
  
  private void showNewUserDialog()
  {
    $(getAppTabId() + "newUserBtn").click();
    $("#newUserModal").shouldBe(visible);
  }
  
  private static String getAppTabId()
  {
    return APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:form\\:";
  }
  
  public static void triggerSync()
  {
    Tab.switchToTab("test-ad");
    String syncBtnId = getAppTabId() + "syncMoreBtn_button";
    $(syncBtnId).shouldBe(visible).click();
    $(syncBtnId).findAll("span").first().shouldHave(cssClass("fa-spin"));
    $(syncBtnId).findAll("span").first().shouldNotHave(cssClass("fa-spin"));
  }
}
