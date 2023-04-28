package ch.ivyteam.enginecockpit.monitor.model;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.ivy.log.provider.LogFile;

public class LogView implements Comparable<LogView> {
  private LogFile logFile;
  private String content;
  private long size;
  private boolean endReached = true;
  private String date;

  public LogView(LogFile log) {
    logFile = log;
    if (log.isLog()) {
      content = readContent();
    } else {
      content = readFile(() -> "Logfile '" + log.name() + "' is compressed.");
    }
    readFileMetadata();
  }

  private void readFileMetadata() {
    try {
      BasicFileAttributes fileMeta = Files.readAttributes(logFile.path(), BasicFileAttributes.class);
      size = fileMeta.size() / 1000;
    } catch (IOException e) {
      size = 0;
    }
  }

  public String getFileName() {
    return logFile.name();
  }

  private String readContent() {
    return readFile(() -> {
      List<String> lines = readFileLines();
      if (lines.isEmpty()) {
        return "Logfile '" + logFile.name() + "' is empty.";
      }
      return lines.stream().collect(Collectors.joining("\n"));
    });
  }

  private String readFile(Supplier<String> readContent) {
    return readContent.get();
  }

  private List<String> readFileLines() {
    List<String> lines = new ArrayList<>();
    try (ReversedLinesFileReader reader = new ReversedLinesFileReader(logFile.path(), StandardCharsets.UTF_8)) {
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
    InputStream newInputStream = Files.newInputStream(logFile.path());
    return DefaultStreamedContent
        .builder()
        .stream(() -> newInputStream)
        .contentType("text/plain")
        .name(logFile.name())
        .build();
  }

  @Override
  public int compareTo(LogView other) {
    return logFile.name().compareTo(other.getFileName());
  }

}
