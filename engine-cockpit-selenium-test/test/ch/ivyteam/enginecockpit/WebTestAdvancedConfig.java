package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectOneMenu;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestAdvancedConfig extends WebTestBase
{
  @Test
  void testAdvancedConfig(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    assertThat(driver.findElementByTagName("h1").getText()).contains("Advanced Config");
  }
  
  @Test
  void testSearchConfig(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    webAssertThat(() -> assertThat(driver.findElementsByClassName("config-name")).isNotEmpty());
    String lastConfig = driver.findElementByXPath("(//*[@class='config-name'])[last()]").getText();
    driver.findElementByXPath("//input[contains(@class, 'table-search-input-withicon')]").sendKeys(lastConfig);
    saveScreenshot(driver, "search_config");
    webAssertThat(() -> assertThat(driver.findElementsByClassName("config-name")).hasSize(1));
  }
  
  @Test
  void testHideDefaults(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    String config = "(//*[@class='config-name'])[text()='Data.AppDirectory']";
    webAssertThat(() -> assertThat(driver.findElementByXPath(config).isDisplayed()).isTrue());
    driver.findElementById("card:showDefaultBtnForm:showDefaultsBtn").click();
    webAssertThat(() -> assertThat(elementNotAvailable(driver, By.xpath(config))).isTrue());
    driver.findElementById("card:showDefaultBtnForm:showDefaultsBtn").click();
    webAssertThat(() -> assertThat(driver.findElementByXPath(config).isDisplayed()).isTrue());
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
  void testNewEditAndResetConfig(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    driver.findElementById("card:newConfigBtn").click();
    saveScreenshot(driver, "new_config_model");
    webAssertThat(() -> assertThat(driver.findElementById("card:newConfigurationModal").isDisplayed()).isTrue());
    
    String key = "testKey";
    String value = "testValue";
    driver.findElementById("card:newConfigurationForm:newConfigurationKey").sendKeys(key);
    driver.findElementById("card:newConfigurationForm:newConfigurationValue").sendKeys(value);
    driver.findElementById("card:newConfigurationForm:savenewConfiguration").click();
    saveScreenshot(driver, "save_new_config");
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='config-name'][text()='testKey']/../../td[2]").getText()).contains(value));
    
    driver.findElementById(getConfigEditBtnForKey(driver, key)).click();
    saveScreenshot(driver, "edit_config");
    assertThatConfigEditModalIsVisible(driver, key, value);
    
    driver.findElementById("card:editConfigurationForm:editConfigurationValue").clear();
    driver.findElementById("card:editConfigurationForm:editConfigurationValue").sendKeys("newValue");
    driver.findElementById("card:editConfigurationForm:saveEditConfiguration").click();
    saveScreenshot(driver, "save_edit_config");
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='config-name'][text()='testKey']/../../td[2]").getText()).contains("newValue"));
  
    driver.findElementById(getConfigTaskBtnForKey(driver, key)).click();
    webAssertThat(() -> assertThat(driver.findElementById(getConfigActivityMenuForKey(driver, key)).isDisplayed()).isTrue());
    driver.findElementById(getConfigResetBtnForKey(driver, key)).click();
    saveScreenshot(driver, "reset_config");
    webAssertThat(() -> assertThat(driver.findElementById("card:form:resetConfigConfirmDialog").isDisplayed()).isTrue());
    
    driver.findElementById("card:form:resetConfigConfirmYesBtn").click();
    saveScreenshot(driver, "reset_config_yes");
    webAssertThat(() -> assertThat(driver.findElementsByClassName("config-name")
            .stream().map(e -> e.getText()).anyMatch(t -> t.equals(key))).isFalse());
  }
  
  @Test
  void testEditConfig_booleanFormat(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    String config = "EMail.Server.SSL.UseKey";
    driver.findElementById(getConfigEditBtnForKey(driver, config)).click();
    saveScreenshot(driver, "boolean_input");
    assertThatConfigEditModalIsVisible(driver, config, "false");
  }
  
  @Test
  void testEditConfig_numberFormat(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    String config = "Elasticsearch.ExternalServer.BootTimeout";
    driver.findElementById(getConfigEditBtnForKey(driver, config)).click();
    saveScreenshot(driver, "number_input");
    assertThatConfigEditModalIsVisible(driver, config, "60");
  }
  
  @Test
  void testEditConfig_daytimeFormat(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    String config = "EMail.DailyTaskSummary.TriggerTime";
    driver.findElementById(getConfigEditBtnForKey(driver, config)).click();
    saveScreenshot(driver, "daytime_input");
    assertThatConfigEditModalIsVisible(driver, config, "00:00");
  }
  
  @Test
  void testEditConfig_enumerationFormat(FirefoxDriver driver)
  {
    toAdvancedConfig(driver);
    String config = "EMail.Server.EncryptionMethod";
    driver.findElementById(getConfigEditBtnForKey(driver, config)).click();
    saveScreenshot(driver, "enum_input");
    assertThatConfigEditModalIsVisible(driver, config, "NONE");
  }
  
  private void assertThatConfigEditModalIsVisible(FirefoxDriver driver, String key, String value)
  {
    webAssertThat(() -> assertThat(driver.findElementById("card:editConfigurationModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("card:editConfigurationForm:editConfigurationKey").getText()).isEqualTo(key));
    String attribute = driver.findElementById("card:editConfigurationForm:editConfigurationValue").getAttribute("class");
    PrimeUi primeUi = new PrimeUi(driver);
    if (StringUtils.contains(attribute, "ui-chkbox"))
    {
      SelectBooleanCheckbox checkbox = primeUi.selectBooleanCheckbox(By.id("card:editConfigurationForm:editConfigurationValue"));
      webAssertThat(() -> assertThat(checkbox.isChecked()).isEqualTo(Boolean.valueOf(value)));
    }
    else if (StringUtils.contains(attribute, "ui-inputnumber"))
    {
      webAssertThat(() -> assertThat(driver.findElementById("card:editConfigurationForm:editConfigurationValue_input").getAttribute("value")).isEqualTo(value));
    }
    else if (StringUtils.contains(attribute, "ui-selectonemenu"))
    {
      SelectOneMenu menu = primeUi.selectOne(By.id("card:editConfigurationForm:editConfigurationValue"));
      webAssertThat(() -> assertThat(menu.getSelectedItem()).isEqualTo(value));
    }
    else
    {
      webAssertThat(() -> assertThat(driver.findElementById("card:editConfigurationForm:editConfigurationValue").getAttribute("value")).isEqualTo(value));
    }
  }
  
  private String getConfigActivityMenuForKey(FirefoxDriver driver, String key)
  {
    return getConfigIdForKey(driver, key) + ":activityMenu";
  }
  
  private String getConfigResetBtnForKey(FirefoxDriver driver, String key)
  {
    return getConfigIdForKey(driver, key) + ":resetConfigBtn";
  }
  
  private String getConfigTaskBtnForKey(FirefoxDriver driver, String key)
  {
    return getConfigIdForKey(driver, key) + ":tasksButton";
  }
  
  private String getConfigEditBtnForKey(FirefoxDriver driver, String key)
  {
    return getConfigIdForKey(driver, key) + ":editConfigBtn";
  }
  
  private String getConfigIdForKey(FirefoxDriver driver, String key)
  {
    String eleNumber = driver.findElementByXPath("//*[@class='config-name'][text()='" + key + "']/../..").getAttribute("data-ri");
    return "card:form:advancedConfigTable:" + eleNumber;
  }
  
  private void toAdvancedConfig(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toAdvancedConfig(driver);
    saveScreenshot(driver);
  }
}
