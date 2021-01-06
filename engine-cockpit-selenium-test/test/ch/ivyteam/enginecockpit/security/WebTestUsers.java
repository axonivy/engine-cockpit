package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestUsers
{
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
    Table table = new Table(By.cssSelector(Tab.ACITVE_PANEL_CSS + " .userTable"), true);
    int users = table.getFirstColumnEntries().size();
    $("#newUserForm\\:newUserNameInput").sendKeys(user);
    $("#newUserForm\\:fullName").sendKeys(fullName);
    $("#newUserForm\\:email").sendKeys(email);
    $("#newUserForm\\:password1").sendKeys(password);
    $("#newUserForm\\:password2").sendKeys(password);
    $("#newUserForm\\:saveNewUser").click();
    $("#newUserModal").shouldNotBe(visible);
    $("#form\\:msgs_container").shouldBe(visible, text("User '" + user + "' created successfully"));
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
    $("#form\\:msgs_container").should(visible, text("User 'foo' couldn't be created"));
  }

  @Test
  void testSynchronizeSingleUser()
  {
    showSynchUserDialog();
    $("#synchUserForm\\:userSynchName").shouldHave(text("")).sendKeys("user1");
    $("#synchUserForm\\:synchUserVar").click();
    $("#synchUserForm\\:logViewer").waitUntil(text("INFO: User synchronization"), 10000);
  }
  
  private void showSynchUserDialog()
  {
    Tab.switchToTab("test-ad");
    $("#form\\:card\\:apps\\:applicationTabView\\:" + Tab.getSelectedTabIndex() + "\\:synchronizeForm\\:moreBtn").click();
    $("#form\\:card\\:apps\\:applicationTabView\\:" + Tab.getSelectedTabIndex() + "\\:synchronizeForm\\:synchUserBtn").shouldBe(visible).click();
    $("#synchUserForm").shouldBe(visible);
  }
  
  private void showNewUserDialog()
  {
    $("#form\\:card\\:apps\\:applicationTabView\\:" + Tab.getSelectedTabIndex() + "\\:newUserBtn").click();
    $("#newUserModal").shouldBe(visible);
  }
  
}
