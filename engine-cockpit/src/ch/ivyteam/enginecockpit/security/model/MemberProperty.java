package ch.ivyteam.enginecockpit.security.model;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.IUser;

public class MemberProperty {
  private String memberName;
  private List<SecurityMemberProperty> properties;
  private List<SecurityMemberProperty> filteredProperties;
  private SecurityMemberProperty property;
  private String filter;
  private ManagerBean managerBean;

  public MemberProperty() {
    var context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    property = new SecurityMemberProperty();
  }

  public String getMemberName() {
    return memberName;
  }

  public void setMemberName(String memberName) {
    this.memberName = memberName;
  }

  public List<SecurityMemberProperty> getProperties() {
    return properties;
  }

  public List<SecurityMemberProperty> getFilteredPropteries() {
    return filteredProperties;
  }

  public void setFilteredPropteries(List<SecurityMemberProperty> filteredProperties) {
    this.filteredProperties = filteredProperties;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public SecurityMemberProperty getProperty() {
    return property;
  }

  public void setProperty(SecurityMemberProperty property) {
    this.property = property;
    if (property == null) {
      this.property = new SecurityMemberProperty();
    }
  }

  public void savePropertyMessage() {
    FacesContext.getCurrentInstance().addMessage("propertiesMessage",
            new FacesMessage("Successfully updated property", ""));
  }

  public void removePropertyMessage() {
    FacesContext.getCurrentInstance().addMessage("propertiesMessage",
            new FacesMessage("Successfully removed property", ""));
  }

  public class RoleProperty extends MemberProperty {
    private IRole role;

    @Override
    public void setMemberName(String memberName) {
      super.setMemberName(memberName);
      role = managerBean.getSelectedIApplication().getSecurityContext().roles().find(memberName);
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

  public class UserProperty extends MemberProperty {
    private IUser user;

    @Override
    public void setMemberName(String memberName) {
      super.setMemberName(memberName);
      user = managerBean.getSelectedIApplication().getSecurityContext().users().find(memberName);
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
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "The property '"
                        + super.property.getKey() + "' has already been imported from your Security System"));
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

}
