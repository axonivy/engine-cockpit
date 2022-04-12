package ch.ivyteam.enginecockpit.security.model;

import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationConfigurationManager;
import ch.ivyteam.ivy.security.ISecurityConstants;
import ch.ivyteam.ivy.security.ISecurityContext;

public class SecuritySystem {

  private ISecurityContext securityContext;
  private long usersCount;
  private int rolesCount;

  public SecuritySystem(ISecurityContext securityContext) {
    this.securityContext = securityContext;
    this.usersCount = securityContext.users().count();
    this.rolesCount = securityContext.roles().all().size();
  }

  public String getSecuritySystemProvider() {
    return securityContext.getExternalSecuritySystemName();
  }

  public String getSecuritySystemName() {
    return securityContext.getName();
  }

  public long getId() {
    return securityContext.getId();
  }

  public List<String> getAppNames() {
    return IApplicationConfigurationManager.all(securityContext).stream()
            .map(IApplication::getName)
            .collect(Collectors.toList());
  }

  public long getUsersCount() {
    return usersCount;
  }

  public int getRolesCount() {
    return rolesCount;
  }

  public boolean getDeletable() {
    if (ISecurityConstants.SECURITY_CONTEXT_DEFAULT.equals(getSecuritySystemName())) {
      return false;
    }
    return getAppNames().isEmpty();
  }

  public String getLink() {
    return "security-detail.xhtml?securitySystemName=" + securityContext.getName();
  }

  public ISecurityContext getSecurityContext() {
    return securityContext;
  }
}
