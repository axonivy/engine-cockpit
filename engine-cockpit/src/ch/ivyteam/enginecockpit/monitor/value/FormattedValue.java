package ch.ivyteam.enginecockpit.monitor.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;

class FormattedValue implements ValueProvider {
  private final List<Part> parts = new ArrayList<>();
  private final ValueProvider[] original;

  FormattedValue(String format, ValueProvider... original) {
    var start = 0;
    var valueIndex = 0;
    var pos = format.indexOf("%");
    while (pos >= 0) {
      if (start < pos) {
        parts.add(new ConstantPart(StringUtils.substring(format, start, pos)));
      }
      start = parsePart(format, pos, valueIndex++);
      pos = format.indexOf("%", start);
    }
    if (start < format.length()) {
      parts.add(new ConstantPart(StringUtils.substring(format, start, format.length())));
    }
    this.original = original;
  }

  private int parsePart(String format, int pos, int valueIndex) {
    if (format.length() <= pos) {
      throw new IllegalArgumentException("Illegal format " + format);
    }
    var start = pos;
    while (!Character.isAlphabetic(format.charAt(pos))) {
      pos++;
      if (format.length() <= pos) {
        throw new IllegalArgumentException("Illegal format " + format);
      }
    }
    String part = StringUtils.substring(format, start, pos + 1);

    if (part.endsWith("d")) {
      parts.add(new IntegerPart(valueIndex, part));
    }
    if (part.endsWith("f")) {
      parts.add(new DecimalPart(valueIndex, part));
    }
    if (part.endsWith("t")) {
      parts.add(new TimePart(valueIndex));
    }
    return pos + 1;
  }

  @Override
  public Value nextValue() {
    var values = Arrays.stream(original).map(ValueProvider::nextValue).toArray(Value[]::new);
    var builder = new StringBuilder();
    parts.forEach(format -> format.append(values, builder));
    return new Value(builder.toString(), Unit.ONE);
  }

  private interface Part {
    void append(Value[] values, StringBuilder builder);
  }

  private static final class ConstantPart implements Part {
    private final String value;

    private ConstantPart(String value) {
      this.value = value;
    }

    @Override
    public void append(Value[] values, StringBuilder builder) {
      builder.append(value);
    }
  }

  private static final class IntegerPart implements Part {
    private final int digits;
    private final int valueIndex;

    public IntegerPart(int valueIndex, String format) {
      this.valueIndex = valueIndex;
      if (format.length() > 2) {
        digits = Integer.parseInt(format.substring(1, format.length() - 1));
      } else {
        digits = 0;
      }
    }

    @Override
    public void append(Value[] values, StringBuilder builder) {
      var value = values[valueIndex];
      if (value == Value.NO_VALUE) {
        builder.append('-');
        return;
      }
      var longValueFormatter = new LongValueFormatter(digits);
      var format = longValueFormatter.format(value.longValue(), value.unit());

      if (format.contains(" ")) {
        builder.append(format.split(" ")[0]);
        builder.append(' ');
        builder.append(format.split(" ")[1]);
      }
      else {
        builder.append(format);
      }
    }
  }

  private static final class DecimalPart implements Part {

    private final int valueIndex;
    private final String format;

    public DecimalPart(int valueIndex, String format) {
      this.valueIndex = valueIndex;
      this.format = format;
    }

    @Override
    public void append(Value[] values, StringBuilder builder) {
      var value = values[valueIndex];
      if (value == Value.NO_VALUE) {
        builder.append('-');
        return;
      }
      builder.append(String.format(format, value.doubleValue()));
      if (value.unit().hasSymbol()) {
        builder.append(' ');
        builder.append(value.unit().symbol());
      }
    }
  }

  private static final class TimePart implements Part {
    private final int valueIndex;
    private final static Map<Unit, Long> MAX_VALUE_PER_UNIT = new HashMap<>();

    static {
      var unit = Unit.MILLI_SECONDS;
      while (unit != null) {
        MAX_VALUE_PER_UNIT.put(unit, 10_000L);
        unit = unit.scaleDown();
      }
      MAX_VALUE_PER_UNIT.put(Unit.SECONDS, 600L);
      MAX_VALUE_PER_UNIT.put(Unit.MINUTES, 600L);
      MAX_VALUE_PER_UNIT.put(Unit.HOURS, 240L);
    }

    private TimePart(int valueIndex) {
      this.valueIndex = valueIndex;
    }

    @Override
    public void append(Value[] values, StringBuilder builder) {
      var value = values[valueIndex];
      if (value == Value.NO_VALUE) {
        builder.append('-');
        return;
      }
      var originalUnit = value.unit();
      var formatUnit = originalUnit;
      var originalValue = value.longValue();
      var formatValue = originalValue;
      boolean scaling = true;
      while (scaling) {
        formatValue = originalUnit.convertTo(originalValue, formatUnit);
        Long maxValue = MAX_VALUE_PER_UNIT.get(formatUnit);
        if (maxValue == null || maxValue > formatValue) {
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
      builder.append(formatValue);
      builder.append(' ');
      builder.append(formatUnit.symbol());
    }
  }
}
