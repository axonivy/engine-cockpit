package ch.ivyteam.enginecockpit.system;

import java.io.IOException;
import java.util.Optional;

import javax.faces.bean.ManagedBean;

import ch.ivyteam.ivy.security.ISecurityContextRepository;

@ManagedBean
public class RestartBean {

  private boolean isRestarting;
  
  public void restart() throws IOException {
    var command = command();    
    if (command.isEmpty()) {
      return;
    }
    isRestarting = true;
    new ProcessBuilder(command.get(), "-restart").start();
  }

  private Optional<String> command() {
    var process = ProcessHandle.current();
    if (process == null) {
      return Optional.empty();
    }
    var command = process.info().command();
   return command;
  }
  
  public boolean isRestarting() {
    return isRestarting;
  }
  
  public boolean isRestartable() {
    var command = command();
    return command.isPresent();
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
