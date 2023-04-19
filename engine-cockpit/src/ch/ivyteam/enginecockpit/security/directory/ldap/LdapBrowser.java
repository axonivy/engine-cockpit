package ch.ivyteam.enginecockpit.security.directory.ldap;

import java.util.Collections;
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
  public List<? extends DirectoryNode> select(Object initialValue) {
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

  @Override
  public Object selectValue(String initialValue) {
    try (var context = new LdapBrowserContext(jndiConfig, insecureSsl)) {
      return parseInitialName(context, initialValue);
    } catch (NamingException ex) {
      throw new RuntimeException(ex);
    }
  }

  private static Name parseInitialName(LdapBrowserContext context, String initialValue)
          throws NamingException {
    if (StringUtils.isBlank(initialValue)) {
      return null;
    }
    return context.parse(initialValue);
  }

  @Override
  public List<? extends DirectoryNode> loadChildren(DirectoryNode node, Object initialValue) {
    if (node.getValue() instanceof Name name && (initialValue == null || initialValue instanceof Name init)) {
      try (var context = new LdapBrowserContext(jndiConfig, insecureSsl)) {
        return context.children(name);
      } catch (NamingException ex) {
        throw new RuntimeException(ex);
      }
    }
    return List.of();
  }

  @Override
  public List<Property> getNodeAttributes(DirectoryNode node) {
    if (node.getValue() instanceof Name name) {
      try (var context = new LdapBrowserContext(jndiConfig, insecureSsl)) {
        return context.getAttributes(name);
      } catch (NamingException ex) {
        throw new RuntimeException(ex);
      }
    }
    return Collections.emptyList();
  }
}
