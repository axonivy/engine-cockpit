package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestAdvancedConfig extends WebTestBase
{
  @Test
  void testAdvancedConfig(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    assertThat(driver.findElementByTagName("h1").getText()).contains("Advanced Config");
    List<WebElement> configs = driver.findElementsByClassName("config-name");
    if (!configs.isEmpty())
    {
      assertThat(driver.findElementsByClassName("config-name")).isNotEmpty();
      WebElement lastConfig = driver.findElementByXPath("(//*[@class='config-name'])[last()]");
      WebElement input = driver.findElementByXPath("//input[contains(@class, 'table-search-input-withicon')]");
      input.sendKeys(lastConfig.getText());
      saveScreenshot(driver, "search_config");
      await().untilAsserted(() -> assertThat(driver.findElementsByClassName("config-name")).hasSize(1));
    }
  }
  
  @Test
  void testNewConfigInvalid(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    driver.findElementById("card:newConfigBtn").click();
    saveScreenshot(driver, "new_config_model");
    webAssertThat(() -> assertThat(driver.findElementById("card:newConfigurationModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("card:newConfigurationForm:newConfigurationKey").getAttribute("value")).isBlank());
    driver.findElementById("card:newConfigurationForm:savenewConfiguration").click();
    saveScreenshot(driver, "invalid_new_config");
    webAssertThat(() -> assertThat(driver.findElementById("card:newConfigurationForm:newConfigurationKeyMessage").isDisplayed()).isTrue());
  }
  
  @Test
  void testNewEditAndDeleteConfig(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    driver.findElementById("card:newConfigBtn").click();
    saveScreenshot(driver, "new_config_model");
    webAssertThat(() -> assertThat(driver.findElementById("card:newConfigurationModal").isDisplayed()).isTrue());
    
    driver.findElementById("card:newConfigurationForm:newConfigurationKey").sendKeys("testKey");
    driver.findElementById("card:newConfigurationForm:newConfigurationValue").sendKeys("testValue");
    driver.findElementById("card:newConfigurationForm:savenewConfiguration").click();
    saveScreenshot(driver, "save_new_config");
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='config-name'][text()='testKey']/../../td[2]").getText()).contains("testValue"));
    
    String eleNumber = driver.findElementByXPath("//*[@class='config-name'][text()='testKey']/../..").getAttribute("data-ri");
    String id = "card:form:advancedConfigTable:" + eleNumber;
    driver.findElementById(id + ":editConfigBtn").click();
    saveScreenshot(driver, "edit_config");
    webAssertThat(() -> assertThat(driver.findElementById("card:editConfigurationModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("card:editConfigurationForm:editConfigurationKey").getText()).isEqualTo("testKey"));
    webAssertThat(() -> assertThat(driver.findElementById("card:editConfigurationForm:editConfigurationValue").getAttribute("value")).isEqualTo("testValue"));
    
    driver.findElementById("card:editConfigurationForm:editConfigurationValue").clear();
    driver.findElementById("card:editConfigurationForm:editConfigurationValue").sendKeys("newValue");
    driver.findElementById("card:editConfigurationForm:saveEditConfiguration").click();
    saveScreenshot(driver, "save_edit_config");
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='config-name'][text()='testKey']/../../td[2]").getText()).contains("newValue"));
  
    driver.findElementById(id + ":tasksButton").click();
    webAssertThat(() -> assertThat(driver.findElementById(id + ":activityMenu").isDisplayed()).isTrue());
    driver.findElementById(id + ":deleteConfigBtn").click();
    saveScreenshot(driver, "delete_config");
    webAssertThat(() -> assertThat(driver.findElementById("card:form:deleteConfigConfirmDialog").isDisplayed()).isTrue());
    
    driver.findElementById("card:form:deleteConfigConfirmYesBtn").click();
    saveScreenshot(driver, "delete_config_yes");
    webAssertThat(() -> assertThat(driver.findElementsByClassName("config-name")
            .stream().map(e -> e.getText()).anyMatch(t -> t.equals("testKey"))).isFalse());
  }
  
  private void toAdvancedConfig(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toAdvancedConfig(driver);
    saveScreenshot(driver);
  }
}
