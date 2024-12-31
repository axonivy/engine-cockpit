package ch.ivyteam.enginecockpit.system;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.util.EmailUtil;
import ch.ivyteam.ivy.mail.MailClientConfigProvider;

@ManagedBean
@ViewScoped
public class EmailBean {

  private String host;
  private int port;
  private String email;
  private String user;
  private String sendTo;
  private String subject;
  private String message;

  public EmailBean() {
    initEmailConfigs();
  }

  private void initEmailConfigs() {
    var config = MailClientConfigProvider.get();
    host = config.host();
    port = config.port();
    email = config.from();
    user = config.user();
    subject = "Test Mail";
    message = "This is a test mail.";
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getEmail() {
    return email;
  }

  public String getUser() {
    return user;
  }

  public String getSendTo() {
    return sendTo;
  }

  public void setSendTo(String sendTo) {
    this.sendTo = sendTo;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void sendTestMail() {
    FacesMessage facesMessage;
    try {
      EmailUtil.sendTestMail(subject, sendTo, message);
      facesMessage = new FacesMessage("Successfully sent test mail", "");
    } catch (Exception ex) {
      facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while sending test mail",
          ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage("msgs", facesMessage);
  }
}
