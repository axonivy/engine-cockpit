package ch.ivyteam.enginecockpit.security.export.sheets;

import java.util.ArrayList;
import java.util.Arrays;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.enginecockpit.security.export.excel.Sheet;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;

  public class SecurityMemberPermissionSheet {
    private final ISecurityContext securityContext;
    private Excel excel;
    private Iterable<ISecurityMember> securityMembers;

    public SecurityMemberPermissionSheet(Excel excel, ISecurityContext securityContext, Iterable<ISecurityMember> securityMembers) {
      this.excel = excel;
      this.securityContext = securityContext;
      this.securityMembers = securityMembers;
    }

    public void create(String sheetName) {
      var rowNr = 1;
      var headers = new ArrayList<String>(Arrays.asList(sheetName + "name"));
      var permissions = securityContext.securityDescriptor().getPermissions();

      Sheet sheet = excel.createSheet(sheetName + " permissions");

      for(var permission : permissions) {
        headers.add(permission.getName());
      }

      for (var securityMember : securityMembers) {
        PermissionShortcut shortcut = new PermissionShortcut(securityContext, securityMember);
        var row = sheet.createRow(rowNr++);
        var cellNr = 0;

        row.createResultCell(cellNr++, securityMember.getDisplayName());
        for(var permission : permissions) {
          row.createResultCell(cellNr++, shortcut.getPermissionShortcut(permission));
        }
      }

      sheet.createHeader(0, headers, UsersSheet.HEADER_WITDH);
    }
}
