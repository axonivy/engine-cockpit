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
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.environment.IvyTest;
import ch.ivyteam.ivy.security.ISecurityContextRepository;

@IvyTest
class TestSecurityExportWithGeneratedUsers {
  private static Path tempDirectory;
  private static final int userCount = 3020;
  private static File[] files;

  @BeforeAll
  static void before(@TempDir Path tempDir) throws IOException {
    var security = ISecurityContextRepository.instance().create("200kUsers");
    var userRepo = security.users();
    for (var i = 1; i <= userCount; i++) {
      if (i % 10000 == 0) {
        Ivy.log().info(i + " Users created");
      }
      var user = userRepo.create("testUser-%04d".formatted(i));
      user.setFullName(UUID.randomUUID().toString());
    }
    var securityContext = ISecurityContextRepository.instance().get("200kUsers");

    StreamedContent export = new SecurityExport(securityContext).export();
    Path zipFile = tempDir.resolve("Export.zip");
    try (var os = Files.newOutputStream(zipFile, StandardOpenOption.CREATE_NEW)) {
      try (var is = export.getStream().get()) {
        is.transferTo(os);
      }
    }

    ZipUtil.extractFromZipFile(zipFile, tempDir, true);
    Files.deleteIfExists(zipFile);
    tempDirectory = tempDir;
    File directory = tempDirectory.toFile();
    files = directory.listFiles();
    Arrays.sort(files);
  }

  @Test
  void checkNumberOfFiles() {
    Assertions.assertThat(tempDirectory.toFile().list().length).isEqualTo(4);
  }

  @Test
  void checkFileNames() {
    var start = 1000;
    for(var file : files) {
      Assertions.assertThat(file.getName()).isEqualTo("AxonivySecurtyReport" + start + ".xlsx");
      start += 1000;
    }
  }

  @Test
  void checkUserNames() throws IOException {
    Excel excel = new Excel();
    var start = 1000;
    for (var file : files) {
      try (var is = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
        excel = new Excel(is);
      }
      var sheet = excel.getSheet("Users");
      var data = sheet.getFirstAndLastNameOfUsers();
      if(start > userCount) {
        Assertions.assertThat(data[0]).isEqualTo("testUser-%04d".formatted((start - 999)));
        Assertions.assertThat(data[1]).isEqualTo("testUser-%04d".formatted(userCount));
      }
      else {
        Assertions.assertThat(data[0]).isEqualTo("testUser-%04d".formatted((start - 999)));
        Assertions.assertThat(data[1]).isEqualTo("testUser-%04d".formatted(start));
      }
      start += 1000;
    }
  }
}
