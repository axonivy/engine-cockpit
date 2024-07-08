package ch.ivyteam.enginecockpit.security.export;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.environment.IvyTest;

@IvyTest
class TestSecurityExport {

  @BeforeEach
  void before() {
    var users = Ivy.wf().getSecurityContext().users();
    users.create("Cedric");
  }

  @Test
  void export() throws IOException {
    var wf = Ivy.wf();
    assertThat(wf.getSecurityContext().users().count()).isEqualTo(1);

    StreamedContent export = new SecurityExport(wf.getSecurityContext()).export();
    export.getStream();
    try (var workbook = new XSSFWorkbook(export.getStream().get())) {
      var userSheet = workbook.getSheet("User");
      var rolesSheet = workbook.getSheet("Roles");
      assertThat(userSheet.getLastRowNum()).as("last row num").isEqualTo(1);
      var userRow = userSheet.getRow(1);
      String userValue = userRow.getCell(0).getStringCellValue().toString();
      assert userValue.equals("Cedric");

      var rolesRow = rolesSheet.getRow(1);
      String roleValue = rolesRow.getCell(0).getStringCellValue().toString();
      assert roleValue.equals("Everybody");
      assertThat(rolesSheet.getLastRowNum()).as("last row num").isEqualTo(1);
      assert workbook.getNumberOfSheets() == 2;
    }
  }
}
