package ch.ivyteam.enginecockpit.monitor.value;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;

import ch.ivyteam.enginecockpit.monitor.unit.Unit;

public class TestValueProvider {
  private long counter = 0;

  @Test
  public void format_integer() {
    assertThatNextValue(ValueProvider.format("value %d", () -> new Value(1234, Unit.BYTES)))
            .isEqualTo("value 1234 B");
    assertThatNextValue(ValueProvider.format("%d value", () -> new Value(123456789, Unit.BYTES)))
            .isEqualTo("123456789 B value");
    assertThatNextValue(ValueProvider.format("value %3d", () -> new Value(1234, Unit.BYTES)))
            .isEqualTo("value 1 KiB");
    assertThatNextValue(ValueProvider.format("value %3d", () -> new Value(12345, Unit.BYTES)))
            .isEqualTo("value 12 KiB");
    assertThatNextValue(ValueProvider.format("value %3d", () -> new Value(12345678, Unit.BYTES)))
            .isEqualTo("value 11 MiB");
    assertThatNextValue(ValueProvider.format("value %4d", () -> new Value(12345678, Unit.BYTES)))
            .isEqualTo("value 11 MiB");
    assertThatNextValue(ValueProvider.format("value %5d", () -> new Value(12345678, Unit.BYTES)))
            .isEqualTo("value 12056 KiB");

    assertThatNextValue(ValueProvider.format("value %d", () -> Value.NO_VALUE)).isEqualTo("value -");
  }

  @Test
  public void format_decimal() {
    assertThatNextValue(ValueProvider.format("value %f", () -> new Value(1.234, Unit.KILO_BYTES)))
            .isEqualTo("value 1.234000 KiB");
    assertThatNextValue(ValueProvider.format("value %.1f", () -> new Value(1.234, Unit.KILO_BYTES)))
            .isEqualTo("value 1.2 KiB");

    assertThatNextValue(ValueProvider.format("value %f", () -> Value.NO_VALUE)).isEqualTo("value -");
  }

  @Test
  public void format_time() {
    assertThatNextValue(ValueProvider.format("value %t", () -> new Value(1234, Unit.MICRO_SECONDS)))
            .isEqualTo("value 1234 us");
    assertThatNextValue(ValueProvider.format("value %t", () -> new Value(12345, Unit.MICRO_SECONDS)))
            .isEqualTo("value 12 ms");
    assertThatNextValue(ValueProvider.format("value %t", () -> new Value(123456, Unit.MICRO_SECONDS)))
            .isEqualTo("value 123 ms");
    assertThatNextValue(ValueProvider.format("value %t", () -> new Value(1234567, Unit.MICRO_SECONDS)))
            .isEqualTo("value 1234 ms");
    assertThatNextValue(ValueProvider.format("value %t", () -> new Value(12345678, Unit.MICRO_SECONDS)))
            .isEqualTo("value 12 s");
    assertThatNextValue(ValueProvider.format("value %t", () -> new Value(123456789, Unit.MICRO_SECONDS)))
            .isEqualTo("value 123 s");
    assertThatNextValue(ValueProvider.format("value %t", () -> new Value(1234567, Unit.MILLI_SECONDS)))
            .isEqualTo("value 20 m");
    assertThatNextValue(ValueProvider.format("value %t", () -> new Value(12345678, Unit.MILLI_SECONDS)))
            .isEqualTo("value 205 m");
    assertThatNextValue(ValueProvider.format("value %t", () -> new Value(123456789, Unit.MILLI_SECONDS)))
            .isEqualTo("value 34 h");
    assertThatNextValue(ValueProvider.format("value %t", () -> new Value(1234567, Unit.SECONDS)))
            .isEqualTo("value 14 d");

    assertThatNextValue(ValueProvider.format("value %t", () -> Value.NO_VALUE)).isEqualTo("value -");
  }

  @Test
  public void cache() {
    ValueProvider cache = ValueProvider.cache(1, increase());
    assertThatNextValue(cache).isEqualTo(0L);
    assertThatNextValue(cache).isEqualTo(0L);
    assertThatNextValue(cache).isEqualTo(1L);
    assertThatNextValue(cache).isEqualTo(1L);
    assertThatNextValue(cache).isEqualTo(2L);
  }

  @Test
  public void difference() {
    ValueProvider difference = ValueProvider.difference(increase(), ValueProvider.value(() -> 2L, Unit.ONE));
    assertThatNextValue(difference).isEqualTo(-2L);
    assertThatNextValue(difference).isEqualTo(-1L);
    assertThatNextValue(difference).isEqualTo(0L);
    assertThatNextValue(difference).isEqualTo(1L);
    assertThatNextValue(difference).isEqualTo(2L);
  }

  @Test
  public void delta() {
    ValueProvider delta = ValueProvider.delta(increase());
    assertThat(delta.nextValue()).isEqualTo(Value.NO_VALUE);
    assertThatNextValue(delta).isEqualTo(1L);
    assertThatNextValue(delta).isEqualTo(1L);
    assertThatNextValue(delta).isEqualTo(1L);
    assertThatNextValue(delta).isEqualTo(1L);
  }

  @Test
  public void percentage() {
    ValueProvider percentage = ValueProvider.percentage(increase());
    assertThatNextValue(percentage).isEqualTo(0L);
    assertThatNextValue(percentage).isEqualTo(100L);
    assertThatNextValue(percentage).isEqualTo(200L);
    assertThatNextValue(percentage).isEqualTo(300L);
    assertThatNextValue(percentage).isEqualTo(400L);
  }

  @Test
  public void quotient() {
    ValueProvider percentage = ValueProvider.quotient(ValueProvider.value(() -> 100, Unit.ONE), increase());
    assertThat(percentage.nextValue()).isEqualTo(Value.NO_VALUE);
    assertThatNextValue(percentage).isEqualTo(100L);
    assertThatNextValue(percentage).isEqualTo(50L);
    assertThatNextValue(percentage).isEqualTo(33L);
    assertThatNextValue(percentage).isEqualTo(25L);
  }

  @Test
  public void derivation() {
    ValueProvider derivation = ValueProvider
            .derivation(ValueProvider.value(() -> counter * counter, Unit.ONE), increase());
    assertThat(derivation.nextValue()).isEqualTo(Value.NO_VALUE);
    assertThatNextValue(derivation).isEqualTo(1L);
    assertThatNextValue(derivation).isEqualTo(3L);
    assertThatNextValue(derivation).isEqualTo(5L);
    assertThatNextValue(derivation).isEqualTo(7L);
  }

  @Test
  public void value() {
    ValueProvider value = ValueProvider.value(() -> "hi", Unit.SECONDS);
    assertThatNextValue(value).isEqualTo("hi");
    assertThat(value.nextValue().unit()).isEqualTo(Unit.SECONDS);
  }

  @Test
  public void value_integer() {
    ValueProvider value = ValueProvider.value(() -> 1L, Unit.SECONDS);
    assertThatNextValue(value).isEqualTo(1L);
    assertThat(value.nextValue().unit()).isEqualTo(Unit.SECONDS);
  }

  @Test
  public void value_decimal() {
    ValueProvider value = ValueProvider.value(() -> 1.234d, Unit.SECONDS);
    assertThatNextValue(value).isEqualTo(1.234d);
    assertThat(value.nextValue().unit()).isEqualTo(Unit.SECONDS);
  }

  private ValueProvider increase() {
    return ValueProvider.value(() -> counter++, Unit.ONE);
  }

  private ObjectAssert<Object> assertThatNextValue(ValueProvider provider) {
    return assertThat(provider.nextValue().value());
  }
}
