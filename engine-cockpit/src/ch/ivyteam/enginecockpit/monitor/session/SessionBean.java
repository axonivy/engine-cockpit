package ch.ivyteam.enginecockpit.monitor.session;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpSession;

import org.ocpsoft.prettytime.PrettyTime;

import ch.ivyteam.enginecockpit.security.model.SecuritySystem;
import ch.ivyteam.enginecockpit.security.model.User;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.security.ISessionInternal;

@ManagedBean
@ViewScoped
public class SessionBean {

  private List<SessionDto> filteredSessions;
  private List<SessionDto> sessions = readData();
  private String filter;

  public List<SessionDto> getSessions() {
    return sessions;
  }

  public List<SessionDto> readData() {
    return ISecurityContextRepository.instance().all()
            .stream()
            .flatMap(s -> s.sessions().all().stream())
            .filter(s -> !s.isSessionUserSystemUser())
            .map(s -> new SessionDto((ISessionInternal) s))
            .collect(Collectors.toList());
  }

  public List<SessionDto> getFilteredSessions() {
    return filteredSessions;
  }

  public void setFilteredSessions(List<SessionDto> filteredSessions) {
    this.filteredSessions = filteredSessions;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public void killSession(SessionDto session) {
    var ses = session.session;
    ses.getSecurityContext().sessions().destroy(ses.getIdentifier(), "ENGINE-COCKPIT");
    sessions = readData();
  }

  public static class SessionDto {

    private ISessionInternal session;

    private SessionDto(ISessionInternal session) {
      this.session = session;
    }

    public Date getCreatedAt() {
      return Date.from(session.createdAt());
    }

    public String getSince() {
      var pretty = new PrettyTime(session.createdAt());
      return pretty.format(Instant.now());
    }

    public String getId() {
      return String.valueOf(session.getIdentifier());
    }

    public String getUser() {
      return session.getSessionUser() == null ? "unauthenticated" : session.getSessionUser().getName();
    }

    public String getUserLink() {
      return session.getSessionUser() == null ? "" : new User(session.getSessionUser()).getViewUrl(session.getSecurityContext().getName());
    }

    public boolean isUserInternal() {
      return session.isSessionUserSystemUser() || session.isSessionUserUnknown();
    }

    public String getAuthMode() {
      return session.getAuthenticationMode();
    }

    public String getSecuritySystem() {
      return session.getSecurityContext().getName();
    }

    public String getSecuritySystemLink() {
      return new SecuritySystem(session.getSecurityContext()).getLink();
    }

    public boolean isSecuritySystemInternal() {
      return ISecurityContext.SYSTEM.equals(session.getSecurityContext().getName());
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
}
