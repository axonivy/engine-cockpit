package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;

import ch.ivyteam.enginecockpit.util.EnvironmentSwitch;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestVariables
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toVariables();
    Tab.switchToDefault();
    EnvironmentSwitch.switchToEnv("Default");
  }
  
  @Test
  void testInitialVariables()
  {
    var table = variableTable();
    table.firstColumnShouldBe(textsInAnyOrder("boolean", "daytime", "enum", "globVar", "number", "password", "PORTAL_DASHBOARD", "variable"));
    assertVariable("globVar", "data", true);
    assertVariable("variable", "hello", true);
    EnvironmentSwitch.switchToEnv("test");
    table.firstColumnShouldBe(textsInAnyOrder("boolean", "daytime", "enum", "globVar", "number", "password", "PORTAL_DASHBOARD", "variable"));
    assertVariable("globVar", "test data", true);
    assertVariable("variable", "hello from env", true);
  }
  
  @Test
  void editVariable()
  {
    String variable = "variable";
    EnvironmentSwitch.switchToEnv("test");
    assertVariable(variable, "hello from env", true);
    editVariable(variable, "env override");
    assertVariable(variable, "env override", false);
    
    EnvironmentSwitch.switchToEnv("Default");
    assertVariable(variable, "hello", true);
    editVariable(variable, "");
    assertVariable(variable, "", false);
    
    EnvironmentSwitch.switchToEnv("test");
    assertVariable(variable, "env override", false);
    resetVariable(variable);
    assertVariable(variable, "hello from env", true);

    EnvironmentSwitch.switchToEnv("Default");
    assertVariable(variable, "", false);
    resetVariable(variable);
    assertVariable(variable, "hello", true);
  }

  @Test
  void testNewEditResetVariable()
  {
    var table = variableTable();
    var entryCount = table.getFirstColumnEntries().size();
    $(By.id(activeTabPanel() + "newVariableBtn")).click();

    String name = "aName";
    String value = "aValue";
    createNewVariable(name, value);
    
    table.firstColumnShouldBe(size(entryCount + 1));
    assertVariable(name, value, false);
    
    String newValue = "aNewValue";
    editVariable(name, newValue, value);
    
    assertVariable(name, newValue, false);
    resetVariable(name);
    
    table.firstColumnShouldBe(size(entryCount));
  }

  @Test
  void testEditConfig_booleanFormat()
  {
    String config = "boolean";
    variableTable().clickButtonForEntry(config, "editConfigBtn");
    assertThatConfigEditModalIsVisible(config, "true", "test boolean");
  }

  @Test
  void testEditConfig_daytimeFormat()
  {
    String config = "daytime";
    variableTable().clickButtonForEntry(config, "editConfigBtn");
    assertThatConfigEditModalIsVisible(config, "10:30", "test daytime");
  }
  
  @Test
  void testEditConfig_enumerationFormat()
  {
    String config = "enum";
    variableTable().clickButtonForEntry(config, "editConfigBtn");
    assertThatConfigEditModalIsVisible(config, "hi", "test enum");
  }
  
  @Test
  void testEditConfig_numberFormat()
  {
    String config = "number";
    variableTable().clickButtonForEntry(config, "editConfigBtn");
    assertThatConfigEditModalIsVisible(config, "123", "test number");
  }
  
  @Test
  void testEditConfig_passwordFormat()
  {
    String config = "password";
    assertVariable(config, "******", true);
    variableTable().clickButtonForEntry(config, "editConfigBtn");
    assertThatConfigEditModalIsVisible(config, "", "test password");
  }
  
  @Test
  void testEditConfig_fileFormat()
  {
    String config = "PORTAL_DASHBOARD";
    variableTable().clickButtonForEntry(config, "editConfigBtn");
    assertThatConfigEditModalIsVisible(config, "{  \"name\": \"this is a json file\"}", "Default Dashboard");
  }
  
  @Test
  void testEditConfig_stringFormat()
  {
    String config = "variable";
    variableTable().clickButtonForEntry(config, "editConfigBtn");
    assertThatConfigEditModalIsVisible(config, "hello", "test variable");
  }
  
  @Test
  void testNewValidation()
  {
    $(By.id(activeTabPanel() + "newVariableBtn")).click();

    $(By.id(activeTabPanel() + "config:newConfigurationModal")).shouldBe(visible);
    $(By.id(activeTabPanel() + "config:newConfigurationForm:newConfigurationKey")).shouldBe(exactValue(""));
    $(By.id(activeTabPanel() + "config:newConfigurationForm:newConfigurationValue")).shouldBe(exactValue(""));
    $(By.id(activeTabPanel() + "config:newConfigurationForm:saveNewConfiguration")).click();

    $(By.id(activeTabPanel() + "config:newConfigurationForm:newConfigurationKeyMessage")).shouldBe(exactText("Value is required"));
    $(By.id(activeTabPanel() + "config:newConfigurationForm:newConfigurationValueMessage")).shouldBe(visible, exactText("Value is required"));

    $(By.id(activeTabPanel() + "config:newConfigurationForm:cancelNewConfiguration")).click();
    $(By.id(activeTabPanel() + "config:newConfigurationModal")).shouldNotBe(visible);
  }

  private void createNewVariable(String name, String value)
  {
    $(By.id(activeTabPanel() + "config:newConfigurationModal")).shouldBe(visible);
    $(By.id(activeTabPanel() + "config:newConfigurationForm:newConfigurationKey")).shouldBe(exactValue("")).sendKeys(name);
    $(By.id(activeTabPanel() + "config:newConfigurationForm:newConfigurationValue")).shouldBe(exactValue("")).sendKeys(value);
    $(By.id(activeTabPanel() + "config:newConfigurationForm:saveNewConfiguration")).click();
    $("#msgs_container").shouldHave(text(name), text("saved"));
  }
  
  private void resetVariable(String name)
  {
    Table table = variableTable();
    table.clickButtonForEntry(name, "tasksButton");
    table.buttonMenuForEntryShouldBeVisible(name, "activityMenu");
    table.clickButtonForEntry(name, "resetConfigBtn");
    
    $(By.id(activeTabPanel() + "config:resetConfigConfirmForm:resetConfigConfirmYesBtn")).click();
    $("#msgs_container").shouldHave(text(name), text("reset"));
  }

  private void editVariable(String name, String value)
  {
    editVariable(name, value, null);
  }
  
  private void editVariable(String name, String value, String oldValue)
  {
    variableTable().clickButtonForEntry(name, "editConfigBtn");
    $(By.id(activeTabPanel() + "config:editConfigurationModal")).shouldBe(visible);
    assertEditVariableDialog(name, oldValue);
    $(By.id(activeTabPanel() + "config:editConfigurationForm:editConfigurationValue")).clear();
    $(By.id(activeTabPanel() + "config:editConfigurationForm:editConfigurationValue")).sendKeys(value);
    $(By.id(activeTabPanel() + "config:saveEditConfiguration")).click();
    $("#msgs_container").shouldHave(text(name), text("saved"));
  }

  
  private void assertEditVariableDialog(String name, String oldValue)
  {
    if (oldValue == null)
    {
      return;
    }
    $(By.id(activeTabPanel() + "config:editConfigurationForm:editConfigurationKey")).shouldBe(exactText(name));
    $(By.id(activeTabPanel() + "config:editConfigurationForm:editConfigurationValue")).shouldBe(exactValue(oldValue));
  }
  
  private void assertVariable(String name, String value, boolean isDefault)
  {
    var table = variableTable();
    table.valueForEntryShould(name, 2, exactText(value));
    if (isDefault)
    {
      table.row(name).shouldHave(cssClass("default-value"));
    }
    else
    {
      table.row(name).shouldNotHave(cssClass("default-value"));
    }
    table.buttonForEntryShouldBeDisabled(name, "tasksButton", isDefault);
  }
  
  private void assertThatConfigEditModalIsVisible(String config, String value, String desc)
  {
    WebTestConfiguration.assertThatConfigEditModalIsVisible(activeTabPanel() + "config", config, value, desc, value);
  }
  
  private Table variableTable()
  {
    return new Table(By.id(activeTabPanel() + "config:form:configTable"));
  }
  
  private String activeTabPanel()
  {
    return "apps:applicationTabView:" + Tab.getSelectedTabIndex() + ":";
  }
  
}
