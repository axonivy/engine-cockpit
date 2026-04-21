package ch.ivyteam.enginecockpit.security.model;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.environment.Ivy;

public class MemberProperty {
  private String memberName;
  protected List<SecurityMemberProperty> properties;
  private List<SecurityMemberProperty> filteredProperties;
  protected SecurityMemberProperty property;
  private String filter;
  final ManagerBean managerBean;

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
        new FacesMessage(Ivy.cm().co("/memberProperties/SavePropertyMessage"), ""));
  }

  public void removePropertyMessage() {
    FacesContext.getCurrentInstance().addMessage("propertiesMessage",
        new FacesMessage(Ivy.cm().co("/memberProperties/RemovePropertyMessage"), ""));
  }
}
