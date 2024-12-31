package ch.ivyteam.enginecockpit.monitor.performance.jfr;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.management.openmbean.CompositeData;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;

public record Recording(long id, String name, String state, LocalDateTime startTime, LocalDateTime stopTime, long size, long duration) {
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());

  static Recording from(CompositeData data) {
    return new Recording(
        (Long) data.get("id"),
        (String) data.get("name"),
        (String) data.get("state"),
        toLocalDateTime(data.get("startTime")),
        toLocalDateTime(data.get("stopTime")),
        (Long) data.get("size"),
        (Long) data.get("duration"));
  }

  private static LocalDateTime toLocalDateTime(Object msSinceEpoch) {
    long ms = (Long) msSinceEpoch;
    if (ms == 0L) {
      return null;
    }
    return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) msSinceEpoch), ZoneId.systemDefault());
  }

  public long getId() {
    return id();
  }

  public String getName() {
    return name();
  }

  public String getState() {
    return state();
  }

  public String getSize() {
    var unit = Unit.BYTES;
    var value = size();
    var scaledValue = Unit.BYTES.convertTo(value, unit);
    while (scaledValue > 10000) {
      unit = unit.scaleUp();
      scaledValue = Unit.BYTES.convertTo(value, unit);
    }
    return scaledValue + " " + unit.symbol();
  }

  public String getStartTime() {
    return format(startTime());
  }

  public String getStopTime() {
    return format(stopTime());
  }

  public String getDuration() {
    var unit = Unit.SECONDS;
    var value = duration();
    if (value == 0) {
      return "Unlimited";
    }
    if (isRunning()) {
      value = Duration.between(LocalDateTime.now(), startTime().plus(Duration.ofSeconds(value))).getSeconds();
      if (value <= 0) {
        return "Now";
      }
    }
    var scaledValue = Unit.SECONDS.convertTo(value, unit);
    while (scaledValue > 100) {
      unit = unit.scaleUp();
      scaledValue = Unit.SECONDS.convertTo(value, unit);
    }
    return scaledValue + " " + unit.symbol();

  }

  public boolean isCanStop() {
    return isRunning();
  }

  public boolean isCanClose() {
    return true;
  }

  public boolean isCanDownload() {
    return switch (state()) {
      case "STOPPED" -> true;
      default -> false;
    };
  }

  public boolean isRunning() {
    return "RUNNING".equals(state);
  }

  public boolean isNotRunning() {
    return !isRunning();
  }

  private String format(LocalDateTime time) {
    if (time == null) {
      return "";
    }
    return time.format(DATE_TIME_FORMATTER);
  }
}
