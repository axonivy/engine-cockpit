package ch.ivyteam.enginecockpit.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;

import org.junit.jupiter.api.Test;

public class TestDateUtil {
  @Test
  void formatDefault() {
    var date = Calendar.getInstance();
    date.set(2021, 2, 8, 10, 54, 35);
    assertThat(DateUtil.formatDate(date.getTime())).isEqualTo("2021-03-08 10:54");
  }
}
