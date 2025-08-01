package ch.ivyteam.enginecockpit.security.export;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
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
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.security.IUser;
import ch.ivyteam.ivy.security.role.NewRole;
import ch.ivyteam.ivy.security.user.NewUser;

@IvyTest
class TestSecurityExport {

  private static IUser userCedric;
  private static IUser userReto;
  private static IUser userRolf;
  private static IRole everybodyRole;
  private static IRole managerRole;
  private static IRole ceoRole;
  private static IRole employeeRole;
  private static Excel excel;
  private static List<IPermission> permissions;
  private static final Comparator<IPermission> PERMISSION_NAME_COMPERATOR = Comparator.comparing(IPermission::getName);

  @BeforeAll
  static void before() throws IOException {
    var users = Ivy.wf().getSecurityContext().users();
    var roles = Ivy.wf().getSecurityContext().roles();

    everybodyRole = Ivy.wf().getSecurityContext().roles().find("Everybody");
    everybodyRole.setProperty("Role without External Property", "no external");
    everybodyRole.setProperty("Sharedproperty", "Shared");

    NewRole manager = NewRole.create("Manager").externalName("Manager").description("Manager").toNewRole();
    managerRole = roles.create(manager);

    NewRole employee = NewRole.create("Employee").externalName("Employee").description("Employee").toNewRole();
    employeeRole = roles.create(employee);
    employeeRole.setProperty("Testproperty", "Test");
    employeeRole.setProperty("Sharedproperty", "Shared");
    employeeRole.addRoleMember(managerRole);
    employeeRole.addRoleMember(everybodyRole);

    NewRole ceo = NewRole.create("CEO").externalName("CEO").description("CEO").toNewRole();
    ceoRole = roles.create(ceo);

    NewUser rolf = NewUser.create("Rolf").fullName("Rolf Stephan").mailAddress("rolf.stephan@axonivy.com")
        .toNewUser();
    userRolf = users.create(rolf);
    userRolf.addRole(ceoRole);

    NewUser reto = NewUser.create("Reto").fullName("Reto Weiss").mailAddress("reto.weiss@axonivy.com")
        .toNewUser();
    userReto = users.create(reto);
    userReto.setProperty("User without External Property", "no external");
    userReto.setProperty("Sharedproperty", "Shared");
    userReto.addRole(managerRole);

    NewUser cedric = NewUser.create("Cedric").fullName("Cedric Weiss").externalId("123456789")
        .externalName("cn=Cedric Weiss,ou=development,dc=ivyteam,dc=ch")
        .mailAddress("cedric.weiss@axonivy.com")
        .toNewUser();
    userCedric = users.create(cedric);
    userCedric.setProperty("Testproperty", "Test");
    userCedric.setProperty("Sharedproperty", "Shared");
    userCedric.addRole(employeeRole);

    permissions = Ivy.wf().getSecurityContext().securityDescriptor().getPermissions();
    Collections.sort(permissions, PERMISSION_NAME_COMPERATOR);
    var wf = Ivy.wf();
    var securityExport = new SecurityExport(wf.getSecurityContext(), ISession.current());
    securityExport.export();
    StreamedContent export = securityExport.getResult();
    excel = new Excel(export.getStream().get());
  }

  @Test
  void exportUsers() {
    var userSheet = excel.getSheet("Users");

    String[][] userData = {
        {"Name", "Displayname", "Fullname", "Email", "SecurityId", "ExternalId", "External Name", "Testproperty", "Sharedproperty",
            "User without External Property"},
        {userCedric.getName(), userCedric.getDisplayName(), userCedric.getFullName(), userCedric.getEMailAddress(),
            userCedric.getSecurityMemberId(), userCedric.getExternalId(),
            userCedric.getExternalName(), "Test", "Shared", null},
        {userReto.getName(), userReto.getDisplayName(), userReto.getFullName(), userReto.getEMailAddress(),
            userReto.getSecurityMemberId(), "", "", null, "Shared", "no external"},
        {userRolf.getName(), userRolf.getDisplayName(), userRolf.getFullName(), userRolf.getEMailAddress(),
            userRolf.getSecurityMemberId(), "", "", null, null, null}
    };
    ExcelAssertions.assertThat(userSheet).contains(userData);
  }

  @Test
  void exportRoles() {
    var rolesSheet = excel.getSheet("Roles");

    String[][] rolesData = {
        {"Name", "Displayname", "Description", "Security Member Id", "External Name", "Testproperty",
            "Sharedproperty", "Role without External Property"},
        {"CEO", "CEO", "CEO", ceoRole.getSecurityMemberId(), "CEO", null, null, null},
        {"Employee", "Employee", "Employee", employeeRole.getSecurityMemberId(), "Employee", "Test", "Shared", null},
        {"Everybody", "Everybody", "Top level role", everybodyRole.getSecurityMemberId(), "", null, "Shared", "no external"},
        {"Manager", "Manager", "Manager", managerRole.getSecurityMemberId(), "Manager", null, null, null},
    };
    ExcelAssertions.assertThat(rolesSheet).contains(rolesData);
  }

