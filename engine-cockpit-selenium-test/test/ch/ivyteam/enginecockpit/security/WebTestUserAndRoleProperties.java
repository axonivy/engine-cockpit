package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.ApplicationTab;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestUserAndRoleProperties extends WebTestBase
{
  private static final By TABLE_ID = By.id("propertiesForm:propertiesTable");
  
  @Test
  public void testUserPropertyInvalid()
  {
    toUserDetail("foo");
    openAddPropertyModal();
    saveInvalidAddProperty();
  }
  
  @Test
  public void testRolePropertyInvalid()
  {
    toRoleDetail("boss");
    openAddPropertyModal();
    saveInvalidAddProperty();
  }
  
  @Test
  public void testUserPropertyAddEditDelete()
  {
    String key = "test";
    toUserDetail("foo");
    openAddPropertyModal();
    addProperty(key, "testValue");
    editProperty(key, "edit");
    deleteProperty(key);
  }
  
  @Test
  public void testRolePropertyAddEditDelete()
  {
    String key = "test";
    toRoleDetail("boss");
    openAddPropertyModal();
    addProperty(key, "testValue");
    editProperty(key, "edit");
    deleteProperty(key);
  }
  
  @Test
  public void testUserDirectoryProperties()
  {
    login();
    Navigation.toUsers(driver);
    saveScreenshot("users");
    ApplicationTab.switchToApplication(driver, "test-ad");
    saveScreenshot("test-ad");
    String syncBtnId = "form:card:apps:applicationTabView:" + ApplicationTab.getSelectedApplicationIndex(driver) + ":panelSyncBtn";
    driver.findElementById(syncBtnId).click();
    saveScreenshot("sync");
    webAssertThat(() -> assertThat(driver.findElementByXPath("//button[@id='" + syncBtnId + "']/span[1]")
            .getAttribute("class")).doesNotContain("fa-spin"));
    saveScreenshot("sync_finish");
    Navigation.toUserDetail(driver, "user1");
    saveScreenshot("user-detail");
    assertTableHasDirectoryProperty("Address", "Baarerstrasse 12");
  }
  
  private void editProperty(String key, String value)
  {
    new Table(driver, TABLE_ID).clickButtonForEntry(key, "editPropertyBtn");
    webAssertThat(() -> assertThat(driver.findElementById("propertyModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("propertyModalForm:propertyName").getText()).isEqualTo(key));
    saveScreenshot("edit_property");
    
    driver.findElementById("propertyModalForm:propertyValueInput").clear();
    driver.findElementById("propertyModalForm:propertyValueInput").sendKeys(value);
    driver.findElementById("propertyModalForm:saveProperty").click();
    assertTableHasKeyValue(key, value);
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertiesMessage_container").getText())
            .contains("Successfully"));
    saveScreenshot("save_property");
  }

  private void deleteProperty(String key)
  {
    Table table = new Table(driver, TABLE_ID);
    table.clickButtonForEntry(key, "deletePropertyBtn");
    saveScreenshot("delete_property");
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertiesMessage_container").getText())
            .contains("Successfully removed"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(0));
  }
  

  private void addProperty(String key, String value)
  {
    Table table = new Table(driver, TABLE_ID);
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(0));
    driver.findElementById("propertyModalForm:propertyNameInput").sendKeys(key);
    driver.findElementById("propertyModalForm:propertyValueInput").sendKeys(value);
    driver.findElementById("propertyModalForm:saveProperty").click();
    
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(1));
    assertTableHasKeyValue(key, value);
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertiesMessage_container").getText())
            .contains("Successfully"));
    saveScreenshot("save_property");
  }

  private void assertTableHasDirectoryProperty(String key, String value)
  {
    assertTableHasKeyValue(key, value);
    Table table = new Table(driver, TABLE_ID);
    webAssertThat(() -> assertThat(table.buttonForEntryDisabled(key, "editPropertyBtn")).isTrue());
    webAssertThat(() -> assertThat(table.buttonForEntryDisabled(key, "deletePropertyBtn")).isTrue());
  }
  
  private void assertTableHasKeyValue(String key, String value)
  {
    Table table = new Table(driver, TABLE_ID);
    webAssertThat(() -> assertThat(table.getValueForEntry(key, 2)).isEqualTo(value));
  }

  private void saveInvalidAddProperty()
  {
    driver.findElementById("propertyModalForm:saveProperty").click();
    webAssertThat(() -> assertThat(driver.findElementById("propertyModalForm:propertyNameMessage").getText()).contains("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("propertyModalForm:propertyValueMessage").getText()).contains("Value is required"));
  }
  
  private void openAddPropertyModal()
  {
    driver.findElementById("propertiesForm:newPropertyBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("propertyModal").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("propertyModalForm:propertyNameInput").getAttribute("value")).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("propertyModalForm:propertyNameMessage").getText()).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("propertyModalForm:propertyValueInput").getAttribute("value")).isEmpty());
    webAssertThat(() -> assertThat(driver.findElementById("propertyModalForm:propertyValueMessage").getText()).isEmpty());
    saveScreenshot("add_prop");
  }
  
  private void toUserDetail(String userName)
  {
    login();
    Navigation.toUserDetail(driver, userName);
    saveScreenshot("userdetail");
  }
  
  private void toRoleDetail(String roleName)
  {
    login();
    Navigation.toRoleDetail(driver, roleName);
    saveScreenshot("userdetail");
  }
}
