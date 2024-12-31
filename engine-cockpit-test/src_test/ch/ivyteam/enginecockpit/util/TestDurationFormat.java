
package ch.ivyteam.enginecockpit.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.ivyteam.util.date.Now;

class TestDurationFormat {

  private static final Duration ONE_MILLI = Duration.ofMillis(1);
  private static final Duration THREE_HOURS = Duration.ofHours(3);

  private final static Instant NOW = Instant.now();

  @BeforeEach
  void beforeEach() {
    Now.set(str -> NOW);
  }

  @Test
  void not_available_null() {
    assertThat(DurationFormat.NOT_AVAILABLE.milliSeconds(null)).isEqualTo("n.a.");
    assertThat(DurationFormat.NOT_AVAILABLE.microSeconds(null)).isEqualTo("n.a.");
    assertThat(DurationFormat.NOT_AVAILABLE.fromNowTo(null)).isEqualTo("n.a.");
  }

  @Test
  void blank_soon_null() {
    assertThat(DurationFormat.BLANK_SOON.milliSeconds(null)).isEmpty();
    assertThat(DurationFormat.BLANK_SOON.microSeconds(null)).isEmpty();
    assertThat(DurationFormat.BLANK_SOON.fromNowTo(null)).isEmpty();
  }

  @Test
  void not_available_negative() {
    assertThat(DurationFormat.NOT_AVAILABLE.milliSeconds(-1L)).isEqualTo("n.a.");
    assertThat(DurationFormat.NOT_AVAILABLE.microSeconds(-1L)).isEqualTo("n.a.");
    assertThat(DurationFormat.NOT_AVAILABLE.fromNowTo(NOW.minus(ONE_MILLI))).isEqualTo("n.a.");
  }

  @Test
  void blank_soon_negative() {
    assertThat(DurationFormat.BLANK_SOON.milliSeconds(-1L)).isEqualTo("soon");
    assertThat(DurationFormat.BLANK_SOON.microSeconds(-1L)).isEqualTo("soon");
    assertThat(DurationFormat.BLANK_SOON.fromNowTo(NOW.minus(ONE_MILLI))).isEqualTo("soon");
  }

  @Test
  void not_available_0() {
    assertThat(DurationFormat.NOT_AVAILABLE.milliSeconds(0L)).isEqualTo("0 ms");
    assertThat(DurationFormat.NOT_AVAILABLE.microSeconds(0L)).isEqualTo("0 us");
    assertThat(DurationFormat.NOT_AVAILABLE.fromNowTo(NOW)).isEqualTo("0 s");
  }

  @Test
  void blank_soon_0() {
    assertThat(DurationFormat.BLANK_SOON.milliSeconds(0L)).isEqualTo("0 ms");
    assertThat(DurationFormat.BLANK_SOON.microSeconds(0L)).isEqualTo("0 us");
    assertThat(DurationFormat.BLANK_SOON.fromNowTo(NOW)).isEqualTo("0 s");
  }

  @Test
  void not_available_3_hours() {
    assertThat(DurationFormat.NOT_AVAILABLE.milliSeconds(THREE_HOURS.toMillis())).isEqualTo("3 h");
    assertThat(DurationFormat.NOT_AVAILABLE.microSeconds(THREE_HOURS.toMillis() * 1000L)).isEqualTo("3 h");
    assertThat(DurationFormat.NOT_AVAILABLE.fromNowTo(NOW.plus(THREE_HOURS))).isEqualTo("3 h");
  }

  @Test
  void blank_soon_3_hours() {
    assertThat(DurationFormat.BLANK_SOON.milliSeconds(THREE_HOURS.toMillis())).isEqualTo("3 h");
    assertThat(DurationFormat.BLANK_SOON.microSeconds(THREE_HOURS.toMillis() * 1000L)).isEqualTo("3 h");
    assertThat(DurationFormat.BLANK_SOON.fromNowTo(NOW.plus(THREE_HOURS))).isEqualTo("3 h");
  }
}
