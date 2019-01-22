package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
    assertThat(ApplicationTab.getApplicationCount(driver)).isGreaterThan(0);
  }

  @Test
  void testApplicationNames(FirefoxDriver driver)
  {
    login(driver);
    navigateToUsers(driver);
    assertThat(ApplicationTab.getApplications(driver)).isNotEmpty();
  }

  @Test
  void testApplicationSwitchPerIndex(FirefoxDriver driver)
  {
    login(driver);
    navigateToUsers(driver);
    int appNotFoundIndex = -1;
    int index = ApplicationTab.getSelectedApplicationIndex(driver);
    assertThat(index).isNotEqualTo(appNotFoundIndex);
    int appCount = ApplicationTab.getApplicationCount(driver);
    if (appCount > 1)
    {
      int wantedIndex = 1;
      ApplicationTab.switchToApplication(driver, wantedIndex);
      saveScreenshot(driver, "switch_app");
      int newIndex = ApplicationTab.getSelectedApplicationIndex(driver);
      assertThat(newIndex).isNotEqualTo(appNotFoundIndex).isNotSameAs(index).isSameAs(wantedIndex);
      navigateToRoles(driver);
      newIndex = ApplicationTab.getSelectedApplicationIndex(driver);
      assertThat(newIndex).isNotEqualTo(appNotFoundIndex).isNotSameAs(index).isSameAs(wantedIndex);
    }
  }

  @Test
  void testApplicationSwtichPerName(FirefoxDriver driver)
  {
    login(driver);
    navigateToUsers(driver);
    String selectedApplication = ApplicationTab.getSelectedApplication(driver);
    System.out.println(selectedApplication);
    assertThat(selectedApplication).isNotBlank();
    List<String> applications = ApplicationTab.getApplications(driver);
    Optional<String> otherApp = applications.stream().filter(app -> !app.equals(selectedApplication)).findAny();
    if (otherApp.isPresent())
    {
      ApplicationTab.switchToApplication(driver, otherApp.get());
      saveScreenshot(driver, "switch_app");
      String newSelectedApp = ApplicationTab.getSelectedApplication(driver);
      assertThat(newSelectedApp).isNotBlank().endsWith(otherApp.get());
      navigateToRoles(driver);
      newSelectedApp = ApplicationTab.getSelectedApplication(driver);
      assertThat(newSelectedApp).isNotBlank().endsWith(otherApp.get());
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
