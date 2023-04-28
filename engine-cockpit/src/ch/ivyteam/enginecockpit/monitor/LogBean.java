package ch.ivyteam.enginecockpit.monitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import ch.ivyteam.ivy.log.provider.LogFileZipper;
import ch.ivyteam.ivy.log.provider.LogProvider;

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
    logs = LogProvider.all().stream()
            .flatMap(t -> t.logs().stream())
            .filter(l -> l.date().isEqual(date))
            .map(log -> new LogView(log))
            .sorted()
            .toList();
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
    try (var out = new ByteArrayOutputStream()) {
      LogFileZipper.zipTo(out);
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
