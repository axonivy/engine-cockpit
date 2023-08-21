package ch.ivyteam.enginecockpit.system;

import java.io.IOException;

import javax.faces.bean.ManagedBean;

import ch.ivyteam.ivy.security.ISecurityContextRepository;

@ManagedBean
public class RestartBean {

  private boolean isRestarting;
  
  public void restart() throws IOException {
    var process = ProcessHandle.current();
    if (process == null) {
      return;
    }
    var command = process.info().command();
    if (command.isEmpty()) {
      return;
    }
    isRestarting = true;
    new ProcessBuilder(command.get(), "-restart").start();
  }
  
  public boolean isRestarting() {
    return isRestarting;
  }
  
  public long getWorkingUsers() {
    return ISecurityContextRepository.instance().all()
      .stream()
      .flatMap(s -> s.sessions().all().stream())
      .filter(s -> !s.isSessionUserSystemUser())
      .filter(s -> !s.isSessionUserUnknown())
      .map(s -> s.getSessionUserName())
      .distinct()
      .count();
  }
}
