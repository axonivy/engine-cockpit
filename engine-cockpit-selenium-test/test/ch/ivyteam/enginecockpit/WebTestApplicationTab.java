package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

public class WebTestApplicationTab extends WebTestBase
{
  @Test
  void testApplicationCount()
  {
    login();
    navigateToUsers();
    assertThat(Tab.getCount()).isGreaterThan(0);
  }

  @Test
  void testApplicationNames()
  {
    login();
    navigateToUsers();
    assertThat(Tab.getTabs()).isNotEmpty();
  }

  @Test
  void testApplicationSwitchPerIndex()
  {
    login();
    navigateToUsers();
    assertThat(Tab.getSelectedTabIndex()).isNotEqualTo(-1);
    Tab.switchToTab(0);
    assertThat(Tab.getSelectedTabIndex()).isSameAs(0);
    Tab.switchToTab(1);
    assertThat(Tab.getSelectedTabIndex()).isSameAs(1);
    Tab.switchToTab(0);
    assertThat(Tab.getSelectedTabIndex()).isSameAs(0);
  }

  @Test
  void testApplicationSwtichPerName()
  {
    login();
    navigateToUsers();
    String selectedApplication = Tab.getSelectedTab();
    assertThat(selectedApplication).isNotBlank();
    String otherApp = Tab.getTabs().get(1);
    Tab.switchToTab(otherApp);
    assertThat(Tab.getSelectedTab()).isNotBlank().endsWith(otherApp);
    navigateToRoles();
    assertThat(Tab.getSelectedTab()).isNotBlank().endsWith(otherApp);
  }

  private void navigateToUsers()
  {
    Navigation.toUsers();
  }

  private void navigateToRoles()
  {
    Navigation.toRoles();
  }
}
