package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.ApplicationTab;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestSecurityProperties extends WebTestBase
{
  private static final By TABLE_ID = By.id("propertiesForm:propertiesTable");
  
  @Test
  public void testUserPropertyInvalid(FirefoxDriver driver)
  {
    toUserDetail(driver, "foo");
    openAddPropertyModal(driver);
    saveInvalidAddProperty(driver);
  }
  
  @Test
  public void testRolePropertyInvalid(FirefoxDriver driver)
  {
    toRoleDetail(driver, "boss");
    openAddPropertyModal(driver);
    saveInvalidAddProperty(driver);
  }
  
  @Test
  public void testUserPropertyAddEditDelete(FirefoxDriver driver)
  {
    String key = "test";
    toUserDetail(driver, "foo");
    openAddPropertyModal(driver);
    addProperty(driver, key, "testValue");
    editProperty(driver, key, "edit");
    deleteProperty(driver, key);
  }
  
  @Test
  public void testRolePropertyAddEditDelete(FirefoxDriver driver)
  {
    String key = "test";
    toRoleDetail(driver, "boss");
    openAddPropertyModal(driver);
    addProperty(driver, key, "testValue");
    editProperty(driver, key, "edit");
    deleteProperty(driver, key);
  }
  
  @Test
  public void testUserDirectoryProperties(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toUsers(driver);
    saveScreenshot(driver, "users");
    ApplicationTab.switchToApplication(driver, "test-ad");
    saveScreenshot(driver, "test-ad");
    String syncBtnId = "form:card:apps:applicationTabView:" + ApplicationTab.getSelectedApplicationIndex(driver) + ":panelSyncBtn";
    driver.findElementById(syncBtnId).click();
    saveScreenshot(driver, "sync");
    webAssertThat(() -> assertThat(driver.findElementByXPath("//button[@id='" + syncBtnId + "']/span[1]")
            .getAttribute("class")).doesNotContain("fa-spin"));
    saveScreenshot(driver, "sync_finish");
    Navigation.toUserDetail(driver, "user1");
    saveScreenshot(driver, "user-detail");
    assertTableHasDirectoryProperty(driver, "Address", "Baarerstrasse 12");
  }
  
  private void editProperty(FirefoxDriver driver, String key, String value)
  {
    new Table(driver, TABLE_ID).clickButtonForEntry(key, "editPropertyBtn");
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertyModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertyName").getText()).isEqualTo(key));
    saveScreenshot(driver, "edit_property");
    
    driver.findElementById("propertiesForm:propertyValueInput").clear();
    driver.findElementById("propertiesForm:propertyValueInput").sendKeys(value);
    driver.findElementById("propertiesForm:saveProperty").click();
    assertTableHasKeyValue(driver, key, value);
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertiesMessage_container").getText())
            .contains("Successfully"));
    saveScreenshot(driver, "save_property");
  }

  private void deleteProperty(FirefoxDriver driver, String key)
  {
    Table table = new Table(driver, TABLE_ID);
    table.clickButtonForEntry(key, "deletePropertyBtn");
    saveScreenshot(driver, "delete_property");
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertiesMessage_container").getText())
            .contains("Successfully removed"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(0));
  }
  

  private void addProperty(FirefoxDriver driver, String key, String value)
  {
    Table table = new Table(driver, TABLE_ID);
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(0));
    driver.findElementById("propertiesForm:propertyNameInput").sendKeys(key);
    driver.findElementById("propertiesForm:propertyValueInput").sendKeys(value);
    driver.findElementById("propertiesForm:saveProperty").click();
    
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(1));
    assertTableHasKeyValue(driver, key, value);
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertiesMessage_container").getText())
            .contains("Successfully"));
    saveScreenshot(driver, "save_property");
  }

  private void assertTableHasDirectoryProperty(FirefoxDriver driver, String key, String value)
  {
    assertTableHasKeyValue(driver, key, value);
    Table table = new Table(driver, TABLE_ID);
    webAssertThat(() -> assertThat(table.buttonForEntryDisabled(key, "editPropertyBtn")).isTrue());
    webAssertThat(() -> assertThat(table.buttonForEntryDisabled(key, "deletePropertyBtn")).isTrue());
  }
  
  private void assertTableHasKeyValue(FirefoxDriver driver, String key, String value)
  {
    Table table = new Table(driver, TABLE_ID);
    webAssertThat(() -> assertThat(table.getValueForEntry(key, 2)).isEqualTo(value));
  }

  private void saveInvalidAddProperty(FirefoxDriver driver)
  {
    driver.findElementById("propertiesForm:saveProperty").click();
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertyNameMessage").getText()).contains("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertyValueMessage").getText()).contains("Value is required"));
  }
  
  private void openAddPropertyModal(FirefoxDriver driver)
  {
    driver.findElementById("propertiesForm:newPropertyBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertyModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertyNameInput").getAttribute("value")).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertyNameMessage").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertyValueInput").getAttribute("value")).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertyValueMessage").getText()).isEmpty());
    saveScreenshot(driver, "add_prop");
  }
  
  private void toUserDetail(FirefoxDriver driver, String userName)
  {
    login(driver);
    Navigation.toUserDetail(driver, userName);
    saveScreenshot(driver, "userdetail");
  }
  
  private void toRoleDetail(FirefoxDriver driver, String roleName)
  {
    login(driver);
    Navigation.toRoleDetail(driver, roleName);
    saveScreenshot(driver, "userdetail");
  }
}
