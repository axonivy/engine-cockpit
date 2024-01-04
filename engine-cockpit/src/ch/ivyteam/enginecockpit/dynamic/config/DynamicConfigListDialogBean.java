package ch.ivyteam.enginecockpit.dynamic.config;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.dynamic.config.ConfigProperty.KeyValueProperty;

@ManagedBean
@ViewScoped
public class DynamicConfigListDialogBean {

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
