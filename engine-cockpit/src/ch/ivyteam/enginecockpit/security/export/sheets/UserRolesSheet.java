package ch.ivyteam.enginecockpit.security.export.sheets;

import java.util.ArrayList;
import java.util.List;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.enginecockpit.security.export.excel.Sheet;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.IUser;

public class UserRolesSheet {
  private Excel excel;
  private Iterable<IUser> users;
  private Iterable<IRole> roles;
  static final int ROTATED_HEADER_WIDTH = 3;
  static final int FIRST_CELL_NR = 1;
  static final int ROTATED_HEADER_HEIGHT = 1500;

  public UserRolesSheet(Excel excel, Iterable<IUser> users, Iterable<IRole> roles) {
    this.excel = excel;
    this.users = users;
    this.roles = roles;
  }

  public void create() {
    int rowNr = 1;
    Sheet sheet = excel.createSheet("User roles");
    var headers = new ArrayList<String>();
    sheet.createHeader(0, List.of("Name"), UsersSheet.HEADER_WITDH);
    addRoleNames(headers);

    for(var user : users) {
      var row = sheet.createRow(rowNr++);
      var directUserRoles = user.getRoles();
      var allUserRoles = user.getAllRoles();
      var cellNr = 0;
      row.createResultCell(cellNr++, user.getName());
      for(var role : roles) {
        if(directUserRoles.contains(role)) {
          row.createResultCell(cellNr, "X");
        }
        else if (allUserRoles.contains(role)) {
          row.createResultCell(cellNr, "x");
        }
        cellNr++;
      }
    }

    sheet.createHeaderRotated(0, FIRST_CELL_NR, headers, ROTATED_HEADER_WIDTH, ROTATED_HEADER_HEIGHT);
  }


  private void addRoleNames(List<String> headers) {
    for(var role : roles) {
      headers.add(role.getName());
    }
  }
}

