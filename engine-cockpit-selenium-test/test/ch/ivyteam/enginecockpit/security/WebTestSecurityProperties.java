package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.ApplicationTab;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestSecurityProperties extends WebTestBase
{
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
    assertTableHasDirectoryProperty(driver, "Adresse", "Baarerstrasse 12");
  }
  
  private void editProperty(FirefoxDriver driver, String key, String value)
  {
    driver.findElementById(getEditButtonId(driver, key)).click();
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

  private String getEditButtonId(FirefoxDriver driver, String key)
  {
    return "propertiesForm:propertiesTable:" + getTableNumberFor(driver, key) + ":editPropertyBtn";
  }
  
  private void deleteProperty(FirefoxDriver driver, String key)
  {
    driver.findElementById(getDeleteButtonId(driver, key)).click();
    saveScreenshot(driver, "delete_property");
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertiesMessage_container").getText())
            .contains("Successfully removed"));
    webAssertThat(() -> assertThat(driver.findElementsByXPath("//td[@class='property-name']")).hasSize(0));
  }
  
  private String getDeleteButtonId(FirefoxDriver driver, String key)
  {
    return "propertiesForm:propertiesTable:" + getTableNumberFor(driver, key) + ":deletePropertyBtn";
  }

  private String getTableNumberFor(FirefoxDriver driver, String key)
  {
    return driver.findElementByXPath("//td[@class='property-name'][text()='" + key + "']/..").getAttribute("data-ri");
  }

  private void addProperty(FirefoxDriver driver, String key, String value)
  {
    webAssertThat(() -> assertThat(driver.findElementsByXPath("//td[@class='property-name']")).hasSize(0));
    driver.findElementById("propertiesForm:propertyNameInput").sendKeys(key);
    driver.findElementById("propertiesForm:propertyValueInput").sendKeys(value);
    driver.findElementById("propertiesForm:saveProperty").click();
    
    webAssertThat(() -> assertThat(driver.findElementsByXPath("//td[@class='property-name']")).hasSize(1));
    assertTableHasKeyValue(driver, key, value);
    webAssertThat(() -> assertThat(driver.findElementById("propertiesForm:propertiesMessage_container").getText())
            .contains("Successfully"));
    saveScreenshot(driver, "save_property");
  }

  private void assertTableHasDirectoryProperty(FirefoxDriver driver, String key, String value)
  {
    assertTableHasKeyValue(driver, key, value);
    webAssertThat(() -> assertThat(driver.findElementById(getEditButtonId(driver, key)).isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById(getDeleteButtonId(driver, key)).isDisplayed()).isTrue());
  }
  
  private void assertTableHasKeyValue(FirefoxDriver driver, String key, String value)
  {
    webAssertThat(() -> assertThat(driver.findElementByXPath("//td[@class='property-name'][text()='" + key + "']")
            .getText()).contains(key));
    webAssertThat(() -> assertThat(driver.findElementByXPath("//td[@class='property-name'][text()='" + key + "']/../td[2]")
            .getText()).contains(value));
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
