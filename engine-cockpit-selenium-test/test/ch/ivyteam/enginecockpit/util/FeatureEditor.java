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

  public void editFeatureSave(String value, Integer editFeatureIndex) {
    edit(value, editFeatureIndex);
    $(By.id(featureEditor + editFeatureIndex + ":editFeatureEditor:featureForm:saveFeature")).click();
  }

  public void editFeatureCancel(String value, Integer editFeatureIndex) {
    edit(value, editFeatureIndex);
    $(By.id(featureEditor + editFeatureIndex + ":editFeatureEditor:featureForm:cancelFeature")).click();
  }

  private void edit(String value, Integer editFeatureIndex) {
    $(By.id(featureEditor + editFeatureIndex + ":editFeatureEditor:editFeatureBtn")).shouldBe(visible).click();
    $(By.id(featureEditor + editFeatureIndex + ":editFeatureEditor:featureForm:nameInput")).clear();
    $(By.id(featureEditor + editFeatureIndex + ":editFeatureEditor:featureForm:nameInput")).sendKeys(value);
  }
}