  @Test
  void exportUserRoles() {
    var userRolesSheet = excel.getSheet("User roles");
    String[][] userRolesData = {
        {"Name", ceoRole.getName(), employeeRole.getName(),
            everybodyRole.getName(), managerRole.getName()},
        {userCedric.getName(), null, "X", "X", null},
        {userReto.getName(), null, "x", "X", "X"},
        {userRolf.getName(), "X", "x", "X", null}
    };
    ExcelAssertions.assertThat(userRolesSheet).contains(userRolesData);

  }

  @Test
  void exportRoleMembers() {
    var roleMembersSheet = excel.getSheet("Role members");
    String[][] roleMembersData = {
        {"Role name", "CEO", "Employee", "Everybody", "Manager"},
        {"CEO", null, null, "P", null},
        {"Employee", null, null, "P/M", "M"},
        {"Everybody", null, null, null, null},
        {"Manager", null, null, "P", null}
    };
    ExcelAssertions.assertThat(roleMembersSheet).contains(roleMembersData);
  }

  @Test
  void userPermissionsMembers() {
    var userPermissionsSheet = excel.getSheet("User permissions");
    String[][] userPermissionsData = new String[4][permissions.size() + 1];
    userPermissionsData[0][0] = "Name";
    userPermissionsData[1][0] = userCedric.getName();
    userPermissionsData[2][0] = userReto.getName();
    userPermissionsData[3][0] = userRolf.getName();
    addSecurityMemberPermissions(userPermissionsData, userCedric, 1);
    addSecurityMemberPermissions(userPermissionsData, userReto, 2);
    addSecurityMemberPermissions(userPermissionsData, userRolf, 3);

    ExcelAssertions.assertThat(userPermissionsSheet).contains(userPermissionsData);

  }

  private String[][] addSecurityMemberPermissions(String[][] permissionsData, ISecurityMember member, int memberCount) {
    var securityContext = Ivy.wf().getSecurityContext();
    var counter = 1;
    for (var permission : permissions) {
      permissionsData[0][counter] = permission.getName();
      var permissionCheck = securityContext.securityDescriptor().getPermissionAccess(permission, member);
      if (permissionCheck.isGranted()) {
        if (permissionCheck.isExplicit()) {
          permissionsData[memberCount][counter] = "G";
        } else {
          permissionsData[memberCount][counter] = "g";
        }
      } else if (permissionCheck.isDenied()) {
        if (permissionCheck.isExplicit()) {
          permissionsData[memberCount][counter] = "D";
        } else {
          permissionsData[memberCount][counter] = "d";
        }
      } else {
        permissionsData[memberCount][counter] = "";
      }
      counter++;
    }
    return permissionsData;
  }

  @Test
  void rolePermissionsMembers() {
    var rolePermissionsSheet = excel.getSheet("Role permissions");
    String[][] rolePermissionsData = new String[5][permissions.size() + 1];
    rolePermissionsData[0][0] = "Name";
    rolePermissionsData[1][0] = ceoRole.getName();
    rolePermissionsData[2][0] = employeeRole.getName();
    rolePermissionsData[3][0] = everybodyRole.getName();
    rolePermissionsData[4][0] = managerRole.getName();
    rolePermissionsData = addSecurityMemberPermissions(rolePermissionsData, ceoRole, 1);
    rolePermissionsData = addSecurityMemberPermissions(rolePermissionsData, employeeRole, 2);
    rolePermissionsData = addSecurityMemberPermissions(rolePermissionsData, everybodyRole, 3);
    rolePermissionsData = addSecurityMemberPermissions(rolePermissionsData, managerRole, 4);

    ExcelAssertions.assertThat(rolePermissionsSheet).contains(rolePermissionsData);
  }

  @Test
  void exportOverview() {
    var overviewSheet = excel.getSheet("Overview");
    String[][] overviewData = {
        {"Axon Ivy Security Report ", null, null},
        {null, null, null},
        {"Security System Name", "default", null},
        {"Applications", "test", null},
        {"Date", overviewSheet.getData()[4][1], null},
        {"Axon Ivy Version", Advisor.getAdvisor().getVersion().toString(), null},
        {"Current User", "Unknown User (Session 1)", null},
        {"Hostname", "test.axonivy.com", null},
        {"Number of Users", "3", null},
        {"Number of Roles", "4", null},
        {"File number", "1 of 1", null},
        {"First and Last User", "Cedric Weiss - Rolf Stephan", null},
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
    var timeStr = overviewSheet.getData()[4][1];
    var dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    var time = LocalDateTime.parse(timeStr, dtf);
    Assertions.assertThat(time).isCloseTo(LocalDateTime.now(), Assertions.within(10, ChronoUnit.SECONDS));
  }
}
