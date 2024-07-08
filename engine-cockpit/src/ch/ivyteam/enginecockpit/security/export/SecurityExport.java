package ch.ivyteam.enginecockpit.security.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.enginecockpit.security.export.excel.Sheet;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.IUser;

public class SecurityExport {

  private final ISecurityContext securityContext;
  private final Map<String, Integer> propertyColumns = new HashMap<>();
  private int propertyCellNr = 8;
  private final Excel excel = new Excel();
  private ArrayList<String> headersUser = new ArrayList<String>(Arrays.asList("Displayname", "Email", "ExternalId", "External Name", "Fullname", "Membername", "Name", "SecurityId"));
  private ArrayList<String> headersRoles = new ArrayList<String>(Arrays.asList("Displayname", "Description", "External Name", "Member Name", "Security Member Id", "Name"));

  public SecurityExport(ISecurityContext securityContext) {
    this.securityContext = securityContext;
  }

  public StreamedContent export(){
    createSheets();
    return DefaultStreamedContent
            .builder()
            .stream(excel::write)
            .contentType("application/xlsx")
            .name("test.xlsx")
            .build();
  }

  public void createSheets() {
    propertyColumns.clear();
    var rowNr = 1;
    propertyCellNr = headersUser.size();
    Sheet userSheet = excel.createSheet("User");
    var users = securityContext.users().paged();
    for (var user : users) {
      writeUserData(userSheet, rowNr++, user);
    }

    for (Map.Entry<String, Integer> entry : propertyColumns.entrySet()) {
      headersUser.add(entry.getKey().toString());
    }

    writeHeader(userSheet, 0, headersUser);

    rowNr = 1;
    propertyCellNr = headersRoles.size();
    Sheet rolesSheet = excel.createSheet("Roles");
    var roles = securityContext.roles().all();
    var userRolesHeader = new ArrayList<String>(Arrays.asList("Username"));
    for (var role : roles) {
      writeRoleData(rolesSheet, rowNr++, role);
      userRolesHeader.add(role.getDisplayName());
    }

    for (Map.Entry<String, Integer> entry : propertyColumns.entrySet()) {
      headersRoles.add(entry.getKey().toString());
    }
    writeHeader(rolesSheet, 0, headersRoles);

    rowNr = 1;
    Sheet userRolesSheet = excel.createSheet("Userroles");
    writeHeader(userRolesSheet, 0, userRolesHeader);
    for (var user : users) {
      writeUserRoleData(userRolesSheet, rowNr++, user);
    }
  }

  private void writeUserRoleData(Sheet sheet, int rowNr, IUser user) {
    var row = sheet.createRow(rowNr++);
    var directUserRoles = user.getRoles();
    var allUserRoles = user.getAllRoles();
    var cellNr = 0;
    row.createResultCell(cellNr++, user.getDisplayName());
    for(var userrole : allUserRoles) {
      if(directUserRoles.contains(userrole)) {
        row.createResultCell(cellNr++, "X");
      }
      else {
        row.createResultCell(cellNr++, "x");
      }
    }
  }

  private void writeRoleData(Sheet sheet, int rowNr, IRole role) {
    var row = sheet.createRow(rowNr++);
    var cellNr = 0;
    row.createResultCell(cellNr++, role.getDisplayName());
    row.createResultCell(cellNr++, role.getDisplayDescription());
    row.createResultCell(cellNr++, role.getExternalName());
    row.createResultCell(cellNr++, role.getMemberName());
    row.createResultCell(cellNr++, role.getSecurityMemberId());
    row.createResultCell(cellNr++, role.getName());
    for (var propertyName : role.getAllPropertyNames()) {
      cellNr = propertyColumns.computeIfAbsent(propertyName, name -> propertyCellNr++);
      row.createResultCell(cellNr, role.getProperty(propertyName));
    }
  }

  private void writeHeader(Sheet sheet, int rowNr, List<String> headers) {
    var row = sheet.createRow(rowNr);
    var cellNr = 0;
    for (var header : headers) {
      row.createHeaderCell(cellNr++, 10, header);
    }
  }

  private void writeUserData(Sheet sheet, int rowNr, IUser user) {
    var row = sheet.createRow(rowNr++);
    var cellNr = 0;
    row.createResultCell(cellNr++, user.getDisplayName());
    row.createResultCell(cellNr++, user.getEMailAddress());
    row.createResultCell(cellNr++, user.getExternalId());
    row.createResultCell(cellNr++,  user.getFullName());
    row.createResultCell(cellNr++, user.getMemberName());
    row.createResultCell(cellNr++, user.getName());
    row.createResultCell(cellNr++, user.getSecurityMemberId());
    for (var propertyName : user.getAllPropertyNames()) {
      cellNr = propertyColumns.computeIfAbsent(propertyName, name -> propertyCellNr++);
      row.createResultCell(cellNr, user.getProperty(propertyName));
    }
  }

}
