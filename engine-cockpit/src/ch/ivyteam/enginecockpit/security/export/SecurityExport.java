package ch.ivyteam.enginecockpit.security.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.enginecockpit.security.export.sheets.OverviewSheet;
import ch.ivyteam.enginecockpit.security.export.sheets.RoleMembersSheet;
import ch.ivyteam.enginecockpit.security.export.sheets.RolesSheet;
import ch.ivyteam.enginecockpit.security.export.sheets.SecurityMemberPermissionSheet;
import ch.ivyteam.enginecockpit.security.export.sheets.UserRolesSheet;
import ch.ivyteam.enginecockpit.security.export.sheets.UsersSheet;
import ch.ivyteam.io.ZipUtil;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.internal.data.AccessControlData;

@SuppressWarnings("restriction")
public class SecurityExport {

  private static final Comparator<IRole> ROLE_NAME_COMPERATOR = Comparator.comparing(IRole::getName);
  private static final int usersPerExcel = 1000;

  private final ISecurityContext securityContext;

  public SecurityExport(ISecurityContext securityContext) {
    this.securityContext = securityContext;
  }

  public StreamedContent export() throws IOException{
    var usersCount = (int)securityContext.users().query().orderBy().name().executor().count();
    var tempDir = Files.createTempDirectory("AxonivySecurityReports");

    if (usersCount < usersPerExcel) {
      return createSingleExcel(usersCount, tempDir);
    }
    else {
      int forCount = Math.ceilDiv(usersCount, usersPerExcel);
      var start = usersPerExcel;

      createFiles(tempDir, forCount, start);
    }

    var zipFile = zipDirectory(tempDir);

    return DefaultStreamedContent
      .builder()
      .stream(() -> {
        try {
          return Files.newInputStream(zipFile);
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }
      })
      .contentType("application/zip")
      .name("AxonivySecurityReport.zip")
      .build();
  }

  private StreamedContent createSingleExcel(int usersCount, Path tempDir) throws IOException {
    Excel excel = new Excel();
    createSheets(0, usersCount, excel, true );
    var file = tempDir.resolve("AxonivySecurtyReport" + ".xlsx");
    try (var os = Files.newOutputStream(file, StandardOpenOption.CREATE_NEW)) {
      excel.write(os);
    }
    return DefaultStreamedContent
            .builder()
            .stream(() -> {
              try {
                return Files.newInputStream(file);
              } catch (IOException ex) {
                throw new RuntimeException(ex);
              }
            })
            .contentType("application/xlsx")
            .name("AxonivySecurityReport.xlsx")
            .build();
  }

  private Path zipDirectory(Path tempDir) throws IOException {
    var zipDir = Files.createTempDirectory("AxonivySecurityReport");
    var zipFile = zipDir.resolve("AxonivySecurityReport.zip");
    ZipUtil.zipDir(tempDir, zipFile);
    return zipFile;
  }

  private void createFiles(Path tempDir, int forCount, int start) throws IOException {
    for(int i=0; i<forCount; i++) {
      clearChache();

      try (Excel excel = new Excel()) {
        if(i == 0) {
          var newStart = start - usersPerExcel;
          createSheets(newStart, usersPerExcel, excel, true);
        }
        else {
          createSheets(start - usersPerExcel, usersPerExcel, excel, false);
        }

        var file = tempDir.resolve("AxonivySecurtyReport" + i + ".xlsx");
        try (var os = Files.newOutputStream(file, StandardOpenOption.CREATE_NEW)) {
          excel.write(os);
        }
        start += usersPerExcel;
      }
    }
  }

  private void clearChache() {
    ISystemDatabasePersistencyService.instance().getClassPersistencyService(AccessControlData.class).clearCache();
  }

  public void createSheets(int start, int end, Excel excel, boolean includesRoles) {
    var roles = getRoles();
    var users = getUsers(start, end);
    new OverviewSheet(excel, securityContext, users).create(end);
    new UsersSheet(excel, users).create();
    new UserRolesSheet(excel, users, getRoles()).create();
    if(includesRoles) {
      new RolesSheet(excel, roles).create();
      new RoleMembersSheet(excel, roles).create();
      new SecurityMemberPermissionSheet(excel, securityContext, rolesToSecurityMembers(roles)).create("Role");
    }
    new SecurityMemberPermissionSheet(excel, securityContext, usersToSecurityMembers(users)).create("User");
  }

  @SuppressWarnings("unchecked")
  private Iterable<ISecurityMember> usersToSecurityMembers(Iterable<IUser> users) {
    return (Iterable<ISecurityMember>)(Iterable<?>)users;
  }

  @SuppressWarnings("unchecked")
  private Iterable<ISecurityMember> rolesToSecurityMembers(Iterable<IRole> roles) {
    return (Iterable<ISecurityMember>)(Iterable<?>)roles;
  }

  private Iterable<IUser> getUsers(int start, int count){
    return securityContext.users().query().orderBy().name().executor().results(start, count);
  }


  private Iterable<IRole> getRoles() {
    var roles = new ArrayList<>(securityContext.roles().all());
    Collections.sort(roles, ROLE_NAME_COMPERATOR);
    return roles;
  }
}
