package ch.ivyteam.enginecockpit.monitor.events.intermediate;

import java.lang.management.ManagementFactory;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import ch.ivyteam.enginecockpit.util.DateUtil;


@ManagedBean
@ViewScoped
public class IntermediateEventBean {
  private List<Bean> intermediateEvents;

  public IntermediateEventBean() {
    refresh();
  }

  public void refresh() {
    try {
      intermediateEvents = ManagementFactory.getPlatformMBeanServer()
              .queryNames(new ObjectName("ivy Engine:type=Process Intermediate Event Bean,application=*,pm=*,pmv=*,name=*"), null)
              .stream()
              .map(Bean::new)
              .toList();
    } catch (MalformedObjectNameException ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<Bean> getIntermediateEvents() {
    return intermediateEvents;
  }

  public static final class Bean {

    private static final String NOT_AVAILABLE = "n.a.";
    private ObjectName name;

    private Bean(ObjectName name) {
      this.name = name;
    }

    public String getName() {
      return name.getKeyProperty("name");
    }

    public String getBeanName() {
      return readStringAttribute("name");
    }

    public String getBeanDescription() {
      return readStringAttribute("description");
    }

    public String getApplication() {
      return name.getKeyProperty("application");
    }

    public String getPm() {
      return name.getKeyProperty("pm");
    }

    public String getPmv() {
      return name.getKeyProperty("pmv");
    }

    public String getLastStartTimestamp() {
      return getDateAttribute("lastStartTimestamp");
    }

    public String getLastFiringTimestamp() {
      return getDateAttribute("lastFiringTimestamp");
    }

    public String getProcessElementId() {
      return readStringAttribute("processElementId");
    }

    public boolean isRunning() {
      return (Boolean)readAttribute("running");
    }

    private String getDateAttribute(String attributeName) {
      Date date = (Date)readAttribute(attributeName);
      if (date == null) {
        return NOT_AVAILABLE;
      }
      return DateUtil.formatDate(date);
    }

    private String readStringAttribute(String attribute) {
      return (String)readAttribute(attribute);
    }

    Object readAttribute(String attribute) {
      try {
        return ManagementFactory.getPlatformMBeanServer().getAttribute(name, attribute);
      } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}
