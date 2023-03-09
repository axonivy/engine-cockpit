package ch.ivyteam.enginecockpit.security.identity;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.security.identity.ConfigProperty.KeyValueProperty;

@ManagedBean
@ViewScoped
public class IdentityProviderListDialogBean {

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

  public void saveProp() {
    property.addKeyValue(newKey, newValue);
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
