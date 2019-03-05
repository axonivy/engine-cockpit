package ch.ivyteam.enginecockpit.model;

public class SecuritySystemDefaultValues {

    public static final String URL = "ldap://localhost:389";
	public static final String DEREF_ALIAS = "always";
	public static final String REFERRAL = "follow";
	public static final String EMAIL = "mail";
	public static final String UPDATE_TIME = "00:00";
	
	public static final String USER_FILTER_ND = "objectClass=inetOrgPerson";
	public static final String NAME_ND = "uid";
	public static final String FULL_NAME_ND = "fullName";
	public static final String USER_MEMBER_OF_ATTRIBUTE_ND = "groupMembership";
	public static final boolean USE_USER_MEMBER_OF_FOR_ROLE_MEMBERSHIP_ND = false;
	public static final String USER_GROUP_MEMBER_OF_ATTRIBUTE_ND = "groupMembership";
	public static final String USER_GROUP_MEMBERS_ATTRIBUTE_ND = "uniqueMember";
	
	public static final String USER_FILTER_AD = "(&(objectClass=user)(!(objectClass=computer)))";
	public static final String NAME_AD = "sAMAccountName";
	public static final String FULL_NAME_AD = "displayName";
	public static final String USER_MEMBER_OF_ATTRIBUTE_AD = "memberOf";
	public static final boolean USE_USER_MEMBER_OF_FOR_ROLE_MEMBERSHIP_AD = true;
	public static final String USER_GROUP_MEMBER_OF_ATTRIBUTE_AD = "memberOf";
	public static final String USER_GROUP_MEMBERS_ATTRIBUTE_AD = "member";
	
}
