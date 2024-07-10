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
import ch.ivyteam.ivy.security.role.NewRole;
import ch.ivyteam.ivy.security.user.NewUser;

@IvyTest
class TestSecurityExport {

  private static IUser userWithExternal;
  private static IUser userWithoutExternal;
  private static IRole role;
  private static IRole roleWithExternal;
  private static Excel excel;
  private static List<IPermission> permissions;

  @BeforeAll
  static void before() throws IOException {
    var users = Ivy.wf().getSecurityContext().users();
    var roles = Ivy.wf().getSecurityContext().roles();
    role = Ivy.wf().getSecurityContext().roles().find("Everybody");
    NewRole roleWithExternalName = NewRole.create("test").externalName("Marketing").description("blabla").toNewRole();
    roleWithExternal = roles.create(roleWithExternalName);
    role.addRoleMember(roleWithExternal);
    NewUser userWithExternalId = NewUser.create("Cedric").fullName("Cedric Weiss").externalId("123456789")
            .externalName("cn=Cedric Weiss,ou=development,dc=ivyteam,dc=ch")
            .mailAddress("cedric.weiss@axonivy.com")
            .toNewUser();

    NewUser userWithoutExternalId = NewUser.create("Reto").fullName("Reto Weiss").mailAddress("reto.weiss@axonivy.com")
            .toNewUser();

    userWithoutExternal = users.create(userWithoutExternalId);
    userWithExternal = users.create(userWithExternalId);
    userWithExternal.setProperty("Testproperty", "Test");

    permissions = Ivy.wf().getSecurityContext().securityDescriptor().getPermissions();
    var wf = Ivy.wf();
    StreamedContent export = new SecurityExport(wf.getSecurityContext()).export();
    excel = new Excel(export.getStream().get());
  }

  @Test
  void exportUsers() {
    var userSheet = excel.getSheet("Users");

    String[][] userData = new String[][] {
      {"Displayname", "Fullname", "Membername", "Name", "Email", "SecurityId", "ExternalId", "External Name", "Testproperty"},
      {userWithExternal.getDisplayName(), userWithExternal.getFullName(), userWithExternal.getMemberName(), userWithExternal.getName(),
        userWithExternal.getEMailAddress(), userWithExternal.getSecurityMemberId(), userWithExternal.getExternalId(),
        userWithExternal.getExternalName(), "Test" },
      {userWithoutExternal.getDisplayName(), userWithoutExternal.getFullName(), userWithoutExternal.getMemberName(), userWithoutExternal.getName(),
        userWithoutExternal.getEMailAddress(), userWithoutExternal.getSecurityMemberId(), "", "", null}
    };
    ExcelAssertions.assertThat(userSheet).contains(userData);
  }

  @Test
  void exportRoles() {
    var rolesSheet = excel.getSheet("Roles");

    String[][] rolesData = new String[][] {
      {"Displayname", "Name", "Description", "Member Name", "Security Member Id", "External Name", "Testproperty"},
      {"Everybody", "Everybody", "Top level role", "Everybody" , role.getSecurityMemberId(), "", null},
      {"test", "test", roleWithExternal.getDisplayDescription(), "test", roleWithExternal.getSecurityMemberId(), roleWithExternal.getExternalName(),
        null}
    };
    ExcelAssertions.assertThat(rolesSheet).contains(rolesData);
  }

  @Test
  void exportUserRoles() {
    var userRolesSheet = excel.getSheet("User roles");

    String[][] userRolesData = new String[][] {
      {"Username", role.getDisplayName(), roleWithExternal.getDisplayName()},
      {userWithExternal.getFullName(), "X", null},
      {userWithoutExternal.getFullName(), "X", null}
    };
    ExcelAssertions.assertThat(userRolesSheet).contains(userRolesData);

  }

  @Test
  void exportRoleMembers() {
    var roleMembersSheet = excel.getSheet("Role members");
    String[][] roleMembersData = new String[][] {
      {"Role name", "Everybody", "test"},
      {"Everybody", null, "M"},
      {"test", null, null}
    };
    ExcelAssertions.assertThat(roleMembersSheet).contains(roleMembersData);

  }

