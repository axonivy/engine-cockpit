package ch.ivyteam.enginecockpit.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;

import org.apache.commons.lang3.SystemUtils;

import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import ch.ivyteam.ivy.security.ISecurityContextRepository;
import ch.ivyteam.ivy.server.restricted.EngineMode;

@ManagedBean
@SuppressWarnings("restriction")
public class RestartBean {

  private boolean isRestarting;

  public void restart() throws IOException {
    if (!isRestartable()) {
      return;
    }
    var command = command();
    if (command.isEmpty()) {
      return;
    }
    command.add("-restart");
    new ProcessBuilder(command).start();
  }

  public String getCommand() {
    return command().stream().collect(Collectors.joining(" "));
  }

  private List<String> command() {
    var process = ProcessHandle.current();
    if (process == null) {
      return List.of();
    }
    var command = process.info().command();
    if (command.isEmpty()) {
      return List.of();
    }
    var commands = new ArrayList<String>();
    if (SystemUtils.IS_OS_WINDOWS) {
      commands.add(command.get());
    } else if (SystemUtils.IS_OS_LINUX) {
      commands.add(command.get());
      var arguments = process.info().arguments();
      if (arguments.isEmpty()) {
        return commands;
      }
      commands.addAll(Arrays.asList(arguments.get()));
    }
    return commands;
  }

  public boolean isRestarting() {
    return isRestarting;
  }

  public long getWorkingUsers() {
    return ISecurityContextRepository.instance().allWithSystem()
      .stream()
      .flatMap(s -> s.sessions().all().stream())
      .filter(s -> !s.isSessionUserSystemUser())
      .filter(s -> !s.isSessionUserUnknown())
      .map(s -> s.getSessionUserName())
      .distinct()
      .count();
  }

  public boolean isRestartable() {
    if (isDisabled()) {
      return false;
    }
    var command = command();
    return !command.isEmpty();
  }

  private boolean isDisabled() {
    var restart = IConfiguration.instance().get("Cockpit.Restart");
    if (restart.isPresent()) {
      return "disabled".equalsIgnoreCase(restart.get());
    }
    if (isRunningInContainer()) {
      return true;
    }
    if (EngineMode.isEmbeddedInDesigner()) {
      return true;
    }
    return false;
  }

  private boolean isRunningInContainer() {
    return Files.exists(Path.of("/.dockerenv"));
  }
}
