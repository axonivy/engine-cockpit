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

  public UserRolesSheet(Excel excel, Iterable<IUser> users, Iterable<IRole> roles) {
    this.excel = excel;
    this.users = users;
    this.roles = roles;
  }

  public void create() {
    int rowNr = 1;
    Sheet sheet = excel.createSheet("User roles");
    var headers = new ArrayList<String>(List.of("Username"));
    addRoleNames(headers);

    for(var user : users) {
      var row = sheet.createRow(rowNr++);
      var directUserRoles = user.getRoles();
      var allUserRoles = user.getAllRoles();
      var cellNr = 0;
      row.createResultCell(cellNr++, user.getDisplayName());
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

    sheet.createHeader(0, headers, UsersSheet.HEADER_WITDH);
  }


  private void addRoleNames(List<String> headers) {
    for(var role : roles) {
      headers.add(role.getDisplayName());
    }
  }
}

