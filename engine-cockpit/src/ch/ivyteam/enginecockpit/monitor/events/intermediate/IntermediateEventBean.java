package ch.ivyteam.enginecockpit.monitor.events.intermediate;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;


@ManagedBean
@ViewScoped
public class IntermediateEventBean {

  private List<IntermediateEvent> intermediateEvents;
  private IntermediateEvent selected;

  public IntermediateEventBean() {
    refresh();
  }

  public void refresh() {
    try {
      intermediateEvents = ManagementFactory.getPlatformMBeanServer()
              .queryNames(new ObjectName("ivy Engine:type=Process Intermediate Event Bean,application=*,pm=*,pmv=*,name=*"), null)
              .stream()
              .map(IntermediateEvent::new)
              .toList();
    } catch (MalformedObjectNameException ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<IntermediateEvent> getIntermediateEvents() {
    return intermediateEvents;
  }

  public IntermediateEvent getSelected() {
    return selected;
  }

  public void setSelected(IntermediateEvent selected) {
    this.selected = selected;
  }

}
