package ch.ivyteam.enginecockpit.util;

import ch.ivyteam.ivy.environment.Ivy;

public class CmsUtil {
  public static String coWithDefault(String uri, String defaultValue) {
    var value = Ivy.cm().co(uri);
    if (value.isEmpty()) {
      return defaultValue;
    }
    return value;
  }
}
