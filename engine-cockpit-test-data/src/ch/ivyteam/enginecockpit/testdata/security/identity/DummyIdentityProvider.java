package ch.ivyteam.enginecockpit.testdata.security.identity;

import java.net.URL;
import java.util.List;

import ch.ivyteam.ivy.extension.IIvyExtensionPointManager;
import ch.ivyteam.ivy.security.identity.core.config.IdpConfig;
import ch.ivyteam.ivy.security.identity.spi.IdentityProvider;
import ch.ivyteam.ivy.security.identity.spi.browser.DirectoryBrowser;
import ch.ivyteam.ivy.security.identity.spi.browser.DirectoryNode;
import ch.ivyteam.ivy.security.identity.spi.browser.DirectoryNodeType;
import ch.ivyteam.ivy.security.identity.spi.browser.Property;
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
    public List<? extends DirectoryNode> root() {
      return List.of(
              new DummyDirectoryNode("Group A"),
              new DummyDirectoryNode("Group B"),
              new DummyDirectoryNode("Group C")
      );
    }

    @Override
    public List<? extends DirectoryNode> children(DirectoryNode node) {
      if (node.getDisplayName().equals("Group A")) {
        return List.of(
                new DummyDirectoryNode("Group A.1"),
                new DummyDirectoryNode("Group A.2")
        );
      }
      return List.of();
    }

    @Override
    public List<Property> properties(DirectoryNode node) {
      if (node.getDisplayName().equals("Group A.1")) {
        return List.of(
                new Property("location", "Zug"),
                new Property("teamMembers", "8")
        );
      }
      return null;
    }

    @Override
    public Object selectValue(String initialValue) {
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
    public String getDisplayName() {
      return displayName;
    }

    @Override
    public boolean isExpandable() {
      return true;
    }

    @Override
    public Object getValue() {
      return null;
    }

    @Override
    public boolean startsWith(Object value) {
      return true;
    }

    @Override
    public boolean isValueEqual(Object value) {
      return false;
    }

    @Override
    public String getValueId() {
      return "Group A.1";
    }

  }
}
