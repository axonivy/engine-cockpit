package ch.ivyteam.enginecockpit.security.export.excel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;

public class Excel implements AutoCloseable{
  private final XSSFWorkbook workbook;
  private final Map<Style, XSSFCellStyle> styles = new HashMap<>();

  public Excel() {
    workbook = new XSSFWorkbook(XSSFWorkbookType.XLSX);
  }

  public Excel(InputStream inputStream) throws IOException {
    workbook = new XSSFWorkbook(inputStream);
  }

  public InputStream write() {
    try (var outputStream = new ByteArrayOutputStream()) {
      workbook.write(outputStream);
      return new ByteArrayInputStream(outputStream.toByteArray());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void close() throws IOException {
    workbook.close();
  }

  public void write(OutputStream os) throws IOException {
    workbook.write(os);
  }

  public Sheet createSheet(String name) {
    return new Sheet(this, workbook.createSheet(name));
  }

  public Sheet createSheet() {
    return new Sheet(this, workbook.createSheet());
  }

  public Sheet getSheet(String sheetName) {
    Sheet sheet = new Sheet(this, workbook.getSheet(sheetName));
    return sheet;
  }

  XSSFCellStyle style(Style style) {
    return styles.computeIfAbsent(style, this::createStyle);
  }

  private XSSFCellStyle createStyle(Style style) {
    var cellStyle = workbook.createCellStyle();
    var font = workbook.createFont();
    cellStyle.setFont(font);
    switch(style) {
      case TITLE -> {
        font.setFontHeight(20);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
      }
      case HEADER -> {
        font.setBold(true);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
      }
      case RESULT -> {
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setBorderBottom(BorderStyle.HAIR);
      }
      case THICK -> {
        font.setBold(true);
      }
    }
    return cellStyle;
  }

}
