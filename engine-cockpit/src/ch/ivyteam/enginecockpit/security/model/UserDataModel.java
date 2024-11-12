package ch.ivyteam.enginecockpit.security.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import ch.ivyteam.enginecockpit.commons.TableFilter;
import ch.ivyteam.ivy.jsf.primefaces.sort.SortMetaConverter;
import ch.ivyteam.ivy.security.IRole;
import ch.ivyteam.ivy.security.query.UserQuery;

public class UserDataModel extends LazyDataModel<User> implements TableFilter {

  private static final String MANUAL_FILTER = "manual";
  private static final String DISABLED_FILTER = "disabled";
  private SecuritySystem securitySystem;
  private IRole filterRole;
  private String filter;
  private boolean showDisabledUsers;
  private boolean showManualUsers;
  private List<SelectItem> contentFilters;
  private List<String> selectedContentFilters;

  public UserDataModel(SecuritySystem securitySystem) {
    this.securitySystem = securitySystem;
  }

  public void setSecuritySystem(SecuritySystem securitySystem) {
    this.securitySystem = securitySystem;
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
  public List<User> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
    var userQuery = userQuery();

    filterRole(userQuery);
    applyFilter(userQuery);
    applyOrdering(userQuery, sortBy);

    var users = userQuery
            .executor()
            .results(first, pageSize).stream()
            .map(User::new)
            .collect(Collectors.toList());
    checkIfUserIsLoggedIn(securitySystem, users);

    applyOrderingPostQuery(users, sortBy);

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
    return securitySystem.getSecurityContext().users().query();
  }

  private static void applyOrdering(UserQuery query, Map<String, SortMeta> sortBy) {
    var sort = new SortMetaConverter(sortBy);
    var sortOrder = sort.toOrder();
    var sortField = sort.toField();

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

  private static void checkIfUserIsLoggedIn(SecuritySystem securitySystem, List<User> appUsers) {
    for (var session : securitySystem.getSecurityContext().sessions().clusterSnapshot().getSessionInfos()) {
      var sessionUser = session.getSessionUserName();
      appUsers.stream()
              .filter(u -> u.getName().equals(sessionUser))
              .findAny()
              .ifPresent(user -> user.setLoggedIn(true));
    }
  }

  private static void applyOrderingPostQuery(List<User> users, Map<String, SortMeta> sortBy) {
    var sort = new SortMetaConverter(sortBy);
    var sortOrder = sort.toOrder();
    var sortField = sort.toField();
    if (sortField == null) {
      return;
    }

    Comparator<User> comparator;
    switch (sortField) {
      case "loggedIn" -> comparator = Comparator.comparing(User::isLoggedIn);
      default -> {
        return;
      }
    }

    if (SortOrder.ASCENDING.equals(sortOrder)) {
      comparator = comparator.reversed();
    }
    users.sort(comparator);
  }

  @Override
  public int count(Map<String, FilterMeta> filterBy) {
    return getRowCount();
  }

}
