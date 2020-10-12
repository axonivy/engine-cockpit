package ch.ivyteam.enginecockpit.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil
{
  
  public static String formatDate(Date date, String format)
  {
    SimpleDateFormat formatter = new SimpleDateFormat(format);
    if (date != null)
    {
      return formatter.format(date);
    }
    return "";
  }
  
  public static String formatDate(Date date)
  {
    return formatDate(date, "yyyy-MM-dd HH:mm");
  }
  
}
