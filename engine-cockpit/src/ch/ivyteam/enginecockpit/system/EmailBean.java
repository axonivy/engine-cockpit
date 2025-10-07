package ch.ivyteam.enginecockpit.system;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.faces.bean.ManagedBean;
import javax.faces.view.ViewScoped;

import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.mail.MailMessage;
import ch.ivyteam.ivy.mail.impl.MailClientConfigProvider;
import ch.ivyteam.ivy.mail.impl.MailClientImpl;

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

  private boolean sent;
  private String debugLog;
  private Exception exception;

  public EmailBean() {
    initEmailConfigs();
  }

  private void initEmailConfigs() {
    var config = MailClientConfigProvider.get();
    host = config.host();
    port = config.port();
    email = config.from();
    user = config.user();
    subject = Ivy.cm().co("/sendtestmail/TestMailSubjectDefault");
    message = Ivy.cm().co("/sendtestmail/TestMailMessageDefault");
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

  public void sendTestMail() throws IOException {
    try (var out = new ByteArrayOutputStream();
        var print = new PrintStream(out)) {
      try (var mailClient = MailClientImpl.newMailClient(print)) {
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
      } catch (Exception ex1) {
        throw new RuntimeException(ex1);
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
      return Ivy.cm().co("/common/Success");
    }
    return Ivy.cm().co("/common/Failure");
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
