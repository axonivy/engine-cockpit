package ch.ivyteam.enginecockpit.system;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.forceLogin;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestAdmins {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toAdmins();
  }

  @Test
  void addEditDeleteAdmin() {
    $("h2").shouldBe(text("Administrators"));
    testAddEditDelete();
  }

  @Test
  void searchAdmin() {
    var table = new Table(By.id("admins:adminForm:adminTable"), "span");
    table.firstColumnShouldBe(sizeGreaterThanOrEqual(1));
    table.search("dmi");
    table.firstColumnShouldBe(sizeGreaterThanOrEqual(1));
    table.search("notexist");
    table.firstColumnShouldBe(CollectionCondition.empty);
  }

  @Test
  void loginWithNewAdmin() {
    addAdmin("support@ivyteam.ch", "support@ivyteam.ch", "password", "password");

    forceLogin("support@ivyteam.ch", "password");
    $("#sessionUserName").shouldBe(exactText("support@ivyteam.ch"));
    $(".profile-image-wrapper img").shouldBe(exist);

    forceLogin();
    $("#sessionUserName").shouldBe(exactText("admin"));
    $(".profile-image-wrapper i").shouldBe(exist);
    Navigation.toAdmins();
    deleteAdmin(new Table(By.id("admins:adminForm:adminTable"), "span"), "support@ivyteam.ch");
  }

  @Test
  void adminDialogInvalid() {
    testAddAdminInvalidValues();
    testAddAdminInvalidPassword();
  }

  @Test
  void ownAdminCannotBeDeleted() {
    assertOwnAdminCannotBeDeleted();
  }

  public static void assertOwnAdminCannotBeDeleted() {
    var table = new Table(By.id("admins:adminForm:adminTable"), "span");
    table.buttonForEntryShouldBeDisabled("admin", "deleteAdmin");
  }

  public static void testAddEditDelete() {
    var table = new Table(By.id("admins:adminForm:adminTable"), "span");
    table.firstColumnShouldBe(exactTexts("admin"));

    var user = "test";
    var email = "test@ivyTeam.ch";
    var password = "password";
    addAdmin(user, email, password, password);
    table.firstColumnShouldBe(exactTexts("admin", user));

    editAdmin(table, user, "test@admin.com");
    table.firstColumnShouldBe(exactTexts("admin", user));
    table.valueForEntryShould(user, 3, exactText("test@admin.com"));

    deleteAdmin(table, user);
    table.firstColumnShouldBe(exactTexts("admin"));
  }

  private static void deleteAdmin(Table table, String user) {
    table.clickButtonForEntry(user, "deleteAdmin");
    $(By.id("admins:adminForm:deleteAdminDialog")).shouldBe(visible);
    $(By.id("admins:adminForm:deleteAdminYesBtn")).shouldBe(visible).click();
    assertGrowlMessage(user, "deleted");
  }

  private static void editAdmin(Table table, String user, String email) {
    table.clickButtonForEntry(user, "editAdminBtn");
    $(By.id("admins:editAdminDialog")).shouldBe(visible);
    $(By.id("admins:editAdminForm:name")).shouldNotBe(enabled);

    $(By.id("admins:editAdminForm:email")).clear();
    $(By.id("admins:editAdminForm:email")).sendKeys(email);

    $(By.id("admins:editAdminForm:updateAdmin")).click();
    assertGrowlMessage(user, "updated");
  }

  private static void assertGrowlMessage(String user, String msgPart) {
    $(".ui-growl-title").shouldBe(text("'" + user + "' " + msgPart));
  }

  public static void testAddAdminInvalidPassword() {
    addAdmin("admin", "test@test.com", "password", "pass");
    $(By.id("admins:editAdminForm:nameMessage")).shouldBe(empty);
    $(By.id("admins:editAdminForm:emailMessage")).shouldBe(empty);
    $(By.id("admins:editAdminForm:password2Message")).shouldBe(empty);
    $(By.id("admins:editAdminForm:passwordMessage")).shouldBe(visible);
    $(By.id("admins:editAdminForm:passwordMessage")).shouldBe(text("Password didn't match"));
  }

  public static void testAddAdminInvalidValues() {
    addAdmin("", "", "", "");
    $(By.id("admins:editAdminForm:nameMessage")).shouldBe(text("Value is required"));
    $(By.id("admins:editAdminForm:emailMessage")).shouldBe(text("Value is required"));
    $(By.id("admins:editAdminForm:passwordMessage")).shouldBe(text("Value is required"));
    $(By.id("admins:editAdminForm:password2Message")).shouldBe(text("Value is required"));
    $(By.id("admins:editAdminForm:cancelEditAdmin")).click();
  }

  public static void addAdmin(String user, String email, String password, String password2) {
    $(By.id("addAdminForm:newAdminBtn")).click();
    $(By.id("admins:editAdminDialog")).shouldBe(visible);

    $(By.id("admins:editAdminForm:name")).clear();
    $(By.id("admins:editAdminForm:name")).sendKeys(user);

    $(By.id("admins:editAdminForm:email")).clear();
    $(By.id("admins:editAdminForm:email")).sendKeys(email);

    $(By.id("admins:editAdminForm:password1")).clear();
    $(By.id("admins:editAdminForm:password1")).sendKeys(password);

    $(By.id("admins:editAdminForm:password2")).clear();
    $(By.id("admins:editAdminForm:password2")).sendKeys(password2);

    $(By.id("admins:editAdminForm:addAdmin")).click();
  }
}
