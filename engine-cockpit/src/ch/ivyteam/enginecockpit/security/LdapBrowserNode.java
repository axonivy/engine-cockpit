package ch.ivyteam.enginecockpit.security;

import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang3.StringUtils;

public class LdapBrowserNode
{
  private static final String ICON_DEFAULT = "folder";
  private static final String ICON_ORGANIZATION = "folder-shared";
  private static final String ICON_DOMAIN = "domain";
  private static final String ICON_GROUP = "group";
  private static final String ICON_USER = "person";
  
  private final Name name;
  private final String icon;
  private final boolean expandable;
  
  private LdapBrowserNode(Name name, boolean expandable, String icon)
  {
    this.name = name;
    this.expandable = expandable;
    this.icon = icon;
  }
  
  public static LdapBrowserNode create(LdapContext context, Name name, String parentName)
  {
    String fullName = StringUtils.isBlank(parentName) ? name.toString() : name.toString() + "," + parentName;
    String icon = evalIconFor(context, name, fullName);
    boolean expandable = !ICON_GROUP.equals(icon) && !ICON_USER.equals(icon);
    return new LdapBrowserNode(name, expandable, icon);
  }
  
  private static String evalIconFor(LdapContext context, Name name, String fullName)
  {
    if (StringUtils.startsWithIgnoreCase(name.toString(), "ou"))
    {
      return ICON_ORGANIZATION;
    }
    if (StringUtils.startsWithIgnoreCase(name.toString(), "dc"))
    {
      return ICON_DOMAIN;
    }
    if (StringUtils.startsWithIgnoreCase(name.toString(), "cn"))
    {
      try
      {
        Attribute attribute = context.getAttributes(fullName, new String[] {"objectClass"}).get("objectClass");
        if (attribute == null)
        {
          return ICON_DEFAULT;
        }
        if (isGroup(attribute))
        {
          return ICON_GROUP;
        }
        if (isUser(attribute))
        {
          return ICON_USER;
        }
      }
      catch (Exception ex)
      {
        return ICON_DEFAULT;
      }
    }
    return ICON_DEFAULT;
  }

  private static boolean isUser(Attribute attribute)
  {
    return (attribute.contains("Person") || attribute.contains("user")) && (!attribute.contains("computer"));
  }

  private static boolean isGroup(Attribute attribute)
  {
    return attribute.contains("group") || attribute.contains("groupOfNames");
  }

  public Name getName()
  {
    return name;
  }
  
  public String getIcon()
  {
    return icon;
  }
  
  boolean isExpandable()
  {
    return expandable;
  }
  
  @Override
  public String toString()
  {
    return name.toString();
  }
}
