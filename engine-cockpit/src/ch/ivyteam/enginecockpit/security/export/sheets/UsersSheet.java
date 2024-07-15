package ch.ivyteam.enginecockpit.security.export.sheets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.enginecockpit.security.export.excel.Sheet;
import ch.ivyteam.enginecockpit.security.export.excel.Sheet.WidthProvider;
import ch.ivyteam.ivy.security.IUser;

public class UsersSheet {
  private static final List<String> HEADERS = Arrays.asList("Displayname", "Fullname", "Membername", "Name", "Email", "SecurityId", "ExternalId", "External Name");
  static final WidthProvider HEADER_WITDH = header -> {
    if(header.contains("Security") || header.contains("External")) {
      return 45;
    }
    return 25;
  };
  private final Map<String, Integer> propertyColumns = new HashMap<>();
  private int propertyCellNr = 8;
  private ArrayList<String> headers = new ArrayList<String>(HEADERS);
  private Iterable<IUser> users;
  private Excel excel;

  public UsersSheet(Excel excel, Iterable<IUser> users) {
    this.excel = excel;
    this.users = users;
  }

  public void create() {
    int rowNr = 1;
    Sheet sheet = excel.createSheet("Users");
    for(var user : users) {
      propertyCellNr = headers.size();
      var row = sheet.createRow(rowNr++);
      var cellNr = 0;
      row.createResultCell(cellNr++, user.getDisplayName());
      row.createResultCell(cellNr++, user.getFullName());
      row.createResultCell(cellNr++, user.getMemberName());
      row.createResultCell(cellNr++, user.getName());
      row.createResultCell(cellNr++, user.getEMailAddress());
      row.createResultCell(cellNr++, user.getSecurityMemberId());
      row.createResultCell(cellNr++, user.getExternalId());
      row.createResultCell(cellNr++, user.getExternalName());
      for (var propertyName : user.getAllPropertyNames()) {
        cellNr = propertyColumns.computeIfAbsent(propertyName, name -> {
          headers.add(name);
          return propertyCellNr++;
        });
        row.createResultCell(cellNr, user.getProperty(propertyName));
      }
    }

    sheet.createHeader(0, headers, HEADER_WITDH);
  }
}
