package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestDatabases {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toDatabases();
    Tab.APP.switchToDefault();
  }

  @Test
  void databasesInTable() {
    Table table = new Table(By.id("tabs:applicationTabView:" +
        Tab.APP.getSelectedTabIndex() + ":form:databasesTable"), true);
    table.firstColumnShouldBe(size(3));

    table.search(table.getFirstColumnEntries().get(0));
    table.firstColumnShouldBe(size(1));
  }
}
