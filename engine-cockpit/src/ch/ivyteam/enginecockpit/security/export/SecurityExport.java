package ch.ivyteam.enginecockpit.security.export;

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
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.ISecurityContext;
import ch.ivyteam.ivy.security.ISecurityMember;
import ch.ivyteam.ivy.security.IUser;

public class SecurityExport {

  private static final Comparator<IRole> ROLE_NAME_COMPERATOR = Comparator.comparing(IRole::getName);

  private final ISecurityContext securityContext;
  private final Excel excel = new Excel();

  public SecurityExport(ISecurityContext securityContext) {
    this.securityContext = securityContext;
  }

  public StreamedContent export(){
    createSheets();
    return DefaultStreamedContent
            .builder()
            .stream(excel::write)
            .contentType("application/xlsx")
            .name("AxonivySecurityReport.xlsx")
            .build();
  }

  public void createSheets() {
    new OverviewSheet(excel, securityContext).create();
    var users = getUsers();
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

  private Iterable<IUser> getUsers(){
    return securityContext.users().query().orderBy().name().executor().resultsPaged();
  }


  private Iterable<IRole> getRoles() {
    var roles = new ArrayList<>(securityContext.roles().all());
    Collections.sort(roles, ROLE_NAME_COMPERATOR);
    return roles;
  }
}
