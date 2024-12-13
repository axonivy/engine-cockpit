package ch.ivyteam.enginecockpit.monitor.trace.export;

import java.util.List;

import ch.ivyteam.enginecockpit.monitor.trace.Span;
import ch.ivyteam.enginecockpit.util.excel.Excel;
import ch.ivyteam.enginecockpit.util.excel.Sheet;
import ch.ivyteam.ivy.trace.Trace;
import ch.ivyteam.ivy.trace.TraceSpan;

public class SlowRequestDetailSheet {

  private static final List<String> HEADERS = List.of("Name", "Execution Time", "Start", "End", "Attributes");

  private final Excel excel;
  private Sheet sheet;

  private final Trace trace;
  private final long traceRootExecutionTime;

  public SlowRequestDetailSheet(Excel excel, Trace trace) {
    this.excel = excel;
    this.trace = trace;
    this.traceRootExecutionTime = trace.rootSpan().times().executionTime().toNanos();
  }

  public void create() {
    sheet = excel.createSheet("Slow Request Detail");
    sheet.createHeader(0, HEADERS, (header) -> 25);
    createRows(trace.rootSpan(), 1, "");
  }

  private void createRows(TraceSpan traceSpan, int rowNr, String prefix) {
    var span = new Span(traceSpan, traceRootExecutionTime);
    var row = sheet.createRow(rowNr++);
    var cellNr = 0;
    row.createResultCell(cellNr++, prefix + span.getName());
    row.createResultCell(cellNr++, Double.toString(span.getExecutionTime()));
    row.createResultCell(cellNr++, span.getStart());
    row.createResultCell(cellNr++, span.getEnd());
    row.createResultCell(cellNr++, span.getAttributes());
    for (TraceSpan child : traceSpan.children()) {
      createRows(child, rowNr, prefix + "> ");
    }
  }
}
