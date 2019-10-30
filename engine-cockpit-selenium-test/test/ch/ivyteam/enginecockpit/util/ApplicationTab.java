package ch.ivyteam.enginecockpit.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import ch.ivyteam.enginecockpit.WebBase;

public class ApplicationTab
{
  private static final By APPLICATION_TAB = By.xpath("//li[contains(@class, 'application-tab')]/a");
  private static final By APPLICATION_TAB_LI = By.xpath("//li[contains(@class, 'application-tab')]");
  private static final By SELECTED_APPLICATION_TAB = By.xpath("//li[contains(@class, 'application-tab') and contains(@class, 'ui-state-active')]/child::a");

  public static int getApplicationCount(RemoteWebDriver driver)
  {
    return driver.findElements(APPLICATION_TAB).size();
  }

  public static List<String> getApplications(RemoteWebDriver driver)
  {
    return driver.findElements(APPLICATION_TAB).stream().map(e -> e.getText()).collect(Collectors.toList());
  }
  
  public static int getSelectedApplicationIndex(RemoteWebDriver driver)
  {
    for (int i = 0; i < driver.findElements(APPLICATION_TAB_LI).size(); i++)
    {
      if (driver.findElements(APPLICATION_TAB_LI).get(i).getAttribute("class").contains("ui-state-active"))
      {
        return i;
      }
    }
    return -1;
  }
  
  public static String getSelectedApplication(RemoteWebDriver driver)
  {
    return driver.findElement(SELECTED_APPLICATION_TAB).getText();
  }

  public static void switchToApplication(RemoteWebDriver driver, int index)
  {
    if (getSelectedApplicationIndex(driver) != index)
    {
      driver.findElements(APPLICATION_TAB).get(index).click();
    }
    WebBase.webAssertThat(() -> assertThat(getSelectedApplicationIndex(driver)).isEqualTo(index));
  }

  public static void switchToApplication(RemoteWebDriver driver, String appName)
  {
    if (getSelectedApplication(driver).equals(appName))
    {
      return;
    }
    driver.findElements(APPLICATION_TAB).stream()
            .filter(e -> e.getText().equals(appName))
            .findFirst()
            .ifPresent(app -> app.click());
    WebBase.webAssertThat(() -> assertThat(getSelectedApplication(driver)).isEqualTo(appName));
  }
}
