package ch.ivyteam.enginecockpit.commons;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class Message {

  public static void info(String clientId, String summary) {
    var msg = new FacesMessage(summary, "");
    FacesContext.getCurrentInstance().addMessage(clientId, msg);
  }

  public static void error(String clientId, Exception ex) {
    var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ex.getMessage());
    FacesContext.getCurrentInstance().addMessage(clientId, msg);
  }
}
