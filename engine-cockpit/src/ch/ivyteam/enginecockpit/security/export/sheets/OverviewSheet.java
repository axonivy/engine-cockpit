package ch.ivyteam.enginecockpit.security.export.sheets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.context.FacesContext;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.enginecockpit.security.export.excel.Row;
import ch.ivyteam.enginecockpit.security.export.excel.Sheet;
import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.IUser;

public class OverviewSheet {

  private final ISecurityContext securityContext;
  private final static List<String> HEADERS = new ArrayList<String>(Arrays.asList("Security System Name", "Date",
          "Axonivy Version", "Current User", "Hostname", "Number of Users", "Number of Roles", "File number", "First and Last User"));
  private Excel excel;
  private List<IUser> users;
  private ISession session;

  public OverviewSheet(Excel excel, ISecurityContext securityContext, List<IUser> users, ISession session) {
    this.excel = excel;
    this.securityContext = securityContext;
    this.users = users;
    this.session = session;
  }

  public void create(int userCount, int fileCount) {
    Sheet sheet = excel.createSheet("Overview");
    var fileNumber = userCount / 1000;
    if(userCount < 1000) {
      fileNumber = 1;
    }
    var firstUser = users.getFirst();
    var lastUser = users.getLast();
    var rowNr = 0;
    var titleRow = sheet.createRow(rowNr++);
    titleRow.createTitleCell(0, "Axonivy Security Report ");

    List<Row> rows = new ArrayList<Row>();
    rowNr++;
    for(var header : HEADERS) {
      var row = sheet.createRow(rowNr++);
      row.createHeaderCell(0, header, 33);
      rows.add(row);
    }

    sheet.mergeCells(0, 2);

    rowNr = 0;
    rows.get(rowNr++).createResultCellWidth(1, securityContext.getName(), 5);
    var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    rows.get(rowNr++).createResultCellWidth(1, dtf.format(LocalDateTime.now()), 5);
    rows.get(rowNr++).createResultCellWidth(1, Advisor.getAdvisor().getVersion().toString(), 5);
    rows.get(rowNr++).createResultCellWidth(1, session.getSessionUserName(), 5);
    rows.get(rowNr++).createResultCellWidth(1, getServerName(), 5);
    rows.get(rowNr++).createResultCellWidth(1, Long.toString(securityContext.users().count()), 5);
    rows.get(rowNr++).createResultCellWidth(1, Integer.toString(securityContext.roles().count()), 5);
    rows.get(rowNr++).createResultCellWidth(1, fileNumber + " of " + fileCount, 5);
    rows.get(rowNr++).createResultCellWidth(1, firstUser.getFullName() + " - " + lastUser.getFullName(), 5);

    createLegend(sheet, rowNr);
  }

  private void createLegend(Sheet sheet, int rowNr) {
    rowNr = 12;
    sheet.createRow(rowNr++).createTitleCellWithoutAlignment(0, "Legend");
    createLegendRow(sheet, rowNr++, "User roles", "X", "User owns role directly");
    createLegendRow(sheet, rowNr++, "", "x", "User owns role indirectly");
    rowNr++;
    createLegendRow(sheet, rowNr++, "Role members", "M", "Role is a role member");
    createLegendRow(sheet, rowNr++, "", "P", "Role is the parent role of a role");
    rowNr++;
    createLegendRow(sheet, rowNr++, "User permissions / Role permissions", "G", "Permission was granted directly");
    createLegendRow(sheet, rowNr++, "", "g", "Permission was granted indirectly");
    createLegendRow(sheet, rowNr++, "", "D", "Permission was denied directly");
    createLegendRow(sheet, rowNr++, "", "d", "Permission was denied indirectly");
  }

  private String getServerName() {
    var currentInstance = FacesContext.getCurrentInstance();
    if(currentInstance != null) {
      return FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
    }
    return "test.axonivy.com";
  }

  private void createLegendRow (Sheet sheet, int rowNr, String sheetName, String shortcut, String meaning) {
    var row = sheet.createRow(rowNr);
    row.createHeaderCell(0, sheetName, 33);
    row.createResultCellWidth(1, shortcut, 5);
    row.createResultCellWidth(2, meaning, 30);
  }
}
