package ch.ivyteam.enginecockpit.services.rest;

import java.util.Date;

import org.ocpsoft.prettytime.PrettyTime;

import ch.ivyteam.enginecockpit.util.DateUtil;

public record ExecHistory(Date timestamp, long execTime, String requestUrl, String requestMethod, String processElementId ) {
  
  public String getPrettyTime() {
    return new PrettyTime().format(timestamp);
  }
  
  public String getFormattedTime() {
    return DateUtil.formatDate(timestamp, "dd-MM-yyyy HH:mm");
  }
}
