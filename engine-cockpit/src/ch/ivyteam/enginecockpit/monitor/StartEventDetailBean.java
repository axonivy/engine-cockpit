package ch.ivyteam.enginecockpit.monitor;

import java.io.Serializable;
import java.util.Hashtable;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.events.start.StartEvent;
import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.log.Logger;

@Named
@ViewScoped
public class StartEventDetailBean implements Serializable {

  private static final Logger LOGGER = Logger.getPackageLogger(StartEventDetailBean.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);
  private StartEvent event;
  private String application;
  private String pm;
  private String pmv;
  private String name;

  public StartEventDetailBean() {}

  public String onLoad() {
    var hashtable = new Hashtable<String, String>();
    hashtable.put("type", "Process Start Event Bean");
    hashtable.put("application", application);
    hashtable.put("pm", pm);
    hashtable.put("pmv", pmv);
    hashtable.put("name", name);
    try {
      event = new StartEvent(new ObjectName("ivy Engine", hashtable));
    } catch (MalformedObjectNameException ex) {
      HANDLER.showError("Cannot create MBean name", ex);
    }
    event.refresh();
    return null;
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

  public StartEvent getEvent() {
    return event;
  }

}
