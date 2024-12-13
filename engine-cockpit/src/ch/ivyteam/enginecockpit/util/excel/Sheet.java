package ch.ivyteam.enginecockpit.util.excel;

import java.util.List;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
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

  public interface WidthProvider {
    int widthFor(String header);
  }

  public void createHeader(int rowNr, List<String> headers, WidthProvider headerWidth) {
    var row = createRow(rowNr);
    var cellNr = 0;
    for (var header : headers) {
      int width = headerWidth.widthFor(header);
      row.createHeaderCell(cellNr++, header, width);
    }
  }

  public void createHeaderRotated(int rowNr, int cellNr, List<String> headers, int width, int height) {
    var row = new Row(this, getRow(rowNr), rowNr);
    for (var header : headers) {
      row.createHeaderRotatedCell(cellNr++, header, width, height);
    }
  }


  XSSFSheet sheet() {
    return sheet;
  }

  public Excel excel() {
    return excel;
  }

  public XSSFRow getRow(int rowNum){
   return sheet.getRow(rowNum);
  }

  public XSSFRow getLastRow(){
    var lastRowNum = sheet.getLastRowNum();
   return getRow(lastRowNum);
  }

  public String[][] getData(){
    var cellMax = 0;
    var rowNum = sheet.getLastRowNum();
    for (var i=0; i<=rowNum; i++) {
      var row = sheet.getRow(i);
      if(row != null) {
        if(row.getPhysicalNumberOfCells() > cellMax) {
          cellMax = row.getPhysicalNumberOfCells();
        }
      }
    }

    String[][] data = new String[sheet.getLastRowNum() + 1][cellMax];
    for (var i=0; i<=sheet.getLastRowNum(); i++) {
      var row = sheet.getRow(i);
      if(row != null) {
        for (var j=0;j<row.getLastCellNum();j++) {
          var cell = row.getCell(j);
          if(cell != null) {
            String cellval = cell.getStringCellValue();
            data[i][j] = cellval;
          }
        }
      }
    }
    return data;
  }

  public void mergeCells(int rowNr, int cellNr) {
    var cellRange = new CellRangeAddress(rowNr, rowNr, 0, cellNr);
    sheet.addMergedRegion(cellRange);
  }

  public void createFreezePane(int colSplit, int rowSplit) {
    sheet.createFreezePane(colSplit, rowSplit);
  }
}
