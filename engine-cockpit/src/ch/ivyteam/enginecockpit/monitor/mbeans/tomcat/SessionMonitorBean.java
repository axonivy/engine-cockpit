package ch.ivyteam.enginecockpit.monitor.mbeans.tomcat;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.monitor.value.ValueProvider;

@ManagedBean
@ViewScoped
public class SessionMonitorBean
{
  private static final String SECURITY_MANAGER = "ivy Engine:type=Security Manager";
  private static final String TOMCAT_MANAGER = "ivy:type=Manager,host=*,context=*";
  private final Monitor sessionsMonitor = Monitor.build().name("Sessions").icon("person").toMonitor();
  
  public SessionMonitorBean()
  {
    setupSessionMonitor();
  }

  private void setupSessionMonitor()
  {
    sessionsMonitor.addInfoValue(format("Licensed Sessions %5d", licensedSessions()));
    sessionsMonitor.addInfoValue(format("Sessions %5d", sessions()));
    sessionsMonitor.addInfoValue(format("Http Sessions %5d", httpSessions()));
    sessionsMonitor.addInfoValue(format("Licensed Users %5d", licensedUsers()));
    
    sessionsMonitor.addSeries(Series.build(licensedSessions(), "Licensed Sessions").toSeries());
    sessionsMonitor.addSeries(Series.build(sessions(), "Sessions").toSeries());
    sessionsMonitor.addSeries(Series.build(httpSessions(), "Http Sessions").toSeries());
  }

  public Monitor getSessionsMonitor()
  {
    return sessionsMonitor;
  }
  
  private ValueProvider licensedUsers()
  {
    return attribute(SECURITY_MANAGER, "licensedUsers", Unit.ONE);
  }

  private ValueProvider sessions()
  {
    return attribute(SECURITY_MANAGER, "sessions", Unit.ONE);
  }

  private ValueProvider licensedSessions()
  {
    return attribute(SECURITY_MANAGER, "licensedSessions", Unit.ONE);
  }

  private ValueProvider httpSessions()
  {
    return attribute(TOMCAT_MANAGER, "activeSessions", Unit.ONE);
  }
}
