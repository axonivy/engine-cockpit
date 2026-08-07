package ch.ivyteam.enginecockpit.security.model;

import java.util.stream.Collectors;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IUser;

public class UserProperty extends MemberProperty {
  private final IUser user;

  public UserProperty(IUser user) {
    this.user = user;
    reloadProperties();
  }

  @Override
  public void saveProperty() {
    if (user.isPropertyBacked(super.property.getKey())) {
      FacesContext.getCurrentInstance().addMessage("propertiesMessage",
          new FacesMessage(FacesMessage.SEVERITY_ERROR, Ivy.cm().co("/common/Error"),
              Ivy.cm().content("/memberProperties/SavePropertyErrorMessage")
                  .replace("property", super.property.getKey()).get()));
      return;
    }
    user.setProperty(super.property.getKey(), super.property.getValue());
    super.savePropertyMessage();
    reloadProperties();
  }

  @Override
  public void removeProperty(String propertyName) {
    user.removeProperty(propertyName);
    super.removePropertyMessage();
    reloadProperties();
  }

  private void reloadProperties() {
    super.properties = user.getAllPropertyNames().stream()
        .map(key -> new SecurityMemberProperty(key, user.getProperty(key), user.isPropertyBacked(key)))
        .collect(Collectors.toList());
  }
}
