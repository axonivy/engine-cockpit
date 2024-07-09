package ch.ivyteam.enginecockpit.security.export;

import org.assertj.core.api.Assertions;

import ch.ivyteam.enginecockpit.security.export.excel.Sheet;

public class ExcelAssertions {
  private Sheet sheet;

  public ExcelAssertions() {

  }

  public static ExcelAssertions assertThat(Sheet actual) {
    ExcelAssertions a = new ExcelAssertions();
    a.sheet = actual;
    return a;
  }

  public void isNotNull() {
    assert sheet != null;
  }

  public void contains(String[][] actualData) {
    var data = sheet.getData();
    Assertions.assertThat(data).isEqualTo(actualData);
  }
}
