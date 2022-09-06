package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
class WebTestApplicationTab {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toVariables();
  }

  @Test
  void applicationCount() {
    assertThat(Tab.APP.getCount()).isGreaterThan(0);
  }

  @Test
  void applicationNames() {
    assertThat(Tab.APP.getTabs()).isNotEmpty();
  }

  @Test
  void applicationSwitchPerIndex() {
    assertThat(Tab.APP.getSelectedTabIndex()).isNotEqualTo(-1);

    Tab.APP.switchToTab(0);
    assertThat(Tab.APP.getSelectedTabIndex()).isSameAs(0);

    Tab.APP.switchToTab(1);
    assertThat(Tab.APP.getSelectedTabIndex()).isSameAs(1);

    Tab.APP.switchToTab(0);
    assertThat(Tab.APP.getSelectedTabIndex()).isSameAs(0);
  }

  @Test
  void applicationSwtichPerName() {
    var selectedApplication = Tab.APP.getSelectedTab();
    assertThat(selectedApplication).isNotBlank();

    var otherApp = Tab.APP.getTabs().get(1);
    Tab.APP.switchToTab(otherApp);
    assertThat(Tab.APP.getSelectedTab()).isNotBlank().endsWith(otherApp);

    Navigation.toBusinessCalendar();
    assertThat(Tab.APP.getSelectedTab()).isNotBlank().endsWith(otherApp);
  }

  @Test
  void witchApplicationAndCheckVariables() {
    var noRecordsString = "No records found.";
    Tab.APP.switchToTab(0);
    var configTable = PrimeUi.table(By.id("apps:applicationTabView:0:config:form:configTable"));
    configTable.containsNot(noRecordsString);
    Tab.APP.switchToTab(1);
    Tab.APP.switchToTab(0);
    Tab.APP.switchToTab(2);
    Tab.APP.switchToTab(0);
    Selenide.sleep(500);
    configTable.containsNot(noRecordsString);
  }
}
