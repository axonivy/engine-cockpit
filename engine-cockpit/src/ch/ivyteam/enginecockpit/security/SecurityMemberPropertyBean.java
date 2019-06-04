package ch.ivyteam.enginecockpit.security;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.SecurityMemberProperty;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.IUser;

@ManagedBean
@ViewScoped
public class SecurityMemberPropertyBean
{
  private String memberName;
  private List<SecurityMemberProperty> properties;
  private List<SecurityMemberProperty> filteredProperties;
  
  private SecurityMemberProperty newProperty;
  private Member member;
  
  private String filter;
  
  private ManagerBean managerBean;
  
  public SecurityMemberPropertyBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}",
            ManagerBean.class);
    newProperty = new SecurityMemberProperty();
  }
  
  public String getUserMemberName()
  {
    return memberName;
  }

  public void setUserMemberName(String memberName)
  {
    this.memberName = memberName;
    member = new Member(managerBean.getSelectedIApplication().getSecurityContext().findUser(memberName));
    reloadProperties();
  }
  
  public String getRoleMemberName()
  {
    return memberName;
  }
  
  public void setRoleMemberName(String memberName)
  {
    this.memberName = memberName;
    member = new Member(managerBean.getSelectedIApplication().getSecurityContext().findRole(memberName));
    reloadProperties();
  }

  private void reloadProperties()
  {
    properties = member.getAllPropertyNames().stream()
            .map(key -> new SecurityMemberProperty(key, member.getProperty(key), member.isPropertyBacked(key)))
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
  
  public SecurityMemberProperty getNewProperty()
  {
    return newProperty;
  }
  
  public void saveNewProperty()
  {
    member.saveProperty(newProperty);
    FacesContext.getCurrentInstance().addMessage("propertiesMessage",
            new FacesMessage("Successful add new property"));
    reloadProperties();
  }
  
  public void removeProperty(String propertyName)
  {
    member.removeProperty(propertyName);
    FacesContext.getCurrentInstance().addMessage("propertiesMessage",
            new FacesMessage("Successful remove property"));
    reloadProperties();
  }
  
  private static class Member
  {
    private IUser user;
    private IRole role;
    
    public Member (IRole role)
    {
      this.role = role;
    }
    
    public Member (IUser user)
    {
      this.user = user;
    }
    
    public List<String> getAllPropertyNames()
    {
      if (role != null)
      {
        return role.getAllPropertyNames();
      }
      else if (user != null)
      {
        return user.getAllPropertyNames();
      }
      else
      {
        return Collections.emptyList();
      }
    }
    
    public String getProperty(String propertyName)
    {
      if (role != null)
      {
        return role.getProperty(propertyName);
      }
      else if (user != null)
      {
        return user.getProperty(propertyName);
      }
      else
      {
        return "";
      }
    }
    
    public boolean isPropertyBacked(String propertyName)
    {
      if (user != null)
      {
        return user.isPropertyBacked(propertyName);
      }
      else
      {
        return false;
      }
    }
    
    public void saveProperty(SecurityMemberProperty property)
    {
      if (role != null)
      {
        role.setProperty(property.getKey(), property.getValue());
      }
      else if (user != null)
      {
        user.setProperty(property.getKey(), property.getValue());
      }
    }
    
    public void removeProperty(String propertyName)
    {
      if (role != null)
      {
        role.removeProperty(propertyName);
      }
      else if (user != null)
      {
        user.removeProperty(propertyName);
      }
    }
  }
}
