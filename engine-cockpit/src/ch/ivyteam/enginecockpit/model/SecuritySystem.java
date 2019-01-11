package ch.ivyteam.enginecockpit.model;

import ch.ivyteam.ivy.security.ISecurityContext;

public class SecuritySystem {

	private String securitySystemProvider;
	private String securitySystemName;
	private long id;

	public SecuritySystem(ISecurityContext securityContext) {
		securitySystemName = securityContext.getExternalSecuritySystemName();
		securitySystemProvider = securityContext.getExternalSecuritySystemProvider().getProviderName();
		id = securityContext.getId();
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
	
}
