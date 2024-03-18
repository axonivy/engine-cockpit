package ch.ivyteam.enginecockpit.monitor.health;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import ch.ivyteam.enginecockpit.util.DurationFormat;
import ch.ivyteam.ivy.health.check.HealthCheck;
import ch.ivyteam.ivy.health.check.HealthChecker;
import ch.ivyteam.ivy.health.check.HealthMessage;
import ch.ivyteam.ivy.health.check.HealthSeverity;

@ManagedBean
@RequestScoped
public class HealthBean {

  private final HealthChecker checker = HealthChecker.instance();

  public void checkNow() {
    checker.checkNow();
  }

  public String getSeverityName() {
    return checker.severity().toString();
  }

  public String getSeverityIcon() {
    return severityIcon(checker.severity());
  }

  private static String severityIcon(HealthSeverity severity) {
    return switch (severity) {
      case HEALTHY -> "si si-check-1";
      case MINOR -> "si si-road-sign-warning";
      case MAJOR -> "si si-alert-circle";
      case CRITICAL -> "si si-alarm-bell";
    };
  }

  public String getSeverityClass() {
    return severityClass(checker.severity());
  }

  private String severityClass(HealthSeverity severity) {
    return switch (severity) {
      case HEALTHY -> "health-healthy";
      case MINOR -> "health-minor";
      case MAJOR -> "health-major";
      case CRITICAL -> "health-critical";
    };
  }

  public String getMessage() {
    if (checker.severity() == HealthSeverity.HEALTHY) {
      return "No problems detected. Engine is healthy.";
    }
    var msgs = checker.messages();
    if (msgs.size() == 1) {
      return msgs.get(0).message();
    }
    return checker.messages().size() + " problems detected.";
  }

  public List<Message> getMessages() {
    return checker.messages().stream().map(Message::new).collect(Collectors.toList());
  }

  public List<Check> getChecks() {
    return checker.checks().stream().map(Check::new).collect(Collectors.toList());
  }

  public class Message {

    private final HealthMessage message;

    public Message(HealthMessage message) {
      this.message = message;
    }

    public HealthSeverity getSeverity() {
      return message.severity();
    }

    public String getSeverityIcon() {
      return severityIcon(message.severity());
    }

    public String getSeverityClass() {
      return severityClass(message.severity());
    }

    public String getMessage() {
      return message.message();
    }

    public String getDescription() {
      return message.description();
    }

    public String getCheck() {
      return checker.checkForMessage(message).name();
    }
  }

  public class Check {

    private final HealthCheck check;

    public Check(HealthCheck check) {
      this.check = check;
    }

    public void checkNow() {
      check.checkNow();
    }

    public HealthSeverity getSeverity() {
      return check.severity();
    }

    public String getSeverityIcon() {
      return severityIcon(check.severity());
    }

    public String getSeverityClass() {
      return severityClass(check.severity());
    }

    public String getName() {
      return check.name();
    }

    public String getDescription() {
      return check.description();
    }

    public String getNextExecution() {
      var nextExecutionTime = check.nextExecutionTime();
      return DurationFormat.NOT_AVAILABLE.fromNowTo(nextExecutionTime.orElse(null));
    }
  }
}
