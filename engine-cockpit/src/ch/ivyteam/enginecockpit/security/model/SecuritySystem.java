package ch.ivyteam.enginecockpit.security.model;

import java.util.List;
import java.util.stream.Collectors;

import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.IApplicationRepository;
import ch.ivyteam.ivy.security.ISecurityContext;

public class SecuritySystem {

  private final ISecurityContext securityContext;
  private final long usersCount;
  private final int rolesCount;
  private final List<String> appNames;

  public SecuritySystem(ISecurityContext securityContext) {
    this.securityContext = securityContext;
    this.usersCount = securityContext.users().count();
    this.rolesCount = securityContext.roles().all().size();
    this.appNames = IApplicationRepository.instance().allOf(securityContext).stream()
            .map(IApplication::getName)
            .collect(Collectors.toList());
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
    return appNames;
  }

  public long getUsersCount() {
    return usersCount;
  }

  public int getRolesCount() {
    return rolesCount;
  }

  public boolean getDeletable() {
    if (ISecurityContext.DEFAULT.equals(getSecuritySystemName())) {
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

  public boolean isIvySecuritySystem() {
    return ManagerBean.isIvySecuritySystem(this);
  }
}
