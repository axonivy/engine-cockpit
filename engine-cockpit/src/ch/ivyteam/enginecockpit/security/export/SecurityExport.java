package ch.ivyteam.enginecockpit.security.export;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.enginecockpit.security.export.excel.Row;
import ch.ivyteam.enginecockpit.security.export.excel.Sheet;
import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.ivy.persistence.query.IPagedResult;
import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.IUser;

public class SecurityExport {

  private static final Comparator<IRole> ROLE_NAME_COMPERATOR = Comparator.comparing(IRole::getName);

  private final ISecurityContext securityContext;
  private final Map<String, Integer> propertyColumns = new HashMap<>();
  private int propertyCellNr = 8;
  private final Excel excel = new Excel();
  private ArrayList<String> headersOverview = new ArrayList<String>(Arrays.asList("Security System Name", "Date", "Axonivy Version", "Current User", "Hostname", "Number of Users", "Number of Roles"));
  private ArrayList<String> headersUser = new ArrayList<String>(Arrays.asList("Displayname", "Fullname", "Membername", "Name", "Email", "SecurityId", "ExternalId", "External Name"));
  private ArrayList<String> headersRoles = new ArrayList<String>(Arrays.asList("Displayname", "Name", "Description", "Member Name", "Security Member Id", "External Name"));
  private ArrayList<String> userRolesHeader = new ArrayList<String>(Arrays.asList("Username"));
  private ArrayList<String> roleMembersHeader = new ArrayList<String>(Arrays.asList("Role name"));

  public SecurityExport(ISecurityContext securityContext) {
    this.securityContext = securityContext;
  }

  public StreamedContent export(){
    createSheets();
    return DefaultStreamedContent
            .builder()
            .stream(excel::write)
            .contentType("application/xlsx")
            .name("AxonivySecurityReport.xlsx")
            .build();
  }

  public void createSheets() {
    createOverviewSheet();
    createUserSheet();
    createRoleSheet();
    createUserRolesSheet();
    createRoleMemberSheet();
    createUserPermissionsSheet();
    createRolePermissionsSheet();
  }

  private IPagedResult<IUser> getUsers(){
    return securityContext.users().query().orderBy().name().executor().resultsPaged();
  }


  private ArrayList<IRole> getRoles() {
    var roles = new ArrayList<>(securityContext.roles().all());
    Collections.sort(roles, ROLE_NAME_COMPERATOR);
    return roles;
  }

  private void createRolePermissionsSheet() {
    var permissions = securityContext.securityDescriptor().getPermissions();
    var roles = getRoles();
    var rowNr = 1;
    var rolePermissionsHeader = new ArrayList<String>(Arrays.asList("Role name"));

    for (var permission : permissions) {
      rolePermissionsHeader.add(permission.getName());
    }

    Sheet rolePermissionsSheet = excel.createSheet("Role permissions");
    writeHeader(rolePermissionsSheet, 0, rolePermissionsHeader);

    for (var role : roles) {
      writeRolePermissionData(rolePermissionsSheet, role, rowNr++, permissions);
    }
  }

  private void createUserPermissionsSheet() {
    var users = getUsers();
    var rowNr = 1;
    var userPermissionsHeader = new ArrayList<String>(Arrays.asList("Username"));
    var permissions = securityContext.securityDescriptor().getPermissions();

    for (var permission : permissions) {
      userPermissionsHeader.add(permission.getName());
    }

    Sheet userPermissionsSheet = excel.createSheet("User permissions");
    writeHeader(userPermissionsSheet, 0, userPermissionsHeader);

    for (var user : users) {
      writeUserPermissionData(userPermissionsSheet, user, rowNr++,permissions);
    }
  }

  private void createRoleMemberSheet() {
    var roles = getRoles();
    var rowNr = 1;
    Sheet roleMembersSheet = excel.createSheet("Role members");
    writeHeader(roleMembersSheet, 0, roleMembersHeader);

    for (var role : roles) {
      writeRoleMembersData(roleMembersSheet, role, rowNr++);
    }
  }


  private void createUserRolesSheet() {
    var users = getUsers();
    var rowNr = 1;
    Sheet userRolesSheet = excel.createSheet("User roles");
    writeHeader(userRolesSheet, 0, userRolesHeader);

    for (var user : users) {
      writeUserRoleData(userRolesSheet, rowNr++, user);
    }
  }

  private void createRoleSheet() {
    var rowNr = 1;
    propertyCellNr = headersRoles.size();
    Sheet rolesSheet = excel.createSheet("Roles");
    var roles = getRoles();

    for (var role : roles) {
      writeRoleData(rolesSheet, rowNr++, role);
      userRolesHeader.add(role.getDisplayName());
      roleMembersHeader.add(role.getDisplayName());
    }

    for (Map.Entry<String, Integer> entry : propertyColumns.entrySet()) {
      headersRoles.add(entry.getKey().toString());
    }

    writeHeader(rolesSheet, 0, headersRoles);
  }

  private void createUserSheet(){
    propertyColumns.clear();
    var rowNr = 1;
    propertyCellNr = headersUser.size();
    Sheet userSheet = excel.createSheet("Users");

    var users = getUsers();
    for (var user : users) {
      writeUserData(userSheet, rowNr++, user);
    }

    for (Map.Entry<String, Integer> entry : propertyColumns.entrySet()) {
      headersUser.add(entry.getKey().toString());
    }

    writeHeader(userSheet, 0, headersUser);
  }

  private void createOverviewSheet() {
    Sheet overviewSheet = excel.createSheet("Overview");
    writeOverviewData(overviewSheet, 0, headersOverview);
  }

