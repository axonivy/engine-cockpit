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
public class WebTestRestClients
{
  @Test
  void testRestClientsInTable()
  {
    login();
    Navigation.toRestClients();
    Table table = new Table(By.id("form:card:tabs:applicationTabView:" + 
            Tab.getSelectedTabIndex() + ":tableForm:restClientsTable"), true);
    table.firstColumnShouldBe(size(2));

    table.search(table.getFirstColumnEntries().get(0));
    table.firstColumnShouldBe(size(1));
  }
  
}
