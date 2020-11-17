package ch.ivyteam.enginecockpit.monitor.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TestUnit
{
  @Test
  public void testToString()
  {
    assertThat(Unit.BYTES.toString()).isEqualTo("B (bytes)");
    assertThat(Unit.KILO_BYTES.toString()).isEqualTo("KiB (kilo bytes)");
    assertThat(Unit.SECONDS.toString()).isEqualTo("s (seconds)");
    assertThat(Unit.MINUTES.toString()).isEqualTo("m (minutes)");
    assertThat(Unit.MILLI_SECONDS.toString()).isEqualTo("ms (milli seconds)");
  }
  
  @Test
  public void symbol()
  {
    assertThat(Unit.BYTES.symbol()).isEqualTo("B");
    assertThat(Unit.KILO_BYTES.symbol()).isEqualTo("KiB");
    assertThat(Unit.SECONDS.symbol()).isEqualTo("s");
    assertThat(Unit.MINUTES.symbol()).isEqualTo("m");
    assertThat(Unit.MILLI_SECONDS.symbol()).isEqualTo("ms");
    assertThat(Unit.ONE.symbol()).isEqualTo("");
  }
  
  @Test
  public void hasSymbol()
  {
    assertThat(Unit.BYTES.hasSymbol()).isTrue();
    assertThat(Unit.ONE.hasSymbol()).isFalse();
  }

  @Test
  public void symbolWithBracesOrEmpty()
  {
    assertThat(Unit.BYTES.symbolWithBracesOrEmpty()).isEqualTo("[B]");
    assertThat(Unit.KILO_BYTES.symbolWithBracesOrEmpty()).isEqualTo("[KiB]");
    assertThat(Unit.SECONDS.symbolWithBracesOrEmpty()).isEqualTo("[s]");
    assertThat(Unit.MINUTES.symbolWithBracesOrEmpty()).isEqualTo("[m]");
    assertThat(Unit.MILLI_SECONDS.symbolWithBracesOrEmpty()).isEqualTo("[ms]");
    assertThat(Unit.ONE.symbolWithBracesOrEmpty()).isEqualTo("");
  }
  
  @Test
  public void name()
  {
    assertThat(Unit.BYTES.name()).isEqualTo("bytes");
    assertThat(Unit.KILO_BYTES.name()).isEqualTo("kilo bytes");
    assertThat(Unit.SECONDS.name()).isEqualTo("seconds");
    assertThat(Unit.MINUTES.name()).isEqualTo("minutes");
    assertThat(Unit.MILLI_SECONDS.name()).isEqualTo("milli seconds");
    assertThat(Unit.ONE.name()).isEqualTo("");
  }

  @Test
  public void scaleUp_integer()
  {
    long value = Unit.BYTES.convertTo(1024*1024*1024, Unit.BYTES);
    assertThat(value).isEqualTo(1024*1024*1024);
    value = Unit.BYTES.convertTo(1024*1024*1024, Unit.KILO_BYTES);
    assertThat(value).isEqualTo(1024*1024);
    value = Unit.BYTES.convertTo(1024*1024*1024, Unit.MEGA_BYTES);
    assertThat(value).isEqualTo(1024);
    value = Unit.BYTES.convertTo(1024*1024*1024, Unit.GIGA_BYTES);
    assertThat(value).isEqualTo(1);
  }

  @Test
  public void scaleUp_decimal()
  {
    double originalValue = 2.5d*1024.0d*1024.0d*1024.0d;
    double value = Unit.BYTES.convertTo(originalValue, Unit.BYTES);
    assertThat(value).isEqualTo(originalValue);
    value = Unit.BYTES.convertTo(originalValue, Unit.KILO_BYTES);
    assertThat(value).isEqualTo(2.5d*1024.0d*1024.0d);
    value = Unit.BYTES.convertTo(originalValue, Unit.MEGA_BYTES);
    assertThat(value).isEqualTo(2.5d*1024.0d);
    value = Unit.BYTES.convertTo(originalValue, Unit.GIGA_BYTES);
    assertThat(value).isEqualTo(2.5d);
  }

  @Test
  public void scaleDown_integer()
  {
    long value = Unit.MINUTES.convertTo(1, Unit.MINUTES);
    assertThat(value).isEqualTo(1);
    value = Unit.MINUTES.convertTo(1, Unit.SECONDS);
    assertThat(value).isEqualTo(60);
    value = Unit.MINUTES.convertTo(1, Unit.MILLI_SECONDS);
    assertThat(value).isEqualTo(60*1000);
    value = Unit.MINUTES.convertTo(1, Unit.MICRO_SECONDS);
    assertThat(value).isEqualTo(60*1000*1000);
  }
  
  @Test
  public void scaleDown_decimal()
  {
    double value = Unit.MINUTES.convertTo(1.25d, Unit.MINUTES);
    assertThat(value).isEqualTo(1.25d);
    value = Unit.MINUTES.convertTo(1.25d, Unit.SECONDS);
    assertThat(value).isEqualTo(1.25d*60.0d);
    value = Unit.MINUTES.convertTo(1.25d, Unit.MILLI_SECONDS);
    assertThat(value).isEqualTo(1.25d * 60.0d * 1000.0d);
    value = Unit.MINUTES.convertTo(1.25d, Unit.MICRO_SECONDS);
    assertThat(value).isEqualTo(1.25d * 60.0d * 1000.0d * 1000.0d);
  }
  
  @Test
  public void hash()
  {
    assertThat(Unit.MINUTES.hashCode()).isEqualTo(Unit.MINUTES.hashCode());
    assertThat(Unit.MINUTES.hashCode()).isNotEqualTo(Unit.SECONDS.hashCode());
  }

  @Test
  public void equal()
  {
    assertThat(Unit.MINUTES).isEqualTo(Unit.MINUTES);
    assertThat(Unit.MINUTES).isNotEqualTo(null);
    assertThat(Unit.MINUTES).isNotEqualTo("hello");
    assertThat(Unit.MINUTES).isNotEqualTo(Unit.SECONDS);
  }

}
