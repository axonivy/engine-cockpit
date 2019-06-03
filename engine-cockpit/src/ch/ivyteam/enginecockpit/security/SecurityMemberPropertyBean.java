package ch.ivyteam.enginecockpit.security;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.SecurityMemberProperty;
import ch.ivyteam.ivy.security.IUser;

@ManagedBean
@ViewScoped
public class SecurityMemberPropertyBean
{
  private String memberName;
  private List<SecurityMemberProperty> properties;
  private List<SecurityMemberProperty> filteredProperties;
  
  private String filter;
  
  private ManagerBean managerBean;
  
  public SecurityMemberPropertyBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
  }
  
  public String getMemberName()
  {
    return memberName;
  }

  public void setMemberName(String memberName)
  {
    this.memberName = memberName;
    IUser user = managerBean.getSelectedIApplication().getSecurityContext().findUser(memberName);
    user.setProperty("test", "value");
    user.setProperty("bla", "test");
    properties = user.getAllPropertyNames().stream()
            .map(key -> new SecurityMemberProperty(key, user.getProperty(key), user.isPropertyBacked(key)))
            .collect(Collectors.toList());
  }
  
  public List<SecurityMemberProperty> getProperties()
  {
    return properties;
  }
  
  public List<SecurityMemberProperty> getFilteredPropteries()
  {
    return filteredProperties;
  }

  public void setFilteredPropteries(List<SecurityMemberProperty> filteredProperties)
  {
    this.filteredProperties = filteredProperties;
  }
  
  public String getFilter()
  {
    return filter;
  }
 
  public void setFilter(String filter)
  {
    this.filter = filter;
  }
}
