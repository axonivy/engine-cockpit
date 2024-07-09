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
import ch.ivyteam.ivy.security.IPermission;
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
    var roleMembersHeader = new ArrayList<String>(Arrays.asList("Role name"));
    for (var role : roles) {
      writeRoleData(rolesSheet, rowNr++, role);
      userRolesHeader.add(role.getDisplayName());
      roleMembersHeader.add(role.getDisplayName());
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

    rowNr = 1;
    Sheet roleMembersSheet = excel.createSheet("Role members");
    writeHeader(roleMembersSheet, 0, roleMembersHeader);
    for (var role : roles) {
      writeRoleMembersData(roleMembersSheet, rowNr++, role, roleMembersHeader);
    }

    rowNr = 1;
    var userPermissionsHeader = new ArrayList<String>(Arrays.asList("Username"));
    var permissions = securityContext.securityDescriptor().getPermissions();
    for (var permission : permissions) {
      userPermissionsHeader.add(permission.getName());
    }
    Sheet userPermissionsSheet = excel.createSheet("User permissions");
    writeHeader(userPermissionsSheet, 0, userPermissionsHeader);
    for (var user : users) {
      writeUserPermissionData(userPermissionsSheet, rowNr++, user, permissions);
    }

    rowNr = 1;
    var rolePermissionsHeader = new ArrayList<String>(Arrays.asList("Role name"));
    for (var permission : permissions) {
      rolePermissionsHeader.add(permission.getName());
    }
    Sheet rolePermissionsSheet = excel.createSheet("Role permissions");
    writeHeader(rolePermissionsSheet, 0, rolePermissionsHeader);
    for (var role : roles) {
      writeRolePermissionData(rolePermissionsSheet, rowNr++, role, permissions);
    }
  }

  private void writeRolePermissionData(Sheet sheet, int rowNr, IRole role, List<IPermission> permissions) {
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

  private void writeUserPermissionData(Sheet sheet, int rowNr, IUser user, List<IPermission> permissions) {
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

  private void writeRoleMembersData(Sheet sheet, int rowNr, IRole role, ArrayList<String> roleMembersHeader) {
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
    row.createResultCell(cellNr++, user.getExternalName());
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
