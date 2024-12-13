package ch.ivyteam.enginecockpit.util.excel;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;

public class Cell {

  private static final int CHAR_WIDTH = 256;

  private final XSSFCell cell;
  private final Row row;
  private final int num;

  Cell(Row row, XSSFCell cell, int num) {
    this.row = row;
    this.cell = cell;
    this.num = num;
  }

  Row row() {
    return row;
  }

  public void mergeTo(int height, int width) {
    var range = new CellRangeAddress(row.num(), row.num()+height-1, num, num+width-1);
    row.sheet().sheet().addMergedRegion(range);
  }

  public void value(String value) {
    cell.setCellValue(value);
  }

  public void value(int value) {
    cell.setCellValue(value);
  }

  public void style(Style style) {
    var cellStyle = row().sheet().excel().style(style);
    cell.setCellStyle(cellStyle);
  }

  public void width(int width) {
    row().sheet().sheet().setColumnWidth(num, width * CHAR_WIDTH);
  }
}
