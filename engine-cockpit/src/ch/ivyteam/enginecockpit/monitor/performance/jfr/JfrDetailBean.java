package ch.ivyteam.enginecockpit.monitor.performance.jfr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.ErrorHandler;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.log.Logger;
import jdk.jfr.EventType;
import jdk.jfr.ValueDescriptor;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

@ManagedBean
@ViewScoped
public class JfrDetailBean {
  private long id;
  private Recording recording;
  private Path recordingFile;
  private EventType eventType;
  private List<EventType> eventTypes;
  private final List<RecordedEvent> events = new ArrayList<>();
  private final List<ValueDescriptor> fields = new ArrayList<>();

  private static final Logger LOGGER = Logger.getPackageLogger(JfrDetailBean.class);
  private static final ErrorHandler HANDLER = new ErrorHandler("msgs", LOGGER);

  public JfrDetailBean() {}

  public void setId(long id) {
    this.id = id;
    try {
      refresh();
    } catch (Exception ex) {
      HANDLER.showError("Can not stop recording", ex);
    }
  }

  private void refresh() throws IOException {
    this.recording = JfrBean.queryRecordings()
        .stream()
        .filter(rec -> rec.getId() == id)
        .findAny()
        .orElse(null);

    if (recordingFile == null) {
      recordingFile = Files.createTempFile("ivy-jfr", ".jfr");
    }

    try (var os = Files.newOutputStream(recordingFile, StandardOpenOption.TRUNCATE_EXISTING)) {
      try (var is = download().getStream().get()) {
        is.transferTo(os);
      }
    }

    try (var file = new RecordingFile(recordingFile)) {
      eventTypes = file.readEventTypes();
    }
    Collections.sort(eventTypes, Comparator.comparing(EventType::getLabel));
  }

  public long getId() {
    return id;
  }

  public Path getRecordingFile() {
    return recordingFile;
  }

  public Recording getRecording() {
    return recording;
  }

  public void stopRecording() {
    try {
      new JfrBean().stopRecording(recording);
      refresh();
    } catch (Exception ex) {
      HANDLER.showError("Can not stop recording", ex);
    }
  }

  public StreamedContent downloadRecording() {
    try {
      var content = download();
      refresh();
      return content;
    } catch (Exception ex) {
      HANDLER.showError("Can not stop recording", ex);
      return null;
    }
  }

  private StreamedContent download() {
    return new JfrBean().downloadRecording(recording);
  }

  public void closeRecording() {
    try {
      new JfrBean().closeRecording(recording);
      refresh();
    } catch (Exception ex) {
      HANDLER.showError("Can not close recording", ex);
    }
  }

  public List<EventType> getEventTypes() {
    return eventTypes;
  }

  public String getEventTypeName() {
    return eventType == null ? null : eventType.getName();
  }

  public void setEventTypeName(String name) {
    try {
      eventType = eventTypes.stream().filter(type -> type.getName().equals(name)).findAny().orElse(null);
      events.clear();
      int count = 0;
      try (var file = new RecordingFile(recordingFile)) {
        while (file.hasMoreEvents()) {
          var event = file.readEvent();
          if (event.getEventType().getName().equals(eventType.getName())) {
            events.add(event);
          }
          count++;
        }
      }
      Ivy.log().info("Events " + events.size() + " of " + count);
      fields.clear();
      fields.addAll(eventType.getFields());
      Ivy.log().info("Fields " + fields.size());
    } catch (Exception ex) {
      HANDLER.showError("Can not read events", ex);
    }
  }

  public EventType getEventType() {
    return eventType;
  }

  public void setEventType(EventType eventType) {
    this.eventType = eventType;
  }

  public List<EventType> completeEventType(String query) {
    return getEventTypes().stream().filter(t -> t.getName().contains(query)).toList();
  }

  public List<RecordedEvent> getEvents() {
    return events;
  }

  public List<ValueDescriptor> getFields() {
    return fields;
  }

  public String valueOf(RecordedEvent event, ValueDescriptor field) {
    if (field.getContentType() == null) {
      return toString(event, field);
    }
    return switch (field.getContentType()) {
      case "jdk.jfr.Timespan" -> toTimespan(event, field);
      case "jdk.jfr.Timestamp" -> toTimestamp(event, field);
      default -> toString(event, field);
    };
  }

  private String toString(RecordedEvent event, ValueDescriptor field) {
    var value = event.getValue(field.getName());
    return value == null ? "n.a." : value.toString() + field.getTypeName() + " " + field.getContentType();
  }

  private String toTimestamp(RecordedEvent event, ValueDescriptor field) {
    var value = switch (field.getTypeName()) {
      case "long" -> Instant.ofEpochMilli(event.getLong(field.getName()));
      case "java.time.Instant" -> event.getInstant(field.getName());
      default -> Instant.EPOCH;
    };
    try {
      return value == null ? "n.a." : DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault()).format(value);
    } catch (Exception ex) {
      Ivy.log().error(ex);
      return "";
    }
  }

  private String toTimespan(RecordedEvent event, ValueDescriptor field) {
    var value = event.getInstant(field.getName());
    return value == null ? "n.a." : value.toString() + " ns";
  }
}
