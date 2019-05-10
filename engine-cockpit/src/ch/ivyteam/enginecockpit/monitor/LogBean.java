package ch.ivyteam.enginecockpit.monitor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang3.time.DateUtils;

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
    String formatDate = "";
    if (date != null && !DateUtils.isSameDay(date, getToday()))
    {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
      formatDate = "." + format.format(date);
    }
    ivyLog = new LogView("ivy.log" + formatDate);
    consoleLog = new LogView("console.log" + formatDate);
    configLog = new LogView("config.log" + formatDate);
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