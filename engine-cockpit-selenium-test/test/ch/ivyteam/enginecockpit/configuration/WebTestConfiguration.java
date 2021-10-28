package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.assertCurrentUrlEndsWith;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectBooleanCheckbox;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneMenu;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestConfiguration
{
  private static final By TABLE_ID = By.id("config:form:configTable");
  private Table table;

  @BeforeEach
  void beforeEach()
  {
    login();
  }
  
  @Test
  void testEmailUrlFilter()
  {
    String filter = "EMail";
    $("#mailConfigForm\\:configureEmailBtn").shouldBe(visible).click();
    assertUrlFiltering(filter);
  }
  
  @Test
  void testSystemDbConfigUrl()
  {
    $("#configureSystemDbBtn").shouldBe(visible).click();
    assertCurrentUrlEndsWith("systemdb.xhtml");
  }
  
  @Nested
  class System
  {
    @BeforeEach
    void beforeEach()
    {
      Navigation.toSystemConfig();
      table = new Table(TABLE_ID);
    }
    
    @Test
    void testSystemConfig()
    {
      $("h1").shouldHave(text("System Config"));
    }
    
    @Test
    void testSearchConfig()
    {
      assertSearchConfigEntry();
    }
    
    @Test
    void testHideDefaults()
    {
      assertDefaultToggle("Data.AppDirectory");
    }
    
    @Test
    void testShowConfigFile()
    {
      String key = "Connector.HTTP.AllowTrace";
      assertShowConfigFile(key);
    }
    
    @Test
    void testNewConfigInvalid()
    {
      $("#newConfigBtn").click();
      assertNewConfigInvalid();
    }

    @Test
    void testNewEditAndResetConfig()
    {
      String key = "testKey";
      String value = "testValue";
      $("#newConfigBtn").click();
      assertNewConfig(key, value);
      assertEditConfig(key, value, "newValue");
      assertResetConfig(key);
    }
    
    @Test
    void testSeachAndUpdateConfig()
    {
      String config = "EMail.Server.EncryptionMethod";
      table.firstColumnShouldBe(sizeGreaterThan(0));
      table.row(config).shouldHave(cssClass("default-value"));
      Selenide.sleep(400);
      table.search("email");
      table.firstColumnShouldBe(size(9));

      table.clickButtonForEntry(config, "editConfigBtn");
      PrimeUi.selectOne(By.id("config:editConfigurationForm:editConfigurationValue")).selectItemByLabel("SSL");
      $("#config\\:editConfigurationForm\\:saveEditConfiguration").click();
      table.row(config).shouldNotHave(cssClass("default-value"));
      assertResetConfig(config);
    }
    
    @Test
    void testEditConfig_booleanFormat()
    {
      String config = "EMail.Server.SSL.UseKey";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "false");
    }
    
    @Test
    void testEditConfig_numberFormat()
    {
      String config = "Elasticsearch.ExternalServer.BootTimeout";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "60");
    }
    
    @Test
    void testEditConfig_daytimeFormat()
    {
      String config = "EMail.DailyTaskSummary.TriggerTime";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "00:00");
    }
    
    @Test
    void testEditConfig_enumerationFormat()
    {
      String config = "SystemTask.Failure.Behaviour";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "FAIL_TASK_DO_RETRY");
    }
  }
  
  @Nested
  class Application
  {
    @BeforeEach
    void beforeEach()
    {
      Navigation.toApplicationDetail("test-ad");
      $(By.id("configMoreForm:configMoreButton"))
              .scrollIntoView("{behavior: \"instant\", block: \"center\", inline: \"center\"}");
      table = new Table(TABLE_ID);
    }
    
    @Test
    void testSearchConfig()
    {
      assertSearchConfigEntry();
    }
    
    @Test
    void testHideDefaults()
    {
      assertDefaultToggle("Data.FilesDirectory");
    }
    
    @Test
    void testShowConfigFile()
    {
      String key = "SecuritySystem";
      assertShowConfigFile(key);
    }
    
    @Test
    void testNewConfigInvalid()
    {
      $("#configMoreForm\\:newConfigBtn").click();
      assertNewConfigInvalid();
    }

    @Test
    void testNewEditAndResetConfig()
    {
      String key = "testKey";
      String value = "testValue";
      $("#configMoreForm\\:newConfigBtn").click();
      assertNewConfig(key, value);
      assertEditConfig(key, value, "newValue");
      assertResetConfig(key);
    }

    @Test
    void overrideProject_pmvSelector() {
      String config = "OverrideProject";
      String value = "notMyLibrary";

      $("#configMoreForm\\:newConfigBtn").click();
      assertNewConfig(config, value);

      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, " ");
      $(By.id("config:editConfigurationForm:editConfigurationValue"))
              .shouldHave(cssClass("ui-selectonemenu"));
    }
  }
  
  @Nested
  class ApplicationPortal
  {
    @BeforeEach
    void beforeEach()
    {
      Navigation.toApplicationDetail("demo-portal");
      $(By.id("configMoreForm:configMoreButton"))
              .scrollIntoView("{behavior: \"instant\", block: \"center\", inline: \"center\"}");
      table = new Table(TABLE_ID);
    }
    
    @Test
    void testStandardProcess_defaultPages()
    {
      String config = "StandardProcess.DefaultPages";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, "ch.ivyteam.ivy.project.portal:portalTemplate");
      $(By.id("config:editConfigurationForm:editConfigurationValue")).shouldHave(cssClass("ui-selectonemenu"));
    }
    
    @Test
    void testStandardProcess_mailNotification()
    {
      String config = "StandardProcess.MailNotification";
      table.clickButtonForEntry(config, "editConfigBtn");
      assertThatConfigEditModalIsVisible(config, " ");
      $(By.id("config:editConfigurationForm:editConfigurationValue")).shouldHave(cssClass("ui-selectonemenu"));
    }
  }
  
  private void assertUrlFiltering(String filter)
  {
    assertCurrentUrlEndsWith("systemconfig.xhtml?filter=" + filter);
    table = new Table(TABLE_ID);
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
  }

  private void assertNewConfig(String key, String value)
  {
    $("#config\\:newConfigurationModal").shouldBe(visible);
    
    $("#config\\:newConfigurationForm\\:newConfigurationKey").sendKeys(key);
    $("#config\\:newConfigurationForm\\:newConfigurationValue").sendKeys(value);
    $("#config\\:newConfigurationForm\\:saveNewConfiguration").click();
    $("#config\\:form\\:msgs_container").shouldHave(text(key), text("created"));
    table.valueForEntryShould(key, 2, exactText(value));
  }
  
  private void assertEditConfig(String key, String value, String newValue)
  {
    table.clickButtonForEntry(key, "editConfigBtn");
    assertThatConfigEditModalIsVisible(key, value);
    
    $("#config\\:editConfigurationForm\\:editConfigurationValue").clear();
    $("#config\\:editConfigurationForm\\:editConfigurationValue").sendKeys(newValue);
    $("#config\\:editConfigurationForm\\:saveEditConfiguration").click();
    $("#config\\:form\\:msgs_container").shouldHave(text(key), text("changed"));
    table.valueForEntryShould(key, 2, exactText(newValue));
  }
  
  private void assertThatConfigEditModalIsVisible(String key, String value)
  {
    $("#config\\:editConfigurationModal").shouldBe(visible);
    $("#config\\:editConfigurationForm\\:editConfigurationKey").shouldBe(exactText(key));
    String classAttr = $("#config\\:editConfigurationForm\\:editConfigurationValue").getAttribute("class");
    if (StringUtils.contains(classAttr, "ui-chkbox"))
    {
      SelectBooleanCheckbox checkbox = PrimeUi.selectBooleanCheckbox(By.id("config:editConfigurationForm:editConfigurationValue"));
      assertThat(checkbox.isChecked()).isEqualTo(Boolean.valueOf(value));
    }
    else if (StringUtils.contains(classAttr, "ui-inputnumber"))
    {
      $("#config\\:editConfigurationForm\\:editConfigurationValue_input").shouldBe(exactValue(value));
    }
    else if (StringUtils.contains(classAttr, "ui-selectonemenu"))
    {
      SelectOneMenu menu = PrimeUi.selectOne(By.id("config:editConfigurationForm:editConfigurationValue"));
      assertThat(menu.getSelectedItem()).isEqualTo(value);
    }
    else
    {
      $("#config\\:editConfigurationForm\\:editConfigurationValue").shouldBe(exactValue(value));
    }
  }
  
}
