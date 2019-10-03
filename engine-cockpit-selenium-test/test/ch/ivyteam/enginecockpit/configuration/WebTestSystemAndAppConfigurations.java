package ch.ivyteam.enginecockpit.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectOneMenu;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestSystemAndAppConfigurations extends WebTestBase
{
  private static final By TABLE_ID = By.id("config:form:configTable");
  private Table table;

  @Test
  void testSystemConfig(FirefoxDriver driver)
  {
    toSystemConfig(driver);
    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("System Config"));
  }
  
  @Test
  void testSearchConfig(FirefoxDriver driver)
  {
    toSystemConfig(driver);
    assertSearchConfigEntry(driver);
  }

  @Test
  void testSearchConfig_app(FirefoxDriver driver)
  {
    toApplicationDetail(driver);
    assertSearchConfigEntry(driver);
  }
  
  @Test
  void testHideDefaults(FirefoxDriver driver)
  {
    toSystemConfig(driver);
    assertDefaultToggle(driver, "Data.AppDirectory");
  }
  
  @Test
  void testHideDefaults_app(FirefoxDriver driver)
  {
    toApplicationDetail(driver);
    assertDefaultToggle(driver, "Data.FilesDirectory");
  }
  
  @Test
  void testShowConfigFile(FirefoxDriver driver)
  {
    toSystemConfig(driver);
    String key = "Connector.HTTP.AllowTrace";
    assertShowConfigFile(driver, key);
  }
  
  @Test
  void testShowConfigFile_app(FirefoxDriver driver)
  {
    toApplicationDetail(driver);
    String key = "SecuritySystem";
    assertShowConfigFile(driver, key);
  }

  @Test
  void testNewConfigInvalid(FirefoxDriver driver)
  {
    toSystemConfig(driver);
    driver.findElementById("newConfigBtn").click();
    assertNewConfigInvalid(driver);
  }
  
  @Test
  void testNewConfigInvalid_app(FirefoxDriver driver)
  {
    toApplicationDetail(driver);
    driver.findElementById("configMoreForm:newConfigBtn").click();
    assertNewConfigInvalid(driver);
  }
  
  @Test
  void testNewEditAndResetConfig(FirefoxDriver driver)
  {
    String key = "testKey";
    String value = "testValue";
    toSystemConfig(driver);
    driver.findElementById("newConfigBtn").click();
    assertNewConfig(driver, key, value);
    assertEditConfig(driver, key, value, "newValue");
    assertResetConfig(driver, key);
  }
  
  @Test
  void testNewEditAndResetConfig_app(FirefoxDriver driver)
  {
    String key = "testKey";
    String value = "testValue";
    toApplicationDetail(driver);
    driver.findElementById("configMoreForm:newConfigBtn").click();
    assertNewConfig(driver, key, value);
    assertEditConfig(driver, key, value, "newValue");
    assertResetConfig(driver, key);
  }
  
  @Test
  void testEditConfig_booleanFormat(FirefoxDriver driver)
  {
    toSystemConfig(driver);
    String config = "EMail.Server.SSL.UseKey";
    table.clickButtonForEntry(config, "editConfigBtn");
    saveScreenshot(driver, "boolean_input");
    assertThatConfigEditModalIsVisible(driver, config, "false");
  }
  
  @Test
  void testEditConfig_numberFormat(FirefoxDriver driver)
  {
    toSystemConfig(driver);
    String config = "Elasticsearch.ExternalServer.BootTimeout";
    table.clickButtonForEntry(config, "editConfigBtn");
    saveScreenshot(driver, "number_input");
    assertThatConfigEditModalIsVisible(driver, config, "60");
  }
  
  @Test
  void testEditConfig_daytimeFormat(FirefoxDriver driver)
  {
    toSystemConfig(driver);
    String config = "EMail.DailyTaskSummary.TriggerTime";
    table.clickButtonForEntry(config, "editConfigBtn");
    saveScreenshot(driver, "daytime_input");
    assertThatConfigEditModalIsVisible(driver, config, "00:00");
  }
  
  @Test
  void testEditConfig_enumerationFormat(FirefoxDriver driver)
  {
    toSystemConfig(driver);
    String config = "SystemTask.Failure.Behaviour";
    table.clickButtonForEntry(config, "editConfigBtn");
    saveScreenshot(driver, "enum_input");
    assertThatConfigEditModalIsVisible(driver, config, "FAIL_TASK_DO_RETRY");
  }
  
  @Test
  void testEmailUrlFilter(FirefoxDriver driver)
  {
    String filter = "EMail";
    login(driver);
    saveScreenshot(driver, "dashboard");
    driver.findElementById("mailConfigForm:configureEmailBtn").click();
    assertUrlFiltering(driver, filter);
  }
  
  @Test
  void testSystemDbUrlFilter(FirefoxDriver driver)
  {
    String filter = "SystemDb";
    login(driver);
    saveScreenshot(driver, "dashboard");
    driver.findElementById("configureSystemDbBtn").click();
    assertUrlFiltering(driver, filter);
  }
  
  private void assertUrlFiltering(FirefoxDriver driver, String filter)
  {
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("systemconfig.xhtml?filter=" + filter));
    table = new Table(driver, TABLE_ID);
    table.getSearchFilter();
    webAssertThat(() -> assertThat(table.getSearchFilter()).isEqualTo(filter));
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).allMatch(e -> e.startsWith(filter)));
  }
  
  private void assertSearchConfigEntry(FirefoxDriver driver)
  {
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).isNotEmpty());
    String search = table.getFirstColumnEntries().get(0);
    table.search(search);
    saveScreenshot(driver, "search_config");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(1).containsOnly(search));
  }

  private void assertDefaultToggle(FirefoxDriver driver, String config)
  {
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).contains(config));
    toggleDefaultValues(driver);
    saveScreenshot(driver, "hide");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).doesNotContain(config));
    toggleDefaultValues(driver);
    saveScreenshot(driver, "show");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).contains(config));
  }
  
  private void toggleDefaultValues(FirefoxDriver driver)
  {
    driver.findElementById("configMoreForm:configMoreButton").click();
    webAssertThat(() -> assertThat(driver.findElementById("configMoreForm:configMoreMenu").isDisplayed()).isTrue());
    driver.findElementById("configMoreForm:showDefaultsBtn").click();
  }
  
  private void assertShowConfigFile(FirefoxDriver driver, String key)
  {
    table.buttonForEntryDisabled(key, "tasksButton");
    webAssertThat(() -> assertThat(table.buttonForEntryDisabled(key, "tasksButton")).isFalse());
    table.clickButtonForEntry(key, "tasksButton");
    webAssertThat(() -> assertThat(table.buttonMenuForEntryVisible(key, "activityMenu")).isTrue());
    table.clickButtonForEntry(key, "showFileBtn");
    webAssertThat(() -> assertThat(driver.findElementById("config:showConfigurationFileModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByClassName("code-block").getText()).contains(key.split("\\.")));
  }

  private void assertNewConfigInvalid(FirefoxDriver driver)
  {
    saveScreenshot(driver, "new_config_model");
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationForm:newConfigurationKey").getAttribute("value")).isBlank());
    driver.findElementById("config:newConfigurationForm:saveNewConfiguration").click();
    saveScreenshot(driver, "invalid_new_config");
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationForm:newConfigurationKeyMessage").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationForm:newConfigurationKeyMessage").getText()).isEqualTo("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationForm:newConfigurationValueMessage").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationForm:newConfigurationValueMessage").getText()).isEqualTo("Value is required"));
  }

  private void assertResetConfig(FirefoxDriver driver, String key)
  {
    table.clickButtonForEntry(key, "tasksButton");
    webAssertThat(() -> assertThat(table.buttonMenuForEntryVisible(key, "activityMenu")).isTrue());
    table.clickButtonForEntry(key, "resetConfigBtn");
    saveScreenshot(driver, "reset_config");
    webAssertThat(() -> assertThat(driver.findElementById("config:resetConfigConfirmDialog").isDisplayed()).isTrue());
    
    driver.findElementById("config:resetConfigConfirmForm:resetConfigConfirmYesBtn").click();
    saveScreenshot(driver, "reset_config_yes");
    webAssertThat(() -> assertThat(driver.findElementById("config:form:msgs_container").getText()).contains(key, "reset"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).doesNotContain(key));
  }

  private void assertNewConfig(FirefoxDriver driver, String key, String value)
  {
    saveScreenshot(driver, "new_config_model");
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationModal").isDisplayed()).isTrue());
    
    driver.findElementById("config:newConfigurationForm:newConfigurationKey").sendKeys(key);
    driver.findElementById("config:newConfigurationForm:newConfigurationValue").sendKeys(value);
    driver.findElementById("config:newConfigurationForm:saveNewConfiguration").click();
    saveScreenshot(driver, "save_new_config");
    webAssertThat(() -> assertThat(driver.findElementById("config:form:msgs_container").getText()).contains(key, "created"));
    webAssertThat(() -> assertThat(table.getValueForEntry(key, 2)).isEqualTo(value));
  }
  
  private void assertEditConfig(FirefoxDriver driver, String key, String value, String newValue)
  {
    table.clickButtonForEntry(key, "editConfigBtn");
    saveScreenshot(driver, "edit_config");
    assertThatConfigEditModalIsVisible(driver, key, value);
    
    driver.findElementById("config:editConfigurationForm:editConfigurationValue").clear();
    driver.findElementById("config:editConfigurationForm:editConfigurationValue").sendKeys(newValue);
    driver.findElementById("config:editConfigurationForm:saveEditConfiguration").click();
    saveScreenshot(driver, "save_edit_config");
    webAssertThat(() -> assertThat(driver.findElementById("config:form:msgs_container").getText()).contains(key, "changed"));
    webAssertThat(() -> assertThat(table.getValueForEntry(key, 2)).isEqualTo(newValue));
  }
  
  private void assertThatConfigEditModalIsVisible(FirefoxDriver driver, String key, String value)
  {
    webAssertThat(() -> assertThat(driver.findElementById("config:editConfigurationModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("config:editConfigurationForm:editConfigurationKey").getText()).isEqualTo(key));
    String classAttr = driver.findElementById("config:editConfigurationForm:editConfigurationValue").getAttribute("class");
    PrimeUi primeUi = new PrimeUi(driver);
    if (StringUtils.contains(classAttr, "ui-chkbox"))
    {
      SelectBooleanCheckbox checkbox = primeUi.selectBooleanCheckbox(By.id("config:editConfigurationForm:editConfigurationValue"));
      webAssertThat(() -> assertThat(checkbox.isChecked()).isEqualTo(Boolean.valueOf(value)));
    }
    else if (StringUtils.contains(classAttr, "ui-inputnumber"))
    {
      webAssertThat(() -> assertThat(driver.findElementById("config:editConfigurationForm:editConfigurationValue_input").getAttribute("value")).isEqualTo(value));
    }
    else if (StringUtils.contains(classAttr, "ui-selectonemenu"))
    {
      SelectOneMenu menu = primeUi.selectOne(By.id("config:editConfigurationForm:editConfigurationValue"));
      webAssertThat(() -> assertThat(menu.getSelectedItem()).isEqualTo(value));
    }
    else
    {
      webAssertThat(() -> assertThat(driver.findElementById("config:editConfigurationForm:editConfigurationValue").getAttribute("value")).isEqualTo(value));
    }
  }
  
  private void toSystemConfig(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toSystemConfig(driver);
    saveScreenshot(driver);
    table = new Table(driver, TABLE_ID);
  }
  
  private void toApplicationDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toApplicationDetail(driver, "test-ad");
    scrollYToBottom(driver);
    saveScreenshot(driver, "app_detail");
    table = new Table(driver, TABLE_ID);
  }
}
