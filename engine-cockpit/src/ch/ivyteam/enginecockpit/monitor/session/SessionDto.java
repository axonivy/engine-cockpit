package ch.ivyteam.enginecockpit.monitor.session;

import java.util.Date;
import java.util.Set;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.security.model.User;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISessionInternal;

public class SessionDto {

  ISessionInternal session;

  private final Date createdAt;
  private final Date lastAccessedAt;
  private final String id;
  private final String user;
  private final String userLink;
  private final boolean isUserInternal;
  private final boolean isSecuritySystemInternal;
  private final String link;
  private final String authMode;

  SessionDto(ISessionInternal session) {
    this.session = session;
    this.createdAt = Date.from(session.createdAt());
    this.lastAccessedAt = Date.from(session.lastAccessedAt());
    this.id = String.valueOf(session.getIdentifier());
    this.user = session.getSessionUser() == null ? "" : session.getSessionUser().getName();
    this.userLink = session.getSessionUser() == null ? "" : new User(session.getSessionUser()).getViewUrl();
    this.isSecuritySystemInternal = ISecurityContext.SYSTEM.equals(session.getSecurityContext().getName());
    this.isUserInternal = session.isSessionUserSystemUser() || session.isSessionUserUnknown() || isSecuritySystemInternal;
    this.link = SecuritySystem.link(session.getSecurityContext());
    this.authMode = StringUtils.trimToEmpty(session.getAuthenticationMode());
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public Date getLastAccessedAt() {
    return lastAccessedAt;
  }

  public String getId() {
    return id;
  }

  public String getUser() {
    return user;
  }

  public String getUserLink() {
    return userLink;
  }

  public boolean isUserInternal() {
    return isUserInternal;
  }

  public String getAuthMode() {
    return authMode;
  }

  public String getSecuritySystem() {
    return session.getSecurityContext().getName();
  }

  public String getSecuritySystemLink() {
    return link;
  }

  public boolean isSecuritySystemInternal() {
    return isSecuritySystemInternal;
  }

  public String getCause() {
    return session.creationReason();
  }

  public Set<HttpSession> getHttpSessions() {
    return session.getHttpSessions();
  }

  public int getHttpSessionCount() {
    return session.getHttpSessions().size();
  }

  public boolean canKill() {
    return !session.isSessionUserSystemUser();
  }
}
