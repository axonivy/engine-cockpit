package ch.ivyteam.enginecockpit.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

  public static String formatDate(Date date, String format) {
    var formatter = new SimpleDateFormat(format);
    if (date != null) {
      return formatter.format(date);
    }
    return "";
  }

  public static String formatDate(Date date) {
    return formatDate(date, "yyyy-MM-dd HH:mm");
  }

  public static String formatInstant(Instant instant, String format) {
    var time = LocalTime.from(instant.atZone(ZoneId.systemDefault()));
    return DateTimeFormatter.ofPattern(format).format(time);
  }

}
