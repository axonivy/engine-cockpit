package ch.ivyteam.enginecockpit.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import ch.ivyteam.log.Logger;

public class ErrorHandler {

  private final String growlId;
  private final Logger logger;

  public ErrorHandler(String growlId, Logger logger) {
    this.growlId = growlId;
    this.logger = logger;
  }

  public void showError(String msg, Exception ex) {
    var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, ex.getMessage());
    FacesContext.getCurrentInstance().addMessage(growlId, message);
    logger.error(msg, ex);
  }
}
