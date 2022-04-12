package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.axonivy.ivy.webtest.IvyWebTest;
import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.SecuritySystemTab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
class WebTestProperties {

  private static final String PROPERTY_VALUE_MESSAGE = "#propertyModalForm\\:propertyValueMessage";
  private static final String PROPERTY_NAME_MESSAGE = "#propertyModalForm\\:propertyNameMessage";
  private static final String PROPERTY_MODAL = "#propertyModalForm\\:propertyModal";
  private static final String PROPERTY_NAME_INPUT = "#propertyModalForm\\:propertyNameInput";
  private static final String PROPERTIES_GROWL = "#propertiesForm\\:propertiesMessage_container";
  private static final String SAVE_PROPERTY = "#propertyModalForm\\:saveProperty";
  private static final String PROPERTY_VALUE_INPUT = "#propertyModalForm\\:propertyValueInput";
  private static final By TABLE_ID = By.id("propertiesForm:propertiesTable");

  @BeforeEach
  void beforeEach() {
    login();
  }

  @Test
  void userADSyncProperties() {
    Navigation.toUsers();
    WebTestUsers.triggerSync();

    Navigation.toUserDetail("user1");
    assertTableHasDirectoryProperty("Address", "Baarerstrasse 12");
  }

  @Nested
  class User {

    @BeforeEach
    void beforeEach() {
      Navigation.toUsers();
      SecuritySystemTab.switchToDefault();
      Navigation.toUserDetail("foo");
      openAddPropertyModal();
    }

    @Test
    void propertyInvalid() {
      saveInvalidAddProperty();
    }

    @Test
    void propertyAddEditDelete() {
      var key = "test";
      addProperty(key, "testValue");
      editProperty(key, "edit");
      deleteProperty(key);
    }
  }

  @Nested
  class Role {

    @BeforeEach
    void beforeEach() {
      login();
      Navigation.toRoleDetail("boss");
      openAddPropertyModal();
    }

    @Test
    void propertyInvalid() {
      saveInvalidAddProperty();
    }

    @Test
    void propertyAddEditDelete() {
      String key = "test";
      addProperty(key, "testValue");
      editProperty(key, "edit");
      deleteProperty(key);
    }
  }

  private void editProperty(String key, String value) {
    new Table(TABLE_ID).clickButtonForEntry(key, "editPropertyBtn");
    $(PROPERTY_MODAL).shouldBe(visible);
    $("#propertyModalForm\\:propertyName").shouldBe(exactText(key));

    $(PROPERTY_VALUE_INPUT).clear();
    $(PROPERTY_VALUE_INPUT).sendKeys(value);
    $(SAVE_PROPERTY).click();
    assertTableHasKeyValue(key, value, false);
    $(PROPERTIES_GROWL).shouldHave(text("Successfully"));
  }

  private void deleteProperty(String key) {
    Table table = new Table(TABLE_ID);
    table.clickButtonForEntry(key, "deletePropertyBtn");
    $(PROPERTIES_GROWL).shouldHave(text("Successfully removed"));
    assertThat(table.getFirstColumnEntries()).hasSize(0);
  }

  private void addProperty(String key, String value) {
    Table table = new Table(TABLE_ID);
    assertThat(table.getFirstColumnEntries()).hasSize(0);
    $(PROPERTY_NAME_INPUT).sendKeys(key);
    $(PROPERTY_VALUE_INPUT).sendKeys(value);
    $(SAVE_PROPERTY).click();

    assertThat(table.getFirstColumnEntries()).hasSize(1);
    assertTableHasKeyValue(key, value, false);
    $(PROPERTIES_GROWL).shouldHave(text("Successfully"));
  }

  private void assertTableHasDirectoryProperty(String key, String value) {
    assertTableHasKeyValue(key, value, true);
  }

  private void assertTableHasKeyValue(String key, String value, boolean managed) {
    Table table = new Table(TABLE_ID);
    table.valueForEntryShould(key, 2, exactText(value));
    if (managed) {
      table.buttonForEntryShouldBeDisabled(key, "deletePropertyBtn");
      table.buttonForEntryShouldBeDisabled(key, "editPropertyBtn");
    }
  }

  private void saveInvalidAddProperty() {
    $(SAVE_PROPERTY).click();
    $(PROPERTY_NAME_MESSAGE).shouldHave(text("Value is required"));
    $(PROPERTY_VALUE_MESSAGE).shouldHave(text("Value is required"));
  }

  private void openAddPropertyModal() {
    $("#propertiesForm\\:newPropertyBtn").click();
    $(PROPERTY_MODAL).shouldBe(visible);
    $(PROPERTY_NAME_INPUT).shouldBe(Condition.exactValue(""));
    $(PROPERTY_NAME_MESSAGE).shouldBe(empty);
    $(PROPERTY_VALUE_INPUT).shouldBe(Condition.exactValue(""));
    $(PROPERTY_VALUE_MESSAGE).shouldBe(empty);
  }
}
