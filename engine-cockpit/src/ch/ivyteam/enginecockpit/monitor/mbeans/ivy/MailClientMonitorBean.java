package ch.ivyteam.enginecockpit.monitor.mbeans.ivy;

import static ch.ivyteam.enginecockpit.monitor.value.ValueProvider.format;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.monitor.monitor.Monitor;
import ch.ivyteam.enginecockpit.monitor.monitor.Series;

@ManagedBean
@ViewScoped
public class MailClientMonitorBean {
  private static final String EXTERNAL_MAIL_SERVER = "ivy Engine:name=External Mail Server";

  private static final ExecutionCounter SENT_MAILS = new ExecutionCounter(EXTERNAL_MAIL_SERVER, "sentMails");

  private final Monitor sentMonitor = Monitor.build().name("Mails Sent").icon("email").toMonitor();
  private final Monitor executionTimeMonitor = Monitor.build().name("Execution Time")
      .title("Mail Sending Execution Time").icon("timer").yAxisLabel("Execution Time").toMonitor();

  public MailClientMonitorBean() {
    setupSentMonitor();
    setupExecutionTimeMonitor();
  }

  private void setupSentMonitor() {
    sentMonitor.addInfoValue(format("Count %5d/%5d Errors %5d/%5d",
        SENT_MAILS.deltaExecutions(), SENT_MAILS.executions(),
        SENT_MAILS.deltaErrors(), SENT_MAILS.errors()));
    sentMonitor.addSeries(Series.build(SENT_MAILS.deltaExecutions(), "Mails").toSeries());
    sentMonitor.addSeries(Series.build(SENT_MAILS.deltaErrors(), "Errors").toSeries());
  }

  private void setupExecutionTimeMonitor() {
    executionTimeMonitor.addInfoValue(format("Min %t Avg %t Max %t Total %t",
        SENT_MAILS.deltaMinExecutionTime(),
        SENT_MAILS.deltaAvgExecutionTime(),
        SENT_MAILS.deltaMaxExecutionTime(),
        SENT_MAILS.executionTime()));
    executionTimeMonitor.addSeries(Series.build(SENT_MAILS.deltaMinExecutionTime(), "Min").toSeries());
    executionTimeMonitor.addSeries(Series.build(SENT_MAILS.deltaAvgExecutionTime(), "Avg").toSeries());
    executionTimeMonitor.addSeries(Series.build(SENT_MAILS.deltaMaxExecutionTime(), "Max").toSeries());
  }

  public Monitor getSentMonitor() {
    return sentMonitor;
  }

  public Monitor getExecutionTimeMonitor() {
    return executionTimeMonitor;
  }
}
