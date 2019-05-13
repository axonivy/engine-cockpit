package ch.ivyteam.enginecockpit.util;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ch.ivyteam.ivy.email.EmailConstants.EmailEncryption;
import ch.ivyteam.ivy.email.EmailSetupConfiguration;
import ch.ivyteam.ivy.email.SimpleMailSender;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.process.model.element.activity.service.EMailConfig;
import ch.ivyteam.ivy.process.model.element.activity.value.email.Attachments;
import ch.ivyteam.ivy.process.model.element.activity.value.email.Headers;
import ch.ivyteam.ivy.process.model.element.activity.value.email.Headers.HeaderFields;
import ch.ivyteam.ivy.process.model.element.value.MacroExpression;

@SuppressWarnings("restriction")
public class EmailUtil
{
  public static final String HOST = "EMail.Server.Host";
  public static final String PORT = "EMail.Server.Port";
  public static final String MAIL_ADDRESS = "EMail.Server.MailAddress";
  public static final String USER = "EMail.Server.User";
  public static final String PASSWORD = "EMail.Server.Password";
  public static final String ENCRYPTION_METHOD = "EMail.Server.EncryptionMethod";
  public static final String DAILYTASKSUMMARY_TRIGGERTIME = "EMail.DailyTaskSummary.TriggerTime";
  
  public static void sendTestMail(String subject, String to, String message) throws Exception
  {
    Map<HeaderFields, MacroExpression> rawHeader = new HashMap<>();
    rawHeader.put(HeaderFields.SUBJECT, new MacroExpression(subject));
    rawHeader.put(HeaderFields.TO, new MacroExpression(to));
    rawHeader.put(HeaderFields.FROM, new MacroExpression(Configuration.getOrDefault(MAIL_ADDRESS)));
    EMailConfig emailConfig = new EMailConfig(new Headers(rawHeader), new MacroExpression(message), new Attachments(), false);

    new SimpleMailSender(Collections.emptyMap(), emailConfig, Ivy.log(), getConfigEmailSetup()).sendMessage();
  }
  
  private static EmailSetupConfiguration getConfigEmailSetup()
  {
    EmailSetupConfiguration emailSetupConfiguration = new EmailSetupConfiguration();
    emailSetupConfiguration.setSmtpPort(Configuration.getOrDefault(PORT, int.class));
    emailSetupConfiguration.setSmtpServer(Configuration.getOrDefault(HOST));
    emailSetupConfiguration.setSmtpUser(Configuration.getOrDefault(USER));
    emailSetupConfiguration.setSmtpPassword(Configuration.getOrDefault(PASSWORD));
    emailSetupConfiguration.setSmtpEncryptionMethod(Configuration.getOrDefault(ENCRYPTION_METHOD, EmailEncryption.class));
    return emailSetupConfiguration;
  }

}
