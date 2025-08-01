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
class WebTestWebservices {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toWebservices();
    Tab.APP.switchToDefault();
  }

  @Test
  void webserviesInTable() {
    Table table = new Table(By.id("tabs:applicationTabView:" +
        Tab.APP.getSelectedTabIndex() + ":form:webservicesTable"), true);
    table.firstColumnShouldBe(size(2));

    table.search("second-web");
    table.firstColumnShouldBe(size(1));
  }
}
