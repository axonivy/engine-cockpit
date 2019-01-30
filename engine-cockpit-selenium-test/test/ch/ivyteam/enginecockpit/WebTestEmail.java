package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectManyCheckbox;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestEmail extends WebTestBase
{
  
  @Test
  void testEmailLanguageSwitch(FirefoxDriver driver)
  {
    toEmail(driver);
    
    String langDropDownId = driver.findElementByXPath("//div[contains(@id, 'applicationTabView:0:emailSetting:languageDropDown')]").getAttribute("id");
    String saveEmailSettingsBtnId = driver.findElementByXPath("//button[contains(@id, 'applicationTabView:0:saveEmailSettings')]").getAttribute("id");
    driver.findElementById(langDropDownId + "_label").click();
    await().until(() -> driver.findElementById(langDropDownId + "_items").isDisplayed());
    String chooseLanguage = driver.findElementById(langDropDownId + "_0").getText();
    saveScreenshot(driver, "languages");
    driver.findElementById(langDropDownId + "_0").click();
    await().untilAsserted(() -> assertThat(driver.findElementById(langDropDownId + "_label").getText()).isEqualTo(chooseLanguage));
    saveScreenshot(driver, "choose_language");
    driver.findElementById(saveEmailSettingsBtnId).click();
    await().untilAsserted(() -> assertThat(driver.findElementById("form:emailSaveSuccess_container").isDisplayed()).isTrue());
    await().untilAsserted(() -> assertThat(driver.findElementById("form:emailSaveSuccess_container").getText()).isEqualTo("User email changes saved"));
    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    await().untilAsserted(() -> assertThat(driver.findElementById(langDropDownId + "_label").getText()).isEqualTo(chooseLanguage));
    
    driver.findElementById(langDropDownId + "_label").click();
    await().until(() -> driver.findElementById(langDropDownId + "_items").isDisplayed());
    driver.findElementById(langDropDownId + "_1").click();
    await().untilAsserted(() -> assertThat(driver.findElementById(langDropDownId + "_label").getText()).isEqualTo("English"));
    driver.findElementById(saveEmailSettingsBtnId).click();
  }
  
  @Test
  void testApplicationEmail(FirefoxDriver driver)
  {
    toEmail(driver);
    
    PrimeUi primeUi = new PrimeUi(driver);
    
    String neverCheckboxId = driver.findElementByXPath("//div[contains(@id, 'applicationTabView:0:emailSetting:neverCheckbox')]").getAttribute("id");
    String taskCheckboxId = driver.findElementByXPath("//div[contains(@id, 'applicationTabView:0:emailSetting:taskCheckbox')]").getAttribute("id");
    String dailyCheckboxesId = driver.findElementByXPath("//div[contains(@id, 'applicationTabView:0:emailSetting:radioDailyNotification')]").getAttribute("id");
    String saveEmailSettingsBtnId = driver.findElementByXPath("//button[contains(@id, 'applicationTabView:0:saveEmailSettings')]").getAttribute("id");
    SelectBooleanCheckbox neverCheckbox = primeUi.selectBooleanCheckbox(new By.ById(neverCheckboxId));
    SelectBooleanCheckbox taskCheckbox = primeUi.selectBooleanCheckbox(new By.ById(taskCheckboxId));
    SelectManyCheckbox dailyCheckbox = primeUi.selectManyCheckbox(By.id(dailyCheckboxesId));
    await().untilAsserted(() -> assertThat(neverCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isTrue());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue());
    await().untilAsserted(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).isEmpty());
    
    neverCheckbox.removeChecked();
    saveScreenshot(driver, "never_unchecked");
    await().untilAsserted(() -> assertThat(neverCheckbox.isChecked()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    
    List<String> days = new ArrayList<String>(Arrays.asList("Tue", "Wed", "Thu", "Sat"));
    dailyCheckbox.setCheckboxes(days);
    saveScreenshot(driver, "days");
    await().untilAsserted(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Tue", "Wed", "Thu", "Sat"));
    
    taskCheckbox.setChecked();
    saveScreenshot(driver, "task_checked");
    await().untilAsserted(() -> assertThat(neverCheckbox.isChecked()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    driver.findElementById(saveEmailSettingsBtnId).click();
    await().untilAsserted(() -> assertThat(driver.findElementById("form:emailSaveSuccess_container").isDisplayed()).isTrue());
    saveScreenshot(driver, "save");

    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    await().untilAsserted(() -> assertThat(neverCheckbox.isChecked()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Tue", "Wed", "Thu", "Sat"));
    
    dailyCheckbox.clear();
    taskCheckbox.removeChecked();
    neverCheckbox.setChecked();
    saveScreenshot(driver, "undo_changes");
    await().untilAsserted(() -> assertThat(neverCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isTrue());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isTrue());
    await().untilAsserted(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).isEmpty());
  }

  private void toEmail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toEmail(driver);
    saveScreenshot(driver, "email");
  }
  
}
