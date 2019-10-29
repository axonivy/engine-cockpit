package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestUsers extends WebTestBase
{
  private String user = "test";
  private String fullName = "test user";
  private String email = "test@test.ch";
  private String password = "password";
  
  @Test
  void testUsersInTable()
  {
    navigateToUsers();
    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("Users"));
    Table table = new Table(driver, By.className("userTable"), true);
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).isNotEmpty());
    String firstUser = table.getFirstColumnEntries().get(0);
    table.search(firstUser);
    saveScreenshot("search_user");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(1));
  }
  
  @Test
  void testNewUserDialogNoUserName()
  {
    showNewUserDialog();
    driver.findElementById("newUserForm:saveNewUser").click();
    saveScreenshot("no_user_name");
    webAssertThat(() -> assertThat(driver.findElementById("newUserForm:newUserNameMessage").isDisplayed()).isTrue());
  }

  @Test
  void testNewUserDialogNoPasswordMatch()
  {
    showNewUserDialog();
    driver.findElementById("newUserForm:newUserNameInput").sendKeys("test");
    driver.findElementById("newUserForm:password1").sendKeys("password");
    saveScreenshot("password");
    driver.findElementById("newUserForm:saveNewUser").click();
    saveScreenshot("no_password_match");
    webAssertThat(() -> assertThat(driver.findElementById("form:msgs_container").isDisplayed()).isTrue());
  }
  
  @Test
  void testNewUserDialogValidInput()
  {
    showNewUserDialog();
    Table table = new Table(driver, By.className("userTable"), true);
    int users = table.getFirstColumnEntries().size();
    driver.findElementById("newUserForm:newUserNameInput").sendKeys(user);
    driver.findElementById("newUserForm:fullName").sendKeys(fullName);
    driver.findElementById("newUserForm:email").sendKeys(email);
    driver.findElementById("newUserForm:password1").sendKeys(password);
    driver.findElementById("newUserForm:password2").sendKeys(password);
    saveScreenshot("new_user_input");
    driver.findElementById("newUserForm:saveNewUser").click();
    saveScreenshot("new_user_saved");
    webAssertThat(() -> assertThat(driver.findElementById("newUserModal").isDisplayed()).isFalse());
    webAssertThat(() -> assertThat(driver.findElementById("form:msgs_container").isDisplayed()).isFalse());
    webAssertThat(() -> assertThat(table.getFirstColumnEntries().size()).isGreaterThan(users));
    webAssertThat(() -> assertThat(table.getFirstColumnEntries().get(users)).isEqualTo(user));
    webAssertThat(() -> assertThat(table.getValueForEntry(user, 2)).isEqualTo(fullName));
    webAssertThat(() -> assertThat(table.getValueForEntry(user, 3)).isEqualTo(email));
  }
  
  private void showNewUserDialog()
  {
    navigateToUsers();
    driver.findElement(By.xpath(("//div[contains(@class, 'ui-tabs-panel')]//button[contains(@id, 'newUserBtn')]"))).click();
    saveScreenshot("new_user");
    webAssertThat(() -> assertThat(driver.findElementById("newUserModal").isDisplayed()).isTrue());
  }
  
  private void navigateToUsers()
  {
    login();
    Navigation.toUsers(driver);
    saveScreenshot("users");
  }
}
