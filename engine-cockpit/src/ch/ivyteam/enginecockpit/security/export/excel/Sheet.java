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

  public String[][] getData(){
    var rowNum = sheet.getFirstRowNum();
    var firstRow = sheet.getRow(rowNum);
    if(firstRow.getCell(0).getStringCellValue().equals("Axonivy Security Report")) {
      rowNum = sheet.getLastRowNum();
      firstRow = sheet.getRow(rowNum);
    }
    String[][] data = new String[sheet.getLastRowNum() + 1][firstRow.getLastCellNum()];
    for (var i=0; i<=sheet.getLastRowNum(); i++)
    {
      var row = sheet.getRow(i);
      if(row != null) {
        for (var j=0;j<row.getLastCellNum();j++)
        {
          var cell = row.getCell(j);
          if(cell != null) {
            String cellval = cell.getStringCellValue();
            data[i][j] = cellval;
          }
          else {
            data[i][j] = null;
          }
        }
      }
    }
    return data;
  }
}
