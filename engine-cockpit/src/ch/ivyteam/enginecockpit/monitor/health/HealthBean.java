package ch.ivyteam.enginecockpit.monitor.health;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.util.DurationFormat;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.health.check.HealthCheck;
import ch.ivyteam.ivy.health.check.HealthChecker;
import ch.ivyteam.ivy.health.check.HealthMessage;
import ch.ivyteam.ivy.health.check.HealthSeverity;

@ManagedBean
@ViewScoped
public class HealthBean {

  private final HealthChecker checker = HealthChecker.instance();
  private final List<Check> checks;
  private List<Message> messages;
  private String check;
  private String filter;

  public HealthBean() {
    checks = checker.checks().stream().map(Check::new).collect(Collectors.toList());
    refresh();
  }

  public void refresh() {
    messages = checker.messages().stream().map(Message::new).collect(Collectors.toList());
  }

  public void checkNow() {
    checker.checkNow();
    refresh();
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
      case LOW -> "si si-road-sign-warning";
      case HIGH -> "si si-alert-circle";
      case CRITICAL -> "si si-alarm-bell";
    };
  }

  public String getSeverityClass() {
    return severityClass(checker.severity());
  }

  private static String severityClass(HealthSeverity severity) {
    return switch (severity) {
      case HEALTHY -> "health-healthy";
      case LOW -> "health-low";
      case HIGH -> "health-high";
      case CRITICAL -> "health-critical";
    };
  }

  public String getMessage() {
    if (checker.severity() == HealthSeverity.HEALTHY) {
      return Ivy.cm().co("/health/HealthyMessage");
    }
    var msgs = checker.messages();
    if (msgs.size() == 1) {
      return msgs.get(0).message();
    }

    return Ivy.cm().content("/health/ProblemsDetectedMessage")
        .replace("number", String.valueOf(checker.messages().size())).get();
  }

  public List<Message> getMessages() {
    return messages;
  }

  public List<Message> getTopMessages() {
    if (messages.size() <= 4) {
      return messages;
    }
    return messages.subList(0, 4);
  }

  public int getMessageCount() {
    return messages.size();
  }

  public List<Check> getChecks() {
    return checks;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getCheck() {
    return check;
  }

  public void setCheck(String check) {
    this.check = check;
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

    public String getActionLink() {
      if (isHasActionLink()) {
        return message.actionLink().get();
      }
      return "monitor-health.xhtml";
    }

    public boolean isHasActionLink() {
      return message.actionLink() != null;
    }
  }

  public static class Check {

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

    public String getTimeUntilNextExecution() {
      var nextExecutionTime = check.nextExecutionTime();
      return DurationFormat.NOT_AVAILABLE.fromNowTo(nextExecutionTime.orElse(null));
    }

    public String getNextExecution() {
      var nextExecutionTime = check.nextExecutionTime();
      return DurationFormat.NOT_AVAILABLE.format(nextExecutionTime.orElse(null));
    }

    public boolean isEnabled() {
      return check.isEnabled();
    }

    public void enable() {
      check.enable();
    }

    public void disable() {
      check.disable();
    }
  }
}
