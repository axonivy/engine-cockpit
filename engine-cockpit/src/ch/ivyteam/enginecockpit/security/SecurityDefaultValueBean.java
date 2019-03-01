package ch.ivyteam.enginecockpit.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.SecuritySystem;
import ch.ivyteam.enginecockpit.model.SecuritySystemDefaultValues;

@ManagedBean
@ViewScoped
public class SecurityDefaultValueBean
{
	private SpecificDefaults specificDefaults;
	
	private String url = SecuritySystemDefaultValues.URL;
	private String derefAliases = SecuritySystemDefaultValues.DEREF_ALIAS;
	private String referral = SecuritySystemDefaultValues.REFERRAL;
	private String email = SecuritySystemDefaultValues.EMAIL;
	private String updateTime = SecuritySystemDefaultValues.UPDATE_TIME;
	
	private ManagerBean managerBean;
	
	public SecurityDefaultValueBean()
	{
		FacesContext context = FacesContext.getCurrentInstance();
		managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);

		specificDefaults = new SpecificDefaults(new SecuritySystem(managerBean.getSelectedIApplication().getSecurityContext(),
				managerBean.getSelectedIApplication().getName()).getConfiguration("Provider"));
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
	
	public void switchProvider(ValueChangeEvent event)
	{
		specificDefaults = new SpecificDefaults(event.getNewValue().toString());
	}
	
	public class SpecificDefaults
	{
		private String userFilter;
		private String name;
		private String fullName;
		private String userMemberOfAttribute;
		private boolean useUserMemberOfForUserRoleMembership;
		private String userGroupMemberOfAttribute;
		private String userGroupMembersAttribute;
		
		public SpecificDefaults(String provider) 
		{
			if (provider.equals("Novell eDirectory"))
			{
				initNovellValues();
			} 
			else if (provider.equals("Microsoft Active Directory"))
			{
				initActiveDirectoryValues();
			}
		}
		
		public String getUserFilter()
		{
			return userFilter;
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
			this.userFilter = SecuritySystemDefaultValues.USER_FILTER_ND;
			this.name = SecuritySystemDefaultValues.NAME_ND;
			this.fullName = SecuritySystemDefaultValues.FULL_NAME_ND;
			this.userMemberOfAttribute = SecuritySystemDefaultValues.USER_MEMBER_OF_ATTRIBUTE_ND;
			this.useUserMemberOfForUserRoleMembership = SecuritySystemDefaultValues.USE_USER_MEMBER_OF_FOR_ROLE_MEMBERSHIP_ND;
			this.userGroupMemberOfAttribute = SecuritySystemDefaultValues.USER_GROUP_MEMBER_OF_ATTRIBUTE_ND;
			this.userGroupMembersAttribute = SecuritySystemDefaultValues.USER_GROUP_MEMBERS_ATTRIBUTE_ND;
		}
		
		private void initActiveDirectoryValues()
		{
			this.userFilter = SecuritySystemDefaultValues.USER_FILTER_AD;
			this.name = SecuritySystemDefaultValues.NAME_AD;
			this.fullName = SecuritySystemDefaultValues.FULL_NAME_AD;
			this.userMemberOfAttribute = SecuritySystemDefaultValues.USER_MEMBER_OF_ATTRIBUTE_AD;
			this.useUserMemberOfForUserRoleMembership = SecuritySystemDefaultValues.USE_USER_MEMBER_OF_FOR_ROLE_MEMBERSHIP_AD;
			this.userGroupMemberOfAttribute = SecuritySystemDefaultValues.USER_GROUP_MEMBER_OF_ATTRIBUTE_AD;
			this.userGroupMembersAttribute = SecuritySystemDefaultValues.USER_GROUP_MEMBERS_ATTRIBUTE_AD;
		}

	}
}
