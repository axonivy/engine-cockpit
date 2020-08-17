package ch.ivyteam.enginecockpit.monitor.mbeans.tomcat;

import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.attribute;
import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.format;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MMonitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MSeries;
import ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider;

@ManagedBean
@ViewScoped
public class SessionMonitorBean
{
  private static final String SECURITY_MANAGER = "ivy Engine:type=Security Manager";
  private static final String TOMCAT_MANAGER = "ivy:type=Manager,host=*,context=*";
  private final MMonitor sessionsMonitor = MMonitor.build().name("Sessions").icon("person").toMonitor();
  
  public SessionMonitorBean()
  {
    setupSessionMonitor();
  }

  private void setupSessionMonitor()
  {
    sessionsMonitor.addInfoValue(format("Licensed Sessions %d", licensedSessions()));
    sessionsMonitor.addInfoValue(format("Sessions %d", sessions()));
    sessionsMonitor.addInfoValue(format("Http Sessions %d", httpSessions()));
    sessionsMonitor.addInfoValue(format("Licensed Users %d", licensedUsers()));
    
    sessionsMonitor.addSeries(new MSeries(licensedSessions(), "Licensed Sessions"));
    sessionsMonitor.addSeries(new MSeries(sessions(), "Sessions"));
    sessionsMonitor.addSeries(new MSeries(httpSessions(), "Http Sessions"));
  }

  public Monitor getSessionsMonitor()
  {
    return sessionsMonitor;
  }
  
  private MValueProvider licensedUsers()
  {
    return attribute(SECURITY_MANAGER, "licensedUsers");
  }

  private MValueProvider sessions()
  {
    return attribute(SECURITY_MANAGER, "sessions");
  }

  private MValueProvider licensedSessions()
  {
    return attribute(SECURITY_MANAGER, "licensedSessions");
  }

  private MValueProvider httpSessions()
  {
    return attribute(TOMCAT_MANAGER, "activeSessions");
  }
}
