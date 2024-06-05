package ch.ivyteam.enginecockpit.monitor.log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.ivy.log.provider.LogFile;

public class LogView {

  private static final int LINES = 100;

  private LogFile logFile;
  private final String content;
  private String size;

  public LogView(LogFile log) {
    logFile = log;
    content = readContent(log);
    size = readFileSize();
  }

  private String readContent(LogFile log) {
    if (log.isCompressed()) {
      return log.name() + " is compressed";
    }
    return readContent();
  }

  private String readFileSize() {
    try {
      var file = logFile.path();
      if (Files.exists(file)) {
        var fileMeta = Files.readAttributes(file, BasicFileAttributes.class);
        return FileUtils.byteCountToDisplaySize(fileMeta.size());
      }
      return "0 bytes";
    } catch (IOException ex) {
      return "Error during reading " + ex.getMessage();
    }
  }

  public String getFileName() {
    return logFile.name();
  }

  public String getChannel() {
    return logFile.channel();
  }

  private String readContent() {
    var lines = readFileLines();
    if (lines.isEmpty()) {
      return logFile.name() + " is empty";
    }
    return lines.stream().collect(Collectors.joining("\n"));
  }

  private List<String> readFileLines() {
    var lines = new ArrayList<String>();
    var builder = ReversedLinesFileReader.builder()
            .setPath(logFile.path())
            .setCharset(StandardCharsets.UTF_8);

    var endReached = true;
    try (var reader = builder.get()) {
      String line = reader.readLine();
      int count = 0;
      while (line != null && count < LINES) {
        lines.add(line);
        count++;
        line = reader.readLine();
      }
      if (count == LINES) {
        endReached = false;
      }
    } catch (IOException ex) {
      return Collections.emptyList();
    }

    if (!endReached) {
      lines.add(" ");
      lines.add("... open or download the log file to see more");
    }

    Collections.reverse(lines);
    return lines;
  }

  public String getContent() {
    return content;
  }

  public String getSize() {
    return size;
  }

  public StreamedContent getFile() throws IOException {
    var newInputStream = Files.newInputStream(logFile.path());
    return DefaultStreamedContent.builder()
            .stream(() -> newInputStream)
            .contentType("text/plain")
            .name(logFile.name())
            .build();
  }

  public static LogViewUriBuilder uri() {
    return new LogViewUriBuilder();
  }

  @SuppressWarnings("hiding")
  public static class LogViewUriBuilder {

    private String channel;
    private String date;

    public LogViewUriBuilder channel(String channel) {
      this.channel = channel;
      return this;
    }

    public LogViewUriBuilder date(LocalDate date) {
      this.date = LogBean.toString(date);
      return this;
    }

    public String toUri() {
      var builder = UriBuilder.fromPath("logs.xhtml").queryParam("channel", channel);
      if (date != null) {
        builder.queryParam("date", date.toString());
      }
      return builder.toString();
    }
  }
}
