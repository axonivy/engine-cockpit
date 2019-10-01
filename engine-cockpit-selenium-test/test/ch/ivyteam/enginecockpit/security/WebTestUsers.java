package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

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
  void testUsersInTable(FirefoxDriver driver)
  {
    navigateToUsers(driver);
    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("Users"));
    Table table = new Table(driver, By.className("userTable"), true);
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).isNotEmpty());
    String firstUser = table.getFirstColumnEntries().get(0);
    table.search(firstUser);
    saveScreenshot(driver, "search_user");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(1));
  }
  
  @Test
  void testNewUserDialogNoUserName(FirefoxDriver driver)
  {
    showNewUserDialog(driver);
    driver.findElementById("newUserForm:saveNewUser").click();
    saveScreenshot(driver, "no_user_name");
    webAssertThat(() -> assertThat(driver.findElementById("newUserForm:newUserNameMessage").isDisplayed()).isTrue());
  }

  @Test
  void testNewUserDialogNoPasswordMatch(FirefoxDriver driver)
  {
    showNewUserDialog(driver);
    driver.findElementById("newUserForm:newUserNameInput").sendKeys("test");
    driver.findElementById("newUserForm:password1").sendKeys("password");
    saveScreenshot(driver, "password");
    driver.findElementById("newUserForm:saveNewUser").click();
    saveScreenshot(driver, "no_password_match");
    webAssertThat(() -> assertThat(driver.findElementById("form:msgs_container").isDisplayed()).isTrue());
  }
  
  @Test
  void testNewUserDialogValidInput(FirefoxDriver driver)
  {
    showNewUserDialog(driver);
    Table table = new Table(driver, By.className("userTable"), true);
    int users = table.getFirstColumnEntries().size();
    driver.findElementById("newUserForm:newUserNameInput").sendKeys(user);
    driver.findElementById("newUserForm:fullName").sendKeys(fullName);
    driver.findElementById("newUserForm:email").sendKeys(email);
    driver.findElementById("newUserForm:password1").sendKeys(password);
    driver.findElementById("newUserForm:password2").sendKeys(password);
    saveScreenshot(driver, "new_user_input");
    driver.findElementById("newUserForm:saveNewUser").click();
    saveScreenshot(driver, "new_user_saved");
    webAssertThat(() -> assertThat(driver.findElementById("newUserModal").isDisplayed()).isFalse());
    webAssertThat(() -> assertThat(driver.findElementById("form:msgs_container").isDisplayed()).isFalse());
    webAssertThat(() -> assertThat(table.getFirstColumnEntries().size()).isGreaterThan(users));
    webAssertThat(() -> assertThat(table.getFirstColumnEntries().get(users)).isEqualTo(user));
    webAssertThat(() -> assertThat(table.getValueForEntry(user, 2)).isEqualTo(fullName));
    webAssertThat(() -> assertThat(table.getValueForEntry(user, 3)).isEqualTo(email));
  }
  
  private void showNewUserDialog(FirefoxDriver driver)
  {
    navigateToUsers(driver);
    driver.findElement(By.xpath(("//div[contains(@class, 'ui-tabs-panel')]//button[contains(@id, 'newUserBtn')]"))).click();
    saveScreenshot(driver, "new_user");
    webAssertThat(() -> assertThat(driver.findElementById("newUserModal").isDisplayed()).isTrue());
  }
  
  private void navigateToUsers(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toUsers(driver);
    saveScreenshot(driver, "users");
  }
}
