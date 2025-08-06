package ch.ivyteam.enginecockpit.monitor.mbeans.tomcat;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;
import static ch.ivyteam.ivy.environment.Ivy.cm;
import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.SPACE;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

@ManagedBean
@ViewScoped
public class SessionMonitorBean {
  private static final String SECURITY_MANAGER = "ivy Engine:type=Security Manager";
  private static final String TOMCAT_MANAGER = "ivy:type=Manager,host=*,context=*";
  private final Monitor sessionsMonitor = Monitor.build().name(cm().co("/common/Sessions")).icon("person").toMonitor();

  public SessionMonitorBean() {
    setupSessionMonitor();
  }

  private void setupSessionMonitor() {
    var licensedSession = join(SPACE, cm().co("/monitor/session/LicensedSessions"), "%5d");
    var session = join(SPACE, cm().co("/common/Sessions"), "%5d");
    var httpSession = join(SPACE, cm().co("/monitor/session/HTTPSessions"), "%5d");
    var licensedUser = join(SPACE, cm().co("/monitor/session/LicensedUsers"), "%5d");
    sessionsMonitor.addInfoValue(format(licensedSession, licensedSessions()));
    sessionsMonitor.addInfoValue(format(session, sessions()));
    sessionsMonitor.addInfoValue(format(httpSession, httpSessions()));
    sessionsMonitor.addInfoValue(format(licensedUser, licensedUsers()));

    sessionsMonitor.addSeries(Series.build(licensedSessions(), cm().co("/monitor/session/LicensedSessions")).toSeries());
    sessionsMonitor.addSeries(Series.build(sessions(), cm().co("/common/Sessions")).toSeries());
    sessionsMonitor.addSeries(Series.build(httpSessions(), cm().co("/monitor/session/HTTPSessions")).toSeries());
  }

  public Monitor getSessionsMonitor() {
    return sessionsMonitor;
  }

  private ValueProvider licensedUsers() {
    return attribute(SECURITY_MANAGER, "licensedUsers", Unit.ONE);
  }

  private ValueProvider sessions() {
    return attribute(SECURITY_MANAGER, "sessions", Unit.ONE);
  }

  private ValueProvider licensedSessions() {
    return attribute(SECURITY_MANAGER, "licensedSessions", Unit.ONE);
  }

  private ValueProvider httpSessions() {
    return attribute(TOMCAT_MANAGER, "activeSessions", Unit.ONE);
  }
}
