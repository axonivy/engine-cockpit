package ch.ivyteam.enginecockpit.security.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.SortOrder;

import ch.ivyteam.enginecockpit.commons.TableFilter;
import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.jsf.primefaces.legazy.LazyDataModel7;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.query.UserQuery;

public class UserDataModel extends LazyDataModel7<User> implements TableFilter {
  private static final String MANUAL_FILTER = "manual";
  private static final String DISABLED_FILTER = "disabled";
  private IApplication app;
  private IRole filterRole;
  private String filter;
  private boolean showDisabledUsers;
  private boolean showManualUsers;
  private List<SelectItem> contentFilters;
  private List<String> selectedContentFilters;

  public UserDataModel() {}

  public UserDataModel(IApplication app) {
    this.app = app;
  }

  public void setApp(IApplication app) {
    this.app = app;
  }

  public void setFilterRole(IRole filterRole) {
    this.filterRole = filterRole;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public String getFilter() {
    return filter;
  }

  public void loadContentFilters(boolean isIvySecSystem) {
    contentFilters = new ArrayList<>();
    contentFilters.add(new SelectItem(DISABLED_FILTER, "Show disabled users"));
    if (!isIvySecSystem) {
      contentFilters.add(new SelectItem(MANUAL_FILTER, "Show manual users"));
    }
  }

  @Override
  public List<SelectItem> getContentFilters() {
    return contentFilters;
  }

  @Override
  public List<String> getSelectedContentFilters() {
    return selectedContentFilters;
  }

  @Override
  public void setSelectedContentFilters(List<String> selectedContentFilters) {
    this.selectedContentFilters = selectedContentFilters;
    showDisabledUsers = selectedContentFilters.contains(DISABLED_FILTER);
    showManualUsers = selectedContentFilters.contains(MANUAL_FILTER);
  }

  @Override
  public void resetSelectedContentFilters() {
    setSelectedContentFilters(Collections.emptyList());
  }

  @Override
  public String getContentFilterText() {
    var joiner = new StringJoiner(" ");
    if (showManualUsers) {
      joiner.add(MANUAL_FILTER);
    }
    if (showDisabledUsers) {
      joiner.add(DISABLED_FILTER);
    }
    var filterText = joiner.toString();
    if (filterText.isBlank()) {
      filterText = "enabled";
    }
    return filterText + " users";
  }

  @Override
  public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder,
          Map<String, Object> filters) {
    var userQuery = userQuery();

    filterRole(userQuery);
    applyFilter(userQuery);
    applyOrdering(userQuery, sortField, sortOrder);

    var users = userQuery
            .executor()
            .resultsPaged()
            .map(User::new)
            .window(first, pageSize);
    checkIfUserIsLoggedIn(app, users);
    setRowCount((int) userQuery.executor().count());
    return users;
  }

  private void filterRole(UserQuery userQuery) {
    if (filterRole != null) {
      userQuery.where().hasRole(filterRole);
    }
  }

  private void applyFilter(UserQuery query) {
    if (StringUtils.isNotEmpty(filter)) {
      var dbFilter = "%" + filter + "%";
      query.where().and(userQuery().where()
              .name().isLikeIgnoreCase(dbFilter)
              .or()
              .fullName().isLikeIgnoreCase(dbFilter)
              .or()
              .eMailAddress().isLikeIgnoreCase(dbFilter));
    }

    query.where().and(userQuery().where().enabled().is(!showDisabledUsers));

    if (showManualUsers) {
      query.where().and(userQuery().where().external().isFalse());
    }
  }

  private UserQuery userQuery() {
    return app.getSecurityContext().users().query();
  }

  private static void applyOrdering(UserQuery query, String sortField, SortOrder sortOrder) {
    if (StringUtils.isEmpty(sortField)) {
      return;
    }
    if ("name".equals(sortField)) {
      applySorting(query.orderBy().name(), sortOrder);
    }
    if ("fullName".equals(sortField)) {
      applySorting(query.orderBy().fullName(), sortOrder);
    }
    if ("email".equals(sortField)) {
      applySorting(query.orderBy().eMailAddress(), sortOrder);
    }
  }

  private static void applySorting(UserQuery.OrderByColumnQuery query, SortOrder sortOrder) {
    if (SortOrder.ASCENDING.equals(sortOrder)) {
      query.ascending();
    }
    if (SortOrder.DESCENDING.equals(sortOrder)) {
      query.descending();
    }
  }

  private static void checkIfUserIsLoggedIn(IApplication app, List<User> appUsers) {
    for (var session : app.getSecurityContext().sessions().clusterSnapshot().getSessionInfos()) {
      var sessionUser = session.getSessionUserName();
      appUsers.stream()
              .filter(u -> u.getName().equals(sessionUser))
              .findAny()
              .ifPresent(user -> user.setLoggedIn(true));
    }
  }

}
