package ch.ivyteam.enginecockpit.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestSecuritySystem extends WebTestBase
{
  @Test
  void testSecuritySystem()
  {
    toSecuritySystem();
    webAssertThat(() -> assertThat(driver.findElementByTagName("h1").getText()).contains("Security Systems"));
    webAssertThat(() -> assertThat(driver.findElementsByXPath("//tbody/tr")).isNotEmpty());
  }
  
  @Test
  void testAddNewSecuritySystemInvalid()
  {
    toSecuritySystem();
    driver.findElementById("card:form:createSecuritySystemBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("card:newSecuritySystemModal").isDisplayed()).isTrue());
    driver.findElementById("card:newSecuritySystemForm:saveNewSecuritySystem").click();
    saveScreenshot("invalid");
    webAssertThat(() -> assertThat(driver.findElementById("card:newSecuritySystemForm:newSecuritySystemNameMessage").getText())
            .contains("Value is required"));
  }
  
  @Test
  void testAddAndDeleteSecuritySystem()
  {
    toSecuritySystem();
    driver.findElementById("card:form:createSecuritySystemBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("card:newSecuritySystemModal").isDisplayed()).isTrue());
    driver.findElementById("card:newSecuritySystemForm:newSecuritySystemNameInput").sendKeys("NewFromTest");
    driver.findElementById("card:newSecuritySystemForm:saveNewSecuritySystem").click();
    saveScreenshot("new_sec_system");
    webAssertThat(() -> assertThat(driver.findElementById("card:newSecuritySystemModal").isDisplayed()).isFalse());
    webAssertThat(() -> assertThat(driver.findElementsByXPath("//tbody//*[@class='security-name'][text()='NewFromTest']")).hasSize(1));
  
    Navigation.toSecuritySystemDetail(driver, "NewFromTest");
    saveScreenshot("new_sec_detail");
    webAssertThat(() -> assertThat(driver.findElementById("securitySystemConfigForm:deleteSecuritySystem").isDisplayed()).isTrue());
    
    driver.findElementById("securitySystemConfigForm:deleteSecuritySystem").click();
    saveScreenshot("delete_system_model");
    webAssertThat(() -> assertThat(driver.findElementById("securitySystemConfigForm:deleteSecuritySystemConfirmDialog").isDisplayed()).isTrue());
    
    driver.findElementById("securitySystemConfigForm:deleteSecuritySystemConfirmYesBtn").click();
    webAssertThat(() -> assertThat(driver.getCurrentUrl()).endsWith("securitysystem.xhtml"));
    saveScreenshot("system_deleted");
    webAssertThat(() -> assertThat(driver.findElements(By.xpath("//span[@class='security-name']"))
            .stream().map(e -> e.getText()).collect(Collectors.toList())).doesNotContain("NewFromTest"));
  }
  
  private void toSecuritySystem()
  {
    login();
    Navigation.toSecuritySystem(driver);
    saveScreenshot();
  }
}
