package ch.ivyteam.enginecockpit.dynamic.config;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import ch.ivyteam.enginecockpit.dynamic.config.ConfigProperty.KeyValueProperty;

@Named
@ViewScoped
public class DynamicConfigListDialogBean implements Serializable {

  private KeyValueProperty property;

  private String newKey;
  private String newValue;

  public void setProperty(KeyValueProperty property) {
    setProperty(property, "", "");
  }

  public void setProperty(KeyValueProperty property, String newKey, String newValue) {
    this.property = property;
    this.newKey = newKey;
    this.newValue = newValue;
  }

  public boolean isDirectoryBrowser() {
    if (property == null) {
      return false;
    }
    return property.isDirectoryBrowser();
  }

  public void saveProp() {
    property.addKeyValue(newKey, newValue);
  }

  public KeyValueProperty getProperty() {
    return property;
  }

  public String getNewKey() {
    return newKey;
  }

  public void setNewKey(String newKey) {
    this.newKey = newKey;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }
}
