package ch.ivyteam.enginecockpit.monitor;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

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
  private List<StartEvent> filteredBeans;
  private String filter;
  private StartEvent selected;

  public StartEventBean() {
    refresh();
  }

  public void refresh() {
    try {
      beans = ManagementFactory.getPlatformMBeanServer()
              .queryNames(new ObjectName("ivy Engine:type=Process Start Event Bean,application=*,pm=*,pmv=*,name=*"), null)
              .stream()
              .map(StartEvent::new)
              .toList();
      beans = new ArrayList<>(beans);
    } catch (MalformedObjectNameException ex) {
      HANDLER.showError("Cannot read start event beans", ex);
    }
  }

  public List<StartEvent> getBeans() {
    return beans;
  }

  public List<StartEvent> getFilteredBeans() {
    return filteredBeans;
  }

  public void setFilteredBeans(List<StartEvent> filteredBeans) {
    this.filteredBeans = filteredBeans;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public StartEvent getSelected() {
    return selected;
  }

  public void setSelected(StartEvent selected) {
    this.selected = selected;
  }
}
