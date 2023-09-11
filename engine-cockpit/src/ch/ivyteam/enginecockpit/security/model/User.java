package ch.ivyteam.enginecockpit.security.model;

import java.util.Locale;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.administrator.Administrator;

public class User implements SecurityMember {

  private String name;
  private String fullName;
  private String email;
  private String password;
  private String realPassword = "";
  private String externalName = "";
  private String externalId = "";
  private String securityMemberId = "";
  private Locale language;
  private Locale formattingLanguage;
  private String securityContext;

  private boolean loggedIn;
  private boolean enabled = true;
  private boolean isExternal;

  public User() {}

  public User(IUser user) {
    this.name = user.getName();
    this.fullName = user.getFullName();
    this.email = user.getEMailAddress();
    this.loggedIn = false;
    this.enabled = user.isEnabled();
    this.isExternal = user.isExternal();
    this.externalName = user.getExternalName();
    this.externalId = user.getExternalId();
    this.securityMemberId = user.getSecurityMemberId();
    this.language = user.getLanguage();
    this.formattingLanguage = user.getFormattingLanguage();
    this.securityContext = user.getSecurityContext().getName();
  }

  public User(Administrator admin) {
    setName(admin.getUsername());
    setFullName(admin.getFullName());
    setEmail(admin.getEmail());
    setRealPassword(admin.getPassword());
    setSecurityContext(ISecurityContext.SYSTEM);
  }

  @Override
  public String getViewUrl() {
    return UriBuilder.fromPath("userdetail.xhtml")
            .queryParam("system", securityContext)
            .queryParam("name", name)
            .build()
            .toString();
  }

  @Override
  public String getCssIconClass() {
    return "si si-single-neutral-actions";
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isExternal() {
    return isExternal;
  }

  public String getExternalName() {
    return externalName;
  }

  public String getExternalNameShort() {
    return StringUtils.substringBefore(externalName, ",") + ",...";
  }

  public String getExternalId() {
    return externalId;
  }

  public String getDisplayName() {
    return name + " (" + fullName + ")";
  }

  public Locale getLanguage() {
    return language;
  }

  public void setLanguage(Locale language) {
    this.language = language;
  }

  public Locale getFormattingLanguage() {
    return formattingLanguage;
  }

  public void setFormattingLanguage(Locale formattingLanguage) {
    this.formattingLanguage = formattingLanguage;
  }

  // Use for <p:password redisplay="true"> without leak the real password in the
  // DOM
  public void setRealPassword(String realPassword) {
    this.password = "*".repeat(realPassword.length());
    this.realPassword = realPassword;
  }

  public String getRealPassword() {
    if (!StringUtils.equals(password, "*".repeat(realPassword.length()))) {
      return password;
    }
    return realPassword;
  }

  public String getSecurityContext() {
    return securityContext;
  }

  public void setSecurityContext(String securityContext) {
    this.securityContext = securityContext;
  }

  public Administrator getAdmin() {
    return new Administrator.Builder().username(getName())
            .fullName(getFullName())
            .email(getEmail())
            .password(getRealPassword())
            .toAdministrator();
  }

  @Override
  public String toString() {
    return name;
  }

  public String getSecurityMemberId() {
    return securityMemberId;
  }
}
