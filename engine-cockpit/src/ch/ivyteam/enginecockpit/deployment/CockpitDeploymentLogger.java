package ch.ivyteam.enginecockpit.deployment;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import ch.ivyteam.ivy.deployment.DeploymentLogger;

public class CockpitDeploymentLogger implements DeploymentLogger {

  private final List<String> logs = new ArrayList<>();

  public List<String> getLogs() {
    return logs;
  }

  @Override
  public void info(String message) {
    logs.add(message);
  }

  @Override
  public void warning(String message) {
    logs.add(message);
  }

  @Override
  public void warning(String message, Throwable t) {
    logs.add(format(message, t));
  }

  @Override
  public void error(String message) {
    logs.add(message);
  }

  @Override
  public void error(String message, Throwable t) {
    logs.add(format(message, t));
  }

  private String format(String message, Throwable t) {
    if (t == null) {
      return message;
    }
    var sw = new StringWriter();
    try (var pw = new PrintWriter(sw)) {
      pw.println(message);
      t.printStackTrace(pw);
    }
    return sw.toString();
  }
}
