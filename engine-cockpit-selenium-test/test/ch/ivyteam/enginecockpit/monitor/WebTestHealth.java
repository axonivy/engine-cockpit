package ch.ivyteam.enginecockpit.monitor;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.url;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

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
    messagesTable.rows().shouldHave(sizeGreaterThanOrEqual(2));
    messagesTable.rows().shouldHave(
        anyMatch("row contains", e -> e.getText().contains("HIGH Release Candidate Don't use this version in production it is a release candidate Release Candidate")),
        anyMatch("row contains", e -> e.getText().contains("LOW Demo Mode No or invalid license installed.")));
  }

  @Test
  void severity() {
    messagesTable.sortByColumn("Severity");
    messagesTable.tableEntry(1, 1).shouldHave(text("LOW"));
    messagesTable.sortByColumn("Severity");
    messagesTable.tableEntry(1, 1).shouldNotHave(text("LOW"));
  }

  @Test
  void message() {
    messagesTable.sortByColumn("Message");
    messagesTable.tableEntry(1, 2).shouldHave(text("Demo Mode"));
    messagesTable.sortByColumn("Message");
    messagesTable.tableEntry(1, 2).shouldNotHave(text("Demo Mode"));
  }

  @Test
  void description() {
    messagesTable.sortByColumn("Description");
    messagesTable.tableEntry(1, 3).shouldHave(text("Don't use this version in production"));
    messagesTable.sortByColumn("Description");
    messagesTable.tableEntry(1, 3).shouldNotHave(text("Don't use this version in production"));
  }

  @Test
  void check() {
    messagesTable.sortByColumn("Check");
    messagesTable.tableEntry(1, 4).shouldHave(text("Engine Mode"));
    messagesTable.sortByColumn("Check");
    messagesTable.tableEntry(1, 4).shouldNotHave(text("Engine Mode"));
  }

  @Test
  void action_external() {
    messagesTable.rows().find(text("Release Candidate")).find("button").click();
    webdriver().shouldHave(url("https://developer.axonivy.com/download"));
  }

  @Test
  void action_internal() {
    messagesTable.rows().find(text("Demo Mode")).find("button").click();
    webdriver().shouldHave(urlContaining("setup.xhtml"));
  }

  @Test
  void checkNow() {
    $(By.id("run")).click();
    messagesTable.rows().shouldHave(sizeGreaterThanOrEqual(2));
  }

  @Test
  void refresh() {
    $(By.id("refresh")).click();
    messagesTable.rows().shouldHave(sizeGreaterThanOrEqual(2));
  }
}
