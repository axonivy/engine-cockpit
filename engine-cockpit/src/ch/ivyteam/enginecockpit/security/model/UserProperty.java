package ch.ivyteam.enginecockpit.security.model;

import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.security.IUser;

public class UserProperty extends MemberProperty {
  private IUser user;

  @Override
  public void setMemberName(String memberName) {
    super.setMemberName(memberName);
    user = managerBean.getSelectedSecuritySystem().getSecurityContext().users().find(memberName);
    reloadProperties();
  }

  private void reloadProperties() {
    super.properties = user.getAllPropertyNames().stream()
        .map(key -> new SecurityMemberProperty(key, user.getProperty(key), user.isPropertyBacked(key)))
        .collect(Collectors.toList());
  }

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

  public void removeProperty(String propertyName) {
    user.removeProperty(propertyName);
    super.removePropertyMessage();
    reloadProperties();
  }
}
