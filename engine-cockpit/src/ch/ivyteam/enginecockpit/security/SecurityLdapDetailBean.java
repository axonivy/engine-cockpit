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
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.internal.config.ExternalSecuritySystemConfiguration;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class SecurityLdapDetailBean
{
  private String name;

  private String userId;
  private String userName;
  private String fullName;
  private String email;
  private String language;
  private String userMemberOfAttribute;
  private boolean groupMemberLookupAllowed;
  private String groupMemberOfAttribute;
  private String groupMembersAttribute;
  private Map<String, LdapProperty> properties;
  private LdapProperty ldapProperty;

  private ExternalSecuritySystemConfiguration securityConfiguration;

  public String getSecuritySystemName()
  {
    return name;
  }
  
  public void setSecuritySystemName(String secSystemName)
  {
    if (StringUtils.isBlank(name))
    {
      this.name = secSystemName;
      securityConfiguration = new ExternalSecuritySystemConfiguration(secSystemName);
      loadSecuritySystem();
    }
  }
  
  private void loadSecuritySystem()
  {
    userId = getConfiguration(ConfigKey.USER_ATTRIBUTE_ID);
    userName = getConfiguration(ConfigKey.USER_ATTRIBUTE_NAME);
    fullName = getConfiguration(ConfigKey.USER_ATTRIBUTE_FULL_NAME);
    email = getConfiguration(ConfigKey.USER_ATTRIBUTE_E_MAIL);
    language = getConfiguration(ConfigKey.USER_ATTRIBUTE_LANGUAGE);
    userMemberOfAttribute = getConfiguration(ConfigKey.MEMBERSHIP_USER_MEMBER_OF_ATTRIBUTE);
    groupMemberLookupAllowed = getInitValueGroupMemberLookupAllowed();
    groupMemberOfAttribute = getConfiguration(ConfigKey.MEMBERSHIP_GROUP_MEMBER_OF_ATTRIBUTE);
    groupMembersAttribute = getConfiguration(ConfigKey.MEMBERSHIP_GROUP_MEMBERS_ATTRIBUTE);
    
    properties = new HashMap<>();
    Map<String, String> yamlProperties = IConfiguration.instance().getMap(
            SecuritySystemConfig.getPrefix(name) + ConfigKey.USER_ATTRIBUTE_PROPERTIES);
    for (String key : yamlProperties.keySet())
    {
      properties.put(key, new LdapProperty(key, yamlProperties.get(key)));
    }
    
    ldapProperty = new LdapProperty();
  }

  public String getUserId()
  {
    return userId;
  }

  public void setUserId(String userId)
  {
    this.userId = userId;
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

  public boolean getGroupMemberLookupAllowed()
  {
    return groupMemberLookupAllowed;
  }

  public void setGroupMemberLookupAllowed(boolean groupMemberLookupAllowed)
  {
    this.groupMemberLookupAllowed = groupMemberLookupAllowed;
  }
  
  private boolean getInitValueGroupMemberLookupAllowed()
  {
    var allowed = getConfiguration(ConfigKey.MEMBERSHIP_GROUP_MEMBER_LOOKUP_ALLOWED);
    if (StringUtils.isBlank(allowed))
    {
      return securityConfiguration.getDefaultBooleanValue(ConfigKey.MEMBERSHIP_GROUP_MEMBER_LOOKUP_ALLOWED);
    }
    return Boolean.parseBoolean(allowed);
  }
  
  private Object getSaveValueGroupMemberLookupAllowed()
  {
    if (this.groupMemberLookupAllowed == securityConfiguration.getDefaultBooleanValue(ConfigKey.MEMBERSHIP_GROUP_MEMBER_LOOKUP_ALLOWED))
    {
      return "";
    }
    return this.groupMemberLookupAllowed;
  }
  
  public String getGroupMemberOfAttribute()
  {
    return groupMemberOfAttribute;
  }

  public void setGroupMemberOfAttribute(String groupMemberOfAttribute)
  {
    this.groupMemberOfAttribute = groupMemberOfAttribute;
  }

  public String getGroupMembersAttribute()
  {
    return groupMembersAttribute;
  }

  public void setGroupMembersAttribute(String groupMembersAttribute)
  {
    this.groupMembersAttribute = groupMembersAttribute;
  }

  public Collection<LdapProperty> getProperties()
  {
    return properties.values();
  }

  public LdapProperty getProperty()
  {
    return ldapProperty;
  }
  
  public void setProperty(LdapProperty ldapProperty)
  {
    this.ldapProperty = ldapProperty;
    if (ldapProperty == null)
    {
      this.ldapProperty = new LdapProperty();
    }
  }
  
  public void saveProperty()
  {
    setConfiguration(ConfigKey.USER_ATTRIBUTE_PROPERTIES + "." + ldapProperty.getName(), 
            ldapProperty.getLdapAttribute());
    properties.put(ldapProperty.getName(), ldapProperty);
    ldapProperty = new LdapProperty();
  }
  
  public void removeLdapAttribute(String attributeName)
  {
    IConfiguration.instance().remove(SecuritySystemConfig.getPrefix(name) + 
            ConfigKey.USER_ATTRIBUTE_PROPERTIES + "." + attributeName);
    properties.remove(attributeName);
  }
  
  public void saveConfiguration()
  {
    setConfiguration(ConfigKey.USER_ATTRIBUTE_ID, this.userId);
    setConfiguration(ConfigKey.USER_ATTRIBUTE_NAME, this.userName);
    setConfiguration(ConfigKey.USER_ATTRIBUTE_FULL_NAME, this.fullName);
    setConfiguration(ConfigKey.USER_ATTRIBUTE_E_MAIL, this.email);
    setConfiguration(ConfigKey.USER_ATTRIBUTE_LANGUAGE, this.language);
    setConfiguration(ConfigKey.MEMBERSHIP_USER_MEMBER_OF_ATTRIBUTE, this.userMemberOfAttribute);
    setConfiguration(ConfigKey.MEMBERSHIP_GROUP_MEMBER_LOOKUP_ALLOWED, getSaveValueGroupMemberLookupAllowed());
    setConfiguration(ConfigKey.MEMBERSHIP_GROUP_MEMBER_OF_ATTRIBUTE, this.groupMemberOfAttribute);
    setConfiguration(ConfigKey.MEMBERSHIP_GROUP_MEMBERS_ATTRIBUTE, this.groupMembersAttribute);

    FacesContext.getCurrentInstance().addMessage("securitySystemLdapSaveSuccess",
            new FacesMessage("Security System LDAP Attributes saved"));
  }
  
  private String getConfiguration(String key)
  {
    return SecuritySystemConfig.getOrBlank(SecuritySystemConfig.getPrefix(name) + key);
  }
  
  private void setConfiguration(String key, Object value)
  {
    SecuritySystemConfig.setOrRemove(SecuritySystemConfig.getPrefix(name) + key, value);
  }
}
