package ch.ivyteam.enginecockpit.monitor;

import java.io.Serializable;
import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.events.intermediate.IntermediateEvent;
import ch.ivyteam.enginecockpit.monitor.performance.jfr.JfrBean;
import ch.ivyteam.log.Logger;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class IntermediateEventDetailBean implements Serializable {

  private static final Logger LOGGER = Logger.getPackageLogger(JfrBean.class);
  private IntermediateEvent event;
  private String context;
  private String app;
  private String version;
  private String project;
  private String name;

  public String onLoad() {
    var hashtable = new Hashtable<String, String>();
    hashtable.put("type", "Process Intermediate Event Bean");
    hashtable.put("context", context);
    hashtable.put("app", app);
    hashtable.put("version", version);
    hashtable.put("project", project);
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

  public String getContext() {
    return context;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public String getApp() {
    return app;
  }

  public void setApp(String app) {
    this.app = app;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
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
