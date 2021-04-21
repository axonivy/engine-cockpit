package ch.ivyteam.enginecockpit.services;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.escapeSelector;
import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

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
    Tab.switchToDefault();
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
    taskCheckbox.shouldBeChecked(false);
    taskCheckbox.shouldBeDisabled(false);
    dailyCheckbox.shouldBeDisabled(false);
    dailyCheckbox.shouldBe(empty);
    
    dailyCheckbox.setCheckboxes(Arrays.asList("Tue", "Wed", "Thu", "Sat"));
    dailyCheckbox.shouldBe(exactTexts("Tue", "Wed", "Thu", "Sat"));
    
    taskCheckbox.setChecked();
    taskCheckbox.shouldBeChecked(true);
    dailyCheckbox.shouldBeDisabled(false);
    $(getActivePanelCss() + "saveEmailSettings").click();
    $(EMAIL_GROWL).shouldBe(visible);

    Selenide.refresh();
    taskCheckbox.shouldBeChecked(true);
    taskCheckbox.shouldBeDisabled(false);
    dailyCheckbox.shouldBeDisabled(false);
    dailyCheckbox.shouldBe(exactTexts("Tue", "Wed", "Thu", "Sat"));
    
    dailyCheckbox.clear();
    taskCheckbox.removeChecked();
    $(getActivePanelCss() + "saveEmailSettings").click();
    taskCheckbox.shouldBeChecked(false);
    taskCheckbox.shouldBeDisabled(false);
    dailyCheckbox.shouldBeDisabled(false);
    dailyCheckbox.shouldBe(empty);
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
