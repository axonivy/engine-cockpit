package ch.ivyteam.enginecockpit.system.administrators;

import java.util.Date;

import ch.ivyteam.enginecockpit.util.EmailUtil;
import ch.ivyteam.ivy.security.administrator.Administrator;

public class AdministratorDto {

  private String userName;
  private String fullName;
  private String email;
  private String password;
  private Date lastLogin;
  private boolean external;
  private String externalId;
  private boolean enabled;

  public AdministratorDto() {

  }

  public AdministratorDto(Administrator admin) {
    this.userName = admin.username();
    this.fullName = admin.fullName();
    this.email = admin.email();
    this.password = admin.password();
    var login = admin.lastLogin();
    this.lastLogin = login == null ? null : Date.from(login);
    this.external = admin.external();
    this.externalId = admin.externalId();
    this.enabled = admin.enabled();
  }

  public String getName() {
    return userName;
  }

  public void setName(String name) {
    this.userName = name;
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

  public String getGravatarHash() {
    return EmailUtil.gravatarHash(email);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Date getLastLogin() {
    return lastLogin;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isExternal() {
    return external;
  }

  public String getExternalId() {
    return externalId;
  }

  public Administrator toAdministrator() {
    return Administrator.create()
        .username(userName)
        .fullName(fullName)
        .email(email)
        .password(password)
        .toAdministrator();
  }
}
