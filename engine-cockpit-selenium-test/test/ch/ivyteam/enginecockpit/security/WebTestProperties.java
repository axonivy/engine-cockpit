package ch.ivyteam.enginecockpit.security;

import static ch.ivyteam.enginecockpit.util.EngineCockpitUtil.login;
import static com.codeborne.selenide.Condition.cssClass;
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
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

@IvyWebTest
public class WebTestProperties
{
  private static final String PROPERTY_VALUE_MESSAGE = "#propertyModalForm\\:propertyValueMessage";
  private static final String PROPERTY_NAME_MESSAGE = "#propertyModalForm\\:propertyNameMessage";
  private static final String PROPERTY_MODAL = "#propertyModal";
  private static final String PROPERTY_NAME_INPUT = "#propertyModalForm\\:propertyNameInput";
  private static final String PROPERTIES_GROWL = "#propertiesForm\\:propertiesMessage_container";
  private static final String SAVE_PROPERTY = "#propertyModalForm\\:saveProperty";
  private static final String PROPERTY_VALUE_INPUT = "#propertyModalForm\\:propertyValueInput";
  private static final By TABLE_ID = By.id("propertiesForm:propertiesTable");
  
  @BeforeEach
  void beforeEach()
  {
    login();
  }
  
  @Test
  public void testUserADSyncProperties()
  {
    Navigation.toUsers();
    Tab.switchToTab("test-ad");
    String syncBtnId = "#form\\:card\\:apps\\:applicationTabView\\:" + Tab.getSelectedTabIndex() + "\\:panelSyncBtn";
    $(syncBtnId).click();
    $(syncBtnId + " > span:first-child").shouldNotHave(cssClass("fa-spin"));
    Navigation.toUserDetail("user1");
    assertTableHasDirectoryProperty("Address", "Baarerstrasse 12");
  }
  
  @Nested
  class User
  {
    @BeforeEach
    void beforeEach()
    {
      Navigation.toUsers();
      Tab.switchToTab("test");
      Navigation.toUserDetail("foo");
      openAddPropertyModal();
    }
    
    @Test
    public void testPropertyInvalid()
    {
      saveInvalidAddProperty();
    }
    
    @Test
    public void testPropertyAddEditDelete()
    {
      String key = "test";
      addProperty(key, "testValue");
      editProperty(key, "edit");
      deleteProperty(key);
    }
  }
  
  @Nested
  class Role
  {
    @BeforeEach
    void beforeEach()
    {
      login();
      Navigation.toRoleDetail("boss");
      openAddPropertyModal();
    }
    
    @Test
    public void testPropertyInvalid()
    {
      saveInvalidAddProperty();
    }
    
    @Test
    public void testPropertyAddEditDelete()
    {
      String key = "test";
      addProperty(key, "testValue");
      editProperty(key, "edit");
      deleteProperty(key);
    }
  }
  
  private void editProperty(String key, String value)
  {
    new Table(TABLE_ID).clickButtonForEntry(key, "editPropertyBtn");
    $(PROPERTY_MODAL).shouldBe(visible);
    $("#propertyModalForm\\:propertyName").shouldBe(exactText(key));
    
    $(PROPERTY_VALUE_INPUT).clear();
    $(PROPERTY_VALUE_INPUT).sendKeys(value);
    $(SAVE_PROPERTY).click();
    assertTableHasKeyValue(key, value);
    $(PROPERTIES_GROWL).shouldHave(text("Successfully"));
  }

  private void deleteProperty(String key)
  {
    Table table = new Table(TABLE_ID);
    table.clickButtonForEntry(key, "deletePropertyBtn");
    $(PROPERTIES_GROWL).shouldHave(text("Successfully removed"));
    assertThat(table.getFirstColumnEntries()).hasSize(0);
  }
  

  private void addProperty(String key, String value)
  {
    Table table = new Table(TABLE_ID);
    assertThat(table.getFirstColumnEntries()).hasSize(0);
    $(PROPERTY_NAME_INPUT).sendKeys(key);
    $(PROPERTY_VALUE_INPUT).sendKeys(value);
    $(SAVE_PROPERTY).click();
    
    assertThat(table.getFirstColumnEntries()).hasSize(1);
    assertTableHasKeyValue(key, value);
    $(PROPERTIES_GROWL).shouldHave(text("Successfully"));
  }

  private void assertTableHasDirectoryProperty(String key, String value)
  {
    assertTableHasKeyValue(key, value);
    Table table = new Table(TABLE_ID);
    table.buttonForEntryShouldBeDisabled(key, "editPropertyBtn");
    table.buttonForEntryShouldBeDisabled(key, "deletePropertyBtn");
  }
  
  private void assertTableHasKeyValue(String key, String value)
  {
    Table table = new Table(TABLE_ID);
    assertThat(table.getValueForEntry(key, 2)).isEqualTo(value);
  }

  private void saveInvalidAddProperty()
  {
    $(SAVE_PROPERTY).click();
    $(PROPERTY_NAME_MESSAGE).shouldHave(text("Value is required"));
    $(PROPERTY_VALUE_MESSAGE).shouldHave(text("Value is required"));
  }
  
  private void openAddPropertyModal()
  {
    $("#propertiesForm\\:newPropertyBtn").click();
    $(PROPERTY_MODAL).shouldBe(visible);
    $(PROPERTY_NAME_INPUT).shouldBe(Condition.exactValue(""));
    $(PROPERTY_NAME_MESSAGE).shouldBe(empty);
    $(PROPERTY_VALUE_INPUT).shouldBe(Condition.exactValue(""));
    $(PROPERTY_VALUE_MESSAGE).shouldBe(empty);
  }
  
}
