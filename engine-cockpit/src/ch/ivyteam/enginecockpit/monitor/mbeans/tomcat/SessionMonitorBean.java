package ch.ivyteam.enginecockpit.monitor.mbeans.tomcat;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;
import static java.lang.String.join;
import static org.apache.commons.lang3.StringUtils.SPACE;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;
import ch.ivyteam.enginecockpit.util.CmsUtil;

@ManagedBean
@ViewScoped
public class SessionMonitorBean {
  private static final String SECURITY_MANAGER = "ivy Engine:type=Security Manager";
  private static final String TOMCAT_MANAGER = "ivy:type=Manager,host=*,context=*";
  private final Monitor sessionsMonitor = Monitor.build().name(CmsUtil.coWithDefault("/common/Sessions", "Sessions")).icon("person").toMonitor();

  public SessionMonitorBean() {
    setupSessionMonitor();
  }

  private void setupSessionMonitor() {
    var licensedSession = join(SPACE, CmsUtil.coWithDefault("/monitor/session/LicensedSessions", "Licensed Sessions"), "%5d");
    var session = join(SPACE, CmsUtil.coWithDefault("/common/Sessions", "Sessions"), "%5d");
    var httpSession = join(SPACE, CmsUtil.coWithDefault("/monitor/session/HTTPSessions", "Http Sessions"), "%5d");
    var licensedUser = join(SPACE, CmsUtil.coWithDefault("/monitor/session/LicensedUsers", "Licensed Users"), "%5d");
    sessionsMonitor.addInfoValue(format(licensedSession, licensedSessions()));
    sessionsMonitor.addInfoValue(format(session, sessions()));
    sessionsMonitor.addInfoValue(format(httpSession, httpSessions()));
    sessionsMonitor.addInfoValue(format(licensedUser, licensedUsers()));

    sessionsMonitor.addSeries(Series.build(licensedSessions(), CmsUtil.coWithDefault("/monitor/session/LicensedSessions", "Licensed Sessions")).toSeries());
    sessionsMonitor.addSeries(Series.build(sessions(), CmsUtil.coWithDefault("/common/Sessions", "Sessions")).toSeries());
    sessionsMonitor.addSeries(Series.build(httpSessions(), CmsUtil.coWithDefault("/monitor/session/HTTPSessions", "Http Sessions")).toSeries());
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
