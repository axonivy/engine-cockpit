package ch.ivyteam.enginecockpit.security.export.excel;

import java.util.List;

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
      row.createHeaderCell(cellNr++, width, header);
    }
  }


  XSSFSheet sheet() {
    return sheet;
  }

  public Excel excel() {
    return excel;
  }

  public String[] getFirstAndLastNameOfUsers(){
    var firstRowNum = sheet.getFirstRowNum() + 1;
    var lastRowNum = sheet.getLastRowNum();

    String[] data = new String[2];
    data[0] = sheet.getRow(firstRowNum).getCell(3).getStringCellValue();
    data[1] = sheet.getRow(lastRowNum).getCell(3).getStringCellValue();

    return data;
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
}
