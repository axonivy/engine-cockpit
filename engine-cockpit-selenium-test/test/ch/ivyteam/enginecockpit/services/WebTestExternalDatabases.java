package ch.ivyteam.enginecockpit.services;

import static com.codeborne.selenide.CollectionCondition.size;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestExternalDatabases extends WebTestBase
{
  @Test
  void testDatabasesInTable()
  {
    navigateToDatabases();
    Table table = new Table(By.id("form:card:tabs:applicationTabView:" + 
            Tab.getSelectedTabIndex() + ":tableForm:externalDatabasesTable"), true);
    table.firstColumnShouldBe(size(3));

    table.search(table.getFirstColumnEntries().get(0));
    table.firstColumnShouldBe(size(1));
  }
  
  private void navigateToDatabases()
  {
    login();
    Navigation.toExternalDatabases();
  }
}
