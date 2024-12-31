package ch.ivyteam.enginecockpit.util;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.openqa.selenium.By;

public class FeatureEditor {

  private final String featureEditor;

  public FeatureEditor(String featureEditor) {
    this.featureEditor = featureEditor;
  }

  public void addFeature(String feature) {
    $(By.id(featureEditor + "newFeatureEditor:newServiceFeatureBtn")).shouldBe(visible).click();
    $(By.id(featureEditor + "newFeatureEditor:featureForm:nameInput")).sendKeys(feature);
    $(By.id(featureEditor + "newFeatureEditor:featureForm:saveFeature")).click();
  }

  public void editFeature(String value, Integer editFeatureNumber, Integer nameInputNumber) {
    $(By.id(featureEditor + editFeatureNumber + ":editFeatureEditor:editFeatureBtn")).shouldBe(visible).click();
    $(By.id(featureEditor + nameInputNumber + ":editFeatureEditor:featureForm:nameInput")).clear();
    $(By.id(featureEditor + nameInputNumber + ":editFeatureEditor:featureForm:nameInput")).sendKeys(value);
    $(By.id(featureEditor + nameInputNumber + ":editFeatureEditor:featureForm:saveFeature")).click();
  }
}
