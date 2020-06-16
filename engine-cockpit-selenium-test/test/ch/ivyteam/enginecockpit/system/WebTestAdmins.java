package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestAdmins
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toAdmins();
  }
  
  @Test
  void testAddEditDeleteAdmin()
  {
    $("h1").shouldBe(text("Administrators"));
    testAddEditDelete();
    $("#adminMessages").shouldBe(text("Your engine needs to be restarted"));
  }
  
  @Test
  void testAdminDialogInvalid()
  {
    testAddAdminInvalidValues();
    testAddAdminInvalidPassword();
  }
  
  @Test
  void testOwnAdminCannotBeDeleted()
  {    
    assertOwnAdminCannotBeDeleted();
  }

  public static void assertOwnAdminCannotBeDeleted()
  {
    Table table = new Table(By.id("admins:adminForm:adminTable"));
    table.buttonForEntryShouldBeDisabled("admin", "deleteAdmin");
  }
  
  public static void testAddEditDelete()
  {
    Table table = new Table(By.id("admins:adminForm:adminTable"));
    table.firstColumnShouldBe(exactTexts("admin"));
    
    String user = "test";
    String email = "test@ivyTeam.ch";
    String password = "password";
    addAdmin(user, email, password, password);
    table.firstColumnShouldBe(exactTexts("admin", user));
    
    editAdmin(table, user, "test@admin.com");
    table.firstColumnShouldBe(exactTexts("admin", user));
    table.valueForEntryShould(user, 2, exactText("test@admin.com"));
    
    deleteAdmin(table, user);
    table.firstColumnShouldBe(exactTexts("admin"));
  }
  
  private static void deleteAdmin(Table table, String user)
  {
    table.clickButtonForEntry(user, "deleteAdmin");
    assertGrowlMessage(user, "removed");
  }

  private static void editAdmin(Table table, String user, String email)
  {
    table.clickButtonForEntry(user, "editAdminBtn");
    $("#admins\\:editAdminDialog").shouldBe(visible);
    $("#admins\\:editAdminForm\\:name").shouldNotBe(enabled);
    
    $("#admins\\:editAdminForm\\:email").clear();
    $("#admins\\:editAdminForm\\:email").sendKeys(email);
    
    $("#admins\\:editAdminForm\\:saveAdmin").click();
    assertGrowlMessage(user, "modified");
  }

  private static void assertGrowlMessage(String user, String msgPart)
  {
    $(".ui-growl-title").shouldBe(text("'" + user + "' " + msgPart + " successfully"));
  }
  
  public static void testAddAdminInvalidPassword()
  {
    addAdmin("admin", "test@test.com", "password", "pass");
    $("#admins\\:editAdminForm\\:nameMessage").shouldBe(empty);
    $("#admins\\:editAdminForm\\:emailMessage").shouldBe(empty);
    $("#admins\\:editAdminForm\\:password2Message").shouldBe(empty);
    $("#admins\\:editAdminForm\\:passwordMessage").shouldBe(visible);
    $("#admins\\:editAdminForm\\:passwordMessage").shouldBe(text("Password didn't match"));
  }

  public static void testAddAdminInvalidValues()
  {
    addAdmin("", "", "", "");
    $("#admins\\:editAdminForm\\:nameMessage").shouldBe(text("Value is required"));
    $("#admins\\:editAdminForm\\:emailMessage").shouldBe(text("Value is required"));
    $("#admins\\:editAdminForm\\:passwordMessage").shouldBe(text("Value is required"));
    $("#admins\\:editAdminForm\\:password2Message").shouldBe(text("Value is required"));
    $("#admins\\:editAdminForm\\:cancelEditAdmin").click();
  }
  
  public static void addAdmin(String user, String email, String password, String password2)
  {
    $("#addAdminForm\\:newAdminBtn").click();
    $("#admins\\:editAdminDialog").shouldBe(visible);
    
    $("#admins\\:editAdminForm\\:name").clear();
    $("#admins\\:editAdminForm\\:name").sendKeys(user);
    
    $("#admins\\:editAdminForm\\:email").clear();
    $("#admins\\:editAdminForm\\:email").sendKeys(email);
    
    $("#admins\\:editAdminForm\\:password1").clear();
    $("#admins\\:editAdminForm\\:password1").sendKeys(password);
    
    $("#admins\\:editAdminForm\\:password2").clear();
    $("#admins\\:editAdminForm\\:password2").sendKeys(password2);
    
    $("#admins\\:editAdminForm\\:saveAdmin").click();
  }

}
