package ch.ivyteam.enginecockpit.monitor;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.log.Logger;

@ManagedBean
@ViewScoped
public class StartEventBean {
  private static final Logger LOGGER = Logger.getPackageLogger(StartEventBean.class);

  private List<Bean> beans;
  private List<Bean> filteredBeans;
  private String filter;
  private Bean selected;

  public StartEventBean() {
    refresh();
  }

  public void refresh() {
    try {
      beans = ManagementFactory.getPlatformMBeanServer()
              .queryNames(new ObjectName("ivy Engine:type=Process Start Event Bean,application=*,pm=*,pmv=*,name=*"), null)
              .stream()
              .map(Bean::new)
              .toList();
      beans = new ArrayList<>(beans);
    } catch (MalformedObjectNameException ex) {
      showError("Cannot read start event beans", ex);
    }
  }

  public List<Bean> getBeans() {
    return beans;
  }

  public List<Bean> getFilteredBeans() {
    return filteredBeans;
  }

  public void setFilteredBeans(List<Bean> filteredBeans) {
    this.filteredBeans = filteredBeans;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public Bean getSelected() {
    return selected;
  }

  public void setSelected(Bean selected) {
    this.selected = selected;
  }

  private static void showError(String msg, Exception ex) {
    var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, ex.getMessage());
    FacesContext.getCurrentInstance().addMessage("msgs", message);
    LOGGER.error(msg, ex);
  }

  public static final class Bean {

    private static final String NOT_AVAILABLE = "n.a.";
    private static final Object[] EMPTY_PARAMS = new Object[0];
    private static final String[] EMPTY_TYPES = new String[0];
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

    public String getFullRequestPath() {
      return getApplication() +
             "/"+
             getPm() +
             "$" +
             getPmv() +
             "/" +
             readStringAttribute("requestPath");
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

    public String getRequestPath() {
      return readStringAttribute("requestPath");
    }

    public boolean isRunning() {
      return (Boolean)readAttribute("running");
    }

    public String getNextPollTime() {
      return getDateAttribute("nextPollTime");
    }

    public long getTimeUntilNextPoll() {
      return readLongAttribute("timeUntilNextPoll");
    }

    public String getTimeUntilNextPollFormated() {
      return formatMillis(readLongAttribute("timeUntilNextPoll"));
    }

    public long getExecutions() {
      return readLongAttribute("processExecutions");
    }

    public long getErrors() {
      return readLongAttribute("processExecutionErrors");
    }

    public void poll() {
      try {
        ManagementFactory.getPlatformMBeanServer().invoke(name, "pollNow", EMPTY_PARAMS, EMPTY_TYPES);
      } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
        showError("Cannot poll bean", ex);
      }
    }

    public void start() {
      try {
        ManagementFactory.getPlatformMBeanServer().invoke(name, "start", EMPTY_PARAMS, EMPTY_TYPES);
      } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
        showError("Cannot start bean", ex);
      }
    }

    public void stop() {
      try {
        ManagementFactory.getPlatformMBeanServer().invoke(name, "stop", EMPTY_PARAMS, EMPTY_TYPES);
      } catch (InstanceNotFoundException | ReflectionException | MBeanException ex) {
        showError("Cannot stop bean", ex);
      }
    }

    private String formatMillis(long value) {
      return format(value, Unit.MILLI_SECONDS);
    }

    private String format(long value, Unit baseUnit) {
      if (value < 0) {
        return "n.a.";
      }
      Unit unit = baseUnit;
      var scaledValue = baseUnit.convertTo(value, unit);
      while (shouldScaleUp(unit, scaledValue)) {
        unit = unit.scaleUp();
        scaledValue = baseUnit.convertTo(value, unit);
      }
      return scaledValue+" "+ unit.symbol();
    }

    private boolean shouldScaleUp(Unit unit, long scaledValue) {
      if (Unit.MICRO_SECONDS.equals(unit) || Unit.MILLI_SECONDS.equals(unit)) {
        return scaledValue >= 1000;
      }
      return scaledValue >= 100;
    }

    private String getDateAttribute(String attributeName) {
      Date date = (Date)readAttribute(attributeName);
      if (date == null) {
        return NOT_AVAILABLE;
      }
      return DateUtil.formatDate(date);
    }

    private long readLongAttribute(String attribute) {
      return (long)readAttribute(attribute);
    }

    private String readStringAttribute(String attribute) {
      return (String)readAttribute(attribute);
    }

    Object readAttribute(String attribute) {
      try {
        return ManagementFactory.getPlatformMBeanServer().getAttribute(name, attribute);
      } catch (InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException ex) {
        showError("Cannot read attribute " + attribute, ex);
        return "";
      }
    }
  }
}
