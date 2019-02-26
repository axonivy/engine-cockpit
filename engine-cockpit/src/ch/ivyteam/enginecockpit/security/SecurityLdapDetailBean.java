package ch.ivyteam.enginecockpit.security;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.SecuritySystem;

@ManagedBean
@ViewScoped
public class SecurityLdapDetailBean
{
	private ManagerBean managerBean;
	private SecuritySystem system;
	
	private String userName;
	private String fullName;
	private String email;
	private String language;
	private String userMemberOfAttribute;
	private boolean useUserMemberOfForUserRoleMembership;
	private String userGroupMemberOfAttribute;
	private String userGroupMembersAttribute;

	public SecurityLdapDetailBean() 
	{
		FacesContext context = FacesContext.getCurrentInstance();
		managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);

		system = new SecuritySystem(managerBean.getSelectedIApplication().getSecurityContext(),
				managerBean.getSelectedIApplication().getName());
		
		userName = system.getConfiguration("UserAttribute.Name");
		fullName = system.getConfiguration("UserAttribute.FullName");
		email = system.getConfiguration("UserAttribute.EMail");
		language = system.getConfiguration("UserAttribute.Language");
		userMemberOfAttribute = system.getConfiguration("Membership.UserMemberOfAttribute");
		useUserMemberOfForUserRoleMembership = Boolean.valueOf(system.getConfiguration("Membership.UseUserMemberOfForUserRoleMembership"));
		userGroupMemberOfAttribute = system.getConfiguration("Membership.UserGroupMemberOfAttribute");
		userGroupMembersAttribute = system.getConfiguration("Membership.UserGroupMembersAttribute");
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
}
