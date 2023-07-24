package ch.ivyteam.enginecockpit.testdata.security.identity;

import java.net.URL;

import ch.ivyteam.ivy.extension.IIvyExtensionPointManager;
import ch.ivyteam.ivy.security.identity.spi.IdentityProvider;
import ch.ivyteam.ivy.security.identity.spi.config.Configurator;

public class DummyIdentityProvider implements IdentityProvider {

  @Override
  public String id() {
    return "dummy-identity-provider";
  }

  @Override
  public String name() {
    return "Dummy Identity Provider";
  }

  @Override
  public Configurator configurator() {
    return new DummyIdentityProviderConfiguration();
  }

  public static void register() {
    IIvyExtensionPointManager.instance().addExtension(IdentityProvider.class, new DummyIdentityProvider());
  }

  private static final class DummyIdentityProviderConfiguration implements Configurator {

    @Override
    public URL configYaml() {
      return DummyIdentityProviderConfiguration.class.getResource("dummy-identity-provider.yaml");
    }
  }
}
