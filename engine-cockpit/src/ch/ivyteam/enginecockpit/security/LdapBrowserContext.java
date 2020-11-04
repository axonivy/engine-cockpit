package ch.ivyteam.enginecockpit.security;

import java.util.ArrayList;
import java.util.List;

import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;

import org.apache.commons.codec.binary.Hex;

import ch.ivyteam.enginecockpit.model.LdapProperty;
import ch.ivyteam.ivy.security.jndi.JndiContextUtil;
import ch.ivyteam.naming.JndiConfig;
import ch.ivyteam.naming.JndiUtil;

public class LdapBrowserContext implements AutoCloseable
{
  
  private LdapContext context;

  public LdapBrowserContext(JndiConfig config, boolean enableInsecureSsl) throws NamingException
  {
    context = JndiContextUtil.openLdapContext(config, enableInsecureSsl);
  }

  @Override
  public void close() throws NamingException
  {
    JndiUtil.closeQuietly(context);
  }
  
  public List<LdapBrowserNode> browse(Name defaultContext) throws NamingException
  {
    List<LdapBrowserNode> names = new ArrayList<>();
    Attribute attribute = context.getAttributes(defaultContext).get("namingContexts");
    for (int pos = 0; pos < attribute.size(); pos++)
    {
      Name name = context.getNameParser(attribute.get(pos).toString()).parse(attribute.get(pos).toString());
      names.add(createLdapNode(name, defaultContext.toString()));
    }
    return names;
  }
  
  public List<LdapBrowserNode> children(String parent) throws NamingException
  {
    List<LdapBrowserNode> children = new ArrayList<>();
    NamingEnumeration<NameClassPair> list = context.list(parent);
    while (list.hasMoreElements())
    {
      NameClassPair nextElement = list.nextElement();
      Name child = context.getNameParser(nextElement.getName()).parse(nextElement.getName());
      children.add(createLdapNode(child, parent));
    }
    return children;
  }
  
  public List<LdapProperty> getAttributes(String node) throws NamingException
  {
    List<LdapProperty> attributeList = new ArrayList<>();
    NamingEnumeration<? extends Attribute> attrs = context.getAttributes(new LdapName(node)).getAll();
    while (attrs.hasMore())
    {
      Attribute attr = attrs.next();
      NamingEnumeration<?> subAttrs = attr.getAll();
      while (subAttrs.hasMore())
      {
        attributeList.add(getLdapProperty(attr.getID(), subAttrs.next()));
      }
    }
    return attributeList;
  }
  
  private LdapProperty getLdapProperty(String attrId, Object attr)
  {
    if (attr instanceof byte[])
    {
      attr = Hex.encodeHexString((byte[])attr, false);
    }
    return new LdapProperty(attrId, attr.toString());
  }
  
  public LdapBrowserNode createLdapNode(Name name, String parent)
  {
    return LdapBrowserNode.create(context, name, parent);
  }
  
}
