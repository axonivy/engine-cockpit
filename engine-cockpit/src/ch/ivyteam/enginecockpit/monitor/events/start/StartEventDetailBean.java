package ch.ivyteam.enginecockpit.monitor.events.start;

import java.util.Hashtable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.ivyteam.enginecockpit.monitor.performance.jfr.JfrBean;
import ch.ivyteam.log.Logger;

@ManagedBean
@ViewScoped
public class StartEventDetailBean {

  private static final Logger LOGGER = Logger.getPackageLogger(JfrBean.class);
  private StartBean startBean;
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
      startBean = new StartBean(new ObjectName("ivy Engine", hashtable));
    } catch (MalformedObjectNameException ex) {
      showError("Cannot create MBean name", ex);
    }
    startBean.refresh();
    return "";
  }

  private static void showError(String msg, Exception ex) {
    var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, ex.getMessage());
    FacesContext.getCurrentInstance().addMessage("msgs", message);
    LOGGER.error(msg, ex);
  }

}
