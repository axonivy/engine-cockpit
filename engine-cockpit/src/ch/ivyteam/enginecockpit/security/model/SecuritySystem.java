package ch.ivyteam.enginecockpit.security.model;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import ch.ivyteam.enginecockpit.system.ManagerBean;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.identity.IdentityProvider;
import ch.ivyteam.ivy.security.restricted.ISecurityContextInternal;

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
    return identityProvider().name();
  }

  public String getIdentityProviderImage() {
    return loadResource(identityProvider().logo());
  }

  private IdentityProvider identityProvider() {
    return ISecurityContextInternal.class.cast(securityContext)
            .identityProviders()
            .get(0);
  }

  private static String loadResource(URI uri) {
    try {
      return IOUtils.toString(uri, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
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

  public boolean isJndiSecuritySystem() {
    return ManagerBean.isJndiSecuritySystem(this);
  }
}
