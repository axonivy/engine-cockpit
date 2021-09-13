package ch.ivyteam.enginecockpit.security.ldapbrowser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;

import org.apache.commons.codec.binary.Hex;

import ch.ivyteam.enginecockpit.commons.Property;
import ch.ivyteam.ivy.security.jndi.JndiContextUtil;
import ch.ivyteam.naming.JndiConfig;
import ch.ivyteam.naming.JndiUtil;

public class LdapBrowserContext implements AutoCloseable {

  private LdapContext context;

  public LdapBrowserContext(JndiConfig config, boolean enableInsecureSsl) throws NamingException {
    context = JndiContextUtil.openLdapContext(config, enableInsecureSsl);
  }

  @Override
  public void close() throws NamingException {
    JndiUtil.closeQuietly(context);
  }

  public List<LdapBrowserNode> browse(Name defaultContext) throws NamingException {
    var names = new ArrayList<LdapBrowserNode>();
    var attribute = context.getAttributes(defaultContext).get("namingContexts");
    for (int pos = 0; pos < attribute.size(); pos++) {
      var name = context.getNameParser(attribute.get(pos).toString()).parse(attribute.get(pos).toString());
      names.add(createLdapNode(name, defaultContext.toString()));
    }
    return names.stream()
            .sorted(Comparator.comparing(LdapBrowserNode::toString, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
  }

  public List<LdapBrowserNode> children(String parent) throws NamingException {
    var children = new ArrayList<LdapBrowserNode>();
    var list = context.list(parent);
    while (list.hasMoreElements()) {
      var nextElement = list.nextElement();
      var child = context.getNameParser(nextElement.getName()).parse(nextElement.getName());
      children.add(createLdapNode(child, parent));
    }

    return children.stream()
            .sorted(Comparator.comparing(LdapBrowserNode::toString, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
  }

  public List<Property> getAttributes(String node) throws NamingException {
    var attributeList = new ArrayList<Property>();
    var attrs = context.getAttributes(new LdapName(node)).getAll();
    while (attrs.hasMore()) {
      var attr = attrs.next();
      var subAttrs = attr.getAll();
      while (subAttrs.hasMore()) {
        attributeList.add(getLdapProperty(attr.getID(), subAttrs.next()));
      }
    }
    return attributeList.stream()
            .sorted(Comparator.comparing(Property::getName, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
  }

  private Property getLdapProperty(String attrId, Object attr) {
    if (attr instanceof byte[]) {
      attr = Hex.encodeHexString((byte[]) attr, false);
    }
    return new Property(attrId, attr.toString());
  }

  public LdapBrowserNode createLdapNode(Name name, String parent) {
    return LdapBrowserNode.create(context, name, parent);
  }
}
