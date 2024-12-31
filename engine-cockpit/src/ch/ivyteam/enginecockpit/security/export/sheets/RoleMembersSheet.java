package ch.ivyteam.enginecockpit.security.export.sheets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.enginecockpit.security.export.excel.Sheet;
import ch.ivyteam.ivy.security.IRole;

public class RoleMembersSheet {
  private final Iterable<IRole> roles;
  private final Excel excel;
  private final List<String> headers = new ArrayList<>();
  static final int ROTATED_HEADER_HEIGHT = 1500;

  public RoleMembersSheet(Excel excel, Iterable<IRole> roles) {
    this.excel = excel;
    this.roles = roles;
  }

  public void create() {
    int rowNr = 1;
    Sheet sheet = excel.createSheet("Role members");
    sheet.createHeader(0, Arrays.asList("Role name"), UsersSheet.HEADER_WITDH);
    addRoleNames();

    for (var role : roles) {
      var row = sheet.createRow(rowNr++);
      var roleMembers = role.getRoleMembers();
      var parent = role.getParent();
      var cellNr = 0;
      row.createResultCell(cellNr++, role.getName());
      for (var roleSecond : roles) {
        var index = headers.indexOf(roleSecond.getName()) + 1;
        String value = getMarker(roleMembers, parent, roleSecond);
        if (value != null) {
          row.createResultCell(index, value);
        }
        index++;
      }
    }

    sheet.createHeaderRotated(0, UserRolesSheet.FIRST_CELL_NR, headers, UserRolesSheet.ROTATED_HEADER_WIDTH, ROTATED_HEADER_HEIGHT);
    sheet.createFreezePane(1, 1);
  }

  private String getMarker(List<IRole> roleMembers, IRole parent, IRole roleSecond) {
    String value = null;
    if (parent != null && parent.equals(roleSecond)) {
      value = "P";
    }
    if (roleMembers.contains(roleSecond)) {
      if (value != null) {
        value = value + "/M";
      } else {
        value = "M";
      }
    }
    return value;
  }

  private void addRoleNames() {
    for (var role : roles) {
      headers.add(role.getName());
    }
  }
}
