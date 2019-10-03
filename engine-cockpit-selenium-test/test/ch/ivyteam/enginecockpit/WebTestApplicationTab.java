package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.ApplicationTab;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestApplicationTab extends WebTestBase
{
  @Test
  void testApplicationCount(FirefoxDriver driver)
  {
    login(driver);
    navigateToUsers(driver);
    webAssertThat(() -> assertThat(ApplicationTab.getApplicationCount(driver)).isGreaterThan(0));
  }

  @Test
  void testApplicationNames(FirefoxDriver driver)
  {
    login(driver);
    navigateToUsers(driver);
    webAssertThat(() -> assertThat(ApplicationTab.getApplications(driver)).isNotEmpty());
  }

  @Test
  void testApplicationSwitchPerIndex(FirefoxDriver driver)
  {
    login(driver);
    navigateToUsers(driver);
    int appNotFoundIndex = -1;
    int index = ApplicationTab.getSelectedApplicationIndex(driver);
    webAssertThat(() -> assertThat(index).isNotEqualTo(appNotFoundIndex));
    if (ApplicationTab.getApplicationCount(driver) > 1)
    {
      int wantedIndex = 1;
      ApplicationTab.switchToApplication(driver, wantedIndex);
      saveScreenshot(driver, "switch_app");
      webAssertThat(() -> assertThat(ApplicationTab.getSelectedApplicationIndex(driver))
              .isNotEqualTo(appNotFoundIndex).isNotSameAs(index).isSameAs(wantedIndex));
      navigateToRoles(driver);
      webAssertThat(() -> assertThat(ApplicationTab.getSelectedApplicationIndex(driver))
              .isNotEqualTo(appNotFoundIndex).isNotSameAs(index).isSameAs(wantedIndex));
    }
  }

  @Test
  void testApplicationSwtichPerName(FirefoxDriver driver)
  {
    login(driver);
    navigateToUsers(driver);
    String selectedApplication = ApplicationTab.getSelectedApplication(driver);
    webAssertThat(() -> assertThat(selectedApplication).isNotBlank());
    Optional<String> otherApp = ApplicationTab.getApplications(driver).stream()
            .filter(app -> !app.equals(selectedApplication)).findAny();
    if (otherApp.isPresent())
    {
      ApplicationTab.switchToApplication(driver, otherApp.get());
      saveScreenshot(driver, "switch_app");
      webAssertThat(() -> assertThat(ApplicationTab.getSelectedApplication(driver))
              .isNotBlank().endsWith(otherApp.get()));
      navigateToRoles(driver);
      webAssertThat(() -> assertThat(ApplicationTab.getSelectedApplication(driver))
              .isNotBlank().endsWith(otherApp.get()));
    }
  }

  private void navigateToUsers(FirefoxDriver driver)
  {
    Navigation.toUsers(driver);
    saveScreenshot(driver, "users");
  }

  private void navigateToRoles(FirefoxDriver driver)
  {
    Navigation.toRoles(driver);
    saveScreenshot(driver, "roles");
  }
}
