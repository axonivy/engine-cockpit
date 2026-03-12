package ch.ivyteam.enginecockpit.profile;

import java.util.Locale;
import ch.ivyteam.ivy.security.administrator.Administrator;

public class ProfileDTO {

  private String userName;
  private String fullName;
  private String password;
  private String email;
  private Locale language;
  private Locale formattingLanguage;

  public ProfileDTO() {
  }

  public ProfileDTO(Administrator admin) {
    this.userName = admin.username();
    this.fullName = admin.fullName();
    this.password = admin.password();
    this.email = admin.email();
    this.language = admin.language();
    this.formattingLanguage = admin.formattingLanguage();
  }

  public String getUserName() {
    return userName;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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

  public Administrator toAdmin() {
    return Administrator.create()
        .username(userName)
        .fullName(fullName)
        .email(email)
        .password(password)
        .language(language)
        .formattingLanguage(formattingLanguage)
        .toAdministrator();
  }

}
