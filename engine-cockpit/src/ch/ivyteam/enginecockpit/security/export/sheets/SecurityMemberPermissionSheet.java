package ch.ivyteam.enginecockpit.security.export.sheets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.enginecockpit.security.export.excel.Sheet;
import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;

  public class SecurityMemberPermissionSheet {
    private final ISecurityContext securityContext;
    private Excel excel;
    private Iterable<ISecurityMember> securityMembers;
    static final int ROTATED_HEADER_HEIGHT = 3000;
    private static final Comparator<IPermission> PERMISSION_NAME_COMPERATOR = Comparator.comparing(IPermission::getName);

    public SecurityMemberPermissionSheet(Excel excel, ISecurityContext securityContext, Iterable<ISecurityMember> securityMembers) {
      this.excel = excel;
      this.securityContext = securityContext;
      this.securityMembers = securityMembers;
    }

    public void create(String sheetName) {
      var rowNr = 1;
      var headers = new ArrayList<String>();
      Sheet sheet = excel.createSheet(sheetName + " permissions");
      sheet.createHeader(0, Arrays.asList("Name"), UsersSheet.HEADER_WITDH);
      var permissions = securityContext.securityDescriptor().getPermissions();
      Collections.sort(permissions, PERMISSION_NAME_COMPERATOR);


      for(var permission : permissions) {
        headers.add(permission.getName());
      }

      for (var securityMember : securityMembers) {
        PermissionShortcut shortcut = new PermissionShortcut(securityContext, securityMember);
        var row = sheet.createRow(rowNr++);
        var cellNr = 0;

        row.createResultCell(cellNr++, securityMember.getName());
        for(var permission : permissions) {
          row.createResultCell(cellNr++, shortcut.getPermissionShortcut(permission));
        }
      }

      sheet.createHeaderRotated(0, UserRolesSheet.FIRST_CELL_NR, headers, UserRolesSheet.ROTATED_HEADER_WIDTH, ROTATED_HEADER_HEIGHT);
      sheet.createFreezePane(1, 1);
    }
}
