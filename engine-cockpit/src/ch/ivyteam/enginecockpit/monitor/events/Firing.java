package ch.ivyteam.enginecockpit.monitor.events;

import java.util.Date;

import javax.management.openmbean.CompositeData;

import ch.ivyteam.enginecockpit.util.DateUtil;
import ch.ivyteam.enginecockpit.util.DurationFormat;
import ch.ivyteam.enginecockpit.util.ErrorValue;

public final class Firing {

  private Date timestamp;
  private long duration;
  private String reason;
  private ErrorValue error;

  public Firing(CompositeData firing) {
    this.timestamp = (Date) firing.get("firingTimestamp");
    this.duration = (long) firing.get("firingTimeInMicroSeconds");
    this.reason = (String) firing.get("firingReason");
    this.error = new ErrorValue((CompositeData) firing.get("error"));
  }

  public String getTimestamp() {
    return DateUtil.formatDate(timestamp);
  }

  public String getDuration() {
    return DurationFormat.NOT_AVAILABLE.microSeconds(duration);
  }

  public String getReason() {
    return reason;
  }

  public ErrorValue getError() {
    return error;
  }
}
