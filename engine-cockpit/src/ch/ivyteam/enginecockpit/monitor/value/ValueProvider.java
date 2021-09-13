package ch.ivyteam.enginecockpit.monitor.value;

import java.util.function.DoubleSupplier;
import java.util.function.LongSupplier;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.google.common.base.Supplier;

import ch.ivyteam.enginecockpit.monitor.mbeans.MAttribute;
import ch.ivyteam.enginecockpit.monitor.mbeans.MName;
import ch.ivyteam.enginecockpit.monitor.unit.Unit;

public interface ValueProvider {
  Value nextValue();

  static ValueProvider attribute(String mBeanName, String attributeName, Unit unit) {
    try {
      return attribute(new ObjectName(mBeanName), attributeName, unit);
    } catch (MalformedObjectNameException ex) {
      throw new IllegalArgumentException("Parameter mBeanName must be valid ObjectName", ex);
    }
  }

  static ValueProvider attribute(ObjectName mBeanName, String attributeName, Unit unit) {
    if (mBeanName.isPattern()) {
      return new MPatternAttributeReader(mBeanName, attributeName, unit);
    } else {
      return new MAttributeReader(mBeanName, attributeName, unit);
    }
  }

  static ValueProvider attribute(MName selected, MAttribute attribute, Unit unit) {
    return attribute(selected.getObjectName(), attribute.getName(), unit);
  }

  static ValueProvider delta(ValueProvider original) {
    return new DeltaValue(original);
  }

  static ValueProvider composite(ValueProvider original, String compositeName, Unit unit) {
    return new MCompositeValue(original, compositeName, unit);
  }

  static ValueProvider quotient(ValueProvider divident, ValueProvider divisor) {
    return new QuotientValue(divident, divisor);
  }

  static ValueProvider derivation(ValueProvider divident, ValueProvider divisor) {
    return quotient(delta(divident), delta(divisor));
  }

  static ValueProvider difference(ValueProvider minuend, ValueProvider subtrahend) {
    return new DifferenceValue(minuend, subtrahend);
  }

  static ValueProvider percentage(ValueProvider original) {
    return new PercentageValue(original);
  }

  static ValueProvider format(String format, ValueProvider... original) {
    return new FormattedValue(format, original);
  }

  static ValueProvider cache(int cacheTimes, ValueProvider original) {
    return new CachedValue(cacheTimes, original);
  }

  static ValueProvider value(Supplier<Object> valueProvider, Unit unit) {
    return () -> new Value(valueProvider.get(), unit);
  }

  static ValueProvider value(DoubleSupplier valueProvider, Unit unit) {
    return value((Supplier<Object>) valueProvider::getAsDouble, unit);
  }

  static ValueProvider value(LongSupplier valueProvider, Unit unit) {
    return value((Supplier<Object>) valueProvider::getAsLong, unit);
  }

}
