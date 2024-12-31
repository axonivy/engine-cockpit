package ch.ivyteam.enginecockpit.security.model;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityConstants;

public class Role implements SecurityMember {

  private String name;
  private String description;
  private String displayName;
  private String externalName;
  private boolean member;
  private boolean dynamic;
  private String parentRoleName;
  private final String securityContext;

  public Role(String securityContext, String name) {
    this.securityContext = securityContext;
    this.name = name;
  }

  public Role(IRole role) {
    this.name = role.getName();
    this.description = role.getDisplayDescription();
    this.displayName = role.getDisplayName();
    this.externalName = role.getExternalName();
    this.member = false;
    this.dynamic = role.isDynamic();
    this.parentRoleName = role.getParent() == null ? "" : role.getParent().getName();
    this.securityContext = role.getSecurityContext().getName();
  }

  public Role(IRole role, boolean member) {
    this(role);
    this.member = member;
  }

  @Override
  public String getViewUrl() {
    return getViewUrl(securityContext, name);
  }

  public static String getViewUrl(String securityContext, String name) {
    return UriBuilder.fromPath("roledetail.xhtml")
        .queryParam("system", securityContext)
        .queryParam("name", name)
        .build()
        .toString();
  }

  @Override
  public String getCssIconClass() {
    return "si si-multiple-neutral-1";
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getExternalName() {
    return externalName;
  }

  public void setExternalName(String externalName) {
    this.externalName = externalName;
  }

  public boolean isMember() {
    return member;
  }

  public void setMember(boolean member) {
    this.member = member;
  }

  public boolean isDynamic() {
    return dynamic;
  }

  public void setDynamic(boolean dynamic) {
    this.dynamic = dynamic;
  }

  public boolean isManaged() {
    return StringUtils.isNotEmpty(externalName);
  }

  public String getParentRoleName() {
    return this.parentRoleName;
  }

  public void setParentRoleName(String parentRoleName) {
    this.parentRoleName = parentRoleName;
  }

  @Override
  public String toString() {
    return name;
  }

  public boolean isTopLevel() {
    return ISecurityConstants.TOP_LEVEL_ROLE_NAME.equals(name);
  }
}
