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
public class WebTestWebservices
{
  @Test
  void testWebserviesInTable()
  {
    login();
    Navigation.toWebservices();
    Tab.switchToDefault();
    Table table = new Table(By.id("form:card:tabs:applicationTabView:" + 
            Tab.getSelectedTabIndex() + ":tableForm:webservicesTable"), true);
    table.firstColumnShouldBe(size(2));
    
    table.search("axis2");
    table.firstColumnShouldBe(size(1));
  }
}
