package ch.ivyteam.enginecockpit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectManyCheckbox;

import ch.ivyteam.enginecockpit.util.Navigation;

public class WebTestEmail extends WebTestBase
{
  
  @Test
  void testEmailLanguageSwitch()
  {
    toEmail();
    
    String langDropDownId = driver.findElementByXPath("//div[contains(@id, 'applicationTabView:0:emailSetting:languageDropDown')]").getAttribute("id");
    String saveEmailSettingsBtnId = driver.findElementByXPath("//button[contains(@id, 'applicationTabView:0:saveEmailSettings')]").getAttribute("id");
    driver.findElementById(langDropDownId + "_label").click();
    webAssertThat(() -> assertThat(driver.findElementById(langDropDownId + "_items").isDisplayed()).isTrue());
    Map<String, String> languages = driver.findElementsByXPath("//*[@id='"+ langDropDownId +"_items']/li").stream().collect(Collectors.toMap(WebElement::getText, e -> e.getAttribute("id")));
    saveScreenshot("languages");
    driver.findElementById(languages.get("German")).click();
    webAssertThat(() -> assertThat(driver.findElementById(langDropDownId + "_label").getText()).isEqualTo("German"));
    saveScreenshot("choose_language");
    driver.findElementById(saveEmailSettingsBtnId).click();
    webAssertThat(() -> assertThat(driver.findElementById("form:emailSaveSuccess_container").isDisplayed()).isTrue());
    webAssertThat(() -> assertThat(driver.findElementById("form:emailSaveSuccess_container").getText()).isEqualTo("User email changes saved"));
    driver.navigate().refresh();
    saveScreenshot("refresh");
    webAssertThat(() -> assertThat(driver.findElementById(langDropDownId + "_label").getText()).isEqualTo("German"));
    
    driver.findElementById(langDropDownId + "_label").click();
    webAssertThat(() -> assertThat(driver.findElementById(langDropDownId + "_items").isDisplayed()).isTrue());
    driver.findElementById(languages.get("English")).click();
    webAssertThat(() -> assertThat(driver.findElementById(langDropDownId + "_label").getText()).isEqualTo("English"));
    driver.findElementById(saveEmailSettingsBtnId).click();
  }
  
  @Test
  void testApplicationEmail()
  {
    toEmail();
    
    PrimeUi primeUi = new PrimeUi(driver);
    
    String taskCheckboxId = driver.findElementByXPath("//div[contains(@id, 'applicationTabView:0:emailSetting:taskCheckbox')]").getAttribute("id");
    String dailyCheckboxesId = driver.findElementByXPath("//div[contains(@id, 'applicationTabView:0:emailSetting:radioDailyNotification')]").getAttribute("id");
    String saveEmailSettingsBtnId = driver.findElementByXPath("//button[contains(@id, 'applicationTabView:0:saveEmailSettings')]").getAttribute("id");
    SelectBooleanCheckbox taskCheckbox = primeUi.selectBooleanCheckbox(By.id(taskCheckboxId));
    SelectManyCheckbox dailyCheckbox = primeUi.selectManyCheckbox(By.id(dailyCheckboxesId));
    webAssertThat(() -> assertThat(taskCheckbox.isChecked()).isFalse());
    webAssertThat(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    webAssertThat(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    webAssertThat(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).isEmpty());
    
    List<String> days = new ArrayList<String>(Arrays.asList("Tue", "Wed", "Thu", "Sat"));
    dailyCheckbox.setCheckboxes(days);
    saveScreenshot("days");
    webAssertThat(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Tue", "Wed", "Thu", "Sat"));
    
    taskCheckbox.setChecked();
    saveScreenshot("task_checked");
    webAssertThat(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    webAssertThat(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    driver.findElementById(saveEmailSettingsBtnId).click();
    webAssertThat(() -> assertThat(driver.findElementById("form:emailSaveSuccess_container").isDisplayed()).isTrue());
    saveScreenshot("save");

    driver.navigate().refresh();
    saveScreenshot("refresh");
    webAssertThat(() -> assertThat(taskCheckbox.isChecked()).isTrue());
    webAssertThat(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    webAssertThat(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    webAssertThat(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Tue", "Wed", "Thu", "Sat"));
    
    dailyCheckbox.clear();
    taskCheckbox.removeChecked();
    driver.findElementById(saveEmailSettingsBtnId).click();
    saveScreenshot("undo_changes");
    webAssertThat(() -> assertThat(taskCheckbox.isChecked()).isFalse());
    webAssertThat(() -> assertThat(taskCheckbox.isDisabled()).isFalse());
    webAssertThat(() -> assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse());
    webAssertThat(() -> assertThat(dailyCheckbox.getSelectedCheckboxes()).isEmpty());
  }

  private void toEmail()
  {
    login();
    Navigation.toEmail(driver);
    saveScreenshot("email");
  }
  
}
