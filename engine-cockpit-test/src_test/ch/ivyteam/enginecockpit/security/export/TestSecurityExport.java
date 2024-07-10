package ch.ivyteam.enginecockpit.security.export;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.primefaces.model.StreamedContent;

import ch.ivyteam.enginecockpit.security.export.excel.Excel;
import ch.ivyteam.ivy.Advisor;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.environment.IvyTest;
import ch.ivyteam.ivy.security.IPermission;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.IUser;

@IvyTest
class TestSecurityExport {

  private static IUser user;
  private static IRole role;
  private static IRole testRole;
  private static Excel excel;
  private static List<IPermission> permissions;

  @BeforeAll
  static void before() throws IOException {
    var users = Ivy.wf().getSecurityContext().users();
    role = Ivy.wf().getSecurityContext().roles().find("Everybody");
    testRole = Ivy.wf().getSecurityContext().roles().create("test");
    role.addRoleMember(testRole);
    user = users.create("Cedric");
    permissions = Ivy.wf().getSecurityContext().securityDescriptor().getPermissions();
    var wf = Ivy.wf();
    StreamedContent export = new SecurityExport(wf.getSecurityContext()).export();
    excel = new Excel(export.getStream().get());
  }

  @Test
  void exportUsers() {
    var userSheet = excel.getSheet("User");

    String[][] userData = new String[][] {
      {"Displayname", "Fullname", "Membername", "Name", "Email", "SecurityId", "ExternalId", "External Name"},
      {"Cedric", "Cedric", "#Cedric", "Cedric", "", user.getSecurityMemberId(), "", "", }
    };
    ExcelAssertions.assertThat(userSheet).contains(userData);
  }

  @Test
  void exportRoles() {
    var rolesSheet = excel.getSheet("Roles");

    String[][] rolesData = new String[][] {
      {"Displayname", "Name", "Description", "Member Name", "Security Member Id", "External Name"},
      {"test", "test", "", "test", testRole.getSecurityMemberId(), ""},
      {"Everybody", "Everybody", "Top level role", "Everybody" , role.getSecurityMemberId(), ""}
    };
    ExcelAssertions.assertThat(rolesSheet).contains(rolesData);
  }

  @Test
  void exportUserRoles() {
    var userRolesSheet = excel.getSheet("User roles");

    String[][] userRolesData = new String[][] {
      {"Username", testRole.getDisplayName() , role.getDisplayName()},
      {"Cedric", "X", null}
    };
    ExcelAssertions.assertThat(userRolesSheet).contains(userRolesData);

  }

  @Test
  void exportRoleMembers() {
    var roleMembersSheet = excel.getSheet("Role members");
    String[][] roleMembersData = new String[][] {
      {"Role name", "test", "Everybody"},
      {"test", null, null},
      {"Everybody", "M", null}
    };
    ExcelAssertions.assertThat(roleMembersSheet).contains(roleMembersData);

  }

  @Test
  void userPermissionsMembers() {
    var securityContext = Ivy.wf().getSecurityContext();
    var userPermissionsSheet = excel.getSheet("User permissions");
    var counter = 1;
    String[][] userPermissionsData = new String[2][permissions.size() + 1];
    userPermissionsData[0][0] = "Username";
    userPermissionsData[1][0] = "Cedric";
    for(var permission : permissions) {
      userPermissionsData[0][counter] = permission.getName();
      var permissionCheck = securityContext.securityDescriptor().getPermissionAccess(permission, user);
      if(permissionCheck.isGranted()) {
        if(permissionCheck.isExplicit()) {
          userPermissionsData[1][counter] = "G";
        }
        else {
          userPermissionsData[1][counter] = "g";
        }
      }
      else if(permissionCheck.isDenied()) {
        if(permissionCheck.isExplicit()) {
          userPermissionsData[1][counter] = "D";
        }
        else {
          userPermissionsData[1][counter] = "d";
        }
      }
      counter++;
    }

    ExcelAssertions.assertThat(userPermissionsSheet).contains(userPermissionsData);

  }

  @Test
  void rolePermissionsMembers() {
    var securityContext = Ivy.wf().getSecurityContext();
    var rolePermissionsSheet = excel.getSheet("Role permissions");
    var counter = 1;
    String[][] rolePermissionsData = new String[3][permissions.size() + 1];
    rolePermissionsData[0][0] = "Role name";
    rolePermissionsData[1][0] = "test";
    rolePermissionsData[2][0] = "Everybody";
    for(var permission : permissions) {
      rolePermissionsData[0][counter] = permission.getName();
      var permissionCheckEverybody = securityContext.securityDescriptor().getPermissionAccess(permission, role);
      var permissionCheckTest = securityContext.securityDescriptor().getPermissionAccess(permission, testRole);
      if(permissionCheckTest.isGranted()) {
        if(permissionCheckTest.isExplicit()) {
          rolePermissionsData[1][counter] = "G";
        }
        else {
          rolePermissionsData[1][counter] = "g";
        }
      }
      else if(permissionCheckTest.isDenied()) {
        if(permissionCheckTest.isExplicit()) {
          rolePermissionsData[1][counter] = "D";
        }
        else {
          rolePermissionsData[1][counter] = "d";
        }
      }

      if(permissionCheckEverybody.isGranted()) {
        if(permissionCheckEverybody.isExplicit()) {
          rolePermissionsData[2][counter] = "G";
        }
        else {
          rolePermissionsData[2][counter] = "g";
        }
      }
      else if(permissionCheckEverybody.isDenied()) {
        if(permissionCheckEverybody.isExplicit()) {
          rolePermissionsData[2][counter] = "G";
        }
        else {
          rolePermissionsData[2][counter] = "g";
        }
      }
      counter++;
    }

    ExcelAssertions.assertThat(rolePermissionsSheet).contains(rolePermissionsData);
  }

  @Test
  void exportOverview() {
    var overviewSheet = excel.getSheet("Overview");
    String[][] overviewData = new String[][] {
      {"Security System Name", "default"},
      {"Date", overviewSheet.getData()[1][1]},
      {"Axonivy Version", Advisor.getAdvisor().getVersion().toString()},
      {"Current User", "Unknown User (Session 1)"},
      {"Hostname", "test.axonivy.com"},
      {"Number of Users", "1"},
      {"Number of Roles", "2"}
    };
    ExcelAssertions.assertThat(overviewSheet).contains(overviewData);
    var timeStr = overviewSheet.getData()[1][1];
    var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    var time = LocalDateTime.parse(timeStr, dtf);
    Assertions.assertThat(time).isCloseTo(LocalDateTime.now(), Assertions.within(10, ChronoUnit.SECONDS));
  }
}
