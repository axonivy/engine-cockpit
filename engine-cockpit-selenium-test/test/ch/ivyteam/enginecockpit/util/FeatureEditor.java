package ch.ivyteam.enginecockpit.util;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.openqa.selenium.By;

public class FeatureEditor {

  private String featureEditor;

  public FeatureEditor(String featureEditor) {
    this.featureEditor = featureEditor;
  }
  
  public void addFeature(String feature) {
    $(By.id(featureEditor + "newServiceFeatureBtn")).shouldBe(visible).click();
    $(By.id(featureEditor + "featureForm:nameInput")).sendKeys(feature);
    $(By.id(featureEditor + "featureForm:saveFeature")).click();
  }
  
  public void editFeature(String value) {
    $(By.id(featureEditor + "editFeatureBtn")).shouldBe(visible).click();
    $(By.id(featureEditor + "featureForm:nameInput")).clear();
    $(By.id(featureEditor + "featureForm:nameInput")).sendKeys(value);
    $(By.id(featureEditor + "featureForm:saveFeature")).click();
  }
  
  public void editSecondFeature(String value, String feature) {
    $(By.id(feature))
        .shouldBe(visible).click();
    $(By.id(featureEditor + "featureForm:nameInput")).clear();
    $(By.id(featureEditor + "featureForm:nameInput")).sendKeys(value);
    $(By.id(featureEditor + "featureForm:saveFeature")).click();
  }
}
