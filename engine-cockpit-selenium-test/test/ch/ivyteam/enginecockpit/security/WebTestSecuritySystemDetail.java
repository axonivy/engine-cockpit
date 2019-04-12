package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestSecuritySystemDetail extends WebTestBase
{
  @Test
  void testSecuritySystemDetail(FirefoxDriver driver)
  {
    toSecurityDetail(driver);
    List<WebElement> infoPanels = driver.findElementsByClassName("ui-panel");
    assertThat(infoPanels).hasSize(4);
  }

  @Test
  void testConnectionInfos(FirefoxDriver driver)
  {
    toSecurityDetail(driver);
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:provider").getText())
            .isEqualTo("Microsoft Active Directory"));
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:url").getAttribute("value"))
            .isEqualTo("ldap://zugtstdirads"));
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:userName").getAttribute("value"))
            .isEqualTo("admin@zugtstdomain.wan"));
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:password").getAttribute("value"))
            .isEqualTo("nimda"));
    
    driver.findElementById("securitySystemConfigForm:url").clear();
    driver.findElementById("securitySystemConfigForm:url").sendKeys("test");
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:url").getAttribute("value"))
            .isEqualTo("test"));
    saveScreenshot(driver, "change_url");
    driver.findElementById("securitySystemConfigForm:saveSecuritySystemConfigBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:securitySystemConfigSaveSuccess_container").isDisplayed())
            .isTrue());
    saveScreenshot(driver, "save");
    driver.navigate().refresh();
    
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:url").getAttribute("value"))
            .isEqualTo("test"));
    saveScreenshot(driver, "refresh");
    driver.findElementById("securitySystemConfigForm:url").clear();
    driver.findElementById("securitySystemConfigForm:url").sendKeys("ldap://zugtstdirads");
    driver.findElementById("securitySystemConfigForm:saveSecuritySystemConfigBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:securitySystemConfigSaveSuccess_container").isDisplayed())
            .isTrue());
  }
  
  @Test
  void testInvalidAndValidSyncTimes(FirefoxDriver driver)
  {
    toSecurityDetail(driver);
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:syncTime").getAttribute("value"))
            .isEqualTo(""));
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:syncTime").getAttribute("placeholder"))
            .isEqualTo("00:00"));
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:syncTimeMessage").isDisplayed())
            .isFalse());
    
    saveInvalidSyncTimeAndAssert(driver, "32:23");
    saveInvalidSyncTimeAndAssert(driver, "12:95");
    
    driver.findElementById("securitySystemConfigForm:syncTime").clear();
    driver.findElementById("securitySystemConfigForm:syncTime").sendKeys("16:47");
    driver.findElementById("securitySystemConfigForm:saveSecuritySystemConfigBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("securitySystemConfigForm:syncTimeMessage").isDisplayed())
            .isFalse());
    webAssertThat(() -> assertThat(driver.findElementById("securitySystemConfigForm:securitySystemConfigSaveSuccess_container").isDisplayed())
            .isTrue());
    saveScreenshot(driver, "valid");
    
    driver.findElementById("securitySystemConfigForm:syncTime").clear();
    driver.findElementById("securitySystemConfigForm:saveSecuritySystemConfigBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemConfigForm:securitySystemConfigSaveSuccess_container").isDisplayed())
            .isTrue());
  }

  private void saveInvalidSyncTimeAndAssert(FirefoxDriver driver, String time)
  {
    driver.findElementById("securitySystemConfigForm:syncTime").clear();
    driver.findElementById("securitySystemConfigForm:syncTime").sendKeys(time);
    driver.findElementById("securitySystemConfigForm:saveSecuritySystemConfigBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("securitySystemConfigForm:syncTimeMessage").isDisplayed())
            .isTrue());
    saveScreenshot(driver, "invalid");
  }
  
  @Test
  void testLdapInfos(FirefoxDriver driver)
  {
    toSecurityDetail(driver);
    PrimeUi primeUi = new PrimeUi(driver);
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:ldapName").getAttribute("value"))
            .isEqualTo(""));
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:ldapFullName").getAttribute("value"))
            .isEqualTo(""));
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:ldapEmail").getAttribute("value"))
            .isEqualTo(""));
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:ldapLanguage").getAttribute("value"))
            .isEqualTo(""));
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:ldapUserMemberOfAttribute").getAttribute("value"))
            .isEqualTo(""));
    await().untilAsserted(() -> assertThat(primeUi.selectBooleanCheckbox(By.id("securityLdapForm:ldapUseUserMemberOfForUserRoleMembership")).isChecked())
            .isTrue());
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:ldapUserGroupMemberOfAttribute").getAttribute("value"))
            .isEqualTo(""));
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:ldapUserGroupMembersAttribute").getAttribute("value"))
            .isEqualTo(""));
    
    driver.findElementById("securityLdapForm:ldapName").sendKeys("test");
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:ldapName").getAttribute("value"))
            .isEqualTo("test"));
    saveScreenshot(driver, "change_name");
    driver.findElementById("securityLdapForm:saveSecurtiySystemLdapBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:securitySystemLdapSaveSuccess_container").isDisplayed())
            .isTrue());
    saveScreenshot(driver, "save");
    driver.navigate().refresh();
    
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:ldapName").getAttribute("value"))
            .isEqualTo("test"));
    saveScreenshot(driver, "refresh");
    driver.findElementById("securityLdapForm:ldapName").clear();
    driver.findElementById("securityLdapForm:saveSecurtiySystemLdapBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapForm:securitySystemLdapSaveSuccess_container").isDisplayed())
            .isTrue());
  }
  
  @Test
  void testBinding(FirefoxDriver driver)
  {
    toSecurityDetail(driver);
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemBindingForm:defaultContext").getAttribute("value"))
            .isEqualTo("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemBindingForm:importUsersOfGroup").getAttribute("value"))
            .isEqualTo(""));
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemBindingForm:userFilter").getAttribute("value"))
            .isEqualTo(""));
    
    driver.findElementById("securitySystemBindingForm:importUsersOfGroup").sendKeys("test");
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemBindingForm:importUsersOfGroup").getAttribute("value"))
            .isEqualTo("test"));
    saveScreenshot(driver, "change");
    driver.findElementById("securitySystemBindingForm:saveSecuritySystemBindingBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemBindingForm:securitySystemBindingSaveSuccess_container").isDisplayed())
            .isTrue());
    saveScreenshot(driver, "save");
    driver.navigate().refresh();
    
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemBindingForm:importUsersOfGroup").getAttribute("value"))
            .isEqualTo("test"));
    saveScreenshot(driver, "refresh");
    driver.findElementById("securitySystemBindingForm:importUsersOfGroup").clear();
    driver.findElementById("securitySystemBindingForm:saveSecuritySystemBindingBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securitySystemBindingForm:securitySystemBindingSaveSuccess_container").isDisplayed())
            .isTrue());
  }
  
  @Test
  void testLdapAttributesNewInvalid(FirefoxDriver driver)
  {
    toSecurityDetail(driver);
    driver.findElementById("securityLdapAttributesForm:newLdapAttributeBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:newLdapAttributeModal").isDisplayed())
            .isTrue());
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:newAttributeNameMessage").getText())
            .isBlank());
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:newAttributeMessage").getText())
            .isBlank());
    saveScreenshot(driver, "modal");
    
    driver.findElementById("securityLdapAttributesForm:saveNewLdapAttribute").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:newAttributeNameMessage").getText())
            .contains("Value is required"));
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:newAttributeMessage").getText())
            .contains("Value is required"));
    saveScreenshot(driver, "invalid");
  }
  
  @Test
  void testLdapAttributes(FirefoxDriver driver)
  {
    toSecurityDetail(driver);
    WebElement table = driver.findElementById("securityLdapAttributesForm:ldapPropertiesTable");
    List<WebElement> attrs = table.findElements(new By.ByClassName("ldap-attribute"));
    assertThat(attrs).isEmpty();
    
    driver.findElementById("securityLdapAttributesForm:newLdapAttributeBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:newLdapAttributeModal").isDisplayed())
            .isTrue());
    driver.findElementById("securityLdapAttributesForm:newAttributeNameInput").sendKeys("test");
    driver.findElementById("securityLdapAttributesForm:newAttributeInput").sendKeys("value");
    saveScreenshot(driver, "new_attr");
    driver.findElementById("securityLdapAttributesForm:saveNewLdapAttribute").click();
    saveScreenshot(driver, "save");
    
    await().untilAsserted(() -> assertThat(driver.findElementsByClassName("ldap-attribute")).isNotEmpty());
    await().untilAsserted(() -> assertThat(driver.findElementByClassName("ldap-attribute").getText())
            .isEqualTo("test"));
    await().untilAsserted(() -> assertThat(driver.findElementByClassName("ldap-value").getText())
            .isEqualTo("value"));
    driver.findElementById("securityLdapAttributesForm:ldapPropertiesTable:0:deleteLdapAttributeBtn").click();
    saveScreenshot(driver, "delete");
    await().untilAsserted(() -> assertThat(driver.findElementsByClassName("ldap-attribute")).isEmpty());
  }
  
  private void toSecurityDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toSecuritySystemDetail(driver, "test-ad");
    saveScreenshot(driver);
  }
  
}
