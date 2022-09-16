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
import static org.assertj.core.api.Assertions.assertThat;

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
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestSecuritySystemDetail {

  private static final String CHANGE_PROVIDER_DIALOG = "#changeSecuritySystemProviderModal";
  private static final String CHANGE_PROVIDER_DIALOG_OK_BUTTON = "#changeSecuritySystemProviderForm\\:ok";

  private static final String SAVE_LANGUAGE_BTN = "#securityLanguageForm\\:saveLanguageConfigBtn";

  private static final String ADD_WORKFLOW_LANGUAGE_BTN = "#securityWorkflowLanguageForm\\:addBtn";
  private static final String ADD_WORKFLOW_LANGUAGE_DIALOG = "#addWorkflowLanguageModal";
  private static final String ADD_WORKFLOW_LANGUAGE_FROM_LANGUAGE = "#addWorkflowLanguageForm\\:language";
  private static final String ADD_WORKFLOW_LANGUAGE_FROM_ADD_BTN = "#addWorkflowLanguageForm\\:addBtn";
  private static final String ADD_WORKFLOW_LANGUAGE_FROM_CACNEL_BTN = "#addWorkflowLanguageForm\\:cancelBtn";
  private static final String REMOVE_WORKFLOW_LANGUAGE_DIALOG = "#deleteWorkflowLanguageModal";
  private static final String REMOVE_WORKFLOW_LANGUAGE_FROM_DELETE_BTN = "#deleteWorkflowLanguageForm\\:deleteBtn";
  private static final String REMOVE_WORKFLOW_LANGUAGE_FROM_CACNEL_BTN = "#deleteWorkflowLanguageForm\\:cancelBtn";

  private static final String SAVE_PROVIDER_BTN = "#securityProviderForm\\:saveProviderBtn";
  private static final String SAVE_PROVIDER_SUCCESS_GROWL = "#securityProviderForm\\:securityProviderSaveSuccess_container";
  private static final String PROVIDER = "#securityProviderForm\\:provider";
  private static final String SYNC_TIME = "#securityProviderForm\\:onScheduleTime";
  private static final String SYNC_TIME_MESSAGE = "#securityProviderForm\\:onScheduleTimeMessage";
  private static final String ON_SCHEDULE_ENABLED = "#securityProviderForm\\:onScheduleEnabled";
  private static final String SYNCH_ON_LOGIN = "#securityProviderForm\\:synchOnLogin";
  private static final String ON_SCHEDULE_IMPORT_USERS = "#securityProviderForm\\:onScheduleImportUsers";

  @BeforeEach
  void beforeEach() {
    login();
    Navigation.toSecuritySystemDetail("test-ad");
  }

  @Test
  void securitySystemDetail() {
    $$(".card").shouldHave(size(4));
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
  void addAndDeleteWorkflowLanguage() {
    Table table = new Table(By.id("securityWorkflowLanguageForm:workflowLanguageTable"));
    assertThat(table.getFirstColumnEntries()).containsExactlyInAnyOrder("English");

    $(ADD_WORKFLOW_LANGUAGE_BTN).click();
    $(ADD_WORKFLOW_LANGUAGE_DIALOG).shouldBe(visible);
    PrimeUi.selectOne(By.cssSelector(ADD_WORKFLOW_LANGUAGE_FROM_LANGUAGE)).selectItemByLabel("French");
    $(ADD_WORKFLOW_LANGUAGE_FROM_CACNEL_BTN).click();
    assertThat(table.getFirstColumnEntries()).containsExactlyInAnyOrder("English");

    $(ADD_WORKFLOW_LANGUAGE_BTN).click();
    $(ADD_WORKFLOW_LANGUAGE_DIALOG).shouldBe(visible);
    PrimeUi.selectOne(By.cssSelector(ADD_WORKFLOW_LANGUAGE_FROM_LANGUAGE)).selectItemByLabel("French");
    $(ADD_WORKFLOW_LANGUAGE_FROM_ADD_BTN).click();

    assertThat(table.getFirstColumnEntries()).containsExactlyInAnyOrder("English", "French");
    table.buttonForEntryShouldBeDisabled("English", "deleteBtn");
    table.clickButtonForEntry("French", "deleteBtn");

    $(REMOVE_WORKFLOW_LANGUAGE_DIALOG).shouldBe(visible);
    $(REMOVE_WORKFLOW_LANGUAGE_FROM_CACNEL_BTN).click();
    assertThat(table.getFirstColumnEntries()).containsExactlyInAnyOrder("English", "French");

    table.clickButtonForEntry("French", "deleteBtn");

    $(REMOVE_WORKFLOW_LANGUAGE_DIALOG).shouldBe(visible);
    $(REMOVE_WORKFLOW_LANGUAGE_FROM_DELETE_BTN).click();
    assertThat(table.getFirstColumnEntries()).containsExactlyInAnyOrder("English");
  }

  @Test
  void changeDefaultWorkflowLanguage() {
    Table table = new Table(By.id("securityWorkflowLanguageForm:workflowLanguageTable"));
    $(ADD_WORKFLOW_LANGUAGE_BTN).click();
    PrimeUi.selectOne(By.cssSelector(ADD_WORKFLOW_LANGUAGE_FROM_LANGUAGE)).selectItemByLabel("French");
    $(ADD_WORKFLOW_LANGUAGE_FROM_ADD_BTN).click();
    assertThat(table.getFirstColumnEntries()).containsExactlyInAnyOrder("English", "French");

    table.buttonForEntryShouldBeDisabled("English", "defaultBtn", true);
    table.buttonForEntryShouldBeDisabled("French", "defaultBtn", false);

    table.clickButtonForEntry("French", "defaultBtn");

    table.buttonForEntryShouldBeDisabled("English", "defaultBtn", false);
    table.buttonForEntryShouldBeDisabled("French", "defaultBtn", true);

    table.clickButtonForEntry("English", "defaultBtn");

    table.buttonForEntryShouldBeDisabled("English", "defaultBtn", true);
    table.buttonForEntryShouldBeDisabled("French", "defaultBtn", false);

    table.clickButtonForEntry("French", "deleteBtn");
    $(REMOVE_WORKFLOW_LANGUAGE_FROM_DELETE_BTN).click();
  }

  @Test
  void invalidAndValidonScheduleTimes() {
    $(SYNC_TIME).shouldBe(exactValue(""));
    $(SYNC_TIME).shouldBe(attribute("placeholder", "00:00"));
    $(SYNC_TIME_MESSAGE).shouldNotBe(visible);

    saveInvalidonScheduleTimeAndAssert("32:23");
    saveInvalidonScheduleTimeAndAssert("12:95");

    setonScheduleTime("16:47");
    saveProvider();

    clearonScheduleTime();
  }

  @Test
  void provider_settings_visible() {
    PrimeUi.selectOne(By.cssSelector(PROVIDER)).selectedItemShould(exactText("Microsoft Active Directory"));
    $(ON_SCHEDULE_ENABLED).shouldBe(visible);
    $(SYNCH_ON_LOGIN).shouldBe(visible);
    $(ON_SCHEDULE_IMPORT_USERS).shouldBe(visible);
    $(SYNC_TIME).shouldBe(visible);

    Navigation.toSecuritySystemDetail("default");

    PrimeUi.selectOne(By.cssSelector(PROVIDER)).selectedItemShould(exactText("Ivy Security System"));
    $(ON_SCHEDULE_ENABLED).shouldNotBe(visible);
    $(SYNCH_ON_LOGIN).shouldNotBe(visible);
    $(ON_SCHEDULE_IMPORT_USERS).shouldNotBe(visible);
    $(SYNC_TIME).shouldNotBe(visible);
  }

  @Test
  void provider() {
    Navigation.toSecuritySystemDetail("default");
    PrimeUi.selectOne(By.cssSelector(PROVIDER)).selectedItemShould(exactText("Ivy Security System"));

    PrimeUi.selectOne(By.cssSelector(PROVIDER)).selectItemByLabel("Microsoft Active Directory");
    $(CHANGE_PROVIDER_DIALOG).shouldBe(visible);
    $(CHANGE_PROVIDER_DIALOG_OK_BUTTON).click();
    saveProvider();

    $(ON_SCHEDULE_ENABLED).shouldBe(visible);
    $(SYNCH_ON_LOGIN).shouldBe(visible);
    $(ON_SCHEDULE_IMPORT_USERS).shouldBe(visible);
    $(SYNC_TIME).shouldBe(visible);

    PrimeUi.selectOne(By.cssSelector(PROVIDER)).selectItemByLabel("Ivy Security System");
    $(CHANGE_PROVIDER_DIALOG).shouldBe(visible);
    $(CHANGE_PROVIDER_DIALOG_OK_BUTTON).click();
    saveProvider();

    $(ON_SCHEDULE_ENABLED).shouldNotBe(visible);
    $(SYNCH_ON_LOGIN).shouldNotBe(visible);
    $(ON_SCHEDULE_IMPORT_USERS).shouldNotBe(visible);
    $(SYNC_TIME).shouldNotBe(visible);
  }

  @Test
  void provider_settings() {
    checkbox(ON_SCHEDULE_ENABLED).shouldBeChecked(true);
    checkbox(SYNCH_ON_LOGIN).shouldBeChecked(true);
    checkbox(ON_SCHEDULE_IMPORT_USERS).shouldBeChecked(true);

    $(ON_SCHEDULE_ENABLED).click();

    saveProvider();
    Navigation.toSecuritySystemDetail("test-ad");

    checkbox(ON_SCHEDULE_ENABLED).shouldBeChecked(false);
    checkbox(SYNCH_ON_LOGIN).shouldBeChecked(true);
    checkbox(ON_SCHEDULE_IMPORT_USERS).shouldBeChecked(true);

    $(ON_SCHEDULE_ENABLED).click();
    $(SYNCH_ON_LOGIN).click();

    saveProvider();
    Navigation.toSecuritySystemDetail("test-ad");

    checkbox(ON_SCHEDULE_ENABLED).shouldBeChecked(true);
    checkbox(SYNCH_ON_LOGIN).shouldBeChecked(false);
    checkbox(ON_SCHEDULE_IMPORT_USERS).shouldBeChecked(true);

    $(SYNCH_ON_LOGIN).click();
    $(ON_SCHEDULE_IMPORT_USERS).click();

    saveProvider();
    Navigation.toSecuritySystemDetail("test-ad");

    checkbox(ON_SCHEDULE_ENABLED).shouldBeChecked(true);
    checkbox(SYNCH_ON_LOGIN).shouldBeChecked(true);
    checkbox(ON_SCHEDULE_IMPORT_USERS).shouldBeChecked(false);

    $(ON_SCHEDULE_IMPORT_USERS).click();

    saveProvider();
    Navigation.toSecuritySystemDetail("test-ad");

    checkbox(ON_SCHEDULE_ENABLED).shouldBeChecked(true);
    checkbox(SYNCH_ON_LOGIN).shouldBeChecked(true);
    checkbox(ON_SCHEDULE_IMPORT_USERS).shouldBeChecked(true);
  }

  private void saveInvalidonScheduleTimeAndAssert( String time) {
    setonScheduleTime(time);
    $(SAVE_PROVIDER_BTN).click();
    $(SYNC_TIME_MESSAGE).shouldBe(visible);
  }

  private void setonScheduleTime(String time) {
      clearonScheduleTime();
      $(SYNC_TIME).sendKeys(time);
      $(SYNC_TIME).shouldBe(exactValue(time));
  }

  private void clearonScheduleTime() {
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
