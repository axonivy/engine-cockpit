package ch.ivyteam.enginecockpit.util;

import java.util.regex.Pattern;

import ch.ivyteam.ivy.mail.MailClient;
import ch.ivyteam.ivy.mail.MailMessage;

public class EmailUtil
{
  private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
  public static final String DAILYTASKSUMMARY_TRIGGERTIME = "EMail.DailyTaskSummary.TriggerTime";

  public static void sendTestMail(String subject, String to, String message) throws Exception
  {
    try (var mailClient = MailClient.newMailClient())
    {
        var mailMessage = MailMessage.create()
            .to(to)
            .subject(subject)
            .textContent(message)
            .toMailMessage();
        mailClient.send(mailMessage);
    }
  }

  public static boolean validateEmailAddress(String email)
  {
    if (email == null)
    {
      return false;
    }
    return EMAIL_REGEX.matcher(email).matches();
  }
}
