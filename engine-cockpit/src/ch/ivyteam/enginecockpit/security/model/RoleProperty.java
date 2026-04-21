package ch.ivyteam.enginecockpit.security.model;

import java.util.stream.Collectors;

import ch.ivyteam.ivy.security.IRole;

public class RoleProperty extends MemberProperty {
  private IRole role;

  @Override
  public void setMemberName(String memberName) {
    super.setMemberName(memberName);
    role = managerBean.getSelectedSecuritySystem().getSecurityContext().roles().find(memberName);
    reloadProperties();
  }

  private void reloadProperties() {
    super.properties = role.getAllPropertyNames().stream()
        .map(key -> new SecurityMemberProperty(key, role.getProperty(key), false))
        .collect(Collectors.toList());
  }

  public void saveProperty() {
    role.setProperty(super.property.getKey(), super.property.getValue());
    super.savePropertyMessage();
    reloadProperties();
  }

  public void removeProperty(String propertyName) {
    role.removeProperty(propertyName);
    super.removePropertyMessage();
    reloadProperties();
  }
}

