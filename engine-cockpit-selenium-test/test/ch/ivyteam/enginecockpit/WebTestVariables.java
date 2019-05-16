package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestVariables extends WebTestBase
{
  
  @Test
  void testNewEditDeleteVariable(FirefoxDriver driver)
  {
    toVariables(driver);
    driver.findElementById("form:card:apps:applicationTabView:0:newGlobalVarBtn").click();

    saveScreenshot(driver, "new_globalVar_modal");
    
    String name = "aName";
    String desc = "aDesc";
    String value = "aValue";
    
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarName").getAttribute("value")).isBlank());
    driver.findElementById("newGlobalVarForm:newGlobalVarName").sendKeys(name);
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarDesc").getAttribute("value")).isBlank());
    driver.findElementById("newGlobalVarForm:newGlobalVarDesc").sendKeys(desc);
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarValue").getAttribute("value")).isBlank());
    driver.findElementById("newGlobalVarForm:newGlobalVarValue").sendKeys(value);
    driver.findElementById("newGlobalVarForm:saveNewGlobalVar").click();
    
    webAssertThat(() -> assertThat(driver.findElementById("form:card:apps:applicationTabView:0:globalVarTable__data")
            .getText()).contains(name, desc, value));
    saveScreenshot(driver, "new_globalVar");
    
    driver.findElementById("form:card:apps:applicationTabView:0:globalVarTable_:0:editGlobalVarBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("editGlobalVarModal").isDisplayed()).isTrue());
    
    webAssertThat(() -> assertThat(driver.findElementById("editGlobalVarForm:editGlobalVarName").getText()).isEqualTo(name));
    webAssertThat(() -> assertThat(driver.findElementById("editGlobalVarForm:editGlobalVarDesc").getText()).isEqualTo(desc));
    webAssertThat(() -> assertThat(driver.findElementById("editGlobalVarForm:editGlobalVarValue").getAttribute("value")).isEqualTo(value));
    
    String newValue = "aNewValue";

    driver.findElementById("editGlobalVarForm:editGlobalVarValue").clear();
    driver.findElementById("editGlobalVarForm:editGlobalVarValue").sendKeys(newValue);
    driver.findElementById("editGlobalVarForm:saveGlobalVarConfiguration").click();
    saveScreenshot(driver, "edit_globalVar_modal");
    
    webAssertThat(() -> assertThat(driver.findElementById("form:card:apps:applicationTabView:0:globalVarTable__data")
            .getText()).contains(name, desc, newValue));
    
    driver.findElementById("form:card:apps:applicationTabView:0:globalVarTable_:0:deleteGlobalVar").click();
    driver.findElementById("form:card:apps:applicationTabView:0:globalVarTable_:0:deleteGlobalVarYesBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("form:card:apps:applicationTabView:0:globalVarTable__data")
            .getText()).contains("No records found."));
    saveScreenshot(driver, "delete_globalVar_modal");
  }
  
  @Test
  void testNewValidation(FirefoxDriver driver)
  {
    toVariables(driver);
    driver.findElementById("form:card:apps:applicationTabView:0:newGlobalVarBtn").click();

    saveScreenshot(driver, "new_globalVar_modal");
    
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarName").getAttribute("value")).isBlank());
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarDesc").getAttribute("value")).isBlank());
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarValue").getAttribute("value")).isBlank());
    driver.findElementById("newGlobalVarForm:saveNewGlobalVar").click();

    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarNameMessage").getText()).isEqualTo("Name is required"));
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarForm:newGlobalVarDescMessage").getText()).isEqualTo("Description is required"));
    
    driver.findElementById("newGlobalVarForm:cancelNewGlobalVar").click();
    webAssertThat(() -> assertThat(driver.findElementById("newGlobalVarModal").isDisplayed()).isFalse());
  }
  
  private void toVariables(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toVariables(driver);
    saveScreenshot(driver, "variables");
  }

}
