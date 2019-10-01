package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

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
    assertThat(driver.findElementByTagName("h1").getText()).contains("Users");
    WebElement table = driver.findElementByClassName("userTable");
    List<WebElement> users = table.findElements(By.className("user-name"));
    if (!users.isEmpty())
    {
      assertThat(table.findElements(By.className("user-name"))).isNotEmpty();
      WebElement lastUser = table.findElement(By.xpath("(.//*[@class='user-name'])[last()]"));
      WebElement input = table.findElement(By.xpath(".//input[contains(@class, 'table-search-input-withicon')]"));
      input.sendKeys(lastUser.getText());
      saveScreenshot(driver, "search_user");
      webAssertThat(() -> assertThat(table.findElements(By.className("user-name"))).hasSize(1));
    }
  }
  
  @Test
  void testNewUserDialogNoUserName(FirefoxDriver driver)
  {
    showNewUserDialog(driver);
    driver.findElementById("newUserForm:saveNewUser").click();
    saveScreenshot(driver, "no_user_name");
    assertThat(driver.findElementById("newUserForm:newUserNameMessage").isDisplayed()).isTrue();
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
    assertThat(driver.findElementById("form:msgs_container").isDisplayed()).isTrue();
  }
  
  @Test
  void testNewUserDialogValidInput(FirefoxDriver driver)
  {
    showNewUserDialog(driver);
    WebElement table = driver.findElementByClassName("userTable");
    int users = table.findElements(By.className("user-name")).size();
    driver.findElementById("newUserForm:newUserNameInput").sendKeys(user);
    driver.findElementById("newUserForm:fullName").sendKeys(fullName);
    driver.findElementById("newUserForm:email").sendKeys(email);
    driver.findElementById("newUserForm:password1").sendKeys(password);
    driver.findElementById("newUserForm:password2").sendKeys(password);
    saveScreenshot(driver, "new_user_input");
    driver.findElementById("newUserForm:saveNewUser").click();
    saveScreenshot(driver, "new_user_saved");
    table = driver.findElementByClassName("userTable");
    assertThat(driver.findElementById("newUserModal").isDisplayed()).isFalse();
    assertThat(driver.findElementById("form:msgs_container").isDisplayed()).isFalse();
    assertThat(table.findElements(By.className("user-name")).size()).isGreaterThan(users);
    assertThat(table.findElement(By.xpath("(.//*[@class='user-name'])[last()]")).getText()).isEqualTo(user);
    assertThat(table.findElement(By.xpath("(.//*[@class='user-fullname'])[last()]")).getText()).isEqualTo(fullName);
    assertThat(table.findElement(By.xpath("(.//*[@class='user-email'])[last()]")).getText()).isEqualTo(email);
  }
  
  private void showNewUserDialog(FirefoxDriver driver)
  {
    navigateToUsers(driver);
    WebElement firstAppPanel = driver.findElement(By.xpath(("//div[contains(@class, 'ui-tabs-panel')]")));
    WebElement newUserBtn = firstAppPanel.findElement(By.xpath((".//button[contains(@id, 'newUserBtn')]")));
    newUserBtn.click();
    saveScreenshot(driver, "new_user");
    assertThat(driver.findElementById("newUserModal").isDisplayed()).isTrue();
  }
  
  private void navigateToUsers(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toUsers(driver);
    saveScreenshot(driver, "users");
  }
}
