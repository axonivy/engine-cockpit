package ch.ivyteam.enginecockpit.monitor.events.intermediate;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.events.Bean;


@ManagedBean
@ViewScoped
public class IntermediateEventBean {

  private List<IntermediateBean> intermediateEvents;
  private IntermediateBean selected;

  public IntermediateEventBean() {
    refresh();
  }

  public void refresh() {
    try {
      intermediateEvents = ManagementFactory.getPlatformMBeanServer()
              .queryNames(new ObjectName("ivy Engine:type=Process Intermediate Event Bean,application=*,pm=*,pmv=*,name=*"), null)
              .stream()
              .map(IntermediateBean::new)
              .toList();
    } catch (MalformedObjectNameException ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<IntermediateBean> getIntermediateEvents() {
    return intermediateEvents;
  }

  public IntermediateBean getSelected() {
    return selected;
  }

  public void setSelected(IntermediateBean selected) {
    this.selected = selected;
  }

  public static final class IntermediateBean extends Bean {

    public IntermediateBean(ObjectName name) {
      super(name);
    }

    @Override
    public String getFullRequestPath() {
      return getApplication() +
              "/" +
              getPm() +
              "$" +
              getPmv() +
              "/" +
              getProcessElementId();
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

    @Override
    public long getExecutions() {
      return readLongAttribute("firings");
    }

    @Override
    public long getErrors() {
      return readLongAttribute("firingErrors");
    }
  }
}
