package ch.ivyteam.enginecockpit.services;

import java.time.Instant;

import org.ocpsoft.prettytime.PrettyTime;

import ch.ivyteam.enginecockpit.util.DateUtil;

public record WebServiceExecHistory(String endpoint, Instant timestamp, long execTime, String operation, String port) {

  public String getPrettyTime() {
    return new PrettyTime().format(timestamp);
  }
  
  public String getFormattedTime() {
    return DateUtil.formatInstantAsDateTime(timestamp);
  }
}
