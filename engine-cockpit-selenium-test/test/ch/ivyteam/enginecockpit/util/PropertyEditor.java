package ch.ivyteam.enginecockpit.util;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.openqa.selenium.By;

public class PropertyEditor {

  private String propertyEditor;

  public PropertyEditor(String propertyEditor) {
    this.propertyEditor = propertyEditor;
  }
  
  public void addProperty(String key, String value) {
    $(By.id(propertyEditor + "newServicePropertyBtn")).shouldBe(visible).click();
    $(By.id(propertyEditor + "propertyForm:nameInput")).sendKeys(key);
    $(By.id(propertyEditor + "propertyForm:valueInput")).sendKeys(value);
    $(By.id(propertyEditor + "propertyForm:saveProperty")).click();
  }
  
  public void editProperty(String value) {
    $(By.id(propertyEditor + "editPropertyBtn")).shouldBe(visible).click();
    $(By.id(propertyEditor + "propertyForm:valueInput")).clear();
    $(By.id(propertyEditor + "propertyForm:valueInput")).sendKeys(value);
    $(By.id(propertyEditor + "propertyForm:saveProperty")).click();
  }
}
