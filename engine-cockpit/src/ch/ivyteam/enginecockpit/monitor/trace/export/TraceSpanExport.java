package ch.ivyteam.enginecockpit.monitor.trace.export;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.util.excel.Excel;
import ch.ivyteam.ivy.trace.Trace;

public class TraceSpanExport {

  private final Trace trace;

  public TraceSpanExport(Trace trace) {
    this.trace = trace;
  }

  public StreamedContent export() {
    var fileName = "TraceSpan_" + trace.id() + ".xlsx";
    InputStream excelFile;
    try {
      excelFile = createExcel(fileName);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return DefaultStreamedContent
            .builder()
            .stream(() -> excelFile)
            .contentType("application/xlsx")
            .name(fileName)
            .build();
  }

  private InputStream createExcel(String fileName) throws IOException {
    var tempDir = Files.createTempDirectory("TraceSpans");
    var file = tempDir.resolve(fileName);
    try (var excel = new Excel()) {
      createSheets(excel);
      try (var os = Files.newOutputStream(file, StandardOpenOption.CREATE_NEW)) {
        excel.write(os);
      }
    }
    return Files.newInputStream(file);
  }

  private void createSheets(Excel excel) {
    new SlowRequestDetailSheet(excel, trace).create();
    // TODO: add pages for attributes
  }
}
