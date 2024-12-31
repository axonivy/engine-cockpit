package ch.ivyteam.enginecockpit.util;

import java.time.Duration;
import java.time.Instant;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;
import ch.ivyteam.util.date.Now;

public class DurationFormat {
  private final String nullStr;
  private final String negativeStr;

  public static final String NOT_AVAILABLE_STR = "n.a.";

  public static final DurationFormat BLANK_SOON = new DurationFormat("", "soon");
  public static final DurationFormat NOT_AVAILABLE = new DurationFormat(NOT_AVAILABLE_STR, NOT_AVAILABLE_STR);

  public DurationFormat(String notAvailable, String negative) {
    this.nullStr = notAvailable;
    this.negativeStr = negative;
  }

  public String fromNowTo(Instant to) {
    if (to == null) {
      return nullStr;
    }
    var duration = Duration.between(Now.asInstant(), to);
    return format(duration.getSeconds(), Unit.SECONDS);
  }

  public String format(Instant instant) {
    if (instant == null) {
      return nullStr;
    }
    return DateUtil.formatInstantAsDateTime(instant);
  }

  public String milliSeconds(Long millis) {
    return format(millis, Unit.MILLI_SECONDS);
  }

  public String microSeconds(Long micros) {
    return format(micros, Unit.MICRO_SECONDS);
  }

  private String format(Long value, Unit unit) {
    if (value == null) {
      return nullStr;
    }
    return format((long) value, unit);
  }

  private String format(long value, Unit baseUnit) {
    if (value < 0) {
      return negativeStr;
    }
    Unit unit = baseUnit;
    var scaledValue = baseUnit.convertTo(value, unit);
    while (shouldScaleUp(unit, scaledValue)) {
      unit = unit.scaleUp();
      scaledValue = baseUnit.convertTo(value, unit);
    }
    return scaledValue + " " + unit.symbol();
  }

  private static boolean shouldScaleUp(Unit unit, long scaledValue) {
    if (Unit.MICRO_SECONDS.equals(unit) || Unit.MILLI_SECONDS.equals(unit)) {
      return scaledValue >= 1000;
    }
    return scaledValue >= 120;
  }
}
