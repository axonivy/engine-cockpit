package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestRoleDetail extends WebTestBase
{
  private static final String DETAIL_ROLE_NAME = "boss";
  
  @Test
  void testRoleDetailOpen(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    await().untilAsserted(() -> assertThat(driver.getCurrentUrl()).endsWith("roledetail.xhtml?roleName=" + DETAIL_ROLE_NAME));
    await().untilAsserted(() -> assertThat(driver.getTitle()).isEqualTo("Role Detail"));
  }
  
  @Test
  void testSaveRoleInformation(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toRoleDetail(driver, DETAIL_ROLE_NAME);
    clearRoleInfoInputs(driver);
    
    driver.findElementById("roleInformationForm:displayName").sendKeys("display");
    driver.findElementById("roleInformationForm:description").sendKeys("desc");
    driver.findElementById("roleInformationForm:externalSecurityName").sendKeys("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan");
    driver.findElementById("roleInformationForm:saveRoleInformation").click();
    saveScreenshot(driver, "save_user_changes");
    
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:informationSaveSuccess_container").isDisplayed()).isTrue());
    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:name").getAttribute("value")).isEqualTo(DETAIL_ROLE_NAME));
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:displayName").getAttribute("value")).isEqualTo("display"));
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:description").getAttribute("value")).isEqualTo("desc"));
    await().untilAsserted(() -> assertThat(driver.findElementById("roleInformationForm:externalSecurityName").getAttribute("value")).isEqualTo("OU=IvyTeam Test-OU,DC=zugtstdomain,DC=wan"));
  
    clearRoleInfoInputs(driver);
    driver.findElementById("roleInformationForm:saveRoleInformation").click();
  }
  
  private void clearRoleInfoInputs(FirefoxDriver driver)
  {
    driver.findElementById("roleInformationForm:displayName").clear();
    driver.findElementById("roleInformationForm:description").clear();
    driver.findElementById("roleInformationForm:externalSecurityName").clear();
  }
}
