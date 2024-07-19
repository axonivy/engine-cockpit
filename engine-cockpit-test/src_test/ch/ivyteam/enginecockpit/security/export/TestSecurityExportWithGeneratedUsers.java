package ch.ivyteam.enginecockpit.security.export;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.io.ZipUtil;
import ch.ivyteam.ivy.environment.IvyTest;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@IvyTest
class TestSecurityExportWithGeneratedUsers {
  @TempDir
  private static Path tempDirectory;
  private static final int userCount = 3020;
  private static File[] files;

  @BeforeAll
  static void before() throws IOException {
    var securityContext = ISecurityContextRepository.instance().create("200kUsers");
    var userRepo = securityContext.users();
    for (var i = 1; i <= userCount; i++) {
      var user = userRepo.create("testUser-%04d".formatted(i));
      user.setFullName(UUID.randomUUID().toString());
    }

    StreamedContent export = new SecurityExport(securityContext).export();
    Path zipFile = tempDirectory.resolve("Export.zip");
    try (var os = Files.newOutputStream(zipFile, StandardOpenOption.CREATE_NEW)) {
      try (var is = export.getStream().get()) {
        is.transferTo(os);
      }
    }

    ZipUtil.extractFromZipFile(zipFile, tempDirectory, true);
    Files.deleteIfExists(zipFile);
    File directory = tempDirectory.toFile();
    files = directory.listFiles();
    Arrays.sort(files);
  }

  @Test
  void checkNumberOfFiles() {
    Assertions.assertThat(files).hasSize(4);
  }

  @Test
  void checkFileNames() {
    var start = 0;
    for(var file : files) {
      Assertions.assertThat(file.getName()).isEqualTo("AxonivySecurtyReport" + start + ".xlsx");
      start++;
    }
  }

  @Test
  void checkUserNames() throws IOException {
    Excel excel = new Excel();
    var start = 1;
    for (var file : files) {
      try (var is = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
        excel = new Excel(is);
      }
      var sheet = excel.getSheet("Users");
      var firstRow = sheet.getRow(1).getCell(3).getStringCellValue();
      var lastRow = sheet.getLastRow().getCell(3).getStringCellValue();
      Assertions.assertThat(firstRow).isEqualTo("testUser-%04d".formatted((start)));
      if(start + 999 > userCount) {
        Assertions.assertThat(lastRow).isEqualTo("testUser-%04d".formatted(userCount));
      }
      else {
        Assertions.assertThat(lastRow).isEqualTo("testUser-%04d".formatted(start + 999));
      }
      start += 1000;
    }
  }
}
