package ch.ivyteam.enginecockpit.monitor.value;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;

public class ScaleValue {

  public String Scale(long size, int digits) {
    if(size != Long.MIN_VALUE && size != 0) {
      var value = new Value(size, Unit.BYTES);
      var originalUnit = value.unit();
      var formatUnit = originalUnit;
      var originalValue = value.longValue();
      var formatValue = originalValue;
      if (digits > 0) {
        boolean scaling = true;
        while (scaling) {
          formatValue = originalUnit.convertTo(originalValue, formatUnit);
          String str = Long.toString(formatValue);
          if (str.length() <= digits) {
            scaling = false;
          } else {
            var unit = formatUnit.scaleUp();
            if (unit == null) {
              scaling = false;
            } else {
              formatUnit = unit;
            }
          }
        }
      }
      return Long.toString(formatValue) + " " + formatUnit.symbol();
    }
    return "n.a.";
  }
}
