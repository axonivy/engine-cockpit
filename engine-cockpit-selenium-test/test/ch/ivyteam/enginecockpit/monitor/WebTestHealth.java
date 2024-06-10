package ch.ivyteam.enginecockpit.monitor;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverConditions;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestHealth {

  private Table messagesTable;

  @BeforeEach
  void beforeEach() {
    EngineCockpitUtil.login();
    Navigation.toHealth();
    messagesTable = new Table(By.id("form:messagesTable"));
  }

  @Test
  void messages() {
    messagesTable.rows().shouldHave(CollectionCondition.size(2));
  }

  @Test
  void severity() {
    var cell = messagesTable.tableEntry(1, 1);
    cell.shouldHave(text("HIGH"));
    cell.$("span").shouldHave(cssClass("health-high"));
    cell.$("span>i").shouldHave(cssClass("si-alert-circle"));

    cell = messagesTable.tableEntry(2, 1);
    cell.shouldHave(text("LOW"));
    cell.$("span").shouldHave(cssClass("health-low"));
    cell.$("span>i").shouldHave(cssClass("si-road-sign-warning"));

    messagesTable.sortByColumn("Severity");

    messagesTable.tableEntry(1, 1).shouldHave(text("LOW"));
    messagesTable.tableEntry(2, 1).shouldHave(text("HIGH"));
  }

  @Test
  void message() {
    messagesTable.tableEntry(1, 2).shouldHave(text("Release Candidate"));
    messagesTable.tableEntry(2, 2).shouldHave(text("Demo Mode"));

    messagesTable.sortByColumn("Message");

    messagesTable.tableEntry(1, 2).shouldHave(text("Demo Mode"));
    messagesTable.tableEntry(2, 2).shouldHave(text("Release Candidate"));
  }

  @Test
  void description() {
    messagesTable.tableEntry(1, 3).shouldHave(text("Don't use this version in production"));
    messagesTable.tableEntry(2, 3).shouldHave(text("No or invalid license installed"));

    messagesTable.sortByColumn("Description");

    messagesTable.tableEntry(1, 3).shouldHave(text("Don't use this version in production"));
    messagesTable.tableEntry(2, 3).shouldHave(text("No or invalid license installed"));

    messagesTable.sortByColumn("Description");

    messagesTable.tableEntry(1, 3).shouldHave(text("No or invalid license installed"));
    messagesTable.tableEntry(2, 3).shouldHave(text("Don't use this version in production"));
  }

  @Test
  void check() {
    messagesTable.tableEntry(1, 4).shouldHave(text("Release Candidate"));
    messagesTable.tableEntry(2, 4).shouldHave(text("Engine Mode"));

    messagesTable.sortByColumn("Check");

    messagesTable.tableEntry(1, 4).shouldHave(text("Engine Mode"));
    messagesTable.tableEntry(2, 4).shouldHave(text("Release Candidate"));
  }

  @Test
  void action_external() {
    messagesTable.tableEntry(1, 5).click();
    Selenide.webdriver().shouldHave(WebDriverConditions.url("https://developer.axonivy.com/download"));
  }

  @Test
  void action_internal() {
    messagesTable.tableEntry(2, 5).click();
    Selenide.webdriver().shouldHave(WebDriverConditions.urlContaining("setup-intro.xhtml"));
  }

  @Test
  void checkNow() {
    $(By.id("run")).click();
    messagesTable.rows().shouldHave(CollectionCondition.size(2));
  }

  @Test
  void refresh() {
    $(By.id("refresh")).click();
    messagesTable.rows().shouldHave(CollectionCondition.size(2));
  }
}
