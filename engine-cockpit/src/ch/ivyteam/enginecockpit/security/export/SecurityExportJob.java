package ch.ivyteam.enginecockpit.security.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import ch.ivyteam.ivy.job.IJob;
import ch.ivyteam.ivy.persistence.db.ISystemDatabasePersistencyService;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.internal.data.AccessControlData;

@SuppressWarnings("restriction")
public class SecurityExportJob implements IJob {

  private static final Comparator<IRole> ROLE_NAME_COMPERATOR = Comparator.comparing(IRole::getName);
  private final ISecurityContext securityContext;
  private volatile int progress = 0;
  private Excel onlyExcel;
  private Path zipFile;
  private ISession session;

  public SecurityExportJob(ISecurityContext securityContext, ISession session) {
    this.securityContext = securityContext;
    this.session = session;
  }

  @Override
  public void execute(){
    var usersCount = (int)securityContext.users().query().orderBy().name().executor().count();
    Path tempDir;

    try {
      tempDir = Files.createTempDirectory("AxonivySecurityReports");

      var usersPerExcel = 1000;

      if (usersCount < usersPerExcel) {
        Excel excel = new Excel();
        createSheets(0, usersCount, excel, 0);
        this.onlyExcel = excel;
        progress=100;
      }
      else {
        int forCount = Math.ceilDiv(usersCount, usersPerExcel);
        var start = usersPerExcel;

        for(int i=0; i<forCount;) {
          if (Thread.currentThread().isInterrupted()) {
            return;
          }

          clearChache();

          try (Excel excel = new Excel()) {
            if(i == 0) {
              var newStart = start - usersPerExcel;
              createSheets(newStart, usersPerExcel, excel, i);
            }
            else {
              createSheets(start - usersPerExcel, usersPerExcel, excel, i);
            }

            var file = tempDir.resolve("AxonivySecurtyReport" + start + ".xlsx");
            try (var os = Files.newOutputStream(file, StandardOpenOption.CREATE_NEW)) {
              excel.write(os);
            }

            start += usersPerExcel;
            i++;
            progress= i *100 /forCount;
          }
        }
      }

      var zipDir = Files.createTempDirectory("AxonivySecurityReport");
      zipFile = zipDir.resolve("AxonivySecurityReport.zip");
      ZipUtil.zipDir(tempDir, zipFile);

    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public String getName() {
    return "Security Export Job";
  }

  public StreamedContent getResult() {
    if(onlyExcel != null) {
      return DefaultStreamedContent
              .builder()
              .stream(onlyExcel::write)
              .contentType("application/xlsx")
              .name("AxonivySecurityReport.xlsx")
              .build();
    }
    else {
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
  }

  public int getProgress() {
    return progress;
  }

  private void clearChache() {
    ISystemDatabasePersistencyService.instance().getClassPersistencyService(AccessControlData.class).clearCache();
  }

  public void createSheets(int start, int end, Excel excel, int i) {
    var roles = getRoles();
    var users = getUsers(start, end);
    new OverviewSheet(excel, securityContext, users , session).create(end, i);
    new UsersSheet(excel, users).create();
    new UserRolesSheet(excel, users, getRoles()).create();
    new SecurityMemberPermissionSheet(excel, securityContext, usersToSecurityMembers(users)).create("User");
    if(i == 0) {
      new RolesSheet(excel, roles).create();
      new SecurityMemberPermissionSheet(excel, securityContext, rolesToSecurityMembers(roles)).create("Role");
      new RoleMembersSheet(excel, roles).create();
    }
  }

  @SuppressWarnings("unchecked")
  private Iterable<ISecurityMember> usersToSecurityMembers(Iterable<IUser> users) {
    return (Iterable<ISecurityMember>)(Iterable<?>)users;
  }

  @SuppressWarnings("unchecked")
  private Iterable<ISecurityMember> rolesToSecurityMembers(Iterable<IRole> roles) {
    return (Iterable<ISecurityMember>)(Iterable<?>)roles;
  }

  private List<IUser> getUsers(int start, int count){
    return securityContext.users().query().orderBy().name().executor().results(start, count);
  }


  private Iterable<IRole> getRoles() {
    var roles = new ArrayList<>(securityContext.roles().all());
    Collections.sort(roles, ROLE_NAME_COMPERATOR);
    return roles;
  }

  public void setProgress(int i) {
    Ivy.log().info("progress: " + i);
    progress = i;
  }
}
