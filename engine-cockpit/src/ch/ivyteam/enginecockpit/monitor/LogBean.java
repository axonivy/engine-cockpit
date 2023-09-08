package ch.ivyteam.enginecockpit.monitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.download.AllResourcesDownload;
import ch.ivyteam.enginecockpit.monitor.model.LogView;
import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.ivy.log.provider.LogFileRepository;
import ch.ivyteam.ivy.log.provider.LogFileZipper;

@ManagedBean
@ViewScoped
public class LogBean implements AllResourcesDownload {
  private List<LogView> logs;
  private LocalDate date;
  private String showLog;

  public LogBean() {
    date = LocalDate.now();
    initLogFiles();
  }

  public void setShowLog(String showLog) {
    this.showLog = showLog;
  }

  public String getShowLog() {
    return showLog;
  }

  private void initLogFiles() {
    logs = LogFileRepository.instance().byDate(date)
            .map(log -> new LogView(log))
            .toList();
  }

  public void onload() {
    if (logs.isEmpty()) {
      return;
    }
    if (showLog == null) {
      showLog = logs.get(0).getFileName();
    }
  }

  public List<LogView> getLogs() {
    return logs;
  }

  public void setDate(LocalDate date) {
    this.date = date;
    initLogFiles();
  }

  public LocalDate getDate() {
    return date;
  }

  public LocalDate getToday() {
    return LocalDate.now();
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
      LogFileZipper.zipTo(new FileOutputStream(zipFile));
      return zipFile;
    } catch (IOException ex) {
      var message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
      FacesContext.getCurrentInstance().addMessage("msgs", message);
    }
    return null;
  }

}
