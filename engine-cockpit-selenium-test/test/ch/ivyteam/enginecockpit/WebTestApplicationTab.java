package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.AppTab;
import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
class WebTestApplicationTab {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toVariables();
  }

  @Test
  void applicationCount() {
    assertThat(AppTab.getCount()).isGreaterThan(0);
  }

  @Test
  void applicationNames() {
    assertThat(AppTab.getTabs()).isNotEmpty();
  }

  @Test
  void applicationSwitchPerIndex() {
    assertThat(AppTab.getSelectedTabIndex()).isNotEqualTo(-1);

    AppTab.switchToTab(0);
    assertThat(AppTab.getSelectedTabIndex()).isSameAs(0);

    AppTab.switchToTab(1);
    assertThat(AppTab.getSelectedTabIndex()).isSameAs(1);

    AppTab.switchToTab(0);
    assertThat(AppTab.getSelectedTabIndex()).isSameAs(0);
  }

  @Test
  void applicationSwtichPerName() {
    var selectedApplication = AppTab.getSelectedTab();
    assertThat(selectedApplication).isNotBlank();

    var otherApp = AppTab.getTabs().get(1);
    AppTab.switchToTab(otherApp);
    assertThat(AppTab.getSelectedTab()).isNotBlank().endsWith(otherApp);

    Navigation.toBusinessCalendar();
    assertThat(AppTab.getSelectedTab()).isNotBlank().endsWith(otherApp);
  }
}
