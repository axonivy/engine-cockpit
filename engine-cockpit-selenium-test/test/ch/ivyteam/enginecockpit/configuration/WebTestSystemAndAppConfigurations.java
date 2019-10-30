package ch.ivyteam.enginecockpit.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

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
  void testSystemConfig()
  {
    toSystemConfig();
    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("System Config"));
  }
  
  @Test
  void testSearchConfig()
  {
    toSystemConfig();
    assertSearchConfigEntry();
  }

  @Test
  void testSearchConfig_app()
  {
    toApplicationDetail();
    assertSearchConfigEntry();
  }
  
  @Test
  void testHideDefaults()
  {
    toSystemConfig();
    assertDefaultToggle("Data.AppDirectory");
  }
  
  @Test
  void testHideDefaults_app()
  {
    toApplicationDetail();
    assertDefaultToggle("Data.FilesDirectory");
  }
  
  @Test
  void testShowConfigFile()
  {
    toSystemConfig();
    String key = "Connector.HTTP.AllowTrace";
    assertShowConfigFile(key);
  }
  
  @Test
  void testShowConfigFile_app()
  {
    toApplicationDetail();
    String key = "SecuritySystem";
    assertShowConfigFile(key);
  }

  @Test
  void testNewConfigInvalid()
  {
    toSystemConfig();
    driver.findElementById("newConfigBtn").click();
    assertNewConfigInvalid();
  }
  
  @Test
  void testNewConfigInvalid_app()
  {
    toApplicationDetail();
    driver.findElementById("configMoreForm:newConfigBtn").click();
    assertNewConfigInvalid();
  }
  
  @Test
  void testNewEditAndResetConfig()
  {
    String key = "testKey";
    String value = "testValue";
    toSystemConfig();
    driver.findElementById("newConfigBtn").click();
    assertNewConfig(key, value);
    assertEditConfig(key, value, "newValue");
    assertResetConfig(key);
  }
  
  @Test
  void testNewEditAndResetConfig_app()
  {
    String key = "testKey";
    String value = "testValue";
    toApplicationDetail();
    driver.findElementById("configMoreForm:newConfigBtn").click();
    assertNewConfig(key, value);
    assertEditConfig(key, value, "newValue");
    assertResetConfig(key);
  }
  
  @Test
  void testEditConfig_booleanFormat()
  {
    toSystemConfig();
    String config = "EMail.Server.SSL.UseKey";
    table.clickButtonForEntry(config, "editConfigBtn");
    saveScreenshot("boolean_input");
    assertThatConfigEditModalIsVisible(config, "false");
  }
  
  @Test
  void testEditConfig_numberFormat()
  {
    toSystemConfig();
    String config = "Elasticsearch.ExternalServer.BootTimeout";
    table.clickButtonForEntry(config, "editConfigBtn");
    saveScreenshot("number_input");
    assertThatConfigEditModalIsVisible(config, "60");
  }
  
  @Test
  void testEditConfig_daytimeFormat()
  {
    toSystemConfig();
    String config = "EMail.DailyTaskSummary.TriggerTime";
    table.clickButtonForEntry(config, "editConfigBtn");
    saveScreenshot("daytime_input");
    assertThatConfigEditModalIsVisible(config, "00:00");
  }
  
  @Test
  void testEditConfig_enumerationFormat()
  {
    toSystemConfig();
    String config = "SystemTask.Failure.Behaviour";
    table.clickButtonForEntry(config, "editConfigBtn");
    saveScreenshot("enum_input");
    assertThatConfigEditModalIsVisible(config, "FAIL_TASK_DO_RETRY");
  }
  
  @Test
  void testEmailUrlFilter()
  {
    String filter = "EMail";
    login();
    saveScreenshot("dashboard");
    driver.findElementById("mailConfigForm:configureEmailBtn").click();
    assertUrlFiltering(filter);
  }
  
  @Test
  void testSystemDbUrlFilter()
  {
    String filter = "SystemDb";
    login();
    saveScreenshot("dashboard");
    driver.findElementById("configureSystemDbBtn").click();
    assertUrlFiltering(filter);
  }
  
  private void assertUrlFiltering(String filter)
  {
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("systemconfig.xhtml?filter=" + filter));
    table = new Table(driver, TABLE_ID);
    table.getSearchFilter();
    webAssertThat(() -> assertThat(table.getSearchFilter()).isEqualTo(filter));
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).allMatch(e -> e.startsWith(filter)));
  }
  
  private void assertSearchConfigEntry()
  {
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).isNotEmpty());
    String search = table.getFirstColumnEntries().get(0);
    table.search(search);
    saveScreenshot("search_config");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(1).containsOnly(search));
  }

  private void assertDefaultToggle(String config)
  {
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).contains(config));
    toggleDefaultValues();
    saveScreenshot("hide");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).doesNotContain(config));
    toggleDefaultValues();
    saveScreenshot("show");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).contains(config));
  }
  
  private void toggleDefaultValues()
  {
    driver.findElementById("configMoreForm:configMoreButton").click();
    webAssertThat(() -> assertThat(driver.findElementById("configMoreForm:configMoreMenu").isDisplayed()).isTrue());
    driver.findElementById("configMoreForm:showDefaultsBtn").click();
  }
  
  private void assertShowConfigFile(String key)
  {
    table.buttonForEntryDisabled(key, "tasksButton");
    webAssertThat(() -> assertThat(table.buttonForEntryDisabled(key, "tasksButton")).isFalse());
    table.clickButtonForEntry(key, "tasksButton");
    webAssertThat(() -> assertThat(table.buttonMenuForEntryVisible(key, "activityMenu")).isTrue());
    table.clickButtonForEntry(key, "showFileBtn");
    webAssertThat(() -> assertThat(driver.findElementById("config:showConfigurationFileModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByClassName("code-block").getText()).contains(key.split("\\.")));
  }

  private void assertNewConfigInvalid()
  {
    saveScreenshot("new_config_model");
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationForm:newConfigurationKey").getAttribute("value")).isBlank());
    driver.findElementById("config:newConfigurationForm:saveNewConfiguration").click();
    saveScreenshot("invalid_new_config");
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationForm:newConfigurationKeyMessage").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationForm:newConfigurationKeyMessage").getText()).isEqualTo("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationForm:newConfigurationValueMessage").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationForm:newConfigurationValueMessage").getText()).isEqualTo("Value is required"));
  }

  private void assertResetConfig(String key)
  {
    table.clickButtonForEntry(key, "tasksButton");
    webAssertThat(() -> assertThat(table.buttonMenuForEntryVisible(key, "activityMenu")).isTrue());
    table.clickButtonForEntry(key, "resetConfigBtn");
    saveScreenshot("reset_config");
    webAssertThat(() -> assertThat(driver.findElementById("config:resetConfigConfirmDialog").isDisplayed()).isTrue());
    
    driver.findElementById("config:resetConfigConfirmForm:resetConfigConfirmYesBtn").click();
    saveScreenshot("reset_config_yes");
    webAssertThat(() -> assertThat(driver.findElementById("config:form:msgs_container").getText()).contains(key, "reset"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).doesNotContain(key));
  }

  private void assertNewConfig(String key, String value)
  {
    saveScreenshot("new_config_model");
    webAssertThat(() -> assertThat(driver.findElementById("config:newConfigurationModal").isDisplayed()).isTrue());
    
    driver.findElementById("config:newConfigurationForm:newConfigurationKey").sendKeys(key);
    driver.findElementById("config:newConfigurationForm:newConfigurationValue").sendKeys(value);
    driver.findElementById("config:newConfigurationForm:saveNewConfiguration").click();
    saveScreenshot("save_new_config");
    webAssertThat(() -> assertThat(driver.findElementById("config:form:msgs_container").getText()).contains(key, "created"));
    webAssertThat(() -> assertThat(table.getValueForEntry(key, 2)).isEqualTo(value));
  }
  
  private void assertEditConfig(String key, String value, String newValue)
  {
    table.clickButtonForEntry(key, "editConfigBtn");
    saveScreenshot("edit_config");
    assertThatConfigEditModalIsVisible(key, value);
    
    driver.findElementById("config:editConfigurationForm:editConfigurationValue").clear();
    driver.findElementById("config:editConfigurationForm:editConfigurationValue").sendKeys(newValue);
    driver.findElementById("config:editConfigurationForm:saveEditConfiguration").click();
    saveScreenshot("save_edit_config");
    webAssertThat(() -> assertThat(driver.findElementById("config:form:msgs_container").getText()).contains(key, "changed"));
    webAssertThat(() -> assertThat(table.getValueForEntry(key, 2)).isEqualTo(newValue));
  }
  
  private void assertThatConfigEditModalIsVisible(String key, String value)
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
  
  private void toSystemConfig()
  {
    login();
    Navigation.toSystemConfig(driver);
    saveScreenshot();
    table = new Table(driver, TABLE_ID);
  }
  
  private void toApplicationDetail()
  {
    login();
    Navigation.toApplicationDetail(driver, "test-ad");
    scrollYToElement(By.id("configMoreForm:configMoreButton"));
    saveScreenshot("app_detail");
    table = new Table(driver, TABLE_ID);
  }
}
