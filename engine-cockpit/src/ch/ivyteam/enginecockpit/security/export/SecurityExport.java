package ch.ivyteam.enginecockpit.security.export;

import java.io.IOException;
import java.nio.file.Files;
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
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.internal.data.AccessControlData;

public class SecurityExport {

  private static final Comparator<IRole> ROLE_NAME_COMPERATOR = Comparator.comparing(IRole::getName);

  private final ISecurityContext securityContext;

  public SecurityExport(ISecurityContext securityContext) {
    this.securityContext = securityContext;
  }

  public StreamedContent export() throws IOException{
    var usersCount = securityContext.users().query().orderBy().name().executor().count();
    var tempDir = Files.createTempDirectory("AxonivySecurityReports");
    var usersPerExcel = 1000;
    if (usersCount < usersPerExcel) {
      try (Excel excel = new Excel()) {
        createSheets(0, (int)usersCount, excel);
        return DefaultStreamedContent
                .builder()
                .stream(excel::write)
                .contentType("application/xlsx")
                .name("AxonivySecurityReport.xlsx")
                .build();
      }
    }
    int forCount = Math.ceilDiv((int)usersCount, usersPerExcel);
    var start = usersPerExcel;
    Ivy.log().info("Start");
    for(int i=0; i<forCount; i++) {
      ISystemDatabasePersistencyService.instance().getClassPersistencyService(AccessControlData.class).clearCache();
      try (Excel excel = new Excel()) {
        if(start > usersCount) {
          var newStart = start - usersPerExcel;
          start = (int)usersCount;
          createSheets(newStart, (int)usersCount - newStart, excel);
        }
        else {
          createSheets(start - usersPerExcel, start, excel);
        }
        var file = tempDir.resolve("AxonivySecurtyReport" + start + ".xlsx");
        try (var os = Files.newOutputStream(file, StandardOpenOption.CREATE_NEW)) {
          excel.write(os);
        }
        Ivy.log().info(start);
        start += usersPerExcel;
      }
    }
    Ivy.log().info("Zipping");
    var zipDir = Files.createTempDirectory("AxonivySecurityReport");
    var zipFile = zipDir.resolve("AxonivySecurityReport.zip");
    ZipUtil.zipDir(tempDir, zipFile);
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

  public void createSheets(int start, int end, Excel excel) {
    new OverviewSheet(excel, securityContext).create();
    var users = getUsers(start, end);
    new UsersSheet(excel, users).create();
    var roles = getRoles();
    new RolesSheet(excel, roles).create();
    new UserRolesSheet(excel, users, getRoles()).create();
    new RoleMembersSheet(excel, roles).create();
    new SecurityMemberPermissionSheet(excel, securityContext, usersToSecurityMembers(users)).create("User");
    new SecurityMemberPermissionSheet(excel, securityContext, rolesToSecurityMembers(roles)).create("Role");
  }

  @SuppressWarnings("unchecked")
  private Iterable<ISecurityMember> usersToSecurityMembers(Iterable<IUser> users) {
    return (Iterable<ISecurityMember>)(Iterable<?>)users;
  }

  @SuppressWarnings("unchecked")
  private Iterable<ISecurityMember> rolesToSecurityMembers(Iterable<IRole> roles) {
    return (Iterable<ISecurityMember>)(Iterable<?>)roles;
  }

  private Iterable<IUser> getUsers(int start, int end){
    return securityContext.users().query().orderBy().name().executor().results(start, end);
  }


  private Iterable<IRole> getRoles() {
    var roles = new ArrayList<>(securityContext.roles().all());
    Collections.sort(roles, ROLE_NAME_COMPERATOR);
    return roles;
  }
}
