package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.CollectionCondition.sizeLessThanOrEqual;
import static com.codeborne.selenide.Condition.cssClass;
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
  private static final String APPLICATION_TAB_VIEW = "#form\\:card\\:apps\\:applicationTabView\\:";
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
    $("h1").shouldHave(text("Users"));
    Table table = new Table(By.className("userTable"), true);
    table.firstColumnShouldBe(sizeGreaterThan(0));
    String firstUser = table.getFirstColumnEntries().get(0);
    table.search(firstUser);
    table.firstColumnShouldBe(size(1));
  }
  
  @Test
  void testManualUsersInManagedApp()
  {
    triggerSync();
    $(APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:syncMoreBtn_menuButton").shouldBe(visible).click();
    $(APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:syncNewUserBtn").shouldBe(visible).click();
    
    $("#newUserModal").shouldBe(visible);
    $("#newUserForm\\:newUserNameInput").sendKeys("manual");
    $("#newUserForm\\:saveNewUser").click();
    
    Table table = new Table(By.className("userTable"), true);
    table.firstColumnShouldBe(sizeGreaterThanOrEqual(4));
    
    $(APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:moreBtn").click();
    $(APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:showManualUserBtn").click();
    
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
    $("h1").shouldHave(text("Users"));
    
    Table table = new Table(By.className("userTable"), true);
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
    $(APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:moreBtn").click();
    $(APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:showDisabledUserBtn").shouldBe(visible).click();
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
    $("#form\\:msgs_container").shouldBe(visible);
  }
  
  @Test
  void testNewUserDialogValidInput()
  {
    showNewUserDialog();
    Table table = new Table(By.className("userTable"), true);
    int users = table.getFirstColumnEntries().size();
    $("#newUserForm\\:newUserNameInput").sendKeys(user);
    $("#newUserForm\\:fullName").sendKeys(fullName);
    $("#newUserForm\\:email").sendKeys(email);
    $("#newUserForm\\:password1").sendKeys(password);
    $("#newUserForm\\:password2").sendKeys(password);
    $("#newUserForm\\:saveNewUser").click();
    $("#newUserModal").shouldNotBe(visible);
    $("#form\\:msgs_container").shouldNotBe(visible);
    assertThat(table.getFirstColumnEntries().size()).isGreaterThan(users);
    assertThat(table.getFirstColumnEntries().get(users)).isEqualTo(user);
    assertThat(table.getValueForEntry(user, 2)).isEqualTo(fullName);
    assertThat(table.getValueForEntry(user, 3)).isEqualTo(email);
  }

  @Test
  void testSynchronizeSingleUser()
  {
    showSynchUserDialog();
    $("#synchUserForm\\:userSynchName").shouldHave(text("")).sendKeys("user1");
    $("#synchUserForm\\:synchUserVar").click();
    $("#synchUserForm\\:logViewer").shouldHave(text("INFO: User synchronization"));
  }
  
  private void showSynchUserDialog()
  {
    Tab.switchToTab("test-ad");
    $(APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:syncMoreBtn_menuButton").click();
    $(APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:synchUserBtn").shouldBe(visible).click();
    $("#synchUserForm").shouldBe(visible);
  }
  
  private void showNewUserDialog()
  {
    $(APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:newUserBtn").click();
    $("#newUserModal").shouldBe(visible);
  }
  
  public static void triggerSync()
  {
    Tab.switchToTab("test-ad");
    String syncBtnId = APPLICATION_TAB_VIEW + Tab.getSelectedTabIndex() + "\\:syncMoreBtn_button";
    $(syncBtnId).shouldBe(visible).click();
    $(syncBtnId).findAll("span").first().shouldHave(cssClass("fa-spin"));
    $(syncBtnId).findAll("span").first().shouldNotHave(cssClass("fa-spin"));
  }
}
