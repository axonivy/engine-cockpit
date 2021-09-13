package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestDatabases {

  @Test
  void testDatabasesInTable() {
    login();
    Navigation.toDatabases();
    Tab.switchToDefault();
    Table table = new Table(By.id("tabs:applicationTabView:" +
            Tab.getSelectedTabIndex() + ":form:databasesTable"), true);
    table.firstColumnShouldBe(size(3));

    table.search(table.getFirstColumnEntries().get(0));
    table.firstColumnShouldBe(size(1));
  }

}
