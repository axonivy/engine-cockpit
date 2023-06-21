package ch.ivyteam.enginecockpit.security.system;

import java.util.Map;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityConstants;
import ch.ivyteam.ivy.security.identity.core.config.IdpKey;
import ch.ivyteam.ivy.security.identity.jndi.ads.MicrosoftActiveDirectoryDefaults;
import ch.ivyteam.ivy.security.identity.jndi.nds.NovellEDirectoryDefaults;
import ch.ivyteam.ivy.security.identity.jndi.ads.MicrosoftActiveDirectoryIdentityProvider;
import ch.ivyteam.ivy.security.identity.jndi.nds.NovellEDirectoryIdentityProvider;

/**
 * legacy layer. will be removed as soon as we are finished.
 */
@SuppressWarnings("restriction")
public class ExternalSecuritySystemConfiguration {

  private final String prefix;

  public ExternalSecuritySystemConfiguration(String securitySystemName) {
    this.prefix = "SecuritySystems." + securitySystemName + "." + "IdentityProvider";
  }

  public boolean getDefaultBooleanValue(String key) {
    var s = getDefaultValue(key);
    return Boolean.parseBoolean(s);
  }

  public String getDefaultValue(String key) {
    return props(getProviderName()).get(key);
  }

  public static Map<String, String> props(String id) {
    if (id == null) {
      return Map.of();
    }
    switch (id) {
      case MicrosoftActiveDirectoryIdentityProvider.ID:
        return MicrosoftActiveDirectoryDefaults.PROPERTIES;
      case NovellEDirectoryIdentityProvider.ID:
        return NovellEDirectoryDefaults.PROPERTIES;
      default:
        return Map.of();
    }
  }

  public String getProviderName() {
    if (prefix == null) {
      return ISecurityConstants.IVY_ENGINE_SECURITY_SYSTEM_PROVIDER_NAME;
    }
    return IConfiguration.instance().get(prefix + "."+ IdpKey.IDP_NAME).orElse(prefix);
  }
}
