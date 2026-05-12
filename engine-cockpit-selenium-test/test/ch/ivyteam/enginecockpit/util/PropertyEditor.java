package ch.ivyteam.enginecockpit.util;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.openqa.selenium.By;

public class PropertyEditor {

  private final String propertyTable;
  private final Table table;

  public PropertyEditor(String propertyTable) {
    this.propertyTable = propertyTable;
    this.table = new Table(By.id(propertyTable));
  }

  public void addProperty(String key, String value) {
    var newEditor = propertyTable + ":newPropertyEditor:";
    $(By.id(newEditor + "newServicePropertyBtn")).shouldBe(visible).click();
    $(By.id(newEditor + "propertyForm:nameInput")).sendKeys(key);
    $(By.id(newEditor + "propertyForm:valueInput")).sendKeys(value);
    $(By.id(newEditor + "propertyForm:saveProperty")).click();
    table.row(key).shouldHave(text(value));
  }

  public void editProperty(String key, String value) {
    var editEditor = propertyTable + ":" + table.getRowNumber(key) + ":editPropertyEditor:";
    table.clickButtonForEntry(key, "editPropertyEditor:editPropertyBtn");
    $(By.id(editEditor + "propertyForm:valueInput")).clear();
    $(By.id(editEditor + "propertyForm:valueInput")).sendKeys(value);
    $(By.id(editEditor + "propertyForm:saveProperty")).click();
    table.row(key).shouldHave(text(value));
  }

  public void deleteProperty(String key) {
    table.clickButtonForEntry(key, "editPropertyEditor:deletePropertyBtn");
    table.body().shouldNotHave(text(key));
  }
}
