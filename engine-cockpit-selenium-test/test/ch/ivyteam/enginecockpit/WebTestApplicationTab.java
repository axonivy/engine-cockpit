package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.util.ApplicationTab;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestApplicationTab extends WebTestBase
{
  @Test
  void testApplicationCount()
  {
    login();
    navigateToUsers();
    webAssertThat(() -> assertThat(ApplicationTab.getApplicationCount(driver)).isGreaterThan(0));
  }

  @Test
  void testApplicationNames()
  {
    login();
    navigateToUsers();
    webAssertThat(() -> assertThat(ApplicationTab.getApplications(driver)).isNotEmpty());
  }

  @Test
  void testApplicationSwitchPerIndex()
  {
    login();
    navigateToUsers();
    int appNotFoundIndex = -1;
    int index = ApplicationTab.getSelectedApplicationIndex(driver);
    webAssertThat(() -> assertThat(index).isNotEqualTo(appNotFoundIndex));
    if (ApplicationTab.getApplicationCount(driver) > 1)
    {
      int wantedIndex = 1;
      ApplicationTab.switchToApplication(driver, wantedIndex);
      saveScreenshot("switch_app");
      webAssertThat(() -> assertThat(ApplicationTab.getSelectedApplicationIndex(driver))
              .isNotEqualTo(appNotFoundIndex).isNotSameAs(index).isSameAs(wantedIndex));
      navigateToRoles();
      webAssertThat(() -> assertThat(ApplicationTab.getSelectedApplicationIndex(driver))
              .isNotEqualTo(appNotFoundIndex).isNotSameAs(index).isSameAs(wantedIndex));
    }
  }

  @Test
  void testApplicationSwtichPerName()
  {
    login();
    navigateToUsers();
    String selectedApplication = ApplicationTab.getSelectedApplication(driver);
    webAssertThat(() -> assertThat(selectedApplication).isNotBlank());
    Optional<String> otherApp = ApplicationTab.getApplications(driver).stream()
            .filter(app -> !app.equals(selectedApplication)).findAny();
    if (otherApp.isPresent())
    {
      ApplicationTab.switchToApplication(driver, otherApp.get());
      saveScreenshot("switch_app");
      webAssertThat(() -> assertThat(ApplicationTab.getSelectedApplication(driver))
              .isNotBlank().endsWith(otherApp.get()));
      navigateToRoles();
      webAssertThat(() -> assertThat(ApplicationTab.getSelectedApplication(driver))
              .isNotBlank().endsWith(otherApp.get()));
    }
  }

  private void navigateToUsers()
  {
    Navigation.toUsers(driver);
    saveScreenshot("users");
  }

  private void navigateToRoles()
  {
    Navigation.toRoles(driver);
    saveScreenshot("roles");
  }
}
