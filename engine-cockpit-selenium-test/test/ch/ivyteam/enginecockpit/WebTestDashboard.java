package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import ch.ivyteam.enginecockpit.util.Table;

public class WebTestDashboard extends WebTestBase
{
  @Test
  void testDashboardContent(FirefoxDriver driver)
  {
    toDashboard(driver);
    webAssertThat(() -> assertThat(driver.findElementsByClassName("overview-box-content")).hasSize(4));
    webAssertThat(() -> assertThat(driver.findElementsByClassName("ui-panel")).hasSize(5));
  }
  
  @Test
  void checkLicenceInfo(FirefoxDriver driver)
  {
    toDashboard(driver);
    driver.findElementById("tasksButtonLicenceDetail").click();
    saveScreenshot(driver, "licenceInfo");
    webAssertThat(() -> assertThat(driver.findElementById("licenceDetailDialog").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(new Table(driver, By.id("licenceInfoForm:licenceInfoTable")).getFirstColumnEntries())
            .isNotEmpty());
  }
  
  @Test
  void checkJavaInfo(FirefoxDriver driver)
  {
    toDashboard(driver);
    driver.findElementById("tasksButtonJavaDetail").click();
    saveScreenshot(driver, "javaInfo");
    webAssertThat(() -> assertThat(driver.findElementById("javaDetailDialog").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(new Table(driver, By.id("javaInfoForm:javaJVMInfoTable")).getFirstColumnEntries())
            .isNotEmpty());
    webAssertThat(() -> assertThat(new Table(driver, By.id("javaInfoForm:javaPropertiesInfoTable")).getFirstColumnEntries())
            .isNotEmpty());
  }
  
  @Test
  public void testSendTestMailInvalidInputs(FirefoxDriver driver)
  {
    toDashboardAndOpenSendMailModal(driver);
    
    driver.findElementById("sendTestMailForm:sendToInput").clear();
    driver.findElementById("sendTestMailForm:subjectInput").clear();
    driver.findElementById("sendTestMailForm:sendTestMailBtn").click();
    saveScreenshot(driver, "invalid_inputs");
    webAssertThat(() -> assertThat(driver.findElementById("sendTestMailForm:sendToInputMessage").getText()).isEqualTo("Value is required"));
    webAssertThat(() -> assertThat(driver.findElementById("sendTestMailForm:subjectInputMessage").getText()).isEqualTo("Value is required"));
  }
  
  @Test
  public void testSendTestMailError(FirefoxDriver driver)
  {
    toDashboardAndOpenSendMailModal(driver);
    
    driver.findElementById("sendTestMailForm:sendToInput").sendKeys("test@example.com");
    driver.findElementById("sendTestMailForm:sendTestMailBtn").click();
    saveScreenshot(driver, "error_send");
    webAssertThat(() -> assertThat(driver.findElementById("mailConfigForm:msgs_container").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("mailConfigForm:msgs_container").getText()).contains("Error while sending test mail"));
  }

  private void toDashboardAndOpenSendMailModal(FirefoxDriver driver)
  {
    toDashboard(driver);
    driver.findElementById("mailConfigForm:openTestMailBtn").click();
    webAssertThat(() -> assertThat(driver.findElementById("sendTestMailModal").isDisplayed()).isTrue());
    saveScreenshot(driver, "send_mail_modal");
  }
  
  private void toDashboard(FirefoxDriver driver)
  {
    login(driver);
    saveScreenshot(driver);
  }
}
