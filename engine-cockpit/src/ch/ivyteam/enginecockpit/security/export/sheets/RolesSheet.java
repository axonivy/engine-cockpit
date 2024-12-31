package ch.ivyteam.enginecockpit.security.export.sheets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.enginecockpit.security.export.excel.Sheet;
import ch.ivyteam.ivy.security.IRole;

public class RolesSheet {
  private static final List<String> HEADERS = Arrays.asList("Name", "Displayname", "Description", "Security Member Id", "External Name");
  private final Map<String, Integer> propertyColumns = new HashMap<>();
  private int propertyCellNr = 8;
  private final ArrayList<String> headers = new ArrayList<>(HEADERS);
  private final Iterable<IRole> roles;
  private final Excel excel;

  public RolesSheet(Excel excel, Iterable<IRole> roles) {
    this.excel = excel;
    this.roles = roles;
  }

  public void create() {
    int rowNr = 1;
    Sheet sheet = excel.createSheet("Roles");

    for (var role : roles) {
      propertyCellNr = headers.size();
      var row = sheet.createRow(rowNr++);
      var cellNr = 0;
      row.createResultCell(cellNr++, role.getName());
      row.createResultCell(cellNr++, role.getDisplayName());
      row.createResultCell(cellNr++, role.getDisplayDescription());
      row.createResultCell(cellNr++, role.getSecurityMemberId());
      row.createResultCell(cellNr++, role.getExternalName());
      for (var propertyName : role.getAllPropertyNames()) {
        cellNr = propertyColumns.computeIfAbsent(propertyName, name -> {
          headers.add(name);
          return propertyCellNr++;
        });
        row.createResultCell(cellNr, role.getProperty(propertyName));
      }
    }
    sheet.createHeader(0, headers, UsersSheet.HEADER_WITDH);
  }
}
