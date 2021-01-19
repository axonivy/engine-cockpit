package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
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
    table.firstColumnShouldBe(exactTexts("globVar", "PORTAL_DASHBOARD", "variable"));
    assertEntry("globVar", "data", true);
    assertEntry("variable", "hello", true);
    EnvironmentSwitch.switchToEnv("test");
    table.firstColumnShouldBe(exactTexts("globVar", "PORTAL_DASHBOARD", "variable"));
    assertEntry("globVar", "test data", false);
    assertEntry("variable", "hello from env", true);
  }
  
  @Test
  void editVariable()
  {
    String variable = "variable";
    EnvironmentSwitch.switchToEnv("test");
    assertEntry(variable, "hello from env", true);
    editVariable(variable, "env override");
    assertEntry(variable, "env override", false);
    
    EnvironmentSwitch.switchToEnv("Default");
    assertEntry(variable, "hello", true);
    editVariable(variable, "override");
    assertEntry(variable, "override", false);
    
    EnvironmentSwitch.switchToEnv("test");
    assertEntry(variable, "env override", false);
    resetVariable(variable);
    assertEntry(variable, "override", false); //should be "default"

    EnvironmentSwitch.switchToEnv("Default");
    assertEntry(variable, "override", false);
    resetVariable(variable);
    assertEntry(variable, "hello", true);
  }

  @Test
  void testNewEditResetVariable()
  {
    var table = variableTable();
    var entryCount = table.getFirstColumnEntries().size();
    $(By.id(activeTabPanel() + "newGlobalVarBtn")).click();

    String name = "aName";
    String value = "aValue";
    
    $("#newGlobalVarModal").shouldBe(visible);
    $("#newGlobalVarForm\\:newGlobalVarName").shouldBe(exactValue("")).sendKeys(name);
    $("#newGlobalVarForm\\:newGlobalVarValue").shouldBe(exactValue("")).sendKeys(value);
    $("#newGlobalVarForm\\:saveNewGlobalVar").click();
    
    table.firstColumnShouldBe(size(entryCount + 1));
    assertEntry(name, value, false);
    
    String newValue = "aNewValue";
    editVariable(name, newValue, value);
    
    assertEntry(name, newValue, false);
    resetVariable(name);
    
    table.firstColumnShouldBe(size(entryCount));
  }
  
  @Test
  void testNewValidation()
  {
    $(By.id(activeTabPanel() + "newGlobalVarBtn")).click();

    $("#newGlobalVarModal").shouldBe(visible);
    $("#newGlobalVarForm\\:newGlobalVarName").shouldBe(exactValue(""));
    $("#newGlobalVarForm\\:newGlobalVarValue").shouldBe(exactValue(""));
    $("#newGlobalVarForm\\:saveNewGlobalVar").click();

    $("#newGlobalVarForm\\:newGlobalVarNameMessage").shouldBe(exactText("Name is required"));
    $("#newGlobalVarForm\\:cancelNewGlobalVar").click();
    $("#newGlobalVarModal").shouldNotBe(visible);
  }

  private void resetVariable(String name)
  {
    variableTable().clickButtonForEntry(name, "resetGlobalVar");
    $("#resetGlobalVarDialog").shouldBe(visible);
    $("#resetGlobalVarYesBtn").shouldBe(visible).click();
  }

  private void editVariable(String name, String value)
  {
    editVariable(name, value, null);
  }
  
  private void editVariable(String name, String value, String oldValue)
  {
    variableTable().clickButtonForEntry(name, "editGlobalVarBtn");
    $("#editGlobalVarModal").shouldBe(visible);
    assertEditVariableDialog(name, oldValue);
    $("#editGlobalVarForm\\:editGlobalVarValue").clear();
    $("#editGlobalVarForm\\:editGlobalVarValue").sendKeys(value);
    $("#editGlobalVarForm\\:saveGlobalVarConfiguration").click();
  }

  
  private void assertEditVariableDialog(String name, String oldValue)
  {
    if (oldValue == null)
    {
      return;
    }
    $("#editGlobalVarForm\\:editGlobalVarName").shouldBe(exactText(name));
    $("#editGlobalVarForm\\:editGlobalVarValue").shouldBe(exactValue(oldValue));
  }
  
  private void assertEntry(String name, String value, boolean isDefault)
  {
    var table = variableTable();
    table.valueForEntryShould(name, 4, exactText(value));
    if (isDefault)
    {
      table.row(name).shouldHave(cssClass("default-value"));
    }
    else
    {
      table.row(name).shouldNotHave(cssClass("default-value"));
    }
    table.buttonForEntryShouldBeDisabled(name, "resetGlobalVar", isDefault);
  }
  
  private Table variableTable()
  {
    return new Table(By.id(activeTabPanel() + "globalVarTable"));
  }
  
  private String activeTabPanel()
  {
    return "apps:applicationTabView:" + Tab.getSelectedTabIndex() + ":form:";
  }
  
}
