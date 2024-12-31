package ch.ivyteam.enginecockpit.monitor;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.events.intermediate.IntermediateEvent;
import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.log.Logger;

@ManagedBean
@ViewScoped
public class IntermediateEventBean {
  private static final Logger LOGGER = Logger.getPackageLogger(StartEventBean.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);

  private List<IntermediateEvent> beans;
  private IntermediateEvent selected;

  public IntermediateEventBean() {
    refresh();
  }

  public void refresh() {
    try {
      // needs to be modifiable for sorting
      beans = ManagementFactory.getPlatformMBeanServer()
          .queryNames(new ObjectName("ivy Engine:type=Process Intermediate Event Bean,application=*,pm=*,pmv=*,name=*"), null)
          .stream()
          .map(IntermediateEvent::new)
          .collect(Collectors.toList());
    } catch (MalformedObjectNameException ex) {
      HANDLER.showError("Cannot read intermediate event bean", ex);
    }
  }

  public List<IntermediateEvent> getBeans() {
    return beans;
  }

  public IntermediateEvent getSelected() {
    return selected;
  }

  public void setSelected(IntermediateEvent selected) {
    this.selected = selected;
  }
}
