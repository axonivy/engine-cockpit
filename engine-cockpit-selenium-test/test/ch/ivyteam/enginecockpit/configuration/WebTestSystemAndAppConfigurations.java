package ch.ivyteam.enginecockpit.configuration;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

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
    $("h1").shouldHave(text("System Config"));
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
    $("#newConfigBtn").click();
    assertNewConfigInvalid();
  }
  
  @Test
  void testNewConfigInvalid_app()
  {
    toApplicationDetail();
    $("#configMoreForm\\:newConfigBtn").click();
    assertNewConfigInvalid();
  }
  
  @Test
  void testNewEditAndResetConfig()
  {
    String key = "testKey";
    String value = "testValue";
    toSystemConfig();
    $("#newConfigBtn").click();
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
    $("#configMoreForm\\:newConfigBtn").click();
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
    assertThatConfigEditModalIsVisible(config, "false");
  }
  
  @Test
  void testEditConfig_numberFormat()
  {
    toSystemConfig();
    String config = "Elasticsearch.ExternalServer.BootTimeout";
    table.clickButtonForEntry(config, "editConfigBtn");
    assertThatConfigEditModalIsVisible(config, "60");
  }
  
  @Test
  void testEditConfig_daytimeFormat()
  {
    toSystemConfig();
    String config = "EMail.DailyTaskSummary.TriggerTime";
    table.clickButtonForEntry(config, "editConfigBtn");
    assertThatConfigEditModalIsVisible(config, "00:00");
  }
  
  @Test
  void testEditConfig_enumerationFormat()
  {
    toSystemConfig();
    String config = "SystemTask.Failure.Behaviour";
    table.clickButtonForEntry(config, "editConfigBtn");
    assertThatConfigEditModalIsVisible(config, "FAIL_TASK_DO_RETRY");
  }
  
  @Test
  void testEmailUrlFilter()
  {
    String filter = "EMail";
    login();
    $("#mailConfigForm\\:configureEmailBtn").click();
    assertUrlFiltering(filter);
  }
  
  @Test
  void testSystemDbConfigUrl()
  {
    login();
    $("#configureSystemDbBtn").click();
    assertCurrentUrlEndsWith("systemdb.xhtml");
  }
  
  private void assertUrlFiltering(String filter)
  {
    assertCurrentUrlEndsWith("systemconfig.xhtml?filter=" + filter);
    table = new Table(TABLE_ID);
    table.getSearchFilter();
    assertThat(table.getSearchFilter()).isEqualTo(filter);
    table.firstColumnShouldBe(size(9));
  }
  
  private void assertSearchConfigEntry()
  {
    table.firstColumnShouldBe(sizeGreaterThan(0));
    String search = table.getFirstColumnEntries().get(0);
    table.search(search);
    table.firstColumnShouldBe(size(1));
    table.firstColumnShouldBe(exactTexts(search));
  }

  private void assertDefaultToggle(String config)
  {
    assertThat(table.getFirstColumnEntries()).contains(config);
    toggleDefaultValues();
    assertThat(table.getFirstColumnEntries()).doesNotContain(config);
    toggleDefaultValues();
    assertThat(table.getFirstColumnEntries()).contains(config);
  }
  
  private void toggleDefaultValues()
  {
    $("#configMoreForm\\:configMoreButton").click();
    $("#configMoreForm\\:configMoreMenu").shouldBe(visible);
    $("#configMoreForm\\:showDefaultsBtn").click();
  }
  
  private void assertShowConfigFile(String key)
  {
    table.clickButtonForEntry(key, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(key, "activityMenu");
    table.clickButtonForEntry(key, "showFileBtn");
    $("#config\\:showConfigurationFileModal").shouldBe(visible);
    $(".code-block").shouldHave(text(key));
  }

  private void assertNewConfigInvalid()
  {
    $("#config\\:newConfigurationModal").shouldBe(visible);
    $("#config\\:newConfigurationForm\\:newConfigurationKey").shouldBe(exactValue(""));
    $("#config\\:newConfigurationForm\\:saveNewConfiguration").click();
    $("#config\\:newConfigurationForm\\:newConfigurationKeyMessage").shouldBe(visible, exactText("Value is required"));
    $("#config\\:newConfigurationForm\\:newConfigurationValueMessage").shouldBe(visible, exactText("Value is required"));
  }

  private void assertResetConfig(String key)
  {
    table.clickButtonForEntry(key, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(key, "activityMenu");
    table.clickButtonForEntry(key, "resetConfigBtn");
    $("#config\\:resetConfigConfirmDialog").shouldBe(visible);
    
    $("#config\\:resetConfigConfirmForm\\:resetConfigConfirmYesBtn").click();
    $("#config\\:form\\:msgs_container").shouldHave(text(key), text("reset"));
    assertThat(table.getFirstColumnEntries()).doesNotContain(key);
  }

  private void assertNewConfig(String key, String value)
  {
    $("#config\\:newConfigurationModal").shouldBe(visible);
    
    $("#config\\:newConfigurationForm\\:newConfigurationKey").sendKeys(key);
    $("#config\\:newConfigurationForm\\:newConfigurationValue").sendKeys(value);
    $("#config\\:newConfigurationForm\\:saveNewConfiguration").click();
    $("#config\\:form\\:msgs_container").shouldHave(text(key), text("created"));
    assertThat(table.getValueForEntry(key, 2)).isEqualTo(value);
  }
  
  private void assertEditConfig(String key, String value, String newValue)
  {
    table.clickButtonForEntry(key, "editConfigBtn");
    assertThatConfigEditModalIsVisible(key, value);
    
    $("#config\\:editConfigurationForm\\:editConfigurationValue").clear();
    $("#config\\:editConfigurationForm\\:editConfigurationValue").sendKeys(newValue);
    $("#config\\:editConfigurationForm\\:saveEditConfiguration").click();
    $("#config\\:form\\:msgs_container").shouldHave(text(key), text("changed"));
    assertThat(table.getValueForEntry(key, 2)).isEqualTo(newValue);
  }
  
  private void assertThatConfigEditModalIsVisible(String key, String value)
  {
    $("#config\\:editConfigurationModal").shouldBe(visible);
    $("#config\\:editConfigurationForm\\:editConfigurationKey").shouldBe(exactText(key));
    String classAttr = $("#config\\:editConfigurationForm\\:editConfigurationValue").getAttribute("class");
    if (StringUtils.contains(classAttr, "ui-chkbox"))
    {
      SelectBooleanCheckbox checkbox = primeUi.selectBooleanCheckbox(By.id("config:editConfigurationForm:editConfigurationValue"));
      assertThat(checkbox.isChecked()).isEqualTo(Boolean.valueOf(value));
    }
    else if (StringUtils.contains(classAttr, "ui-inputnumber"))
    {
      $("#config\\:editConfigurationForm\\:editConfigurationValue_input").shouldBe(exactValue(value));
    }
    else if (StringUtils.contains(classAttr, "ui-selectonemenu"))
    {
      SelectOneMenu menu = primeUi.selectOne(By.id("config:editConfigurationForm:editConfigurationValue"));
      assertThat(menu.getSelectedItem()).isEqualTo(value);
    }
    else
    {
      $("#config\\:editConfigurationForm\\:editConfigurationValue").shouldBe(exactValue(value));
    }
  }
  
  private void toSystemConfig()
  {
    login();
    Navigation.toSystemConfig();
    table = new Table(TABLE_ID);
  }
  
  private void toApplicationDetail()
  {
    login();
    Navigation.toApplicationDetail("test-ad");
    scrollToElement(By.id("configMoreForm:configMoreButton"));
    table = new Table(TABLE_ID);
  }
}
