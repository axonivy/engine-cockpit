package ch.ivyteam.enginecockpit.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil
{
  
  public static String formatDate(Date date)
  {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    if (date != null)
    {
      return formatter.format(date);
    }
    return "";
  }
}
