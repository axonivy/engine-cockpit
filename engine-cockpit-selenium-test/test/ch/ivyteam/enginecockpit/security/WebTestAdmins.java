package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestAdmins extends WebTestBase
{
  
  @Test
  void testAddEditDeleteAdmin()
  {
    navigateToAdmins();
    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("Administrators"));
    testAddEditDeleteAdmin(driver);
  }
  
  @Test
  void testAdminDialogInvalid()
  {
    navigateToAdmins();
    testAddAdminInvalidValues(driver);
    testAddAdminInvalidPassword(driver);
  }
  
  public static void testAddEditDeleteAdmin(RemoteWebDriver driver)
  {
    Table table = new Table(driver, By.id("admins:adminForm:adminTable"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("admin_name")).isEmpty());
    
    String user = "admin";
    String email = "admin@ivyTeam.ch";
    String password = "password";
    addAdmin(driver, user, email, password, password);
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("admin_name")).containsOnly(user));
    
    editAdmin(driver, table, user, "test@admin.com");
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("admin_name")).containsOnly(user));
    webAssertThat(() -> assertThat(table.getValueForEntry(user, 2)).isEqualTo("test@admin.com"));
    
    deleteAdmin(driver, table, user);
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("admin_name")).isEmpty());
  }
  
  private static void deleteAdmin(RemoteWebDriver driver, Table table, String user)
  {
    table.clickButtonForEntry(user, "deleteAdmin");
    assertGrowlMessage(driver, user, "removed");
  }

  private static void editAdmin(RemoteWebDriver driver, Table table, String user, String email)
  {
    table.clickButtonForEntry(user, "editPropertyBtn");
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminDialog").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:name").isEnabled()).isFalse());
    
    driver.findElementById("admins:editAdminForm:email").clear();
    driver.findElementById("admins:editAdminForm:email").sendKeys(email);
    
    driver.findElementById("admins:editAdminForm:saveAdmin").click();
    assertGrowlMessage(driver, user, "modified");
  }

  private static void assertGrowlMessage(RemoteWebDriver driver, String user, String msgPart)
  {
    webAssertThat(() -> assertThat(driver.findElementByClassName("ui-growl-title").getText()).contains("'" + user + "' " + msgPart + " successfully"));
  }
  
  public static void testAddAdminInvalidPassword(RemoteWebDriver driver)
  {
    addAdmin(driver, "admin", "test@test.com", "password", "pass");
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:nameMessage").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:emailMessage").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:password2Message").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:passwordMessage").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:passwordMessage").getText())
            .contains("Password didn't match"));
  }

  public static void testAddAdminInvalidValues(RemoteWebDriver driver)
  {
    addAdmin(driver, "", "", "", "");
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:nameMessage").getText())
            .contains("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:emailMessage").getText())
            .contains("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:passwordMessage").getText())
            .contains("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:password2Message").getText())
            .contains("Value is required"));
    driver.findElementById("admins:editAdminForm:cancelEditAdmin").click();
  }
  
  public static void addAdmin(RemoteWebDriver driver, String user, String email, String password, String password2)
  {
    driver.findElementById("addAdminForm:newAdminBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminDialog").isDisplayed()).isTrue());
    
    driver.findElementById("admins:editAdminForm:name").clear();
    driver.findElementById("admins:editAdminForm:name").sendKeys(user);
    
    driver.findElementById("admins:editAdminForm:email").clear();
    driver.findElementById("admins:editAdminForm:email").sendKeys(email);
    
    driver.findElementById("admins:editAdminForm:password1").clear();
    driver.findElementById("admins:editAdminForm:password1").sendKeys(password);
    
    driver.findElementById("admins:editAdminForm:password2").clear();
    driver.findElementById("admins:editAdminForm:password2").sendKeys(password2);
    
    driver.findElementById("admins:editAdminForm:saveAdmin").click();
  }

  private void navigateToAdmins()
  {
    login();
    Navigation.toAdmins(driver);
    saveScreenshot("admins");
  }
}
