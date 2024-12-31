package ch.ivyteam.enginecockpit.monitor;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.events.start.StartEvent;
import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.log.Logger;

@ManagedBean
@ViewScoped
public class StartEventBean {
  private static final Logger LOGGER = Logger.getPackageLogger(StartEventBean.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);

  private List<StartEvent> beans;
  private StartEvent selected;

  public StartEventBean() {
    refresh();
  }

  public void refresh() {
    try {
      // needs to be modifiable for sorting
      beans = ManagementFactory.getPlatformMBeanServer()
          .queryNames(new ObjectName("ivy Engine:type=Process Start Event Bean,application=*,pm=*,pmv=*,name=*"), null)
          .stream()
          .map(StartEvent::new)
          .collect(Collectors.toList());
    } catch (MalformedObjectNameException ex) {
      HANDLER.showError("Cannot read start event beans", ex);
    }
  }

  public List<StartEvent> getBeans() {
    return beans;
  }

  public StartEvent getSelected() {
    return selected;
  }

  public void setSelected(StartEvent selected) {
    this.selected = selected;
  }
}
