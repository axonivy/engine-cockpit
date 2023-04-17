package ch.ivyteam.enginecockpit.security.directory.ldap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.directory.Property;
import ch.ivyteam.ivy.security.identity.jndi.JndiContextUtil;
import ch.ivyteam.naming.JndiConfig;
import ch.ivyteam.naming.JndiUtil;

class LdapBrowserContext implements AutoCloseable {

  private LdapContext context;

  LdapBrowserContext(JndiConfig config, boolean enableInsecureSsl) throws NamingException {
    context = JndiContextUtil.openLdapContext(config, enableInsecureSsl);
  }

  @Override
  public void close() throws NamingException {
    JndiUtil.closeQuietly(context);
  }

  List<LdapBrowserNode> browse(Name defaultContext) throws NamingException {
    var names = new ArrayList<LdapBrowserNode>();
    var attribute = context.getAttributes(defaultContext).get("namingContexts");
    if (attribute == null) {
      throw new NamingException("Couldn't find any 'namingContexts' attributes");
    }
    for (int pos = 0; pos < attribute.size(); pos++) {
      var val = attribute.get(pos).toString();
      var name = parse(val);
      names.add(createLdapNode(val, name));
    }
    return names.stream()
            .sorted(Comparator.comparing(LdapBrowserNode::getDisplayName, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());
  }

  List<LdapBrowserNode> children(Name parentName) throws NamingException {
    var children = new ArrayList<LdapBrowserNode>();
    var list = context.list(parentName);
    try {
      while (list.hasMoreElements()) {
        var nextElement = list.nextElement();
        var fqChildName = parse(nextElement.getNameInNamespace());
        var childName = parse(nextElement.getName());
        var displayName = toDisplayName(childName);
        children.add(createLdapNode(displayName, fqChildName));
      }
      return children.stream()
              .sorted(Comparator.comparing(LdapBrowserNode::getDisplayName, String.CASE_INSENSITIVE_ORDER))
              .collect(Collectors.toList());
    } finally {
      list.close();
    }
  }

  String toDisplayName(Name childName) {
    List<Rdn> rdns = new ArrayList<>(((LdapName) childName).getRdns());
    Collections.reverse(rdns);
    return rdns
            .stream()
            .map(LdapBrowserContext::toDisplayName)
            .collect(Collectors.joining(","));
  }

  private static String toDisplayName(Rdn rdn) {
    return rdn.getType() + "=" + Rdn.unescapeValue(Objects.toString(rdn.getValue()));
  }

  List<Property> getAttributes(Name nodeName) throws NamingException {
    var attributeList = new ArrayList<Property>();
    var attrs = context.getAttributes(nodeName).getAll();
    try {
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
    } finally {
      attrs.close();
    }
  }

  private Property getLdapProperty(String name, Object value) {
    return new Property(name, toDisplayName(value));
  }

  private static String toDisplayName(Object value) {
    if (value instanceof byte[]) {
      return Hex.encodeHexString((byte[]) value, false);
    }
    return Objects.toString(value);
  }

  LdapBrowserNode createLdapNode(String displayName, Name name) {
    return LdapBrowserNode.create(context, displayName, name);
  }

  Name parse(String name) throws NamingException {
    name = StringUtils.unwrap(name, "\""); // If name contains a forward slash
                                           // it is quoted.
    name = name.replace("/", "\\2F"); // For Active Directory forward slash
                                      // needs to be encoded too!
    return context.getNameParser(name).parse(name);
  }
}
