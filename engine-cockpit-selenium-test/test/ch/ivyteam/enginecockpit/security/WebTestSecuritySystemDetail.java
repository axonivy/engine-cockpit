package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Table;

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
  void testDirNotDeletableIfUsedByApp(FirefoxDriver driver)
  {
    toSecurityDetail(driver);
    Throwable result = null;
    try
    {
      driver.findElementById("securitySystemConfigForm:deleteSecuritySystem");
    }
    catch (Exception e)
    {
      result = e;
    }
    assertThat(result).isInstanceOf(NoSuchElementException.class);
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
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:ldapAttributeModal").isDisplayed())
            .isTrue());
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:attributeNameMessage").getText())
            .isBlank());
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:attributeMessage").getText())
            .isBlank());
    saveScreenshot(driver, "modal");
    
    driver.findElementById("securityLdapAttributesForm:saveLdapAttribute").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:attributeNameMessage").getText())
            .contains("Value is required"));
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:attributeMessage").getText())
            .contains("Value is required"));
    saveScreenshot(driver, "invalid");
  }
  
  @Test
  void testLdapAttributes(FirefoxDriver driver)
  {
    toSecurityDetail(driver);
    Table table = new Table(driver, By.id("securityLdapAttributesForm:ldapPropertiesTable"));
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(2));
    
    driver.findElementById("securityLdapAttributesForm:newLdapAttributeBtn").click();
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:ldapAttributeModal").isDisplayed())
            .isTrue());
    driver.findElementById("securityLdapAttributesForm:attributeNameInput").sendKeys("test");
    driver.findElementById("securityLdapAttributesForm:attributeInput").sendKeys("value");
    saveScreenshot(driver, "new_attr");
    driver.findElementById("securityLdapAttributesForm:saveLdapAttribute").click();
    saveScreenshot(driver, "save");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(3).contains("test"));
    webAssertThat(() -> assertThat(table.getValueForEntry("test", 2)).isEqualTo("value"));
    
    table.clickButtonForEntry("test", "editPropertyBtn");
    await().untilAsserted(() -> assertThat(driver.findElementById("securityLdapAttributesForm:ldapAttributeModal").isDisplayed())
            .isTrue());
    driver.findElementById("securityLdapAttributesForm:attributeInput").clear();
    driver.findElementById("securityLdapAttributesForm:attributeInput").sendKeys("newValue");
    driver.findElementById("securityLdapAttributesForm:saveLdapAttribute").click();
    saveScreenshot(driver, "edit");
    webAssertThat(() -> assertThat(table.getValueForEntry("test", 2)).isEqualTo("newValue"));
    
    table.clickButtonForEntry("test", "deleteLdapAttributeBtn");
    saveScreenshot(driver, "delete");
    webAssertThat(() -> assertThat(table.getFirstColumnEntries()).hasSize(2).doesNotContain("test"));
  }
  
  private void toSecurityDetail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toSecuritySystemDetail(driver, "test-ad");
    saveScreenshot(driver);
  }
  
}
