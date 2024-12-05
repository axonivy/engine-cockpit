package ch.ivyteam.enginecockpit.security.model;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.enginecockpit.util.EmailUtil;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.IUserAbsence;
import ch.ivyteam.ivy.security.IUserSubstitute;
import ch.ivyteam.ivy.security.SubstitutionType;
import ch.ivyteam.ivy.security.administrator.Administrator;

public class User implements SecurityMember {

  private IUser iUser;
  private String name;
  private String fullName;
  private String email;
  private String gravatarHash;
  private String password;
  private String realPassword = "";
  private String externalName = "";
  private String externalId = "";
  private String securityMemberId = "";
  private Locale language;
  private Locale formattingLanguage;
  private String securityContext;
  private boolean isIvySecuritySystem;
  private List<Substitute> substitutes;
  private List<Absence> absences;

  private boolean loggedIn;
  private boolean working;
  private boolean enabled = true;
  private boolean isExternal;

  public User() {}

  public User(IUser user) {
    this.iUser = user;
    this.name = user.getName();
    this.fullName = user.getFullName();
    this.email = user.getEMailAddress();
    this.gravatarHash = EmailUtil.gravatarHash(email);
    this.loggedIn = false;
    this.enabled = user.isEnabled();
    this.isExternal = user.isExternal();
    this.externalName = user.getExternalName();
    this.externalId = user.getExternalId();
    this.securityMemberId = user.getSecurityMemberId();
    this.language = user.getLanguage();
    this.formattingLanguage = user.getFormattingLanguage();
    this.securityContext = user.getSecurityContext().getName();
    this.working = !user.isAbsent();
    this.isIvySecuritySystem = SecuritySystem.isIvySecuritySystem(user.getSecurityContext());
    this.substitutes = substitutesOf(user);
    this.absences = absencesOf(user);
  }

  public User(Administrator admin) {
    setName(admin.getUsername());
    setFullName(admin.getFullName());
    setEmail(admin.getEmail());
    setRealPassword(admin.getPassword());
    setSecurityContext(ISecurityContext.SYSTEM);
    this.isIvySecuritySystem = true;
    this.gravatarHash = EmailUtil.gravatarHash(admin.getEmail());
  }

  @Override
  public String getViewUrl() {
    return getViewUrl(securityContext, name);
  }

  private static String getViewUrl(String securityContext, String name) {
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

  public String getGravatarHash() {
    return gravatarHash;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public boolean isWorking() {
    return working;
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

  public String getExternalId() {
    return externalId;
  }

  public String getDisplayName() {
    return name + " (" + fullName + ")";
  }

  public String getExternalIdentifier() {
    if (externalId != null && !externalId.isBlank()) {
      return externalId;
    }
    return externalName;
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

  public IUser getIUser() {
    return iUser;
  }

  @Override
  public String toString() {
    return name;
  }

  public String getSecurityMemberId() {
    return securityMemberId;
  }

  public List<Substitute> getSubstitutes() {
    return substitutes;
  }

  public List<Absence> getAbsences() {
    return absences;
  }
  
  public boolean isIvySecuritySystem() {
    return isIvySecuritySystem;
  }

  private List<Substitute> substitutesOf(IUser user) {
    return user.getSubstitutes().stream()
            .map(Substitute::of)
            .collect(Collectors.toList());
  }

  private List<Absence> absencesOf(IUser user){
    return user.getAbsences().stream()
            .map(Absence::of)
            .collect(Collectors.toList());
  }

  public static class Substitute {

    private final String name;
    private final String role;
    private final String memberIcon;
    private final String memberTitle;
    private final String typeIcon;
    private final String typeTitle;

    private final String securityContext;

    public Substitute(String name, String role, String memberIcon, String memberTitle, String typeIcon,
            String typeTitle, String securityContext) {
      this.name = name;
      this.role = role;
      this.memberIcon = memberIcon;
      this.memberTitle = memberTitle;
      this.typeIcon = typeIcon;
      this.typeTitle = typeTitle;
      this.securityContext = securityContext;
    }

    public String getName() {
      return name;
    }

    public String getRole() {
      return role;
    }

    public String getMemberIcon() {
      return memberIcon;
    }

    public String getMemberTitle() {
      return memberTitle;
    }

    public String getTypeIcon() {
      return typeIcon;
    }

    public String getTypeTitle() {
      return typeTitle;
    }

    public String getSubstituteViewUrl() {
      return User.getViewUrl(securityContext, name);
    }

    public String getRoleViewUrl() {
      return Role.getViewUrl(securityContext, role);
    }

    public static Substitute of(IUserSubstitute substitute) {
      var substituteMember = substitute.getSubstituteUser();
      var substitutionRole = substitute.getSubstitutionRole();

      String name = substituteMember.getName();
      String role = "";

      String memberIcon;
      String memberTitle;
      if (substitutionRole == null) {
        memberIcon = "single-neutral-actions";
        memberTitle = "Personal";
      } else {
        role = substitutionRole.getName();
        memberIcon = "multiple-neutral-1";
        memberTitle = "Role";
      }

      boolean onAbsence = substitute.getSubstitutionType().equals(SubstitutionType.ON_ABSENCE);
      String typeIcon = onAbsence ? "time-clock-circle" : "pin";
      String typeTitle = onAbsence ? "On absence" : "Permanent";

      String securityContext = substituteMember.getSecurityContext().getName();

      return new Substitute(name, role, memberIcon, memberTitle, typeIcon, typeTitle, securityContext);
    }
  }

  public static class Absence {

    private final Date start;
    private final Date end;

    public Absence(Date start, Date end) {
      this.start = start;
      this.end = end;
    }

    public Date getStart() {
      return start;
    }

    public Date getEnd() {
      return end;
    }

    public static Absence of(IUserAbsence absence) {
      return new Absence(absence.getStartTimestamp(), absence.getStopTimestamp());
    }
  }
}
