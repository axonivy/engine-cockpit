package ch.ivyteam.enginecockpit.security.model;

import java.util.stream.Collectors;

import ch.ivyteam.ivy.security.IRole;

public class RoleProperty extends MemberProperty {
  private final IRole role;

  public RoleProperty(IRole role) {
    this.role = role;
    reloadProperties();
  }

  @Override
  public void saveProperty() {
    role.setProperty(super.property.getKey(), super.property.getValue());
    super.savePropertyMessage();
    reloadProperties();
  }
  
  @Override
  public void removeProperty(String propertyName) {
    role.removeProperty(propertyName);
    super.removePropertyMessage();
    reloadProperties();
  }
  
  private void reloadProperties() {
    super.properties = role.getAllPropertyNames().stream()
        .map(key -> new SecurityMemberProperty(key, role.getProperty(key), false))
        .collect(Collectors.toList());
  }
}
