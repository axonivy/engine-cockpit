package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EnvironmentSwitch;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestDatabases {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toDatabases();
    Tab.APP.switchToDefault();
    EnvironmentSwitch.switchToEnv("Default");
  }

  @Test
  void databasesInTable() {
    Table table = new Table(By.id("tabs:applicationTabView:" +
            Tab.APP.getSelectedTabIndex() + ":form:databasesTable"), true);
    table.firstColumnShouldBe(size(3));

    table.search(table.getFirstColumnEntries().get(0));
    table.firstColumnShouldBe(size(1));
  }

  @Test
  void envSwitch() {
    Table table = new Table(By.id("tabs:applicationTabView:" +
            Tab.APP.getSelectedTabIndex() + ":form:databasesTable"), true);
    table.valueForEntryShould("test-db", 2, text("localhost:3306/test-db"));
    EnvironmentSwitch.switchToEnv("test");
    table.valueForEntryShould("test-db", 2, text("test.com:3306/test-db"));
    EnvironmentSwitch.switchToEnv("Default");
  }
}
