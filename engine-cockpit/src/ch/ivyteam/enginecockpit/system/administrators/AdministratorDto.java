package ch.ivyteam.enginecockpit.system.administrators;

import ch.ivyteam.enginecockpit.util.EmailUtil;
import ch.ivyteam.ivy.security.administrator.Administrator;

public class AdministratorDto {

  private String userName;
  private String fullName;
  private String email;
  private String password;
  
  public AdministratorDto() {
    
  }
  
  public AdministratorDto(Administrator admin) {
    this.userName = admin.getUsername();
    this.fullName = admin.getFullName();
    this.email = admin.getEmail();
    this.password = admin.getPassword();
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
  
  public Administrator toAdministrator() {
    return Administrator.create()
        .username(userName)
        .fullName(fullName)
        .email(email)
        .password(password)
        .toAdministrator();
  }
}
