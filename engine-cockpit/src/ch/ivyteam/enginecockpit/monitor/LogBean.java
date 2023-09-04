package ch.ivyteam.enginecockpit.monitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.download.AllResourcesDownload;
import ch.ivyteam.enginecockpit.monitor.model.LogView;
import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.enginecockpit.util.UrlUtil;

@ManagedBean
@ViewScoped
public class LogBean implements AllResourcesDownload {
  private List<LogView> logs;
  private Date date;
  private String showLog;

  public LogBean() {
    date = Calendar.getInstance().getTime();
    initLogFiles();
  }

  public void setShowLog(String showLog) {
    this.showLog = showLog;
  }

  public String getShowLog() {
    return showLog;
  }

  private void initLogFiles() {
    try {
      logs = Files.walk(UrlUtil.getLogDir().toRealPath() , FileVisitOption.FOLLOW_LINKS)
        .filter(Files::isRegularFile)
        .filter(log -> log.toString().endsWith(".log"))
        .map(log -> new LogView(log.getFileName().toString(), date))
        .sorted()
        .collect(Collectors.toList());
    } catch (IOException ex) {
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not load logs", ex.getMessage()));
    }
  }

  public List<LogView> getLogs() {
    return logs;
  }

  public void setDate(Date date) {
    this.date = date;
    initLogFiles();
  }

  public Date getDate() {
    return date;
  }

  public Date getToday() {
    return Calendar.getInstance().getTime();
  }

  @Override
  public StreamedContent getAllResourcesDownload() {
    var zipFile = writeLogsToZip();
    return DefaultStreamedContent
            .builder()
            .stream(() -> DownloadUtil.getFileStream(zipFile))
            .contentType("application/zip")
            .name("logs.zip")
            .build();
  }

  private File writeLogsToZip() {
    try {
      var zipFile = Files.createTempFile("logs", ".zip").toFile();
      DownloadUtil.zipDir(UrlUtil.getLogDir().toRealPath(), new FileOutputStream(zipFile));
      return zipFile;
    } catch (IOException ex) {
      var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
      FacesContext.getCurrentInstance().addMessage("msgs", message);
    }
    return null;
  }

}
