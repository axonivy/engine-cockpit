package ch.ivyteam.enginecockpit.security.export.excel;

import org.apache.poi.xssf.usermodel.XSSFSheet;

public class Sheet {

  private final Excel excel;
  private final XSSFSheet sheet;

  Sheet(Excel excel, XSSFSheet sheet) {
    this.excel = excel;
    this.sheet = sheet;
  }

  public Row createRow(int row) {
    return new Row(this, sheet.createRow(row), row);
  }

  XSSFSheet sheet() {
    return sheet;
  }

  public Excel excel() {
    return excel;
  }
}
