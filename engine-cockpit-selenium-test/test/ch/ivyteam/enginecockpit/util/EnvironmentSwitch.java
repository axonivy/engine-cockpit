package ch.ivyteam.enginecockpit.util;

import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

public class EnvironmentSwitch
{
  private static final By ENV_SWITCH = By.xpath("//div[contains(@class, 'environment-switch')]");

  public static void switchToEnv(FirefoxDriver driver, String env)
  {
    clickOnEnvSwitch(driver);
    driver.findElementByXPath("//ul[@id='" + getEnvId(driver) + "_items']/li[text()='" + env + "']").click();
  }
  
  public static String getEnv(FirefoxDriver driver)
  {
    String envId = getEnvId(driver);
    return driver.findElementById(envId + "_label").getText();
  }
  
  public static List<String> getAvailableEnvs(FirefoxDriver driver)
  {
    clickOnEnvSwitch(driver);
    return driver.findElementsByXPath("//ul[@id='" + getEnvId(driver) + "_items']/li").stream()
            .map(e -> e.getText()).collect(Collectors.toList());
  }
  
  private static void clickOnEnvSwitch(FirefoxDriver driver)
  {
    driver.findElement(ENV_SWITCH).click();
    await().until(() -> driver.findElementById(getEnvId(driver) + "_items").isDisplayed());
  }
  
  private static String getEnvId(FirefoxDriver driver)
  {
    return driver.findElement(ENV_SWITCH).getAttribute("id");
  }
}
