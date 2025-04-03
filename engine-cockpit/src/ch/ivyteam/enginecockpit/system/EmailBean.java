package ch.ivyteam.enginecockpit.system;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.util.EmailUtil;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;
import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.ivy.mail.MailClient;
import ch.ivyteam.ivy.mail.MailClientConfigProvider;
import ch.ivyteam.ivy.mail.MailMessage;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class EmailBean {
  private String host;
  private int port;
  private String email;
  private String user;
  private String triggerTime;
  private String sendTo;
  private String subject;
  private String message;

  private boolean sent;
  private String debugLog;
  private Exception exception;

  public EmailBean() {
    initEmailConfigs();
  }

  private void initEmailConfigs() {
    var config = MailClientConfigProvider.get(null);
    host = config.host();
    port = config.port();
    email = config.from();
    user = config.user();
    triggerTime = IConfiguration.instance().getOrDefault(EmailUtil.DAILYTASKSUMMARY_TRIGGERTIME);
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

  public String getTriggerTime() {
    return triggerTime;
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

  public void sendTestMail() throws IOException {
    try (var out = new ByteArrayOutputStream();
        var print = new PrintStream(out)) {
      try (var mailClient = MailClient.newMailClient(print)) {

        var mailMessage = MailMessage.create()
            .to(sendTo)
            .subject(subject)
            .textContent(message)
            .toMailMessage();

        sent = true;
        try {
          mailClient.send(mailMessage);
        } catch (Exception ex) {
          exception = ex;
        } finally {
          debugLog = out.toString();
          if (exception != null) {
            debugLog += ExceptionUtils.getStackTrace(exception);
          }
        }
      }
    }
  }

  public String getDebugLog() {
    return debugLog;
  }

  public String getResult() {
    if (!sent) {
      return "";
    }
    if (exception == null) {
      return "Success";
    }
    return "Failure";
  }

  public boolean isSuccess() {
    if (!sent) {
      return false;
    }
    return exception == null;
  }

  public boolean isFailed() {
    if (!sent) {
      return false;
    }
    return exception != null;
  }

  public String getError() {
    return exception == null ? "" : exception.getMessage();
  }
}
