package ch.ivyteam.enginecockpit.services;

import static com.codeborne.selenide.CollectionCondition.size;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestWebservices extends WebTestBase
{
  @Test
  void testWebserviesInTable()
  {
    navigateToWebservices();
    
    Table table = new Table(By.id("form:card:tabs:applicationTabView:" + 
            Tab.getSelectedTabIndex() + ":tableForm:webservicesTable"), true);
    table.firstColumnShouldBe(size(2));
    
    table.search(table.getFirstColumnEntries().get(0));
    table.firstColumnShouldBe(size(1));
  }
  
  private void navigateToWebservices()
  {
    login();
    Navigation.toWebservices();
  }
}
