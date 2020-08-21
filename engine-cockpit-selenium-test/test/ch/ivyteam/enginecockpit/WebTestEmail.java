package ch.ivyteam.enginecockpit;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.escapeSelector;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectBooleanCheckbox;
import com.axonivy.ivy.webtest.primeui.widget.SelectManyCheckbox;
import com.axonivy.ivy.webtest.primeui.widget.SelectOneMenu;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.EngineCockpitUtil;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;

@IvyWebTest
public class WebTestEmail
{
  
  private static final String EMAIL_GROWL = "#emailSaveSuccess_container";

  @BeforeEach
  void beforeEach()
  {
    login();
    Navigation.toEmail();
  }
  
  @Test
  void testEmailLanguageSwitch()
  {
    SelectOneMenu language = PrimeUi.selectOne(By.id(getActivePanel() + "emailSetting:languageDropDown"));
    language.selectItemByLabel("German");
    language.selectedItemShould(Condition.exactText("German"));
    $(getActivePanelCss() + "saveEmailSettings").click();
    $(EMAIL_GROWL).shouldBe(visible, exactText("User email changes saved"));
    
    Selenide.refresh();
    language.selectedItemShould(Condition.exactText("German"));
    language.selectItemByLabel("English");
    language.selectedItemShould(Condition.exactText("English"));
    $(getActivePanelCss() + "saveEmailSettings").click();
  }
  
  @Test
  void testApplicationEmail()
  {
    SelectBooleanCheckbox taskCheckbox = PrimeUi.selectBooleanCheckbox(By.id(getActivePanel() + "emailSetting:taskCheckbox"));
    SelectManyCheckbox dailyCheckbox = PrimeUi.selectManyCheckbox(By.id(getActivePanel() + "emailSetting:radioDailyNotification"));
    assertThat(taskCheckbox.isChecked()).isFalse();
    assertThat(taskCheckbox.isDisabled()).isFalse();
    assertThat(dailyCheckbox.isManyCheckboxDisabled()).isFalse();
    assertThat(dailyCheckbox.getSelectedCheckboxes()).isEmpty();
    
    dailyCheckbox.setCheckboxes(Arrays.asList("Tue", "Wed", "Thu", "Sat"));
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
  
  @Test
  void liveStats()
  {
    EngineCockpitUtil.assertLiveStats(List.of("Mails Sent", "Mail Sending Execution Time"));
  }
  
  private String getActivePanel()
  {
    return "tabs:applicationTabView:" + Tab.getSelectedTabIndex() + ":form:";
  }
  
  private String getActivePanelCss()
  {
    return escapeSelector(getActivePanel());
  }

}
