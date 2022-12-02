package ch.ivyteam.enginecockpit.monitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
      logs = Files.walk(UrlUtil.getLogDir().toRealPath())
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
    try (var out = new ByteArrayOutputStream()) {
      DownloadUtil.zipDir(UrlUtil.getLogDir().toRealPath(), out);
      return DefaultStreamedContent
              .builder()
              .stream(() -> new ByteArrayInputStream(out.toByteArray()))
              .contentType("application/zip")
              .name("logs.zip")
              .build();
    } catch (IOException ex) {
      var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
      FacesContext.getCurrentInstance().addMessage("msgs", message);
    }
    return null;
  }

}
