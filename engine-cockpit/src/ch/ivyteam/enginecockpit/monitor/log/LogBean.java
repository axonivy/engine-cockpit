package ch.ivyteam.enginecockpit.monitor.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.commons.ResponseHelper;
import ch.ivyteam.enginecockpit.download.AllResourcesDownload;
import ch.ivyteam.enginecockpit.util.DownloadUtil;
import ch.ivyteam.ivy.log.provider.LogFileRepository;
import ch.ivyteam.ivy.log.provider.LogFileZipper;

@ManagedBean
@ViewScoped
public class LogBean implements AllResourcesDownload {

  private List<LogView> logs;
  private LocalDate date;
  private String file;
  private LogView logView;

  public void setFile(String file) {
    this.file = file;
  }

  public String getFile() {
    return file;
  }

  public void setDate(String date) {
    var parts = date.split("-");
    if (parts.length != 3) {
      return;
    }
    if (Arrays.stream(parts).anyMatch(p -> !StringUtils.isNumeric(p))) {
      return;
    }
    var year = Integer.parseInt(parts[0]);
    var month = Integer.parseInt(parts[1]);
    var day = Integer.parseInt(parts[2]);
    this.date = LocalDate.of(year, month, day);
  }

  public String getDate() {
    if (date == null) {
      return "";
    }
    return toString(date);
  }

  public static String toString(LocalDate date) {
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return date.format(formatter);
  }

  public void onload() {
    if (date == null) {
      date = LocalDate.now();
    }

    logs = LogFileRepository.instance().byDate(date)
            .map(LogView::new)
            .collect(Collectors.toList());

    if (logs.isEmpty()) {
      return;
    }

    if (file == null) {
      file = logs.get(0).getFileName();
    }

    logView = logs.stream()
            .filter(l -> l.getFileName().equals(file))
            .findAny()
            .orElse(null);
    if (logView == null) {
      ResponseHelper.notFound("Log " + file + " does not exist");
    }
  }

  public void navigate() throws IOException {
    var path = LogView.uri().file(file).date(date).toUri();
    FacesContext.getCurrentInstance().getExternalContext().redirect(path);
  }

  public LocalDate getToday() {
    return LocalDate.now();
  }

  public List<LogView> getLogs() {
    return logs;
  }

  public LogView getLog() {
    return logView;
  }

  public void setLog(LogView log) {
    this.logView = log;
  }

  public void setLocalDate(LocalDate date) {
    this.date = date;
  }

  public LocalDate getLocalDate() {
    return date;
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
      var zipFile = Files.createTempFile("logs", ".zip");
      try (var out = Files.newOutputStream(zipFile)) {
        LogFileZipper.zipTo(out);
      }
      return zipFile.toFile();
    } catch (IOException ex) {
      var msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not zip logs", ex.getMessage());
      FacesContext.getCurrentInstance().addMessage("msgs", msg);
    }
    return null;
  }
}
