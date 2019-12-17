package ch.ivyteam.enginecockpit.security;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.codeborne.selenide.Condition;

import ch.ivyteam.enginecockpit.WebTestBase;
import ch.ivyteam.enginecockpit.util.Navigation;
import ch.ivyteam.enginecockpit.util.Tab;
import ch.ivyteam.enginecockpit.util.Table;

public class WebTestUserAndRoleProperties extends WebTestBase
{
  private static final String PROPERTY_VALUE_MESSAGE = "#propertyModalForm\\:propertyValueMessage";
  private static final String PROPERTY_NAME_MESSAGE = "#propertyModalForm\\:propertyNameMessage";
  private static final String PROPERTY_MODAL = "#propertyModal";
  private static final String PROPERTY_NAME_INPUT = "#propertyModalForm\\:propertyNameInput";
  private static final String PROPERTIES_GROWL = "#propertiesForm\\:propertiesMessage_container";
  private static final String SAVE_PROPERTY = "#propertyModalForm\\:saveProperty";
  private static final String PROPERTY_VALUE_INPUT = "#propertyModalForm\\:propertyValueInput";
  private static final By TABLE_ID = By.id("propertiesForm:propertiesTable");
  
  @Test
  public void testUserPropertyInvalid()
  {
    toUserDetail();
    openAddPropertyModal();
    saveInvalidAddProperty();
  }
  
  @Test
  public void testRolePropertyInvalid()
  {
    toRoleDetail("boss");
    openAddPropertyModal();
    saveInvalidAddProperty();
  }
  
  @Test
  public void testUserPropertyAddEditDelete()
  {
    String key = "test";
    toUserDetail();
    openAddPropertyModal();
    addProperty(key, "testValue");
    editProperty(key, "edit");
    deleteProperty(key);
  }
  
  @Test
  public void testRolePropertyAddEditDelete()
  {
    String key = "test";
    toRoleDetail("boss");
    openAddPropertyModal();
    addProperty(key, "testValue");
    editProperty(key, "edit");
    deleteProperty(key);
  }
  
  @Test
  public void testUserDirectoryProperties()
  {
    login();
    Navigation.toUsers();
    Tab.switchToTab("test-ad");
    String syncBtnId = "#form\\:card\\:apps\\:applicationTabView\\:" + Tab.getSelectedTabIndex() + "\\:panelSyncBtn";
    $(syncBtnId).click();
    $(syncBtnId + " > span:first-child").shouldNotHave(cssClass("fa-spin"));
    Navigation.toUserDetail("user1");
    assertTableHasDirectoryProperty("Address", "Baarerstrasse 12");
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
  
  private void toUserDetail()
  {
    login();
    Navigation.toUsers();
    Tab.switchToTab("test");
    Navigation.toUserDetail("foo");
  }
  
  private void toRoleDetail(String roleName)
  {
    login();
    Navigation.toRoleDetail(roleName);
  }
}
