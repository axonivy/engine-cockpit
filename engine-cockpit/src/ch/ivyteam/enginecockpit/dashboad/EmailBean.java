package ch.ivyteam.enginecockpit.dashboad;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import ch.ivyteam.enginecockpit.util.EmailUtil;
import ch.ivyteam.ivy.configuration.restricted.IConfiguration;

@SuppressWarnings("restriction")
@ManagedBean
@ViewScoped
public class EmailBean
{
  private String host;
  private int port;
  private String email;
  private String user;
  private String triggerTime;
  private String sendTo;
  private String subject;
  private String message;
  
  public EmailBean()
  {
    initEmailConfigs();
  }
  
  private void initEmailConfigs()
  {
    host = IConfiguration.get().getOrDefault(EmailUtil.HOST);
    port = IConfiguration.get().getOrDefault(EmailUtil.PORT, int.class);
    email = IConfiguration.get().getOrDefault(EmailUtil.MAIL_ADDRESS);
    user = IConfiguration.get().getOrDefault(EmailUtil.USER);
    triggerTime = IConfiguration.get().getOrDefault(EmailUtil.DAILYTASKSUMMARY_TRIGGERTIME);
    subject = "Test Mail";
    message = "This is a test mail.";
  }
  
  public String getHost()
  {
    return host;
  }

  public int getPort()
  {
    return port;
  }

  public String getEmail()
  {
    return email;
  }

  public String getUser()
  {
    return user;
  }

  public String getTriggerTime()
  {
    return triggerTime;
  }
  
  public String getSendTo()
  {
    return sendTo;
  }

  public void setSendTo(String sendTo)
  {
    this.sendTo = sendTo;
  }

  public String getSubject()
  {
    return subject;
  }

  public void setSubject(String subject)
  {
    this.subject = subject;
  }

  public String getMessage()
  {
    return message;
  }

  public void setMessage(String message)
  {
    this.message = message;
  }

  public void sendTestMail()
  {
    FacesMessage facesMessage;
    try
    {
      EmailUtil.sendTestMail(subject, sendTo, message);
      facesMessage = new FacesMessage("Successful send test mail", "");
    }
    catch (Exception ex)
    {
      facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error while sending test mail", ex.getMessage());
    }
    FacesContext.getCurrentInstance().addMessage("msgs", facesMessage);
  }
}
