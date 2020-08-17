package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.mbeans.value.MValueProvider.format;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MMonitor;
import ch.ivyteam.enginecockpit.monitor.mbeans.MSeries;

@ManagedBean
@ViewScoped
public class MailClientMonitorBean
{
  private static final String EXTERNAL_MAIL_SERVER = "ivy Engine:name=External Mail Server";

  private static final ExecutionCounter SENT_MAILS = new ExecutionCounter(EXTERNAL_MAIL_SERVER, "sentMails");
  
  private final MMonitor sentMonitor = MMonitor.build().name("Mails Sent").icon("email").toMonitor();
  private final MMonitor executionTimeMonitor = MMonitor.build().name("Execution Time").title("Mail Sending Execution Time").icon("timer").yAxisLabel("Execution Time [us]").toMonitor();
  
  public MailClientMonitorBean()
  {
    setupSentMonitor();
    setupExecutionTimeMonitor();
  }

  private void setupSentMonitor()
  {
    sentMonitor.addInfoValue(format("Count %d/%d Errors %d/%d", 
        SENT_MAILS.deltaExecutions(), SENT_MAILS.executions(), 
        SENT_MAILS.deltaErrors(), SENT_MAILS.errors()));
    sentMonitor.addSeries(new MSeries(SENT_MAILS.deltaExecutions(), "Mails"));
    sentMonitor.addSeries(new MSeries(SENT_MAILS.deltaErrors(), "Errors"));
  }
  
  private void setupExecutionTimeMonitor()
  {
    executionTimeMonitor.addInfoValue(format("Min %d Avg %d Max %d Total %d us",
        SENT_MAILS.deltaMinExecutionTime(),
        SENT_MAILS.deltaAvgExecutionTime(),
        SENT_MAILS.deltaMaxExecutionTime(),
        SENT_MAILS.executionTime()));
    executionTimeMonitor.addSeries(new MSeries(SENT_MAILS.deltaMinExecutionTime(), "Min"));
    executionTimeMonitor.addSeries(new MSeries(SENT_MAILS.deltaAvgExecutionTime(), "Avg"));
    executionTimeMonitor.addSeries(new MSeries(SENT_MAILS.deltaMaxExecutionTime(), "Max"));
  }

  public Monitor getSentMonitor()
  {
    return sentMonitor;
  } 

  public Monitor getExecutionTimeMonitor()
  {
    return executionTimeMonitor;
  } 
}
