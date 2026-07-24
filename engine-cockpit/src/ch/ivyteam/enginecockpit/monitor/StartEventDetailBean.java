package ch.ivyteam.enginecockpit.monitor;

import java.io.Serializable;
import java.util.Hashtable;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.events.start.StartEvent;
import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.log.Logger;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named
@ViewScoped
public class StartEventDetailBean implements Serializable {

  private static final Logger LOGGER = Logger.getPackageLogger(StartEventDetailBean.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);
  private StartEvent event;

  private String context;
  private String app;
  private String version;
  private String project;
  private String name;

  public StartEventDetailBean() {}

  public String onLoad() {
    var hashtable = new Hashtable<String, String>();
    hashtable.put("type", "Process Start Event Bean");
    hashtable.put("context", context);
    hashtable.put("app", app);
    hashtable.put("version", version);
    hashtable.put("project", project);
    hashtable.put("name", name);
    try {
      event = new StartEvent(new ObjectName("ivy Engine", hashtable));
    } catch (MalformedObjectNameException ex) {
      HANDLER.showError("Cannot create MBean name", ex);
    }
    event.refresh();
    return null;
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

  public StartEvent getEvent() {
    return event;
  }
}
