package ch.ivyteam.enginecockpit.monitor;

import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import ch.ivyteam.enginecockpit.model.LogView;


@ManagedBean
@ViewScoped
public class LogBean
{
  private LogView ivyLog;
  private LogView consoleLog;
  private LogView configLog;
  private Date date;
  private String showLog;
  
  public LogBean()
  {
    date = Calendar.getInstance().getTime();
    initLogFiles();
  }
  
  public void setShowLog(String showLog)
  {
    this.showLog = showLog;
  }
  
  public String getShowLog()
  {
    return showLog;
  }

  private void initLogFiles()
  {
    ivyLog = new LogView("ivy.log", date);
    consoleLog = new LogView("console.log", date);
    configLog = new LogView("config.log", date);
  }
  
  public LogView getIvyLog()
  {
    return ivyLog;
  }
  
  public LogView getConsoleLog()
  {
    return consoleLog;
  }
  
  public LogView getConfigLog()
  {
    return configLog;
  }
  
  public void setDate(Date date)
  {
    this.date = date;
    initLogFiles();
  }
  
  public Date getDate()
  {
    return date;
  }
  
  public Date getToday()
  {
    return Calendar.getInstance().getTime();
  }
  
}