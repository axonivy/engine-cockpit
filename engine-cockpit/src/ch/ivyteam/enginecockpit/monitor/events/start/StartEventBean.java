package ch.ivyteam.enginecockpit.monitor.events.start;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.log.Logger;

@ManagedBean
@ViewScoped
public class StartEventBean {
  private static final Logger LOGGER = Logger.getPackageLogger(StartEventBean.class);

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
      showError("Cannot read start event beans", ex);
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

  private static void showError(String msg, Exception ex) {
    var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, ex.getMessage());
    FacesContext.getCurrentInstance().addMessage("msgs", message);
    LOGGER.error(msg, ex);
  }
}
