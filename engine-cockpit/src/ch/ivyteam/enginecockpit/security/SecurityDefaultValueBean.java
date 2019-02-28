package ch.ivyteam.enginecockpit.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.SecuritySystem;

@ManagedBean
@ViewScoped
public class SecurityDefaultValueBean
{
	private SpecificDefaults specificDefaults;
	
	private String url = "ldap://localhost:389";
	private String derefAliases = "always";
	private String referral = "follow";
	private String email = "email";
	private String userMemberOfAttribute = "memberOf";
	private boolean useUserMemberOfForUserRoleMembership = true;
	private String userGroupMemberOfAttribute = "memberOf";
	private String userGroupMembersAttribute = "member";
	private String updateTime = "00:00";
	
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

	public String getUsermemberofattribute()
	{
		return userMemberOfAttribute;
	}

	public boolean getUseusermemberofforuserrolemembership()
	{
		return useUserMemberOfForUserRoleMembership;
	}

	public String getUsergroupmemberofattribute()
	{
		return userGroupMemberOfAttribute;
	}

	public String getUsergroupmembersattribute()
	{
		return userGroupMembersAttribute;
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
			this.userFilter = "objectClass=inetOrgPerson";
			this.name = "uid";
			this.fullName = "fullName";
			this.userMemberOfAttribute = "groupMembership";
			this.useUserMemberOfForUserRoleMembership = false;
			this.userGroupMemberOfAttribute = "groupMembership";
			this.userGroupMembersAttribute = "uniqueMember";
		}
		
		private void initActiveDirectoryValues()
		{
			this.userFilter = "(&(objectClass=user)(!(objectClass=computer)))";
			this.name = "sAMAccountName";
			this.fullName = "displayName";
			this.userMemberOfAttribute = "memberOf";
			this.useUserMemberOfForUserRoleMembership = true;
			this.userGroupMemberOfAttribute = "memberOf";
			this.userGroupMembersAttribute = "member";
		}

	}
}
