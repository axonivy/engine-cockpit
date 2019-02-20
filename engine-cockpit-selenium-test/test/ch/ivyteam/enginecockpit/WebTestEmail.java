package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
    Map<String, String> languages = driver.findElementsByXPath("//*[@id='"+ langDropDownId +"_items']/li").stream().collect(Collectors.toMap(WebElement::getText, e -> e.getAttribute("id")));
    saveScreenshot(driver, "languages");
    driver.findElementById(languages.get("German")).click();
    await().untilAsserted(() -> assertThat(driver.findElementById(langDropDownId + "_label").getText()).isEqualTo("German"));
    saveScreenshot(driver, "choose_language");
    driver.findElementById(saveEmailSettingsBtnId).click();
    await().untilAsserted(() -> assertThat(driver.findElementById("form:emailSaveSuccess_container").isDisplayed()).isTrue());
    await().untilAsserted(() -> assertThat(driver.findElementById("form:emailSaveSuccess_container").getText()).isEqualTo("User email changes saved"));
    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    await().untilAsserted(() -> assertThat(driver.findElementById(langDropDownId + "_label").getText()).isEqualTo("German"));
    
    driver.findElementById(langDropDownId + "_label").click();
    await().until(() -> driver.findElementById(langDropDownId + "_items").isDisplayed());
    driver.findElementById(languages.get("English")).click();
    await().untilAsserted(() -> assertThat(driver.findElementById(langDropDownId + "_label").getText()).isEqualTo("English"));
    driver.findElementById(saveEmailSettingsBtnId).click();
  }
  
  @Test
  void testApplicationEmail(FirefoxDriver driver)
  {
    toEmail(driver);
    
    PrimeUi primeUi = new PrimeUi(driver);
    
    String taskCheckboxId = driver.findElementByXPath("//div[contains(@id, 'applicationTabView:0:emailSetting:taskCheckbox')]").getAttribute("id");
    String dailyCheckboxesId = driver.findElementByXPath("//div[contains(@id, 'applicationTabView:0:emailSetting:radioDailyNotification')]").getAttribute("id");
    String saveEmailSettingsBtnId = driver.findElementByXPath("//button[contains(@id, 'applicationTabView:0:saveEmailSettings')]").getAttribute("id");
    SelectBooleanCheckbox taskCheckbox = primeUi.selectBooleanCheckbox(new By.ById(taskCheckboxId));
    SelectManyCheckbox dailyCheckbox = primeUi.selectManyCheckbox(By.id(dailyCheckboxesId));
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).isEmpty());
    
    List<String> days = new ArrayList<String>(Arrays.asList("Tue", "Wed", "Thu", "Sat"));
    dailyCheckbox.setCheckboxes(days);
    saveScreenshot(driver, "days");
    await().untilAsserted(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Tue", "Wed", "Thu", "Sat"));
    
    taskCheckbox.setChecked();
    saveScreenshot(driver, "task_checked");
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    driver.findElementById(saveEmailSettingsBtnId).click();
    await().untilAsserted(() -> assertThat(driver.findElementById("form:emailSaveSuccess_container").isDisplayed()).isTrue());
    saveScreenshot(driver, "save");

    driver.navigate().refresh();
    saveScreenshot(driver, "refresh");
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Tue", "Wed", "Thu", "Sat"));
    
    dailyCheckbox.clear();
    taskCheckbox.removeChecked();
    driver.findElementById(saveEmailSettingsBtnId).click();
    saveScreenshot(driver, "undo_changes");
    await().untilAsserted(() -> assertThat(taskCheckbox.isChecked()).isFalse());
    await().untilAsserted(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    await().untilAsserted(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).isEmpty());
  }

  private void toEmail(FirefoxDriver driver)
  {
    login(driver);
    Navigation.toEmail(driver);
    saveScreenshot(driver, "email");
  }
  
}
