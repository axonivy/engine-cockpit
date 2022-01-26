package ch.ivyteam.enginecockpit.monitor.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang3.time.DateUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.UrlUtil;

public class LogView implements Comparable<LogView> {
  private Path file;
  private String fileName;
  private String content;
  private boolean downloadEnabled = false;
  private long size;
  private boolean endReached = true;
  private String date;

  public LogView(String logName, Date date) {
    fileName = logName;
    this.date = new SimpleDateFormat("yyyy-MM-dd").format(date);
    if (DateUtils.isSameDay(date, Calendar.getInstance().getTime())) {
      file = UrlUtil.getLogFile(logName);
    } else {
      file = UrlUtil.getLogFile(logName + "." + this.date);
    }
    content = readContent();
    readFileMetadata();
  }

  private void readFileMetadata() {
    try {
      BasicFileAttributes fileMeta = Files.readAttributes(file, BasicFileAttributes.class);
      size = fileMeta.size() / 1000;
    } catch (IOException e) {
      size = 0;
    }
  }

  public String getFileName() {
    return fileName;
  }

  private String readContent() {
    if (!Files.exists(file)) {
      return "Logfile '" + file.getFileName().toString() + "' doesn't exist.";
    }
    List<String> lines = readFileLines();
    if (lines.isEmpty()) {
      return "Logfile '" + file.getFileName().toString() + "' is empty.";
    }
    downloadEnabled = true;
    return lines.stream().collect(Collectors.joining("\n"));
  }

  private List<String> readFileLines() {
    List<String> lines = new ArrayList<>();
    try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file.toFile(), StandardCharsets.UTF_8)) {
      String line = reader.readLine();
      int count = 0;
      while (line != null && count < 1000) {
        lines.add(0, line);
        count++;
        line = reader.readLine();
      }
      if (count == 1000) {
        endReached = false;
      }
    } catch (IOException ex) {
      return Collections.emptyList();
    }
    return lines;
  }

  public String getContent() {
    return content;
  }

  public boolean isDownloadEnabled() {
    return downloadEnabled;
  }

  public long getSize() {
    return size;
  }

  public boolean isEndReached() {
    return endReached;
  }

  public String getDate() {
    return date;
  }

  public StreamedContent getFile() throws IOException {
    InputStream newInputStream = Files.newInputStream(file);
    return new DefaultStreamedContent(newInputStream, "text/plain", file.getFileName().toString());
  }

  @Override
  public int compareTo(LogView other) {
    return fileName.compareTo(other.getFileName());
  }

}
