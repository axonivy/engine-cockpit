package ch.ivyteam.enginecockpit.security.model;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;

import ch.ivyteam.enginecockpit.application.model.App;
import ch.ivyteam.enginecockpit.application.model.Application;
import ch.ivyteam.ivy.application.app.IApplicationRepository;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.identity.spi.IdentityProvider;
import ch.ivyteam.ivy.security.restricted.ISecurityContextInternal;

public class SecuritySystem {

  private final ISecurityContext securityContext;
  private long usersCount = -1;
  private int rolesCount = -1;
  private List<App> apps;

  public SecuritySystem(ISecurityContext securityContext) {
    this.securityContext = securityContext;
  }

  public String getSecuritySystemProvider() {
    return identityProvider().name();
  }

  public String getSecuritySystemProviderId() {
    return identityProvider().id();
  }

  public String getIdentityProviderImage() {
    return loadResource(identityProvider().logo());
  }

  private IdentityProvider identityProvider() {
    return ISecurityContextInternal.class.cast(securityContext).identityProvider();
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

  public List<App> getApps() {
    if (apps == null) {
      apps = IApplicationRepository.of(securityContext).all().stream()
          .map(app -> new App(app.getName(), app.getVersion()))
          .collect(Collectors.toList());
    }
    return apps;
  }

  public String getApplicationDetailLink(App app) {
    return Application.getDetailViewLink(app.name(), app.version());
  }

  public long getUsersCount() {
    if (usersCount == -1) {
      usersCount = securityContext.users().count();
    }
    return usersCount;
  }

  public int getRolesCount() {
    if (rolesCount == -1) {
      rolesCount = securityContext.roles().count();
    }
    return rolesCount;
  }

  public boolean getDeletable() {
    if (ISecurityContext.DEFAULT.equals(getSecuritySystemName())) {
      return false;
    }
    return getApps().isEmpty();
  }

  public String getLink() {
    return link(securityContext);
  }

  public ISecurityContext getSecurityContext() {
    return securityContext;
  }

  public boolean isIvySecuritySystem() {
    return isIvySecuritySystem(securityContext);
  }

  public static String link(ISecurityContext securityContext) {
    return UriBuilder.fromPath("security-detail.xhtml")
        .queryParam("securitySystemName", securityContext.getName())
        .build()
        .toString();
  }

  public static boolean isIvySecuritySystem(ISecurityContext securityContext) {
    return !((ISecurityContextInternal) securityContext).managed();
  }
}
