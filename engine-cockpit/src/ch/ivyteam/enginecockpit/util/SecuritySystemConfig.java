package ch.ivyteam.enginecockpit.util;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
public class SecuritySystemConfig
{

  public static final String SECURITY_SYSTEMS = "SecuritySystems";
  public static final String SECURITY_STSTEM = "SecuritySystem";
  public static final String IVY_SECURITY_SYSTEM = "ivy Security System";

  public interface ConfigKey
  {
    String PROVIDER = "Provider";
    String CONNECTION_URL = "Connection.Url";
    String CONNECTION_USER_NAME = "Connection.UserName";
    String CONNECTION_PASSWORD = "Connection.Password";
    String CONNECTION_AUTHENTICATION_KIND = "Connection.AuthenticationKind";
    String CONNECTION_USE_LDAP_CONNECTION_POOL = "Connection.UseLdapConnectionPool";
    String CONNECTION_ENVIRONMENT_ALIASES = "Connection.Environment.java.naming.ldap.derefAliases";
    String CONNECTION_ENVIRONMENT_PROTOCOL = "Connection.Environment.java.naming.security.protocol";
    String CONNECTION_ENVIRONMENT_REFERRAL = "Connection.Environment.java.naming.referral";
    String BINDING_DEFAULT_CONTEXT = "Binding.DefaultContext";
    String BINDING_IMPORT_USERS_OF_GROUP = "Binding.ImportUsersOfGroup";
    String BINDING_USER_FILTER = "Binding.UserFilter";
    String UPDATE_ENABLED = "UpdateEnabled";
    String UPDATE_TIME = "UpdateTime";
    String IMPORT_ONDEMAND = "Import.OnDemand";
    
    String USER_ATTRIBUTE_NAME = "UserAttribute.Name";
    String USER_ATTRIBUTE_FULL_NAME = "UserAttribute.FullName";
    String USER_ATTRIBUTE_E_MAIL = "UserAttribute.EMail";
    String USER_ATTRIBUTE_LANGUAGE = "UserAttribute.Language";
    String USER_ATTRIBUTE_PROPERTIES = "UserAttribute.Properties";
    String MEMBERSHIP_USER_MEMBER_OF_ATTRIBUTE = "Membership.UserMemberOfAttribute";
    String MEMBERSHIP_USE_USER_MEMBER_OF_FOR_USER_ROLE_MEMBERSHIP = "Membership.UseUserMemberOfForUserRoleMembership";
    String MEMBERSHIP_USER_GROUP_MEMBER_OF_ATTRIBUTE = "Membership.UserGroupMemberOfAttribute";
    String MEMBERSHIP_USER_GROUP_MEMBERS_ATTRIBUTE = "Membership.UserGroupMembersAttribute";
  }
  
  public interface DefaultValue
  {
    String URL = "ldap://localhost:389";
    boolean USE_LDAP_CONNECTION_POOL = false;
    String DEREF_ALIAS = "always";
    String REFERRAL = "follow";
    String EMAIL = "mail";
    boolean UPDATE_ENABLED = true;
    String UPDATE_TIME = "00:00";
    boolean IMPORT_ONDEMAND = false;

    String USER_FILTER_ND = "objectClass=inetOrgPerson";
    String NAME_ND = "uid";
    String FULL_NAME_ND = "fullName";
    String USER_MEMBER_OF_ATTRIBUTE_ND = "groupMembership";
    boolean USE_USER_MEMBER_OF_FOR_ROLE_MEMBERSHIP_ND = false;
    String USER_GROUP_MEMBER_OF_ATTRIBUTE_ND = "groupMembership";
    String USER_GROUP_MEMBERS_ATTRIBUTE_ND = "uniqueMember";

    String USER_FILTER_AD = "(&(objectClass=user)(!(objectClass=computer)))";
    String NAME_AD = "sAMAccountName";
    String FULL_NAME_AD = "displayName";
    String USER_MEMBER_OF_ATTRIBUTE_AD = "memberOf";
    boolean USE_USER_MEMBER_OF_FOR_ROLE_MEMBERSHIP_AD = true;
    String USER_GROUP_MEMBER_OF_ATTRIBUTE_AD = "memberOf";
    String USER_GROUP_MEMBERS_ATTRIBUTE_AD = "member";
  }
  
  public static String getPrefix(String name)
  {
    return SECURITY_SYSTEMS + "." + name + "."; 
  }
  
  public static String getOrBlank(String key)
  {
    return IConfiguration.get().get(key).orElse("");
  }
  
  public static void setOrRemove(String key, Object value)
  {
    if (StringUtils.isBlank(value.toString()))
    {
      IConfiguration.get().remove(key);
      return;
    }
    IConfiguration.get().set(key, value);
  }
  
  public static void setAuthenticationKind(String name)
  {
    if (!IConfiguration.get().get(getPrefix(name) + ConfigKey.CONNECTION_USER_NAME).isPresent())
    {
      IConfiguration.get().set(getPrefix(name) + ConfigKey.CONNECTION_AUTHENTICATION_KIND, "none");
      return;
    }
    IConfiguration.get().remove(getPrefix(name) + ConfigKey.CONNECTION_AUTHENTICATION_KIND);
  }

  public static Collection<String> getSecuritySystems()
  {
    return IConfiguration.get().getNames(SecuritySystemConfig.SECURITY_SYSTEMS, "Provider");
  }
}
