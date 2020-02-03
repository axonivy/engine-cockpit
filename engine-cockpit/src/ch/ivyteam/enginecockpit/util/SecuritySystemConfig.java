package ch.ivyteam.enginecockpit.util;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityConstants;
import ch.ivyteam.ivy.security.jndi.IJndiSecuritySystemConstants;

@SuppressWarnings("restriction")
public class SecuritySystemConfig
{

  public static final String SECURITY_SYSTEMS = "SecuritySystems";
  public static final String SECURITY_STSTEM = "SecuritySystem";
  public static final String IVY_SECURITY_SYSTEM = "ivy Security System";

  public interface ConfigKey
  {
    String PROVIDER = ISecurityConstants.PROVIDER_CONFIG_KEY;
    String CONNECTION_URL = IJndiSecuritySystemConstants.CONNECTION_URL;
    String CONNECTION_USER_NAME = IJndiSecuritySystemConstants.CONNECTION_USER_NAME;
    String CONNECTION_PASSWORD = IJndiSecuritySystemConstants.CONNECTION_PASSWORD;
    String CONNECTION_AUTHENTICATION_KIND = IJndiSecuritySystemConstants.CONNECTION_AUTHENTICATION_KIND;
    String CONNECTION_USE_LDAP_CONNECTION_POOL = IJndiSecuritySystemConstants.CONNECTION_USE_LDAP_CONNECTION_POOL;
    String CONNECTION_ENVIRONMENT_ALIASES = IJndiSecuritySystemConstants.CONNECTION_ENVIRONMENT_DEREF_ALIASES;
    String CONNECTION_ENVIRONMENT_PROTOCOL = IJndiSecuritySystemConstants.CONNECTION_ENVIRONMENT_SECURITY_PROTOCOL;
    String CONNECTION_ENVIRONMENT_REFERRAL = IJndiSecuritySystemConstants.CONNECTION_ENVIRONMENT_REFFERAL;
    String BINDING_DEFAULT_CONTEXT = IJndiSecuritySystemConstants.BINDING_DEFAULT_CONTEXT;
    String BINDING_IMPORT_USERS_OF_GROUP = IJndiSecuritySystemConstants.BINDING_IMPORT_USERS_OF_GROUP;
    String BINDING_USER_FILTER = IJndiSecuritySystemConstants.BINDING_USER_FILTER;
    String UPDATE_TIME = IJndiSecuritySystemConstants.UPDATE_TIME;
    String IMPORT_ONDEMAND = IJndiSecuritySystemConstants.IMPORT_ON_DEMAND;
    
    String USER_ATTRIBUTE_ID = IJndiSecuritySystemConstants.USER_ATTRIBUTE_ID;
    String USER_ATTRIBUTE_NAME = IJndiSecuritySystemConstants.USER_ATTRIBUTE_NAME;
    String USER_ATTRIBUTE_FULL_NAME = IJndiSecuritySystemConstants.USER_ATTRIBUTE_FULL_NAME;
    String USER_ATTRIBUTE_E_MAIL = IJndiSecuritySystemConstants.USER_ATTRIBUTE_EMAIL;
    String USER_ATTRIBUTE_LANGUAGE = IJndiSecuritySystemConstants.USER_ATTRIBUTE_LANGUAGE;
    String USER_ATTRIBUTE_PROPERTIES = StringUtils.removeEnd(IJndiSecuritySystemConstants.USER_ATTRIBUTE_PROPERTIES_PREFIX, ".");
    String MEMBERSHIP_USER_MEMBER_OF_ATTRIBUTE = IJndiSecuritySystemConstants.MEMBERSHIP_USER_MEMBER_OF_GROUP;
    String MEMBERSHIP_USE_USER_MEMBER_OF_FOR_USER_ROLE_MEMBERSHIP =IJndiSecuritySystemConstants.MEMBERSHIP_USE_USER_MEMBER_OF_FOR_USER_ROLE_MEMBERSHIP;
    String MEMBERSHIP_USER_GROUP_MEMBER_OF_ATTRIBUTE = IJndiSecuritySystemConstants.MEMBERSHIP_USER_GROUP_MEMBER_OF_GROUP;
    String MEMBERSHIP_USER_GROUP_MEMBERS_ATTRIBUTE = IJndiSecuritySystemConstants.MEMBERSHIP_USER_GROUP_MEMBERS;
  }
  
  public interface DefaultValue
  {
    String URL = "ldap://localhost:389";
    boolean USE_LDAP_CONNECTION_POOL = false;
    String DEREF_ALIAS = "always";
    String REFERRAL = "follow";
    String EMAIL = "mail";
    String UPDATE_TIME = "00:00";
    boolean IMPORT_ONDEMAND = false;

    String USER_FILTER_ND = "objectClass=inetOrgPerson";
    String ID_ND = "guid";
    String NAME_ND = "uid";
    String FULL_NAME_ND = "fullName";
    String USER_MEMBER_OF_ATTRIBUTE_ND = "groupMembership";
    boolean USE_USER_MEMBER_OF_FOR_ROLE_MEMBERSHIP_ND = false;
    String USER_GROUP_MEMBER_OF_ATTRIBUTE_ND = "groupMembership";
    String USER_GROUP_MEMBERS_ATTRIBUTE_ND = "uniqueMember";

    String USER_FILTER_AD = "(&(objectClass=user)(!(objectClass=computer)))";
    String ID_AD = "objectGUID";
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
