package ch.ivyteam.enginecockpit.security.system;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.system.SecuritySystemConfig.ConfigKey;
import ch.ivyteam.ivy.security.internal.config.ExternalSecuritySystemConfiguration;

@ManagedBean
@ViewScoped
public class SecurityDefaultValueBean {
  private String url;
  private String updateTime;
  private String derefAliases;
  private String referral;

  private String userFilter;

  private String id;
  private String name;
  private String fullName;
  private String email;
  private String userMemberOfAttribute;
  private boolean useUserMemberOfForUserRoleMembership;
  private String userGroupMemberOfAttribute;
  private String userGroupMembersAttribute;

  private String secSystemName;

  private ExternalSecuritySystemConfiguration securityConfiguration;

  public String getSecuritySystemName() {
    return secSystemName;
  }

  public void setSecuritySystemName(String secSystemName) {
    this.secSystemName = secSystemName;
    securityConfiguration = new ExternalSecuritySystemConfiguration(secSystemName);
    loadSecuritySystem();
  }

  private void loadSecuritySystem() {
    url = securityConfiguration.getDefaultValue(ConfigKey.CONNECTION_URL);
    updateTime = securityConfiguration.getDefaultValue(ConfigKey.UPDATE_TIME);
    if (StringUtils.equals(updateTime, "0")) {
      updateTime = "00:00";
    }
    derefAliases = securityConfiguration.getDefaultValue(ConfigKey.CONNECTION_ENVIRONMENT_ALIASES);
    referral = securityConfiguration.getDefaultValue(ConfigKey.CONNECTION_ENVIRONMENT_REFERRAL);

    userFilter = securityConfiguration.getDefaultValue(ConfigKey.BINDING_USER_FILTER);

    id = securityConfiguration.getDefaultValue(ConfigKey.USER_ATTRIBUTE_ID);
    name = securityConfiguration.getDefaultValue(ConfigKey.USER_ATTRIBUTE_NAME);
    fullName = securityConfiguration.getDefaultValue(ConfigKey.USER_ATTRIBUTE_FULL_NAME);
    email = securityConfiguration.getDefaultValue(ConfigKey.USER_ATTRIBUTE_E_MAIL);
    userMemberOfAttribute = securityConfiguration
            .getDefaultValue(ConfigKey.MEMBERSHIP_USER_MEMBER_OF_ATTRIBUTE);
    useUserMemberOfForUserRoleMembership = securityConfiguration
            .getDefaultBooleanValue(ConfigKey.MEMBERSHIP_USER_MEMBER_OF_LOOKUP_ALLOWED);
    userGroupMemberOfAttribute = securityConfiguration
            .getDefaultValue(ConfigKey.MEMBERSHIP_GROUP_MEMBER_OF_ATTRIBUTE);
    userGroupMembersAttribute = securityConfiguration
            .getDefaultValue(ConfigKey.MEMBERSHIP_GROUP_MEMBERS_ATTRIBUTE);
  }

  public String getUrl() {
    return url;
  }

  public String getDerefaliases() {
    return derefAliases;
  }

  public String getReferral() {
    return referral;
  }

  public String getEmail() {
    return email;
  }

  public String getUpdatetime() {
    return updateTime;
  }

  public String getUserFilter() {
    return userFilter;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getFullName() {
    return fullName;
  }

  public String getUserMemberOfAttribute() {
    return userMemberOfAttribute;
  }

  public boolean getUseUserMemberOfForUserRoleMembership() {
    return useUserMemberOfForUserRoleMembership;
  }

  public String getUserGroupMemberOfAttribute() {
    return userGroupMemberOfAttribute;
  }

  public String getUserGroupMembersAttribute() {
    return userGroupMembersAttribute;
  }

}
