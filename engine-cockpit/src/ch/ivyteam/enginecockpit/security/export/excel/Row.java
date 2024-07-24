package ch.ivyteam.enginecockpit.security.export.excel;

import org.apache.poi.xssf.usermodel.XSSFRow;

public class Row {

  private final Sheet sheet;
  private final XSSFRow row;
  private final int num;

  Row(Sheet sheet, XSSFRow row, int num) {
    this.sheet = sheet;
    this.row = row;
    this.num = num;
  }

  public void createHeaderCell(int cellNum, String header, int width) {
    var cell = createCell(cellNum);
    cell.style(Style.THICK);
    cell.width(width);
    cell.value(header);
  }

  public void createHeaderRotatedCell(int cellNum, String header, int width, int height) {
    var cell = createCell(cellNum);
    cell.style(Style.HEADER);
    row.setHeight((short)height);
    cell.width(width);
    cell.value(header);
  }

  public void createResultCell(int column, String value) {
    var cell = createCell(column);
    cell.style(Style.RESULT);
    cell.value(value);
  }

  public void createTitleCell(int column, String value) {
    var cell = createCell(column);
    cell.style(Style.TITLE);
    cell.value(value);
  }

  public void createTitleCellWithoutAlignment(int column, String value) {
    var cell = createCell(column);
    cell.style(Style.TITLE_NO_ALIGNMENT);
    cell.value(value);
  }

  public void createResultCellWidth(int column, String value, int width) {
    var cell = createCell(column);
    cell.style(Style.RESULT);
    cell.width(width);
    cell.value(value);
  }

  public void createResultCellWidth(int column, int value, int width) {
    var cell = createCell(column);
    cell.style(Style.RESULT);
    cell.width(width);
    cell.value(value);
  }

  public void createResultCell(int column, int value) {
    var cell = createCell(column);
    cell.style(Style.RESULT);
    cell.value(value);
  }

  public Cell createCell(int cell) {
    return new Cell(this, row.createCell(cell), cell);
  }

  public int num() {
    return num;
  }

  public Sheet sheet() {
    return sheet;
  }

  public void height(short height) {
    row.setHeight(height);
  }

}
