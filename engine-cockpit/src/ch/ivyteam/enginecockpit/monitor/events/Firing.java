package ch.ivyteam.enginecockpit.monitor.events;

import java.util.Date;

import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.enginecockpit.util.ErrorValue;

public final class Firing {

  private Date timestamp;
  private long duration;
  private String reason;
  private ErrorValue error;

  public Firing(Date timestamp, long duration, String reason, ErrorValue error) {
    this.timestamp = timestamp;
    this.duration = duration;
    this.reason = reason;
    this.error = error;
  }

  public String getTimestamp() {
    return DateUtil.formatDate(timestamp);
  }

  public String getDuration() {
    return Event.formatMicros(duration);
  }

  public String getReason() {
    return reason;
  }

  public ErrorValue getError() {
    return error;
  }
}