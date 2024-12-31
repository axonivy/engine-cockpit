package ch.ivyteam.enginecockpit.monitor.unit;

import java.util.Objects;

public class BaseUnit {
  private final String symbol;
  private final String name;
  private final Scaling scaling;

  static final BaseUnit SECONDS = new BaseUnit("s", "seconds", Scaling.TIME);
  static final BaseUnit BYTES = new BaseUnit("B", "bytes", Scaling.MEMORY);
  static final BaseUnit DAY_TIME = new BaseUnit("", "", Scaling.DAY_TIME);
  static final BaseUnit ONE = new BaseUnit("", "", Scaling.METRIC);
  static final BaseUnit PERCENTAGE = new BaseUnit("%", "percentage", Scaling.NONE);

  static final BaseUnit[] ALL = {SECONDS, BYTES, DAY_TIME, PERCENTAGE, ONE};

  private BaseUnit(String symbol, String name, Scaling scaling) {
    this.symbol = symbol;
    this.name = name;
    this.scaling = scaling;
  }

  Scaling getScaling() {
    return scaling;
  }

  String getSymbol() {
    return symbol;
  }

  String getName() {
    return name;
  }

  @Override
  public String toString() {
    return symbol + " (" + name + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj.getClass() != BaseUnit.class) {
      return false;
    }
    BaseUnit other = (BaseUnit) obj;
    return Objects.equals(name, other.name) && Objects.equals(symbol, other.symbol)
        && Objects.equals(scaling, other.scaling);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, symbol, scaling);
  }
}
