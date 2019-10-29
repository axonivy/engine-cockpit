package ch.ivyteam.enginecockpit.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.remote.RemoteWebDriver;

import ch.ivyteam.enginecockpit.WebBase;

public class EnvironmentSwitch
{
  private static final By ENV_SWITCH = By.xpath("//div[contains(@class, 'environment-switch')]");

  public static void switchToEnv(RemoteWebDriver driver, String env)
  {
    clickOnEnvSwitch(driver);
    driver.findElementByXPath("//ul[@id='" + getEnvId(driver) + "_items']/li[text()='" + env + "']").click();
  }
  
  public static String getEnv(RemoteWebDriver driver)
  {
    String envId = getEnvId(driver);
    return driver.findElementById(envId + "_label").getText();
  }
  
  public static List<String> getAvailableEnvs(RemoteWebDriver driver)
  {
    clickOnEnvSwitch(driver);
    return driver.findElementsByXPath("//ul[@id='" + getEnvId(driver) + "_items']/li").stream()
            .map(e -> e.getText()).collect(Collectors.toList());
  }
  
  private static void clickOnEnvSwitch(RemoteWebDriver driver)
  {
    driver.findElement(ENV_SWITCH).click();
    WebBase.webAssertThat(() -> assertThat(driver.findElementById(getEnvId(driver) + "_items").isDisplayed()).isTrue());
  }
  
  private static String getEnvId(RemoteWebDriver driver)
  {
    return driver.findElement(ENV_SWITCH).getAttribute("id");
  }
}