  @Test
  void userPermissionsMembers() {
    var userPermissionsSheet = excel.getSheet("User permissions");
    String[][] userPermissionsData = new String[3][permissions.size() + 1];
    userPermissionsData[0][0] = "Username";
    userPermissionsData[1][0] = userWithExternal.getFullName();
    userPermissionsData[2][0] = userWithoutExternal.getFullName();
    userPermissionsData = getUserPermissions(userPermissionsData, userWithExternal, 1);
    userPermissionsData = getUserPermissions(userPermissionsData, userWithoutExternal, 2);

    ExcelAssertions.assertThat(userPermissionsSheet).contains(userPermissionsData);

  }

  private String[][] getUserPermissions(String[][] userPermissionsData, IUser user, int userCount) {
    var securityContext = Ivy.wf().getSecurityContext();
    var counter = 1;
    for(var permission : permissions) {
      userPermissionsData[0][counter] = permission.getName();
      var permissionCheck = securityContext.securityDescriptor().getPermissionAccess(permission, user);
      if(permissionCheck.isGranted()) {
        if(permissionCheck.isExplicit()) {
          userPermissionsData[userCount][counter] = "G";
        }
        else {
          userPermissionsData[userCount][counter] = "g";
        }
      }
      else if(permissionCheck.isDenied()) {
        if(permissionCheck.isExplicit()) {
          userPermissionsData[userCount][counter] = "D";
        }
        else {
          userPermissionsData[userCount][counter] = "d";
        }
      }
      counter++;
    }
    return userPermissionsData;
  }

  @Test
  void rolePermissionsMembers() {
    var rolePermissionsSheet = excel.getSheet("Role permissions");
    String[][] rolePermissionsData = new String[3][permissions.size() + 1];
    rolePermissionsData[0][0] = "Role name";
    rolePermissionsData[1][0] = role.getDisplayName();
    rolePermissionsData[2][0] = roleWithExternal.getDisplayName();
    rolePermissionsData = getRolesPermissions(rolePermissionsData, role, 1);
    rolePermissionsData = getRolesPermissions(rolePermissionsData, roleWithExternal, 2);

    ExcelAssertions.assertThat(rolePermissionsSheet).contains(rolePermissionsData);
  }

  private String[][] getRolesPermissions(String[][] rolePermissionsData, IRole roleParam, int roleCount) {
    var securityContext = Ivy.wf().getSecurityContext();
    var counter = 1;
    for(var permission : permissions) {
      rolePermissionsData[0][counter] = permission.getName();
      var permissionCheck = securityContext.securityDescriptor().getPermissionAccess(permission, roleParam);
      if(permissionCheck.isGranted()) {
        if(permissionCheck.isExplicit()) {
          rolePermissionsData[roleCount][counter] = "G";
        }
        else {
          rolePermissionsData[roleCount][counter] = "g";
        }
      }
      else if(permissionCheck.isDenied()) {
        if(permissionCheck.isExplicit()) {
          rolePermissionsData[roleCount][counter] = "D";
        }
        else {
          rolePermissionsData[roleCount][counter] = "d";
        }
      }
      counter++;
    }
    return rolePermissionsData;
  }

  @Test
  void exportOverview() {
    var overviewSheet = excel.getSheet("Overview");
    String[][] overviewData = new String[][] {
      {"Axonivy Security Report", null, null},
      {null, null, null},
      {"Security System Name", "default", null},
      {"Date", overviewSheet.getData()[3][1], null},
      {"Axonivy Version", Advisor.getAdvisor().getVersion().toString(), null},
      {"Current User", "Unknown User (Session 1)", null},
      {"Hostname", "test.axonivy.com", null},
      {"Number of Users", "2", null},
      {"Number of Roles", "2", null},
      {null, null, null},
      {null, null, null},
      {null, null, null},
      {"Legend", null, null},
      {"User roles", "X", "User owns role directly"},
      {"", "x", "User owns role indirectly"},
      {null, null, null},
      {"Role members", "M", "Role is a role member"},
      {"", "P", "Role is the parent role of a role"},
      {null, null, null},
      {"User permissions / Role permissions", "G", "Permission was granted directly"},
      {"", "g", "Permission was granted indirectly"},
      {"", "D", "Permission was denied directly"},
      {"", "d", "Permission was denied indirectly"}
    };

    ExcelAssertions.assertThat(overviewSheet).contains(overviewData);
    var timeStr = overviewSheet.getData()[3][1];
    var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    var time = LocalDateTime.parse(timeStr, dtf);
    Assertions.assertThat(time).isCloseTo(LocalDateTime.now(), Assertions.within(10, ChronoUnit.SECONDS));
  }
}
