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
public class WebTestRestClients {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toRestClients();
    Tab.APP.switchToDefault();
    EnvironmentSwitch.switchToEnv("Default");
  }

  @Test
  void restClientsInTable() {
    Table table = new Table(By.id("tabs:applicationTabView:" +
            Tab.APP.getSelectedTabIndex() + ":form:restClientsTable"), true);
    table.firstColumnShouldBe(size(2));

    table.search(table.getFirstColumnEntries().get(0));
    table.firstColumnShouldBe(size(1));
  }

  @Test
  void envSwitch() {
    Table table = new Table(By.id("tabs:applicationTabView:" +
            Tab.APP.getSelectedTabIndex() + ":form:restClientsTable"), true);
    table.valueForEntryShould("test-rest", 2, text("test-webservices"));
    EnvironmentSwitch.switchToEnv("test");
    table.valueForEntryShould("test-rest", 2, text("localhost/test"));
    EnvironmentSwitch.switchToEnv("Default");
  }
}
