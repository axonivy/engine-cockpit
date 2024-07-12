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

  private static IUser userCedric;
  private static IUser userReto;
  private static IRole everybodyRole;
  private static IRole testRole;
  private static Excel excel;
  private static List<IPermission> permissions;

  @BeforeAll
  static void before() throws IOException {
    var users = Ivy.wf().getSecurityContext().users();
    var roles = Ivy.wf().getSecurityContext().roles();

    everybodyRole = Ivy.wf().getSecurityContext().roles().find("Everybody");
    everybodyRole.setProperty("Role without External Property", "no external");
    everybodyRole.setProperty("Sharedproperty", "Shared");

    NewRole test = NewRole.create("test").externalName("Marketing").description("blabla").toNewRole();
    testRole = roles.create(test);
    testRole.setProperty("Testproperty", "Test");
    testRole.setProperty("Sharedproperty", "Shared");
    testRole.addRoleMember(everybodyRole);

    NewUser reto = NewUser.create("Reto").fullName("Reto Weiss").mailAddress("reto.weiss@axonivy.com")
            .toNewUser();
    userReto = users.create(reto);
    userReto.setProperty("User without External Property", "no external");
    userReto.setProperty("Sharedproperty", "Shared");

    NewUser cedric = NewUser.create("Cedric").fullName("Cedric Weiss").externalId("123456789")
            .externalName("cn=Cedric Weiss,ou=development,dc=ivyteam,dc=ch")
            .mailAddress("cedric.weiss@axonivy.com")
            .toNewUser();
    userCedric = users.create(cedric);
    userCedric.setProperty("Testproperty", "Test");
    userCedric.setProperty("Sharedproperty", "Shared");
    userCedric.addRole(testRole);

    permissions = Ivy.wf().getSecurityContext().securityDescriptor().getPermissions();
    var wf = Ivy.wf();
    StreamedContent export = new SecurityExport(wf.getSecurityContext()).export();
    excel = new Excel(export.getStream().get());
  }

  @Test
  void exportUsers() {
    var userSheet = excel.getSheet("Users");

    String[][] userData = new String[][] {
      {"Displayname", "Fullname", "Membername", "Name", "Email", "SecurityId", "ExternalId", "External Name", "Testproperty", "Sharedproperty",
        "User without External Property"},
      {userCedric.getDisplayName(), userCedric.getFullName(), userCedric.getMemberName(), userCedric.getName(),
        userCedric.getEMailAddress(), userCedric.getSecurityMemberId(), userCedric.getExternalId(),
        userCedric.getExternalName(), "Test", "Shared", null },
      {userReto.getDisplayName(), userReto.getFullName(), userReto.getMemberName(), userReto.getName(),
        userReto.getEMailAddress(), userReto.getSecurityMemberId(), "", "", null, "Shared", "no external"}
    };
    ExcelAssertions.assertThat(userSheet).contains(userData);
  }

  @Test
  void exportRoles() {
    var rolesSheet = excel.getSheet("Roles");

    String[][] rolesData = new String[][] {
      {"Displayname", "Name", "Description", "Member Name", "Security Member Id", "External Name", "Role without External Property",
        "Sharedproperty", "Testproperty"},
      {"Everybody", "Everybody", "Top level role", "Everybody" , everybodyRole.getSecurityMemberId(), "",  "no external", "Shared", null},
      {"test", "test", testRole.getDisplayDescription(), "test", testRole.getSecurityMemberId(), testRole.getExternalName()
        , null, "Shared", "Test"}
    };
    ExcelAssertions.assertThat(rolesSheet).contains(rolesData);
  }

  @Test
  void exportUserRoles() {
    var userRolesSheet = excel.getSheet("User roles");
    String[][] userRolesData = new String[][] {
      {"Username", everybodyRole.getDisplayName(), testRole.getDisplayName()},
      {userCedric.getFullName(), "X", "X"},
      {userReto.getFullName(), "X", "x"}
    };
    ExcelAssertions.assertThat(userRolesSheet).contains(userRolesData);

  }

  @Test
  void exportRoleMembers() {
    var roleMembersSheet = excel.getSheet("Role members");
    String[][] roleMembersData = new String[][] {
      {"Role name", "Everybody", "test"},
      {"Everybody", null, null},
      {"test", "P/M", null}
    };
    ExcelAssertions.assertThat(roleMembersSheet).contains(roleMembersData);
  }

  @Test
  void userPermissionsMembers() {
    var userPermissionsSheet = excel.getSheet("User permissions");
    String[][] userPermissionsData = new String[3][permissions.size() + 1];
    userPermissionsData[0][0] = "Username";
    userPermissionsData[1][0] = userCedric.getFullName();
    userPermissionsData[2][0] = userReto.getFullName();
    addUserPermissions(userPermissionsData, userCedric, 1);
    addUserPermissions(userPermissionsData, userReto, 2);

    ExcelAssertions.assertThat(userPermissionsSheet).contains(userPermissionsData);

  }

  private String[][] addUserPermissions(String[][] userPermissionsData, IUser user, int userCount) {
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
      else {
        userPermissionsData[userCount][counter] = "";
      }
      counter++;
    }
    return userPermissionsData;
  }

  @Test
  void rolePermissionsMembers() {
    var rolePermissionsSheet = excel.getSheet("Role permissions");
    String[][] rolePermissionsData = new String[3][permissions.size() + 1];
    rolePermissionsData[0][0] = "Rolename";
    rolePermissionsData[1][0] = everybodyRole.getDisplayName();
    rolePermissionsData[2][0] = testRole.getDisplayName();
    rolePermissionsData = addRolesPermissions(rolePermissionsData, everybodyRole, 1);
    rolePermissionsData = addRolesPermissions(rolePermissionsData, testRole, 2);

    ExcelAssertions.assertThat(rolePermissionsSheet).contains(rolePermissionsData);
  }

  private String[][] addRolesPermissions(String[][] rolePermissionsData, IRole roleParam, int roleCount) {
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
      else {
        rolePermissionsData[roleCount][counter] = "";
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
