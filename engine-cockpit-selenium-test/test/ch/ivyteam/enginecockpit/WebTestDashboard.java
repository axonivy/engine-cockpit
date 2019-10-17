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
  void checkLicenceEvents(FirefoxDriver driver)
  {
    createLicenceEvents(driver);
    toDashboard(driver);
    webAssertThat(() -> assertThat(driver.findElementById("tasksButtonLicenceEvents").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementByXPath("//*[@class='topbar-notifications']/a/span[1]").getText()).isEqualTo("2"));
    
    driver.findElementById("tasksButtonLicenceEvents").click();
    saveScreenshot(driver, "licence_dialog");
    webAssertThat(() -> assertThat(driver.findElementById("licenceEventsDialog").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementsByXPath("//*[@id='licenceEventForm:licenceEventList']//li")).hasSize(2));
    driver.findElementById("licenceEventForm:licenceEventList:0:confirmEventBtn").click();
    webAssertThat(() -> assertThat(driver.findElementsByXPath("//*[@id='licenceEventForm:licenceEventList']//li")).hasSize(1));
    driver.findElementById("licenceEventForm:closeLicenceEventsDialog").click();
    webAssertThat(() -> assertThat(driver.findElementById("licenceEventsDialog").isDisplayed()).isFalse());
   
    driver.findElementByXPath("//*[@class='topbar-notifications']/a").click();
    saveScreenshot(driver, "licence_dialog2");
    webAssertThat(() -> assertThat(driver.findElementById("licenceEventsDialog").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementsByXPath("//*[@id='licenceEventForm:licenceEventList']//li")).hasSize(1));
    driver.findElementById("licenceEventForm:confirmAllLicenceEvents").click();
    
    webAssertThat(() -> assertThat(driver.findElementById("tasksButtonLicenceDetail").isDisplayed()).isTrue());
    saveScreenshot(driver, "no_events_left");
    webAssertThat(() -> assertThat(driver.findElementById("licenceEventsDialog").isDisplayed()).isFalse());
    elementNotAvailable(driver, By.id("tasksButtonLicenceEvents"));
    elementNotAvailable(driver, By.className("topbar-notifications"));
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
