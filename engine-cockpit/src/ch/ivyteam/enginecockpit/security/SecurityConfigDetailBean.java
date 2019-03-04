package ch.ivyteam.enginecockpit.security;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

import org.apache.commons.codec.binary.StringUtils;

import ch.ivyteam.enginecockpit.ManagerBean;
import ch.ivyteam.enginecockpit.model.SecuritySystem;

@ManagedBean
@ViewScoped
public class SecurityConfigDetailBean
{
	private String name;
	private ManagerBean managerBean;
	private SecuritySystem system;
	private List<String> usedByApps;

	private List<String> providers;
	private List<String> derefAliases;
	private List<String> protocols;
	private List<String> referrals;
	
	private String provider;
	private String url;
	private String userName;
	private String password;
	private boolean useLdapConnectionPool;
	private String derefAlias;
	private boolean ssl;
	private String referral;
	private String defaultContext;
	private String importUsersOfGroup;
	private String userFilter;
	private String updateTime;

	public SecurityConfigDetailBean() 
	{
		FacesContext context = FacesContext.getCurrentInstance();
		managerBean = context.getApplication().evaluateExpressionGet(context, "#{managerBean}", ManagerBean.class);

		system = new SecuritySystem(managerBean.getSelectedIApplication().getSecurityContext(),
				managerBean.getSelectedIApplication().getName());
		
		providers = Arrays.asList("Microsoft Active Directory", "Novell eDirectory", "ivy Security System");
		derefAliases = Arrays.asList("", "never", "finding", "searching");
		protocols = Arrays.asList("", "ssl");
		referrals = Arrays.asList("", "ignore", "throw");
	}

	public void init(ComponentSystemEvent event)
	{
		String securityName = event.getComponent().getAttributes().get("securityName").toString();
		system.setSecuritySystemName(securityName);

		provider = system.getConfiguration("Provider");
		url = system.getConfiguration("Connection.Url");
		userName = system.getConfiguration("Connection.UserName");
		password = system.getConfiguration("Connection.Password");
		useLdapConnectionPool = Boolean.valueOf(system.getConfiguration("Connection.UseLdapConnectionPool"));
		derefAlias = system.getConfiguration("Connection.Environment.java.naming.ldap.derefAliases");
		ssl = system.getConfiguration("Connection.Environment.java.naming.security.protocol").equalsIgnoreCase("ssl");
		referral = system.getConfiguration("Connection.Environment.java.naming.referral");
		defaultContext = system.getConfiguration("Binding.DefaultContext");
		importUsersOfGroup = system.getConfiguration("Binding.ImportUsersOfGroup");
		userFilter = system.getConfiguration("Binding.UserFilter");
		updateTime = system.getConfiguration("UpdateTime");
		initUsedBy(securityName);
	}
	
	private void initUsedBy(String securityName)
	{
		usedByApps = managerBean.getApplications().stream()
				.filter(app -> StringUtils.equals(app.getSecuritySystemName(), securityName))
				.map(app -> app.getName())
				.collect(Collectors.toList());
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public List<String> getUsedByApps()
	{
		return usedByApps;
	}

	public String getProvider()
	{
		return provider;
	}
	
	public void setProvider(String provider)
	{
		this.provider = provider;
	}
	
	public String getUrl()
	{
		return url;
	}
	
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean getUseLdapConnectionPool()
	{
		return useLdapConnectionPool;
	}

	public void setUseLdapConnectionPool(boolean useLdapConnectionPool)
	{
		this.useLdapConnectionPool = useLdapConnectionPool;
	}
	
	public String getDerefAlias()
	{
		return derefAlias;
	}
	
	public void setDerefAlias(String derefAlias)
	{
		this.derefAlias = derefAlias;
	}
	
	public boolean getSsl()
	{
		return ssl;
	}
	
	public void setSsl(boolean ssl)
	{
		this.ssl = ssl;
	}
	
	public String getReferral()
	{
		return referral;
	}
	
	public void setReferral(String referral)
	{
		this.referral = referral;
	}
	
	public String getDefaultContext()
	{
		return defaultContext;
	}
	
	public void setDefaultContext(String defaultContext)
	{
		this.defaultContext = defaultContext;
	}
	
	public String getImportUsersOfGroup()
	{
		return importUsersOfGroup;
	}
	
	public void setImportUsersOfGroup(String importUsersOfGroup)
	{
		this.importUsersOfGroup = importUsersOfGroup;
	}
	
	public String getUserFilter()
	{
		return userFilter;
	}
	
	public void setUserFilter(String userFilter)
	{
		this.userFilter = userFilter;
	}
	
	public String getUpdateTime()
	{
		return updateTime;
	}
	
	public void setUpdateTime(String updateTime)
	{
		this.updateTime = updateTime;
	}
	
	public List<String> getProviders()
	{
		return providers;
	}
	
	public List<String> getDerefAliases()
	{
		return derefAliases;
	}
	
	public List<String> getReferrals()
	{
		return referrals;
	}
	
	public List<String> getProtocols()
	{
		return protocols;
	}
	
	public void saveConfiguration()
	{
		system.setConfiguration("Provider", this.provider);
		system.setConfiguration("Connection.Url", this.url);
		system.setConfiguration("Connection.UserName", this.userName);
		system.setConfiguration("Connection.Password", "${encrypt:" + this.password + "}");
		system.setConfiguration("Connection.UseLdapConnectionPool", String.valueOf(this.useLdapConnectionPool));
		system.setConfiguration("Connection.Environment.java.naming.ldap.derefAliases", this.derefAlias);
		system.setConfiguration("Connection.Environment.java.naming.security.protocol", this.ssl ? "ssl" : "");
		system.setConfiguration("Connection.Environment.java.naming.referral", this.referral);
		system.setConfiguration("Binding.DefaultContext", this.defaultContext);
		system.setConfiguration("Binding.ImportUsersOfGroup", this.importUsersOfGroup);
		system.setConfiguration("Binding.UserFilter", this.userFilter);
		system.setConfiguration("UpdateTime", this.updateTime);
		saveAuthenticationKind();
	    FacesContext.getCurrentInstance().addMessage("securitySystemConfigSaveSuccess",
	            new FacesMessage("Security System configuration saved"));
	}
	
	private void saveAuthenticationKind()
	{
		system.setAuthenticationKind();
	}
}
