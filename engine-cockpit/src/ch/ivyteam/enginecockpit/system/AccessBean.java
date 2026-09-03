package ch.ivyteam.enginecockpit.system;

import java.io.Serializable;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@Named
@ApplicationScoped
public class AccessBean implements Serializable {

  private static final String PAAS_CONFIG_KEY = "Cockpit.PaaS";
  private static final Set<String> PAAS_RESTRICTED_AREAS = Set.of(
      "system", "securitySystems", "setup", "licence", "systemDatabase", "logging");

  public boolean hasAccess(String area) {
    return !isPaaS() || !PAAS_RESTRICTED_AREAS.contains(area);
  }

  protected boolean isPaaS() {
    return IConfiguration.instance().getOrDefault(PAAS_CONFIG_KEY, boolean.class);
  }
}
