package ch.ivyteam.enginecockpit.monitor.health;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import ch.ivyteam.ivy.health.check.HealthChecker;
import ch.ivyteam.ivy.health.check.HealthSeverity;
import ch.ivyteam.ivy.manager.IManager;

class TestHealthBean {

  HealthBean testee = new HealthBean();

  @AfterEach
  void after() {
    ((IManager)HealthChecker.instance()).stop();
  }

  @Test
  void set_get_check() {
    assertThat(testee.getCheck()).isNull();
    testee.setCheck("Hello");
    assertThat(testee.getCheck()).isEqualTo("Hello");
    testee.setCheck(null);
    assertThat(testee.getCheck()).isNull();
  }

  @Test
  void checks() {
    assertThat(testee.getChecks()).isNotEmpty();
  }

  void check() {
    var check = testee.getChecks().stream().filter(c -> c.getName().equals("Release Candidate")).findAny().orElseThrow();
    assertThat(check.getName()).isEqualTo("Release Candidate");
    assertThat(check.getDescription()).isEqualTo("Release Candidate");
    assertThat(check.getSeverity()).isEqualTo(HealthSeverity.HEALTHY);
    assertThat(check.getSeverityClass()).isEqualTo("health-healthy");
    assertThat(check.getSeverityIcon()).isEqualTo("si si-check-1");
    assertThat(check.getNextExecution()).isEqualTo("n.a.");
    assertThat(check.getTimeUntilNextExecution()).isEqualTo("n.a.");

    check.checkNow();

    assertThat(check.getSeverity()).isEqualTo(HealthSeverity.HIGH);
    assertThat(check.getSeverityClass()).isEqualTo("health-high");
    assertThat(check.getSeverityIcon()).isEqualTo("si si-alert-circle");
  }

  @Test
  void enable_disable_check() {
    var check = testee.getChecks().get(0);
    assertThat(check.isEnabled()).isTrue();
    try {
      check.disable();
      assertThat(check.isEnabled()).isFalse();
    } finally {
      check.enable();
    }
    assertThat(check.isEnabled()).isTrue();
  }

  @Test
  void messages() {
    assertThat(testee.getMessageCount()).isEqualTo(0);
    assertThat(testee.getMessage()).isEqualTo("No problems detected. Engine is healthy.");
    assertThat(testee.getMessages()).isEmpty();

    ((IManager)HealthChecker.instance()).start();
    testee.refresh();

    assertThat(testee.getMessageCount()).isEqualTo(1);
    assertThat(testee.getMessage()).isEqualTo("Release Candidate");
    assertThat(testee.getMessages()).hasSize(1);
  }

  @Test
  void message() {
    ((IManager)HealthChecker.instance()).start();
    testee.refresh();

    var msg = testee.getMessages().get(0);
    assertThat(msg.getMessage()).isEqualTo("Release Candidate");
    assertThat(msg.getDescription()).isEqualTo("Don't use this version in production it is a release candidate");
    assertThat(msg.getSeverity()).isEqualTo(HealthSeverity.HIGH);
    assertThat(msg.getSeverityClass()).isEqualTo("health-high");
    assertThat(msg.getSeverityIcon()).isEqualTo("si si-alert-circle");
    assertThat(msg.getActionLink()).isEqualTo("https://developer.axonivy.com/download");
    assertThat(msg.getCheck()).isEqualTo("Release Candidate");
  }

  @Test
  void severity() {
    assertThat(testee.getSeverityClass()).isEqualTo("health-healthy");
    assertThat(testee.getSeverityIcon()).isEqualTo("si si-check-1");
    assertThat(testee.getSeverityName()).isEqualTo("HEALTHY");

    ((IManager)HealthChecker.instance()).start();
    testee.refresh();

    assertThat(testee.getSeverityClass()).isEqualTo("health-high");
    assertThat(testee.getSeverityIcon()).isEqualTo("si si-alert-circle");
    assertThat(testee.getSeverityName()).isEqualTo("HIGH");
  }

  @Test
  void checkNow() {
    assertThat(testee.getMessageCount()).isEqualTo(0);

    ((IManager)HealthChecker.instance()).start();
    testee.checkNow();

    assertThat(testee.getMessageCount()).isEqualTo(1);
  }
}
