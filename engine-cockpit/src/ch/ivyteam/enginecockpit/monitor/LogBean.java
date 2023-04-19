package ch.ivyteam.enginecockpit.monitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

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
import ch.ivyteam.ivy.elasticsearch.IElasticsearchManager;

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
      logs = Stream.concat(
        getLogs(UrlUtil.getLogDir().toRealPath()),
        getElasticsearchLogs()
      ).toList();
    } catch (IOException ex) {
      FacesContext.getCurrentInstance().addMessage("msgs",
              new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not load logs", ex.getMessage()));
    }
  }

  private Stream<LogView> getElasticsearchLogs() throws IOException {
    var clusterName = IElasticsearchManager.instance().info().clusterName();
    return getLogFiles(UrlUtil.getElasticsearchLogDir())
            .map(Path::getFileName)
            .filter(log -> log.toString().startsWith(clusterName))
            .map(log -> new LogView("elasticsearch" + log.getFileName().toString().substring(clusterName.length()), date));
  }

  private Stream<LogView> getLogs(Path path) throws IOException {
    return getLogFiles(path).map(log -> new LogView(log.getFileName().toString(), date));
}

  private Stream<Path> getLogFiles(Path path) throws IOException {
    return Files.exists(path) ? Files.walk(path)
            .filter(Files::isRegularFile)
            .filter(log -> log.toString().endsWith(".log"))
            : Stream.empty();
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
