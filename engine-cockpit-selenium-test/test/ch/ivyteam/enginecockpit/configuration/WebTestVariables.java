package ch.ivyteam.enginecockpit.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestVariables extends WebTestBase
{
  
  @Test
  void testNewEditVariable()
  {
    toVariables();
    driver.findElementById("form:card:apps:applicationTabView:0:newGlobalVarBtn").click();

    saveScreenshot("new_globalVar_modal");
    
    String name = "aName";
    String value = "aValue";
    
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarName").getAttribute("value")).isBlank());
    driver.findElementById("newGlobalVarForm:newGlobalVarName").sendKeys(name);
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarValue").getAttribute("value")).isBlank());
    driver.findElementById("newGlobalVarForm:newGlobalVarValue").sendKeys(value);
    driver.findElementById("newGlobalVarForm:saveNewGlobalVar").click();
    
    webAssertThat(() -> assertThat(driver.findElementById("form:card:apps:applicationTabView:0:globalVarTable__data")
            .getText()).contains(name, value));
    saveScreenshot("new_globalVar");
    
    driver.findElementById("form:card:apps:applicationTabView:0:globalVarTable_:0:editGlobalVarBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("editGlobalVarModal").isDisplayed()).isTrue());
    
    webAssertThat(() -> assertThat(driver.findElementById("editGlobalVarForm:editGlobalVarName").getText()).isEqualTo(name));
    webAssertThat(() -> assertThat(driver.findElementById("editGlobalVarForm:editGlobalVarValue").getAttribute("value")).isEqualTo(value));
    
    String newValue = "aNewValue";

    driver.findElementById("editGlobalVarForm:editGlobalVarValue").clear();
    driver.findElementById("editGlobalVarForm:editGlobalVarValue").sendKeys(newValue);
    driver.findElementById("editGlobalVarForm:saveGlobalVarConfiguration").click();
    saveScreenshot("edit_globalVar_modal");
    
    webAssertThat(() -> assertThat(driver.findElementById("form:card:apps:applicationTabView:0:globalVarTable__data")
            .getText()).contains(name, newValue));
  }
  
  @Test
  void testNewValidation()
  {
    toVariables();
    driver.findElementById("form:card:apps:applicationTabView:0:newGlobalVarBtn").click();

    saveScreenshot("new_globalVar_modal");
    
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarName").getAttribute("value")).isBlank());
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarValue").getAttribute("value")).isBlank());
    driver.findElementById("newGlobalVarForm:saveNewGlobalVar").click();

    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarNameMessage").getText()).isEqualTo("Name is required"));
    driver.findElementById("newGlobalVarForm:cancelNewGlobalVar").click();
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarModal").isDisplayed()).isFalse());
  }
  
  private void toVariables()
  {
    login();
    Navigation.toVariables(driver);
    saveScreenshot("variables");
  }

}
