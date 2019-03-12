package ch.ivyteam.enginecockpit.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.model.LdapProperty;
import ch.ivyteam.enginecockpit.util.SecuritySystemConfig;
import ch.ivyteam.enginecockpit.util.SecuritySystemConfig.ConfigKey;

@ManagedBean
@ViewScoped
public class SecurityLdapDetailBean
{
  private SecurityDefaultValueBean defaultBean;
  
  private String name;

  private String userName;
  private String fullName;
  private String email;
  private String language;
  private String userMemberOfAttribute;
  private boolean useUserMemberOfForUserRoleMembership;
  private String userGroupMemberOfAttribute;
  private String userGroupMembersAttribute;
  private Map<String, LdapProperty> properties;
  private LdapProperty newProperty;

  public SecurityLdapDetailBean()
  {
    FacesContext context = FacesContext.getCurrentInstance();
    
    defaultBean = context.getApplication().evaluateExpressionGet(context, "#{securityDefaultValueBean}", SecurityDefaultValueBean.class);
  }

  public String getSecuritySystemName()
  {
    return name;
  }
  
  public void setSecuritySystemName(String secSystemName)
  {
    if (StringUtils.isBlank(name))
    {
      this.name = secSystemName;
      loadSecuritySystem();
    }
  }
  
  private void loadSecuritySystem()
  {
    userName = getConfiguration(ConfigKey.USER_ATTRIBUTE_NAME);
    fullName = getConfiguration(ConfigKey.USER_ATTRIBUTE_FULL_NAME);
    email = getConfiguration(ConfigKey.USER_ATTRIBUTE_E_MAIL);
    language = getConfiguration(ConfigKey.USER_ATTRIBUTE_LANGUAGE);
    userMemberOfAttribute = getConfiguration(ConfigKey.MEMBERSHIP_USER_MEMBER_OF_ATTRIBUTE);
    useUserMemberOfForUserRoleMembership = getInitValueUseUserMemberOfForUserRoleMembership();
    userGroupMemberOfAttribute = getConfiguration(ConfigKey.MEMBERSHIP_USER_GROUP_MEMBER_OF_ATTRIBUTE);
    userGroupMembersAttribute = getConfiguration(ConfigKey.MEMBERSHIP_USER_GROUP_MEMBERS_ATTRIBUTE);
    
    properties = new HashMap<>();
    Map<String, String> yamlProperties = SecuritySystemConfig.getConfigurationMap(
            SecuritySystemConfig.getConfigPrefix(name) + ConfigKey.USER_ATTRIBUTE_PROPERTIES);
    for (String key : yamlProperties.keySet())
    {
      properties.put(key, new LdapProperty(key, yamlProperties.get(key)));
    }
    
    newProperty = new LdapProperty();
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getFullName()
  {
    return fullName;
  }

  public void setFullName(String fullName)
  {
    this.fullName = fullName;
  }

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public String getLanguage()
  {
    return language;
  }

  public void setLanguage(String language)
  {
    this.language = language;
  }

  public String getUserMemberOfAttribute()
  {
    return userMemberOfAttribute;
  }

  public void setUserMemberOfAttribute(String userMemberOfAttribute)
  {
    this.userMemberOfAttribute = userMemberOfAttribute;
  }

  public boolean getUseUserMemberOfForUserRoleMembership()
  {
    return useUserMemberOfForUserRoleMembership;
  }

  public void setUseUserMemberOfForUserRoleMembership(boolean useUserMemberOfForUserRoleMembership)
  {
    this.useUserMemberOfForUserRoleMembership = useUserMemberOfForUserRoleMembership;
  }
  
  private boolean getInitValueUseUserMemberOfForUserRoleMembership()
  {
    String membership = getConfiguration(ConfigKey.MEMBERSHIP_USE_USER_MEMBER_OF_FOR_USER_ROLE_MEMBERSHIP);
    if (StringUtils.isBlank(membership))
    {
      return defaultBean.getSpecificDefaults().getUseUserMemberOfForUserRoleMembership();
    }
    return Boolean.parseBoolean(membership);
  }
  
  private Object getSaveValueUseUserMemberOfForUserRoleMembership()
  {
    if (this.useUserMemberOfForUserRoleMembership == defaultBean.getSpecificDefaults().getUseUserMemberOfForUserRoleMembership())
    {
      return "";
    }
    return this.useUserMemberOfForUserRoleMembership;
  }

  public String getUserGroupMemberOfAttribute()
  {
    return userGroupMemberOfAttribute;
  }

  public void setUserGroupMemberOfAttribute(String userGroupMemberOfAttribute)
  {
    this.userGroupMemberOfAttribute = userGroupMemberOfAttribute;
  }

  public String getUserGroupMembersAttribute()
  {
    return userGroupMembersAttribute;
  }

  public void setUserGroupMembersAttribute(String userGroupMembersAttribute)
  {
    this.userGroupMembersAttribute = userGroupMembersAttribute;
  }

  public Collection<LdapProperty> getProperties()
  {
    return properties.values();
  }

  public LdapProperty getNewProperty()
  {
    return newProperty;
  }
  
  public void saveNewProperty()
  {
    properties.put(newProperty.getName(), newProperty);
    newProperty = new LdapProperty();
    saveLdapAttributes();
  }
  
  public void removeLdapAttribute(String attributeName)
  {
    properties.remove(attributeName);
    saveLdapAttributes();
  }
  
  private void saveLdapAttributes()
  {
    SecuritySystemConfig.removeConfig(SecuritySystemConfig.getConfigPrefix(name) + ConfigKey.USER_ATTRIBUTE_PROPERTIES);
    properties.values().stream()
            .filter(prop -> StringUtils.isNotBlank(prop.getName()))
            .forEach(prop -> setConfiguration(ConfigKey.USER_ATTRIBUTE_PROPERTIES + "." + prop.getName(),
                    prop.getLdapAttribute()));
  }
  
  public void saveConfiguration()
  {
    setConfiguration(ConfigKey.USER_ATTRIBUTE_NAME, this.userName);
    setConfiguration(ConfigKey.USER_ATTRIBUTE_FULL_NAME, this.fullName);
    setConfiguration(ConfigKey.USER_ATTRIBUTE_E_MAIL, this.email);
    setConfiguration(ConfigKey.USER_ATTRIBUTE_LANGUAGE, this.language);
    setConfiguration(ConfigKey.MEMBERSHIP_USER_MEMBER_OF_ATTRIBUTE, this.userMemberOfAttribute);
    setConfiguration(ConfigKey.MEMBERSHIP_USE_USER_MEMBER_OF_FOR_USER_ROLE_MEMBERSHIP, getSaveValueUseUserMemberOfForUserRoleMembership());
    setConfiguration(ConfigKey.MEMBERSHIP_USER_GROUP_MEMBER_OF_ATTRIBUTE, this.userGroupMemberOfAttribute);
    setConfiguration(ConfigKey.MEMBERSHIP_USER_GROUP_MEMBERS_ATTRIBUTE, this.userGroupMembersAttribute);

    FacesContext.getCurrentInstance().addMessage("securitySystemConfigSaveSuccess",
            new FacesMessage("Security System LDAP Attributes saved"));
  }
  
  private String getConfiguration(String key)
  {
    return SecuritySystemConfig.getConfiguration(SecuritySystemConfig.getConfigPrefix(name) + key);
  }
  
  private void setConfiguration(String key, Object value)
  {
    SecuritySystemConfig.setConfiguration(SecuritySystemConfig.getConfigPrefix(name) + key, value);
  }
}
