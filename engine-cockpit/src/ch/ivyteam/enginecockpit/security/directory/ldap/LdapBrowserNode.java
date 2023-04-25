package ch.ivyteam.enginecockpit.security.directory.ldap;

import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.security.directory.DirectoryNode;

public class LdapBrowserNode implements DirectoryNode {

  private static final String ICON_DEFAULT = "folder-empty";
  private static final String ICON_ORGANIZATION = "folder-share";
  private static final String ICON_DOMAIN = "buildings-1";
  private static final String ICON_GROUP = "multiple-neutral-1";
  private static final String ICON_USER = "single-neutral-actions";
  private final String displayName;
  private final Name name;
  private final String icon;
  private final boolean expandable;

  private LdapBrowserNode(String displayName, Name name, boolean expandable, String icon) {
    this.displayName = displayName;
    this.name = name;
    this.expandable = expandable;
    this.icon = icon;
  }

  static LdapBrowserNode create(LdapContext context, String displayName, Name name) {
    var icon = evalIconFor(context, name);
    var expandable = !ICON_GROUP.equals(icon) && !ICON_USER.equals(icon);
    return new LdapBrowserNode(displayName, name, expandable, icon);
  }
  
  private static String evalIconFor(LdapContext context, Name name) {
    if (name.isEmpty()) {
      return ICON_DEFAULT;
    }
    var suffix = name.getSuffix(name.size() - 1);
    if (StringUtils.startsWithIgnoreCase(suffix.toString(), "ou")) {
      return ICON_ORGANIZATION;
    }
    if (StringUtils.startsWithIgnoreCase(suffix.toString(), "dc")) {
      return ICON_DOMAIN;
    }
    if (StringUtils.startsWithIgnoreCase(suffix.toString(), "cn")) {
      try {
        var attribute = context.getAttributes(name, new String[] {"objectClass"}).get("objectClass");
        if (attribute == null) {
          return ICON_DEFAULT;
        }
        if (isGroup(attribute)) {
          return ICON_GROUP;
        }
        if (isUser(attribute)) {
          return ICON_USER;
        }
      } catch (Exception ex) {
        return ICON_DEFAULT;
      }
    }
    return ICON_DEFAULT;
  }

  private static boolean isUser(Attribute attribute) {
    return (attribute.contains("Person") || attribute.contains("user")) && (!attribute.contains("computer"));
  }

  private static boolean isGroup(Attribute attribute) {
    return attribute.contains("group") || attribute.contains("groupOfNames");
  }

  @Override
  public String getId() {
    return name.toString();
  }

  @Override
  public String getIcon() {
    return icon;
  }

  @Override
  public boolean isExpandable() {
    return expandable;
  }

  @Override
  public String getDisplayName() {
    return displayName;
  }

  @Override
  public String toString() {
    return displayName.toString();
  }
}
