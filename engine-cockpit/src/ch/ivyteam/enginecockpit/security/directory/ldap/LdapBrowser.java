package ch.ivyteam.enginecockpit.security.directory.ldap;

import java.util.List;

import javax.naming.Name;
import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.directory.DirectoryBrowser;
import ch.ivyteam.enginecockpit.security.directory.DirectoryNode;
import ch.ivyteam.enginecockpit.security.directory.Property;
import ch.ivyteam.naming.JndiConfig;

public class LdapBrowser implements DirectoryBrowser {

  private JndiConfig jndiConfig;
  private boolean insecureSsl;

  public LdapBrowser(JndiConfig jndiConfig, boolean insecureSsl) {
    this.jndiConfig = jndiConfig;
    this.insecureSsl = insecureSsl;
  }

  @Override
  public List<? extends DirectoryNode> root() {
    try (var context = new LdapBrowserContext(jndiConfig, insecureSsl)) {
      var name = jndiConfig.getDefaultContextName();
      if (name.isEmpty()) {
        return context.browse(name);
      }
      var displayName = context.toDisplayName(name);
      LdapBrowserNode newNode = context.createLdapNode(displayName, name);
      return List.of(newNode);
    } catch (NamingException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static Name parseInitialName(LdapBrowserContext context, String initialValue) throws NamingException {
    if (StringUtils.isBlank(initialValue)) {
      return null;
    }
    return context.parse(initialValue);
  }

  @Override
  public List<? extends DirectoryNode> children(DirectoryNode node) {
    try (var context = new LdapBrowserContext(jndiConfig, insecureSsl)) {
      var name = parseInitialName(context, node.getId());
      return context.children(name);
    } catch (NamingException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public List<Property> getNodeAttributes(DirectoryNode node) {
      try (var context = new LdapBrowserContext(jndiConfig, insecureSsl)) {
    	var name = parseInitialName(context, node.getId());
        return context.getAttributes(name);
      } catch (NamingException ex) {
        throw new RuntimeException(ex);
      }
  }
}
