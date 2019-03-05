package ch.ivyteam.enginecockpit.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.CellEditEvent;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.LdapProperty;
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
	private List<LdapProperty> properties;

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
		
		properties = new ArrayList<>();
		Map<String, String> yamlProperties = system.getConfigurationMap("UserAttribute.Properties");
		for (String key : yamlProperties.keySet())
		{
			properties.add(new LdapProperty(key, yamlProperties.get(key)));
		}
		properties.add(new LdapProperty());
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
	
	public List<LdapProperty> getProperties()
	{
		return properties;
	}	
	
	public void setProperties(List<LdapProperty> properties)
	{
		this.properties = properties;
	}
	
    public void onCellEdit(CellEditEvent event)
    {
    	if (properties.stream().noneMatch(property -> !property.isComplete()))
    	{
    		properties.add(new LdapProperty());
    	}
    }
    
	public void saveConfiguration()
	{
		system.setConfiguration("UserAttribute.Name", this.userName);
		system.setConfiguration("UserAttribute.FullName", this.fullName);
		system.setConfiguration("UserAttribute.EMail", this.email);
		system.setConfiguration("UserAttribute.Language", this.language);
		system.setConfiguration("Membership.UserMemberOfAttribute", this.userMemberOfAttribute);
		system.setConfiguration("Membership.UseUserMemberOfForUserRoleMembership", String.valueOf(this.useUserMemberOfForUserRoleMembership));
		system.setConfiguration("Membership.UserGroupMemberOfAttribute", this.userGroupMemberOfAttribute);
		system.setConfiguration("Membership.UserGroupMembersAttribute", this.userGroupMembersAttribute);
		
		system.cleanLdapPropertiesMapping();
		properties.stream()
		  .filter(prop -> StringUtils.isNotBlank(prop.getName()))
		  .forEach(prop -> system.setConfiguration("UserAttribute.Properties." + prop.getName(), prop.getLdapAttribute()));
		
	    FacesContext.getCurrentInstance().addMessage("securitySystemConfigSaveSuccess",
	            new FacesMessage("Security System LDAP Attributes saved"));
	}
}
