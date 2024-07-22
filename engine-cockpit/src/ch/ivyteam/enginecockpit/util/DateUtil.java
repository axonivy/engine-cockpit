package ch.ivyteam.enginecockpit.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
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

  public static String formatInstantAsDateTime(Instant instant) {
    var dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    return DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm").format(dateTime);

  }

  public static String formatInstantAsTime(Instant instant, String format) {
    var time = instant.atZone(ZoneId.systemDefault()).toLocalTime();
    return DateTimeFormatter.ofPattern(format).format(time);
  }
}
