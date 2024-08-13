package ch.ivyteam.enginecockpit.monitor.value;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;

public class LongValueFormatter {

  private int digits;

  public LongValueFormatter(int digits) {
    this.digits = digits;
  }

  public String format(long value, Unit unit) {
    var valueUnit = new Value(value, unit);
    var originalUnit = valueUnit.unit();
    var formatUnit = originalUnit;
    var originalValue = valueUnit.longValue();
    var formatValue = originalValue;
    if (digits > 0) {
      boolean scaling = true;
      while (scaling) {
        formatValue = originalUnit.convertTo(originalValue, formatUnit);
        String str = Long.toString(formatValue);
        if (str.length() <= digits) {
          scaling = false;
        } else {
          var newUnit = formatUnit.scaleUp();
          if (newUnit == null) {
            scaling = false;
          } else {
            formatUnit = newUnit;
          }
        }
      }
    }
    if (formatUnit.hasSymbol()) {
      return Long.toString(formatValue) + " " + formatUnit.symbol();
    } else {
      return Long.toString(formatValue);
    }
  }
}
