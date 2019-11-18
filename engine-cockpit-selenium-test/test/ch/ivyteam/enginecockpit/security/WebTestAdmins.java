package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

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
    Table table = new Table(driver, By.id("admins:adminForm:adminTable"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("admin_name")).isEmpty());
    
    String user = "admin";
    String email = "admin@ivyTeam.ch";
    String password = "password";
    addAdmin(user, email, password);
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("admin_name")).containsOnly(user));
    
    editAdmin(table, user, "test@admin.com");
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("admin_name")).containsOnly(user));
    webAssertThat(() -> assertThat(table.getValueForEntry(user, 2)).isEqualTo("test@admin.com"));
    
    deleteAdmin(table, user);
    webAssertThat(() -> assertThat(table.getFirstColumnEntriesForSpanClass("admin_name")).isEmpty());
  }
  
  @Test
  void testAdminDialogInvalid()
  {
    navigateToAdmins();
    
    addAdmin("", "", "");
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:nameMessage").getText())
            .contains("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:emailMessage").getText())
            .contains("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:passwordMessage").getText())
            .contains("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:password2Message").getText())
            .contains("Value is required"));
    driver.findElementById("admins:editAdminForm:cancelEditAdmin").click();
    
    addAdmin("admin", "test@test.com", "password", "pass");
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:nameMessage").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:emailMessage").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:password2Message").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:passwordMessage").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:passwordMessage").getText())
            .contains("Password didn't match"));
  }
  
  private void deleteAdmin(Table table, String user)
  {
    table.clickButtonForEntry(user, "deleteAdmin");
    saveScreenshot("delete_admin");
  }

  private void editAdmin(Table table, String user, String email)
  {
    table.clickButtonForEntry(user, "editPropertyBtn");
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminDialog").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("admins:editAdminForm:name").isEnabled()).isFalse());
    
    driver.findElementById("admins:editAdminForm:email").clear();
    driver.findElementById("admins:editAdminForm:email").sendKeys(email);
    
    driver.findElementById("admins:editAdminForm:saveAdmin").click();
    saveScreenshot("edit_admin");
  }

  private void addAdmin(String user, String email, String password)
  {
    addAdmin(user, email, password, password);
  }
  
  private void addAdmin(String user, String email, String password, String password2)
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
    saveScreenshot("save_new_admin");
  }

  private void navigateToAdmins()
  {
    login();
    Navigation.toAdmins(driver);
    saveScreenshot("admins");
  }
}
