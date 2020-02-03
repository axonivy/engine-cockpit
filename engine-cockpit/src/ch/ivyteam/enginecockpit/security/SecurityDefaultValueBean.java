package ch.ivyteam.enginecockpit.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.codec.binary.StringUtils;

import ch.ivyteam.enginecockpit.util.SecuritySystemConfig;
import ch.ivyteam.enginecockpit.util.SecuritySystemConfig.ConfigKey;

@ManagedBean
@ViewScoped
public class SecurityDefaultValueBean
{
  private SpecificDefaults specificDefaults;

  private String url = SecuritySystemConfig.DefaultValue.URL;
  private String derefAliases = SecuritySystemConfig.DefaultValue.DEREF_ALIAS;
  private String referral = SecuritySystemConfig.DefaultValue.REFERRAL;
  private String email = SecuritySystemConfig.DefaultValue.EMAIL;
  private String updateTime = SecuritySystemConfig.DefaultValue.UPDATE_TIME;
  
  private String name;

  public SecurityDefaultValueBean()
  {
  }
  
  public String getSecuritySystemName()
  {
    return name;
  }
  
  public void setSecuritySystemName(String secSystemName)
  {
    this.name = secSystemName;
    loadSecuritySystem();
  }
  
  private void loadSecuritySystem()
  {
    specificDefaults = new SpecificDefaults(SecuritySystemConfig.getOrBlank(
            SecuritySystemConfig.getPrefix(name) + ConfigKey.PROVIDER));
  }

  public String getUrl()
  {
    return url;
  }

  public String getDerefaliases()
  {
    return derefAliases;
  }

  public String getReferral()
  {
    return referral;
  }

  public String getEmail()
  {
    return email;
  }

  public String getUpdatetime()
  {
    return updateTime;
  }

  public SpecificDefaults getSpecificDefaults()
  {
    return specificDefaults;
  }

  public static class SpecificDefaults
  {
    private String userFilter;
    private String id;
    private String name;
    private String fullName;
    private String userMemberOfAttribute;
    private boolean useUserMemberOfForUserRoleMembership;
    private String userGroupMemberOfAttribute;
    private String userGroupMembersAttribute;

    public SpecificDefaults(String provider)
    {
      if (StringUtils.equals(provider, "Novell eDirectory"))
      {
        initNovellValues();
      }
      else if (StringUtils.equals(provider, "Microsoft Active Directory"))
      {
        initActiveDirectoryValues();
      }
    }

    public String getUserFilter()
    {
      return userFilter;
    }

    public String getId()
    {
      return id;
    }

    public String getName()
    {
      return name;
    }

    public String getFullName()
    {
      return fullName;
    }

    public String getUserMemberOfAttribute()
    {
      return userMemberOfAttribute;
    }

    public boolean getUseUserMemberOfForUserRoleMembership()
    {
      return useUserMemberOfForUserRoleMembership;
    }

    public String getUserGroupMemberOfAttribute()
    {
      return userGroupMemberOfAttribute;
    }

    public String getUserGroupMembersAttribute()
    {
      return userGroupMembersAttribute;
    }

    private void initNovellValues()
    {
      this.userFilter = SecuritySystemConfig.DefaultValue.USER_FILTER_ND;
      this.id = SecuritySystemConfig.DefaultValue.ID_ND;
      this.name = SecuritySystemConfig.DefaultValue.NAME_ND;
      this.fullName = SecuritySystemConfig.DefaultValue.FULL_NAME_ND;
      this.userMemberOfAttribute = SecuritySystemConfig.DefaultValue.USER_MEMBER_OF_ATTRIBUTE_ND;
      this.useUserMemberOfForUserRoleMembership = SecuritySystemConfig.DefaultValue.USE_USER_MEMBER_OF_FOR_ROLE_MEMBERSHIP_ND;
      this.userGroupMemberOfAttribute = SecuritySystemConfig.DefaultValue.USER_GROUP_MEMBER_OF_ATTRIBUTE_ND;
      this.userGroupMembersAttribute = SecuritySystemConfig.DefaultValue.USER_GROUP_MEMBERS_ATTRIBUTE_ND;
    }

    private void initActiveDirectoryValues()
    {
      this.userFilter = SecuritySystemConfig.DefaultValue.USER_FILTER_AD;
      this.id = SecuritySystemConfig.DefaultValue.ID_AD;
      this.name = SecuritySystemConfig.DefaultValue.NAME_AD;
      this.fullName = SecuritySystemConfig.DefaultValue.FULL_NAME_AD;
      this.userMemberOfAttribute = SecuritySystemConfig.DefaultValue.USER_MEMBER_OF_ATTRIBUTE_AD;
      this.useUserMemberOfForUserRoleMembership = SecuritySystemConfig.DefaultValue.USE_USER_MEMBER_OF_FOR_ROLE_MEMBERSHIP_AD;
      this.userGroupMemberOfAttribute = SecuritySystemConfig.DefaultValue.USER_GROUP_MEMBER_OF_ATTRIBUTE_AD;
      this.userGroupMembersAttribute = SecuritySystemConfig.DefaultValue.USER_GROUP_MEMBERS_ATTRIBUTE_AD;
    }

  }
}
