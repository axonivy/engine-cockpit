package ch.ivyteam.enginecockpit.monitor;

import java.io.Serializable;
import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import ch.ivyteam.enginecockpit.monitor.events.intermediate.IntermediateEvent;
import ch.ivyteam.enginecockpit.monitor.performance.jfr.JfrBean;
import ch.ivyteam.log.Logger;

@Named
@ViewScoped
public class IntermediateEventDetailBean implements Serializable {

  private static final Logger LOGGER = Logger.getPackageLogger(JfrBean.class);
  private IntermediateEvent event;
  private String application;
  private String pm;
  private String pmv;
  private String name;

  public IntermediateEventDetailBean() {}

  public String onLoad() {
    var hashtable = new Hashtable<String, String>();
    hashtable.put("type", "Process Intermediate Event Bean");
    hashtable.put("application", application);
    hashtable.put("pm", pm);
    hashtable.put("pmv", pmv);
    hashtable.put("name", name);
    try {
      event = new IntermediateEvent(new ObjectName("ivy Engine", hashtable));
    } catch (MalformedObjectNameException ex) {
      showError("Cannot create MBean name", ex);
    }
    event.refresh();
    return "";
  }

  private static void showError(String msg, Exception ex) {
    var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, ex.getMessage());
    FacesContext.getCurrentInstance().addMessage("msgs", message);
    LOGGER.error(msg, ex);
  }

  public String getApplication() {
    return application;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getPm() {
    return pm;
  }

  public void setPm(String pm) {
    this.pm = pm;
  }

  public String getPmv() {
    return pmv;
  }

  public void setPmv(String pmv) {
    this.pmv = pmv;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public IntermediateEvent getEvent() {
    return event;
  }

}
