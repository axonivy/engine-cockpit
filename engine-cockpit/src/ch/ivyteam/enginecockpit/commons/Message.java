package ch.ivyteam.enginecockpit.commons;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class Message {

  public static Builder info() {
    return new Builder(FacesMessage.SEVERITY_INFO);
  }

  public static Builder error() {
    return new Builder(FacesMessage.SEVERITY_ERROR).summary("Error");
  }

  @SuppressWarnings("hiding")
  public static class Builder {

    private final Severity severity;
    private String clientId;
    private String summary;
    private String detail;

    public Builder(Severity severity) {
      this.severity = severity;
    }

    public Builder clientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public Builder summary(String summary) {
      this.summary = summary;
      return this;
    }

    public Builder detail(String detail) {
      this.detail = detail;
      return this;
    }

    public Builder exception(Exception exception) {
      this.detail = exception.getMessage();
      return this;
    }

    public void show() {
      var msg = new FacesMessage(severity, summary, detail);
      FacesContext.getCurrentInstance().addMessage(clientId, msg);
    }
  }
}
