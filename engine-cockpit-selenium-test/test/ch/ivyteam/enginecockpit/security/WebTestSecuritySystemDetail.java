package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.axonivy.ivy.webtest.primeui.PrimeUi;
import com.axonivy.ivy.webtest.primeui.widget.SelectBooleanCheckbox;
import com.codeborne.selenide.Selenide;

import ch.ivyteam.enginecockpit.util.Navigation;

@IvyWebTest
public class WebTestSecuritySystemDetail {

  private static final String CHANGE_PROVIDER_DIALOG = "#changeSecuritySystemProviderModal";
  private static final String CHANGE_PROVIDER_DIALOG_OK_BUTTON = "#changeSecuritySystemProviderForm\\:ok";

  private static final String SAVE_LANGUAGE_BTN = "#securityLanguageForm\\:saveLanguageConfigBtn";

  private static final String SAVE_PROVIDER_BTN = "#securityProviderForm\\:saveProviderBtn";
  private static final String SAVE_PROVIDER_SUCCESS_GROWL = "#securityProviderForm\\:securityProviderSaveSuccess_container";
  private static final String PROVIDER = "#securityProviderForm\\:provider";
  private static final String SYNC_TIME = "#securityProviderForm\\:syncTime";
  private static final String SYNC_TIME_MESSAGE = "#securityProviderForm\\:syncTimeMessage";
  private static final String ENABLE_DAILY_SYNCH = "#securityProviderForm\\:enableDailySynch";
  private static final String SYNCH_ON_LOGIN = "#securityProviderForm\\:synchOnLogin";
  private static final String ON_DEMAND_IMPORT = "#securityProviderForm\\:onDemandImport";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSecuritySystemDetail("test-ad");
  }

  @Test
  void securitySystemDetail() {
    $$(".card").shouldHave(size(3));
  }

  @Test
  void dirNotDeletableIfUsedByApp() {
    $(By.id("securitySystemConfigForm:usedByHelp")).parent().parent().find("a").shouldHave(exactText("test-ad"));
    $("#securitySystemConfigForm\\:deleteSecuritySystem").shouldBe(visible).click();
    $("#securitySystemConfigForm\\:deleteSecuritySystemConfirmYesBtn").shouldBe(disabled);
  }

  @Test
  void language() {
    var language = PrimeUi.selectOne(By.id("securityLanguageForm:language"));
    language.selectItemByLabel("German (de)");
    $(SAVE_LANGUAGE_BTN).click();
    language.selectedItemShould(value("de"));

    Selenide.refresh();
    language.selectedItemShould(value("de"));
    language.selectItemByLabel("English (en)");
    $(SAVE_LANGUAGE_BTN).click();

    Selenide.refresh();
    language.selectedItemShould(value("en"));
  }

  @Test
  void formattingLanguage() {
    var language = PrimeUi.selectOne(By.id("securityLanguageForm:formattingLanguage"));
    language.selectItemByLabel("Aghem (agq)");
    $(SAVE_LANGUAGE_BTN).click();
    language.selectedItemShould(value("agq"));

    Selenide.refresh();
    language.selectedItemShould(value("agq"));
    language.selectItemByLabel("English (en)");
    $(SAVE_LANGUAGE_BTN).click();

    Selenide.refresh();
    language.selectedItemShould(value("en"));
  }

  @Test
  void invalidAndValidSyncTimes() {
    $(SYNC_TIME).shouldBe(exactValue(""));
    $(SYNC_TIME).shouldBe(attribute("placeholder", "00:00"));
    $(SYNC_TIME_MESSAGE).shouldNotBe(visible);

    saveInvalidSyncTimeAndAssert("32:23");
    saveInvalidSyncTimeAndAssert("12:95");

    setSyncTime("16:47");
    saveProvider();

    clearSyncTime();
  }

  @Test
  void provider_settings_visible() {
    PrimeUi.selectOne(By.cssSelector(PROVIDER)).selectedItemShould(exactText("Microsoft Active Directory"));
    $(ENABLE_DAILY_SYNCH).shouldBe(visible);
    $(SYNCH_ON_LOGIN).shouldBe(visible);
    $(ON_DEMAND_IMPORT).shouldBe(visible);
    $(SYNC_TIME).shouldBe(visible);

    Navigation.toSecuritySystemDetail("default");

    PrimeUi.selectOne(By.cssSelector(PROVIDER)).selectedItemShould(exactText("Ivy Security System"));
    $(ENABLE_DAILY_SYNCH).shouldNotBe(visible);
    $(SYNCH_ON_LOGIN).shouldNotBe(visible);
    $(ON_DEMAND_IMPORT).shouldNotBe(visible);
    $(SYNC_TIME).shouldNotBe(visible);
  }

  @Test
  void provider() {
    Navigation.toSecuritySystemDetail("default");

    PrimeUi.selectOne(By.cssSelector(PROVIDER)).selectItemByLabel("Microsoft Active Directory");
    $(CHANGE_PROVIDER_DIALOG).shouldBe(visible);
    $(CHANGE_PROVIDER_DIALOG_OK_BUTTON).click();
    saveProvider();

    $(ENABLE_DAILY_SYNCH).shouldBe(visible);
    $(SYNCH_ON_LOGIN).shouldBe(visible);
    $(ON_DEMAND_IMPORT).shouldBe(visible);
    $(SYNC_TIME).shouldBe(visible);

    PrimeUi.selectOne(By.cssSelector(PROVIDER)).selectItemByLabel("Ivy Security System");
    $(CHANGE_PROVIDER_DIALOG).shouldBe(visible);
    $(CHANGE_PROVIDER_DIALOG_OK_BUTTON).click();
    saveProvider();

    $(ENABLE_DAILY_SYNCH).shouldNotBe(visible);
    $(SYNCH_ON_LOGIN).shouldNotBe(visible);
    $(ON_DEMAND_IMPORT).shouldNotBe(visible);
    $(SYNC_TIME).shouldNotBe(visible);
  }

  @Test
  void provider_settings() {
    checkbox(ENABLE_DAILY_SYNCH).shouldBeChecked(false);
    checkbox(SYNCH_ON_LOGIN).shouldBeChecked(false);
    checkbox(ON_DEMAND_IMPORT).shouldBeChecked(false);

    $(ENABLE_DAILY_SYNCH).click();

    saveProvider();
    Navigation.toSecuritySystemDetail("test-ad");

    checkbox(ENABLE_DAILY_SYNCH).shouldBeChecked(true);
    checkbox(SYNCH_ON_LOGIN).shouldBeChecked(false);
    checkbox(ON_DEMAND_IMPORT).shouldBeChecked(false);

    $(ENABLE_DAILY_SYNCH).click();
    $(SYNCH_ON_LOGIN).click();

    saveProvider();
    Navigation.toSecuritySystemDetail("test-ad");

    checkbox(ENABLE_DAILY_SYNCH).shouldBeChecked(false);
    checkbox(SYNCH_ON_LOGIN).shouldBeChecked(true);
    checkbox(ON_DEMAND_IMPORT).shouldBeChecked(false);

    $(SYNCH_ON_LOGIN).click();
    $(ON_DEMAND_IMPORT).click();

    saveProvider();
    Navigation.toSecuritySystemDetail("test-ad");

    checkbox(ENABLE_DAILY_SYNCH).shouldBeChecked(false);
    checkbox(SYNCH_ON_LOGIN).shouldBeChecked(false);
    checkbox(ON_DEMAND_IMPORT).shouldBeChecked(true);

    $(ON_DEMAND_IMPORT).click();

    saveProvider();
    Navigation.toSecuritySystemDetail("test-ad");

    checkbox(ENABLE_DAILY_SYNCH).shouldBeChecked(false);
    checkbox(SYNCH_ON_LOGIN).shouldBeChecked(false);
    checkbox(ON_DEMAND_IMPORT).shouldBeChecked(false);
  }

  private void saveInvalidSyncTimeAndAssert( String time) {
    setSyncTime(time);
    $(SAVE_PROVIDER_BTN).click();
    $(SYNC_TIME_MESSAGE).shouldBe(visible);
  }

  private void setSyncTime(String time) {
      clearSyncTime();
      $(SYNC_TIME).sendKeys(time);
      $(SYNC_TIME).shouldBe(exactValue(time));
  }

  private void clearSyncTime() {
    while (StringUtils.isNotEmpty($(SYNC_TIME).getValue())) {
      $(SYNC_TIME).sendKeys(Keys.BACK_SPACE);
    }
    saveProvider();
  }

  private void saveProvider() {
    $(SAVE_PROVIDER_BTN).click();
    $(SYNC_TIME_MESSAGE).shouldNotBe(visible);
    $(SAVE_PROVIDER_SUCCESS_GROWL).shouldBe(visible);
    Selenide.executeJavaScript("arguments[0].click();", $(SAVE_PROVIDER_SUCCESS_GROWL + " .ui-growl-icon-close"));
  }

  private SelectBooleanCheckbox checkbox(String selector) {
    return PrimeUi.selectBooleanCheckbox(By.cssSelector(selector));
  }

}
