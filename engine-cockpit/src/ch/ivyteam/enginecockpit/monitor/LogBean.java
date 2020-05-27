package ch.ivyteam.enginecockpit.monitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.model.LogView;
import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.enginecockpit.util.UrlUtil;


@ManagedBean
@ViewScoped
public class LogBean
{
  private LogView ivyLog;
  private LogView consoleLog;
  private LogView configLog;
  private LogView deprecationLog;
  private LogView usersynchLog;
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
    deprecationLog = new LogView("deprecation.log", date);
    usersynchLog = new LogView("usersynch.log", date);
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
  
  public LogView getDeprecationLog()
  {
    return deprecationLog;
  }
  
  public LogView getUserSynchLog()
  {
    return usersynchLog;
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
  
  public StreamedContent getAllLogs() throws IOException 
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DownloadUtil.zipDir(UrlUtil.getLogDir().toPath().toRealPath(), out);
    return new DefaultStreamedContent(new ByteArrayInputStream(out.toByteArray()), "application/zip", "logs.zip");
  }
  
}