package ch.ivyteam.enginecockpit.testdata.security.identity;

import java.net.URL;
import java.util.List;

import ch.ivyteam.ivy.configuration.configurator.Configurator;
import ch.ivyteam.ivy.extension.IIvyExtensionPointManager;
import ch.ivyteam.ivy.security.identity.core.config.IdpConfig;
import ch.ivyteam.ivy.security.identity.spi.IdentityProvider;
import ch.ivyteam.ivy.security.identity.spi.browser.DirectoryBrowser;
import ch.ivyteam.ivy.security.identity.spi.browser.DirectoryNode;
import ch.ivyteam.ivy.security.identity.spi.browser.DirectoryNodeType;
import ch.ivyteam.ivy.security.identity.spi.browser.Property;

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

  @Override
  public DirectoryBrowser directoryBrowser(IdpConfig config) {
    return new DummyDirectoryBrowser();
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

  private static final class DummyDirectoryBrowser implements DirectoryBrowser {

    @Override
    public List<DirectoryNode> root() {
      return List.of(
          new DummyDirectoryNode("Group A"),
          new DummyDirectoryNode("Group B"),
          new DummyDirectoryNode("Group C"));
    }

    @Override
    public List<DirectoryNode> children(DirectoryNode node) {
      if ("Group A".equals(node.displayName())) {
        return List.of(
            new DummyDirectoryNode("Group A.1"),
            new DummyDirectoryNode("Group A.2"));
      }
      return List.of();
    }

    @Override
    public List<Property> properties(DirectoryNode node) {
      if ("Group A.1".equals(node.displayName())) {
        return List.of(
            new Property("location", "Zug"),
            new Property("teamMembers", "8"));
      }
      return null;
    }

    @Override
    public DirectoryNode find(String node) {
      return null;
    }
  }

  private static final class DummyDirectoryNode implements DirectoryNode {

    private final String displayName;

    public DummyDirectoryNode(String displayName) {
      this.displayName = displayName;
    }

    @Override
    public DirectoryNodeType type() {
      return DirectoryNodeType.DEFAULT;
    }

    @Override
    public String displayName() {
      return displayName;
    }

    @Override
    public boolean expandable() {
      return true;
    }

    @Override
    public String id() {
      return "Group A.1";
    }
  }
}
