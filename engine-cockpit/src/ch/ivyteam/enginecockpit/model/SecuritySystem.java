package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityContext;

@SuppressWarnings("restriction")
public class SecuritySystem {

	private String securitySystemProvider;
	private String securitySystemName;
	private long id;
	private String appName;

	public SecuritySystem(ISecurityContext securityContext, String appName) {
		securitySystemName = IConfiguration.get().get("Applications." + appName + ".SecuritySystem").orElse(securityContext.getExternalSecuritySystemName());
		securitySystemProvider = securityContext.getExternalSecuritySystemProvider().getProviderName();
		id = securityContext.getId();
		this.appName = appName;
	}

	public String getSecuritySystemProvider() {
		return securitySystemProvider;
	}

	public void setSecuritySystemProvider(String securitySystemProvider) {
		this.securitySystemProvider = securitySystemProvider;
	}

	public String getSecuritySystemName() {
		return securitySystemName;
	}

	public void setSecuritySystemName(String securitySystemName) {
		this.securitySystemName = securitySystemName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getAppName() {
		return appName;
	}
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
}
