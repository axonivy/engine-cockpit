package ch.ivyteam.enginecockpit.util;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

import org.openqa.selenium.By;

public class FeatureEditor {

  private final String featureTable;
  private final Table table;

  public FeatureEditor(String featureTable) {
    this.featureTable = featureTable;
    this.table = new Table(By.id(featureTable));
  }

  public void addFeature(String feature) {
    var newEditor = featureTable + ":newFeatureEditor:";
    $(By.id(newEditor + "newServiceFeatureBtn")).shouldBe(visible).click();
    $(By.id(newEditor + "featureForm:nameInput")).sendKeys(feature);
    $(By.id(newEditor + "featureForm:saveFeature")).click();
    table.row(feature).shouldBe(visible);
  }

  public void editFeatureSave(String feature, String value) {
    var editEditor = edit(feature, value);
    $(By.id(editEditor + "featureForm:saveFeature")).click();
    table.row(value).shouldBe(visible);
    table.row(feature).shouldNotBe(visible);
  }

  public void editFeatureCancel(String feature) {
    var editEditor = edit(feature, "cancel");
    $(By.id(editEditor + "featureForm:cancelFeature")).click();
    table.row(feature).shouldBe(visible);
  }

  public void deleteFeature(String feature) {
    table.clickButtonForEntry(feature, "editFeatureEditor:deleteFeatureBtn");
    table.body().shouldNotHave(text(feature));
  }

  private String edit(String feature, String value) {
    var editEditor = featureTable + ":" + table.getRowNumber(feature) + ":editFeatureEditor:";
    table.clickButtonForEntry(feature, "editFeatureEditor:editFeatureBtn");
    $(By.id(editEditor + "featureForm:nameInput")).clear();
    $(By.id(editEditor + "featureForm:nameInput")).sendKeys(value);
    return editEditor;
  }
}
