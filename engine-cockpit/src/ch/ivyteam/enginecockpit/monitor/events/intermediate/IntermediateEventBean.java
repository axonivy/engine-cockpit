package ch.ivyteam.enginecockpit.monitor.events.intermediate;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.events.start.StartEventBean;
import ch.ivyteam.log.Logger;


@ManagedBean
@ViewScoped
public class IntermediateEventBean {
  private static final Logger LOGGER = Logger.getPackageLogger(StartEventBean.class);

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
      showError("Cannot read intermediate event bean", ex);
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

  private static void showError(String msg, Exception ex) {
    var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, ex.getMessage());
    FacesContext.getCurrentInstance().addMessage("msgs", message);
    LOGGER.error(msg, ex);
  }

}
