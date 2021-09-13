package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestApplicationTab {

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toUsers();
  }

  @Test
  void testApplicationCount() {
    assertThat(Tab.getCount()).isGreaterThan(0);
  }

  @Test
  void testApplicationNames() {
    assertThat(Tab.getTabs()).isNotEmpty();
  }

  @Test
  void testApplicationSwitchPerIndex() {
    assertThat(Tab.getSelectedTabIndex()).isNotEqualTo(-1);
    Tab.switchToTab(0);
    assertThat(Tab.getSelectedTabIndex()).isSameAs(0);
    Tab.switchToTab(1);
    assertThat(Tab.getSelectedTabIndex()).isSameAs(1);
    Tab.switchToTab(0);
    assertThat(Tab.getSelectedTabIndex()).isSameAs(0);
  }

  @Test
  void testApplicationSwtichPerName() {
    String selectedApplication = Tab.getSelectedTab();
    assertThat(selectedApplication).isNotBlank();
    String otherApp = Tab.getTabs().get(1);
    Tab.switchToTab(otherApp);
    assertThat(Tab.getSelectedTab()).isNotBlank().endsWith(otherApp);
    Navigation.toRoles();
    assertThat(Tab.getSelectedTab()).isNotBlank().endsWith(otherApp);
  }

}
