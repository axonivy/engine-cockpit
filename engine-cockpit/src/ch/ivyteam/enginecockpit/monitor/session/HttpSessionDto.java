package ch.ivyteam.enginecockpit.monitor.session;

import javax.servlet.http.HttpSession;

public class HttpSessionDto {

  private long creationTime;
  private long lastAccessedTime;
  private boolean isValid;

  public HttpSessionDto(HttpSession httpSession) {
    try {
      this.creationTime = httpSession.getCreationTime();
      this.lastAccessedTime = httpSession.getLastAccessedTime();
      this.isValid = true;
    } catch (RuntimeException ex) {
      this.isValid = false;
    }

  }

  public long getCreationTime() {
    return creationTime;
  }

  public long getLastAccessedTime() {
    return lastAccessedTime;
  }

  public boolean isValid() {
    return isValid;
  }
}
