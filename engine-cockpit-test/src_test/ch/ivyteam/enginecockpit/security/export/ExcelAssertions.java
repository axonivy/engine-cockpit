package ch.ivyteam.enginecockpit.security.export;

import org.assertj.core.api.Assertions;

import ch.ivyteam.enginecockpit.security.export.excel.Sheet;

public class ExcelAssertions {
  private final Sheet sheet;

  private ExcelAssertions(Sheet actual) {
    this.sheet = actual;
  }

  public static ExcelAssertions assertThat(Sheet actual) {
    return new ExcelAssertions(actual);
  }

  public void isNotNull() {
    assert sheet != null;
  }

  public void contains(String[][] actualData) {
    var data = sheet.getData();
    Assertions.assertThat(data).isEqualTo(actualData);
  }
}