  private void writeOverviewData(Sheet sheet, int rowNr, ArrayList<String> headers) {
    var titleRow = sheet.createRow(rowNr++);
    titleRow.createTitleCell(0, "Axonivy Security Report");



    ArrayList<Row> rows = new ArrayList<Row>();
    rowNr++;
    for(var header : headers) {
      var row = sheet.createRow(rowNr++);
      row.createHeaderCell(0, 50, header);
      rows.add(row);
    }

    ArrayList<String> values = new ArrayList<String>();
    values.add(securityContext.getName());
    var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    values.add(dtf.format(LocalDateTime.now()));
    values.add(Advisor.getAdvisor().getVersion().toString());
    values.add(ISession.current().getSessionUserName());
    values.add(getServerName());
    values.add(Long.toString(securityContext.users().count()));
    values.add(Integer.toString(securityContext.roles().count()));
    rowNr = 0;

    for(var row : rows) {
      row.createResultCellWidth(1, 20, values.get(rowNr));
      rowNr ++;
    }

    rowNr = 12;
    sheet.createRow(rowNr++).createTitleCell(0, "Legend");
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


  private void createLegendRow (Sheet sheet, int rowNr, String sheetName, String shortcut, String meaning) {
    var row = sheet.createRow(rowNr);
    row.createHeaderCell(0, 60, sheetName);
    row.createResultCellWidth(1, 20, shortcut);
    row.createResultCellWidth(2, 60, meaning);
  }

  private String getServerName() {
    var currentInstance = FacesContext.getCurrentInstance();
    if(currentInstance != null) {
      return FacesContext.getCurrentInstance().getExternalContext().getRequestServerName();
    }
    return "test.axonivy.com";
  }

  private void writeRolePermissionData(Sheet sheet, IRole role, int rowNr, List<IPermission> permissions) {
    var row = sheet.createRow(rowNr++);
    var cellNr = 0;
    row.createResultCell(cellNr++, role.getDisplayName());
    for(var permission : permissions) {
      var permissionCheck = securityContext.securityDescriptor().getPermissionAccess(permission, role);
      if(permissionCheck.isGranted()) {
        if(permissionCheck.isExplicit()) {
          row.createResultCell(cellNr, "G");
        }
        else {
          row.createResultCell(cellNr, "g");
        }
      }
      else if(permissionCheck.isDenied()) {
        if(permissionCheck.isExplicit()) {
          row.createResultCell(cellNr, "D");
        }
        else {
          row.createResultCell(cellNr, "d");
        }
      }
      cellNr++;
    }
  }

  private void writeUserPermissionData(Sheet sheet, IUser user, int rowNr, List<IPermission> permissions) {
    var row = sheet.createRow(rowNr++);
    var cellNr = 0;
    row.createResultCell(cellNr++, user.getDisplayName());
    for(var permission : permissions) {
      var permissionCheck = securityContext.securityDescriptor().getPermissionAccess(permission, user);
      if(permissionCheck.isGranted()) {
        if(permissionCheck.isExplicit()) {
          row.createResultCell(cellNr, "G");
        }
        else {
          row.createResultCell(cellNr, "g");
        }
      }
      else if(permissionCheck.isDenied()) {
        if(permissionCheck.isExplicit()) {
          row.createResultCell(cellNr, "D");
        }
        else {
          row.createResultCell(cellNr, "d");
        }
      }
      cellNr++;
    }
  }

  private void writeRoleMembersData(Sheet sheet, IRole role, int rowNr) {
    var row = sheet.createRow(rowNr++);
    var roleMembers = role.getRoleMembers();
    var parent = role.getParent();
    var cellNr = 0;
    row.createResultCell(cellNr++, role.getDisplayName());
    for(var member : roleMembers) {
      var index = roleMembersHeader.indexOf(member.getDisplayName());
      row.createResultCell(index++, "M");

      if(parent != null) {
        row.createResultCell(index++, "P");
      }
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
    row.createResultCell(cellNr++, role.getName());
    row.createResultCell(cellNr++, role.getDisplayDescription());
    row.createResultCell(cellNr++, role.getMemberName());
    row.createResultCell(cellNr++, role.getSecurityMemberId());
    row.createResultCell(cellNr++, role.getExternalName());
    for (var propertyName : role.getAllPropertyNames()) {
      cellNr = propertyColumns.computeIfAbsent(propertyName, name -> propertyCellNr++);
      row.createResultCell(cellNr, role.getProperty(propertyName));
    }
  }

  private void writeHeader(Sheet sheet, int rowNr, List<String> headers) {
    var row = sheet.createRow(rowNr);
    var cellNr = 0;
    for (var header : headers) {
      if(header.contains("Security") || header.contains("External")) {
        row.createHeaderCell(cellNr++, 45, header);
      }
      else {
        row.createHeaderCell(cellNr++, 25, header);

      }
    }
  }

  private void writeUserData(Sheet sheet, int rowNr, IUser user) {
    var row = sheet.createRow(rowNr++);
    var cellNr = 0;
    row.createResultCell(cellNr++, user.getDisplayName());
    row.createResultCell(cellNr++,  user.getFullName());
    row.createResultCell(cellNr++, user.getMemberName());
    row.createResultCell(cellNr++, user.getName());
    row.createResultCell(cellNr++, user.getEMailAddress());
    row.createResultCell(cellNr++, user.getSecurityMemberId());
    row.createResultCell(cellNr++, user.getExternalId());
    row.createResultCell(cellNr++, user.getExternalName());
    for (var propertyName : user.getAllPropertyNames()) {
      cellNr = propertyColumns.computeIfAbsent(propertyName, name -> propertyCellNr++);
      row.createResultCell(cellNr, user.getProperty(propertyName));
    }
  }

}
