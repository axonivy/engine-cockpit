package ch.ivyteam.enginecockpit.configuration;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.ivy.supplements.IvySelenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvySelenide
public class WebTestVariables
{
  
  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toVariables();
    Tab.switchToTab("test");
  }
  
  @Test
  void testNewEditVariable()
  {
    $(activeTabPanel() + "newGlobalVarBtn").click();

    String name = "aName";
    String value = "aValue";
    
    $("#newGlobalVarModal").shouldBe(visible);
    $("#newGlobalVarForm\\:newGlobalVarName").shouldBe(exactValue("")).sendKeys(name);
    $("#newGlobalVarForm\\:newGlobalVarValue").shouldBe(exactValue("")).sendKeys(value);
    $("#newGlobalVarForm\\:saveNewGlobalVar").click();
    
    $(activeTabPanel() + "globalVarTable__data").shouldHave(text(name), text(value));
    
    $(activeTabPanel() + "globalVarTable_\\:0\\:editGlobalVarBtn").click();
    $("#editGlobalVarModal").shouldBe(visible);
    
    $("#editGlobalVarForm\\:editGlobalVarName").shouldBe(exactText(name));
    $("#editGlobalVarForm\\:editGlobalVarValue").shouldBe(exactValue(value));
    
    String newValue = "aNewValue";

    $("#editGlobalVarForm\\:editGlobalVarValue").clear();
    $("#editGlobalVarForm\\:editGlobalVarValue").sendKeys(newValue);
    $("#editGlobalVarForm\\:saveGlobalVarConfiguration").click();
    
    $(activeTabPanel() + "globalVarTable__data").shouldHave(text(name), text(newValue));
  }
  
  @Test
  void testNewValidation()
  {
    $(activeTabPanel() + "newGlobalVarBtn").click();

    $("#newGlobalVarModal").shouldBe(visible);
    $("#newGlobalVarForm\\:newGlobalVarName").shouldBe(exactValue(""));
    $("#newGlobalVarForm\\:newGlobalVarValue").shouldBe(exactValue(""));
    $("#newGlobalVarForm\\:saveNewGlobalVar").click();

    $("#newGlobalVarForm\\:newGlobalVarNameMessage").shouldBe(exactText("Name is required"));
    $("#newGlobalVarForm\\:cancelNewGlobalVar").click();
    $("#newGlobalVarModal").shouldNotBe(visible);
  }
  
  private String activeTabPanel()
  {
    return "#form\\:card\\:apps\\:applicationTabView\\:" + Tab.getSelectedTabIndex() + "\\:";
  }
  
}
