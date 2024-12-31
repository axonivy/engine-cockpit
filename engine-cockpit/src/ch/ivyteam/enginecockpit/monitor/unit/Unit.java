package ch.ivyteam.enginecockpit.monitor.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class Unit {
  private final BaseUnit baseUnit;
  private final Scale scale;

  public static final Unit BYTES = new Unit(BaseUnit.BYTES);
  public static final Unit KILO_BYTES = BYTES.scaleUp();
  public static final Unit MEGA_BYTES = KILO_BYTES.scaleUp();
  public static final Unit GIGA_BYTES = MEGA_BYTES.scaleUp();
  public static final Unit TERA_BYTES = GIGA_BYTES.scaleUp();

  public static final Unit SECONDS = new Unit(BaseUnit.SECONDS);
  public static final Unit MINUTES = SECONDS.scaleUp();
  public static final Unit HOURS = MINUTES.scaleUp();
  public static final Unit DAYS = HOURS.scaleUp();
  public static final Unit MILLI_SECONDS = SECONDS.scaleDown();
  public static final Unit MICRO_SECONDS = MILLI_SECONDS.scaleDown();
  public static final Unit NANO_SECONDS = MICRO_SECONDS.scaleDown();

  public static final Unit DAY_TIME = new Unit(BaseUnit.DAY_TIME);
  public static final Unit ONE = new Unit(BaseUnit.ONE);
  public static final Unit PERCENTAGE = new Unit(BaseUnit.PERCENTAGE);

  public static final List<Unit> ALL;
  static {
    List<Unit> all = new ArrayList<>();
    for (BaseUnit baseUnit : BaseUnit.ALL) {
      Unit bUnit = new Unit(baseUnit);
      all.add(bUnit);
      Unit upScaleUnit = bUnit.scaleUp();
      while (upScaleUnit != null) {
        all.add(upScaleUnit);
        upScaleUnit = upScaleUnit.scaleUp();
      }
      Unit downScaleUnit = bUnit.scaleDown();
      while (downScaleUnit != null) {
        all.add(downScaleUnit);
        downScaleUnit = downScaleUnit.scaleDown();
      }
    }
    ALL = all;
  }

  private Unit(BaseUnit baseUnit) {
    this.baseUnit = baseUnit;
    this.scale = baseUnit.getScaling().getNormal();
  }

  private Unit(BaseUnit baseUnit, Scale scale) {
    this.baseUnit = baseUnit;
    this.scale = scale;
  }

  public Unit scaleUp() {
    Scale upScale = scale.scaleUp();
    if (upScale == null) {
      return null;
    }
    return new Unit(baseUnit, scale.scaleUp());
  }

  public Unit scaleDown() {
    Scale downScale = scale.scaleDown();
    if (downScale == null) {
      return null;
    }
    return new Unit(baseUnit, scale.scaleDown());
  }

  public String symbol() {
    return scale.enhanceSymbol(baseUnit.getSymbol());
  }

  public boolean hasSymbol() {
    return StringUtils.isNotBlank(symbol());
  }

  public String name() {
    return scale.enhanceName(baseUnit.getName());
  }

  public String symbolWithBracesOrEmpty() {
    String symbol = symbol();
    if (symbol.isEmpty()) {
      return symbol;
    }
    return "[" + symbol + "]";
  }

  public long convertTo(long value, Unit toUnit) {
    return scale.converTo(value, toUnit.scale);
  }

  public double convertTo(double value, Unit toUnit) {
    return scale.convertTo(value, toUnit.scale);
  }

  @Override
  public String toString() {
    return scale.enhanceSymbol(baseUnit.getSymbol()) + " (" + scale.enhanceName(baseUnit.getName()) + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() != Unit.class) {
      return false;
    }
    Unit other = (Unit) obj;
    return baseUnit.equals(other.baseUnit) && scale.equals(other.scale);
  }

  @Override
  public int hashCode() {
    return Objects.hash(baseUnit, scale);
  }
}
