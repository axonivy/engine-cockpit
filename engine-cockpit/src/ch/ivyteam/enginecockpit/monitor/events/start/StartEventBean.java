package ch.ivyteam.enginecockpit.monitor.events.start;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

@ManagedBean
@ViewScoped
public class StartEventBean {

  private List<StartBean> beans;
  private List<StartBean> filteredBeans;
  private String filter;
  private StartBean selected;

  public StartEventBean() {
    refresh();
  }

  public void refresh() {
    try {
      beans = ManagementFactory.getPlatformMBeanServer()
              .queryNames(new ObjectName("ivy Engine:type=Process Start Event Bean,application=*,pm=*,pmv=*,name=*"), null)
              .stream()
              .map(StartBean::new)
              .toList();
      beans = new ArrayList<>(beans);
    } catch (MalformedObjectNameException ex) {
      throw new RuntimeException(ex);
    }
  }

  public List<StartBean> getBeans() {
    return beans;
  }

  public List<StartBean> getFilteredBeans() {
    return filteredBeans;
  }

  public void setFilteredBeans(List<StartBean> filteredBeans) {
    this.filteredBeans = filteredBeans;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public StartBean getSelected() {
    return selected;
  }

  public void setSelected(StartBean selected) {
    this.selected = selected;
  }

}
