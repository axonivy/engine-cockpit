package ch.ivyteam.enginecockpit;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectBooleanCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectManyCheckbox;
import com.axonivy.ivy.supplements.primeui.tester.PrimeUi.SelectOneMenu;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

public class WebTestEmail extends WebTestBase
{
  
  private static final String EMAIL_GROWL = "#form\\:emailSaveSuccess_container";

  @Test
  void testEmailLanguageSwitch()
  {
    toEmail();
    
    SelectOneMenu language = primeUi.selectOne(By.id(getActivePanel() + "emailSetting:languageDropDown"));
    language.selectItemByLabel("German");
    assertThat(language.getSelectedItem()).isEqualTo("German");
    $(getActivePanelCss() + "saveEmailSettings").click();
    $(EMAIL_GROWL).shouldBe(visible, exactText("User email changes saved"));
    
    Selenide.refresh();
    //FIXME remove when primeUi improved
    $(getActivePanelCss() + "emailSetting\\:languageDropDown").shouldBe(visible);
    assertThat(language.getSelectedItem()).isEqualTo("German");
    language.selectItemByLabel("English");
    assertThat(language.getSelectedItem()).isEqualTo("English");
    $(getActivePanelCss() + "saveEmailSettings").click();
  }
  
  @Test
  void testApplicationEmail()
  {
    toEmail();
    
    SelectBooleanCheckbox taskCheckbox = primeUi.selectBooleanCheckbox(By.id(getActivePanel() + "emailSetting:taskCheckbox"));
    SelectManyCheckbox dailyCheckbox = primeUi.selectManyCheckbox(By.id(getActivePanel() + "emailSetting:radioDailyNotification"));
    assertThat(taskCheckbox.isChecked()).isFalse();
    assertThat(taskCheckbox.isDisabled()).isFalse();
    assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse();
    assertThat(dailyCheckbox.getSelectedCheckboxes()).isEmpty();
    
    List<String> days = new ArrayList<String>(Arrays.asList("Tue", "Wed", "Thu", "Sat"));
    dailyCheckbox.setCheckboxes(days);
    assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Tue", "Wed", "Thu", "Sat");
    
    taskCheckbox.setChecked();
    assertThat(taskCheckbox.isChecked()).isTrue();
    assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse();
    $(getActivePanelCss() + "saveEmailSettings").click();
    $(EMAIL_GROWL).shouldBe(visible);

    Selenide.refresh();
    assertThat(taskCheckbox.isChecked()).isTrue();
    assertThat(taskCheckbox.isDisabled()).isFalse();
    assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse();
    assertThat(dailyCheckbox.getSelectedCheckboxes()).containsExactlyInAnyOrder("Tue", "Wed", "Thu", "Sat");
    
    dailyCheckbox.clear();
    taskCheckbox.removeChecked();
    $(getActivePanelCss() + "saveEmailSettings").click();
    assertThat(taskCheckbox.isChecked()).isFalse();
    assertThat(taskCheckbox.isDisabled()).isFalse();
    assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse();
    assertThat(dailyCheckbox.getSelectedCheckboxes()).isEmpty();
  }
  
  private String getActivePanel()
  {
    return "form:card:tabs:applicationTabView:" + Tab.getSelectedTabIndex() + ":";
  }
  
  private String getActivePanelCss()
  {
    return escapeSelector(getActivePanel());
  }

  private void toEmail()
  {
    login();
    Navigation.toEmail();
  }
  
}
