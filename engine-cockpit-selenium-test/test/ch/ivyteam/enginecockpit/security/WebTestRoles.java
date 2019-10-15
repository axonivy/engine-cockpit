package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestRoles extends WebTestBase
{
  @Test
  void testRolesInTable(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRoles(driver);
    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("Roles"));
    WebElement panel = getVisibleRolePanel(driver);
    List<WebElement> roles = panel.findElements(By.className("ui-treenode-content"));
    webAssertThat(() -> assertThat(roles).isNotEmpty());
    int roleCount = roles.size();
    webAssertThat(() -> assertThat(roleCount).isGreaterThanOrEqualTo(1));
    panel.findElement(By.xpath(".//input[contains(@class, 'ui-inputfield')]")).sendKeys("Everybody");
    sleep(300); //sleep search delay
    saveScreenshot(driver);
    webAssertThat(() -> assertThat(getVisibleRolePanel(driver).findElements(By.xpath("//*[contains(@class, 'ui-treenode-content')]")).stream()
              .filter(found -> found.isDisplayed()).collect(Collectors.toList())).hasSize(1));
  }

  private WebElement getVisibleRolePanel(FirefoxDriver driver)
  {
    return driver.findElementsByClassName("ui-tabs-panels").stream().filter(e -> e.isDisplayed()).findAny().get();
  }
  
  void sleep(int ms)
  {
    try
    {
      Thread.sleep(ms);
    }
    catch (InterruptedException ex)
    {
      ex.printStackTrace();
    }
  }
}
