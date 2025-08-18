package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.util.CmsUtil;
import ch.ivyteam.ivy.environment.Ivy;

public final class SystemDatabase extends AbstractDatabase {
  private static final ObjectName DATABASE_PERSISTENCY_SERVICE;

  static {
    try {
      DATABASE_PERSISTENCY_SERVICE = new ObjectName("ivy Engine:type=Database Persistency Service");
    } catch (MalformedObjectNameException ex) {
      throw new IllegalArgumentException("Wrong object name", ex);
    }
  }

  SystemDatabase() {
    super(
        DATABASE_PERSISTENCY_SERVICE,
        CmsUtil.coWithDefault("/liveStats/Transactions", "Transactions"),
        Monitor.build().name(CmsUtil.coWithDefault("/common/Connections", "Connections")).icon("insert_link").toMonitor(),
        Monitor.build().name(CmsUtil.coWithDefault("/liveStats/Transactions", "Transactions")).icon("dns").toMonitor(),
        Monitor.build().name(CmsUtil.coWithDefault("/liveStats/ProcessingTime", "Processing Time")).icon("timer").yAxisLabel(Ivy.cm().co("/common/Time")).toMonitor());
  }
}
